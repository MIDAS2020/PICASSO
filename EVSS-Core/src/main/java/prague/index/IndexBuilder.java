package prague.index;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.SetMultimap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import gblend.db.DatabaseInfo;
import prague.cam.CamBuilderFactory;
import prague.cam.CamBuilderInterface;
import prague.data.DataPath;
import prague.graph.Graph;
import prague.graph.ImmutableGraph;
import prague.graph.LabelEdge;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import prague.index.data.DifIndexAccessor;
import prague.index.data.PIndexDataItem;
import prague.index.data.PItemType;
import prague.index.jdbc.PIndexAccessor;
import prague.index.model.GOperation;
import prague.index.repository.PIndexRepository;
import prague.util.IntegerListHelper;
import prague.util.MergeIntersect;
import prague.verify.VF2;
import wch.guava2.base.Timer;

/**
 * this class is used to build indexes, both memory based and disk based, both frequent index and
 * infrequent index
 *
 * Created by ch.wang on 24 Jan 2014.
 */
public class IndexBuilder {

  private static final int MAGIC_EDGE_SIZE = 99;
  private static final String INDEX_BUILDER_KEY = "IndexBuilt";
  private static final String INDEX_BUILDER_VALUE = "Built";
  private static final String THREAD_CONTEXT_KEY = "builderName";

  private final Logger logger;

  private final PragueIndex pi;
  private final DataPath path;
  private final PIndexAccessor indexAccessor;
  private final PIndexRepository indexRepository;
  private final CamBuilderInterface cb = CamBuilderFactory.getCamBuilder();
  private final SubgraphChecker sc;

  private Set<String> freqCamSet;
  private Set<String> infCamSet = new HashSet<>();
  private SetMultimap<String, LinkGraph<LGVertex>> fgMap;
  private Map<String, LinkGraph<LGVertex>> difGraphMap;
  private int aSize;
  private ImmutableList<ImmutableGraph> graphSet;
  private int labelSize;
  private boolean[][] freqEdges;
  private OperationRegistration or;

  public IndexBuilder(PragueIndex pi) {
    this.pi = pi;
    path = pi.getPath();
    ThreadContext.put(THREAD_CONTEXT_KEY, path.getPLog());
    logger = LogManager.getLogger(this);
    indexRepository = pi.getPIndexRepository();
    indexAccessor = indexRepository.getPiAccessor();
    sc = new SubgraphChecker(indexRepository);
  }

  private void cleanUp() throws Exception {
    indexAccessor.close();
    ThreadContext.remove(THREAD_CONTEXT_KEY);
  }

  // read or construct the index
  public void prepareIndex() throws Exception {
    String s = indexAccessor.selectProperty(INDEX_BUILDER_KEY);
    if (INDEX_BUILDER_VALUE.equals(s)) {
      return;
  }
  Timer timer = new Timer("IndexBuilder");
  prepareFreqFragment();
  int maxSize = pi.getMaxFreqFragmentSize();
  freqCamSet = pi.getFreqCamSet();
  processEdge();
  for (int i = 1; i <= Math.min(MAGIC_EDGE_SIZE, maxSize); i++) {
    buildIndex(i);
  }
  logger.info(timer.time());
  indexAccessor.insertProperty(INDEX_BUILDER_KEY, INDEX_BUILDER_VALUE);
  cleanUp();
  }

  private void prepareFreqFragment() throws IOException, SQLException {
    int maxSize = pi.getMaxFreqFragmentSize();
    ImmutableListMultimap<Integer, LinkGraph<LGVertex>> piFreqGraphs = pi.getFreqGraphs();
    Timer timer = new Timer("prepareFreqFragment");
    for (int i = 1; i <= maxSize; i++) {
      ImmutableList<LinkGraph<LGVertex>> freqGraphs = piFreqGraphs.get(i);
      for (LinkGraph<LGVertex> fg : freqGraphs) {
        String cam = fg.getCam();
        Optional<PIndexDataItem> item = indexAccessor.selectCam(cam);
        if (!item.isPresent()) {
          indexAccessor.newItem(cam, PItemType.F, fg.getEdgeNum(), fg.getIdList());
        }
      }
    }
    logger.info(timer.time());
  }

  private void processEdge() throws SQLException {
    if (freqEdges != null) {
      return;
    }
    logger.info("processing edge");
    aSize = pi.aSize();
    graphSet = pi.getDataSet().getGraphSet();
    labelSize = DatabaseInfo.getLabels().length;
    freqEdges = new boolean[labelSize][labelSize];

    ImmutableMap<LabelEdge, int[]> allEdges = pi.getEdges();
    for (LabelEdge edge : allEdges.keySet()) {
      int[] edgeIdList = allEdges.get(edge);
      int i = edge.getFromLabel();
      int j = edge.getToLabel();
      if (edgeIdList.length >= aSize) {
        freqEdges[i][j] = freqEdges[j][i] = true;
      } else { // infrequent fragment
        String cam = cb.buildCam(i, j);
        Optional<PIndexDataItem> item = indexAccessor.selectCam(cam);
        if (!item.isPresent()) {
          List<Integer> list = IntegerListHelper.asUnmodifiableList(edgeIdList);
          indexAccessor.newItem(cam, PItemType.D, 1, list);
        } else {
          checkState(item.get().getType() == PItemType.D);
        }
      }
    }
  }

  private void setUpLevel() {
    infCamSet = new HashSet<>();
    fgMap = HashMultimap.create();
    difGraphMap = new HashMap<>();
    indexRepository.reload();
    or = new OperationRegistration(indexRepository);
  }

  // build the index
  private void buildIndex(int i) throws IOException, SQLException {
    logger.info("processing edge size: " + i);
    setUpLevel();

    ImmutableList<LinkGraph<LGVertex>> freqGraphs = pi.getFreqGraphs().get(i);
    logger.info("freq size: " + freqGraphs.size());
    Timer timer = new Timer("fragment generate");
    for (LinkGraph<LGVertex> fg : freqGraphs) {
      String cam = fg.getCam();
      PIndexDataItem dataItem = indexRepository.checkedSelectCam(cam);
      if (dataItem.getInfo() != null) {
        continue;
      }
      int id = dataItem.getId();
      or.registerFreq(cam, id);
      tryAddNewNode(fg);
      tryAddEdgeNonNode(fg);
    }
    logger.info("dif size: " + difGraphMap.size() + ", inf size: " + infCamSet.size());
    logger.info(timer.time());
    File fgFile = path.getPIndexFgMap(i);
    DifIndexAccessor.writeFgMap(fgFile, fgMap);

    verification();
  }

  private void tryAddNewNode(LinkGraph<LGVertex> fg) {
    for (int i = 0; i < fg.getNodeNum(); i++) { // i is the node position of this graph
      int oldLabel = fg.getNode(i).getLabel();

      for (int newLabel = 0; newLabel < labelSize; newLabel++) { // the new added label
        // 1.check new edge is frequent one
        if (!freqEdges[oldLabel][newLabel]) {
          continue;
        }
        // construct the new graph
        int oldNodeNum = fg.getNodeNum();
        LinkGraph<LGVertex> newGraph = new LinkGraph<>(oldNodeNum + 1);
        for (int nodeId = 0; nodeId < fg.getNodeNum(); nodeId++) {
          LGVertex oldNode = new LGVertex(fg.getNode(nodeId).getLabel());
          newGraph.addNode(oldNode);
        }
        // add new node
        LGVertex newNode = new LGVertex(newLabel);
        newGraph.addNode(newNode);
        // set the edge of the new graph
        for (int fgi = 0; fgi < fg.getNodeNum(); fgi++) {
          for (int fgj = fgi + 1; fgj < fg.getNodeNum(); fgj++) {
            if (fg.getEdge(fgi, fgj) == 1) {
              newGraph.addEdge(fgi, fgj);
            }
          }
        }
        // update the affected elements
        newGraph.addEdge(fg.getNodeNum(), i);
        String op = GOperation.newNode(i, newLabel);
        processNewGraph(fg, newGraph, op);
      }
    }
  }

  private void tryAddEdgeNonNode(LinkGraph<LGVertex> fg) {
    int n = fg.getNodeNum();
    for (int i = 0; i < n; i++) { // i is the node position of this graph
      int labelI = fg.getNode(i).getLabel();
      for (int j = i + 1; j < n; j++) {
        int labelJ = fg.getNode(j).getLabel();
        // 1.check new edge is frequent one
        if (fg.getEdge(i, j) != 0 || !freqEdges[labelI][labelJ]) {
          continue;
        }
        // construct the new graph
        LinkGraph<LGVertex> newGraph = new LinkGraph<>(n);
        for (int nodeId = 0; nodeId < n; nodeId++) {
          LGVertex node = new LGVertex(fg.getNode(nodeId).getLabel());
          newGraph.addNode(node);
        }
        // set the edge of the new graph
        for (int fgi = 0; fgi < n; fgi++) {
          for (int fgj = fgi + 1; fgj < n; fgj++) {
            if (fg.getEdge(fgi, fgj) == 1) {
              newGraph.addEdge(fgi, fgj);
            }
          }
        }
        // update the affected elements
        newGraph.addEdge(i, j);
        String op = GOperation.connectNodes(i, j);
        processNewGraph(fg, newGraph, op);
      }
    }
  }

  private boolean testIsDif(LinkGraph<LGVertex> newGraph) {
    if (difGraphMap.containsKey(newGraph.getCam())) {
      return true;
    }
    if (!testSubgraphs(newGraph)) {
      return false;
    }
    difGraphMap.put(newGraph.getCam(), newGraph);
    return true;
  }

  private boolean testNewGraph(LinkGraph<LGVertex> fg, LinkGraph<LGVertex> newGraph) {
    String newCam = newGraph.getCam();
    // 2.the whole graph can't be frequent, and can't exist in infrequent
    if (freqCamSet.contains(newCam)) {
      return true;
    } else if (!infCamSet.contains(newCam)) {
      if (testIsDif(newGraph)) {
        fgMap.put(newCam, fg);
        return true;
      } else {
        infCamSet.add(newCam);
      }
    }
    return false;
  }

  private void processNewGraph(LinkGraph<LGVertex> fg, LinkGraph<LGVertex> newGraph, String op) {
    // construct the new cam
    int[] newIds = cb.buildCamWithNewIds(newGraph);
    String newCam = newGraph.getCam();

    if (testNewGraph(fg, newGraph)) {
      or.register(fg.getCam(), op, newCam, newIds);
    }
  }

  private boolean testSubgraphs(LinkGraph<LGVertex> newGraph) {
    try {
      Optional<PIndexDataItem> item = indexAccessor.selectCam(newGraph.getCam());
      if (item.isPresent()) {
        checkState(item.get().getType() == PItemType.D);
        return true;
      }
      if (!sc.check(newGraph)) {
        return false;
      }

      indexAccessor.newDifItem(newGraph.getCam(), newGraph.getEdgeNum());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return true;
  }

  private List<Integer> getFIds(String cam) {
    List<Integer> fIds = fgMap.get(cam).stream().map(Graph::getIdList).collect(
        Collectors.collectingAndThen(Collectors.toList(), MergeIntersect::mergeIntersect));
    return fIds;
  }

  private void verification() throws SQLException {
    Timer timer = new Timer("verification");
    for (String cam : difGraphMap.keySet()) {
      PIndexDataItem item = indexRepository.checkedSelectCam(cam);
      int id = item.getId();
      LinkGraph<LGVertex> newGraph = difGraphMap.get(cam);
      List<Integer> fIds = getFIds(cam);
      VF2 verify = new VF2(newGraph);
      List<Integer> ids = fIds.stream().filter(fid -> verify.verify(graphSet.get(fid)))
          .collect(Collectors.toList());
      newGraph.setIdList(ids);
      int realSize = ids.size();
      checkState(realSize < aSize, "a error detected");
      indexAccessor.addDelIds(id, newGraph.getIdList());
    }
    logger.info(timer.time());
    or.buildAndWrite();
  }

}
