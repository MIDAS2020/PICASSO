package prague.spig;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Maps;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import prague.graph.lg.ELGraph;
import prague.graph.lg.LGVertex;
import prague.index.data.PIndexDataItem;
import prague.index.model.GInfo;
import prague.index.model.GTransform;
import prague.index.repository.PIndexRepository;
import prague.query.SVertex;

/**
 * spindle graph creator
 *
 * Created by ch.wang on 13 Mar 2014.
 */
class SpindleGraphCreator {

  private final PIndexRepository pir;
  private SimpleSGraph spiGraph;
  private ELGraph<LGVertex> query;
  private int qn;
  private Set<ArraySet> edgeLabelSet;
  private Queue<SimpleSN> queue;
  private GInfo newSeedInfo;
  private Map<Integer, PIndexDataItem> indexMap;

  public SpindleGraphCreator(PIndexRepository pir) {
    this.pir = pir;
  }

  private int getSeedEdge(ELGraph<SVertex> seed) {
    return seed.getEdge(0, 1);
  }

  private void processSeed(ELGraph<SVertex> seed, int id) {
    int[] qIds = new int[2];
    for (int i = 0; i < 2; i++) {
      qIds[i] = seed.getNode(i).getQId();
    }
    SimpleSN ssnSeed = new SimpleSN(qn, qIds, BitArraySet.of(getSeedEdge(seed)), id);
    addToQueue(ssnSeed);
  }

  private void prepare(SimpleSGraph spiGraph) {
    this.spiGraph = spiGraph;
    edgeLabelSet = new HashSet<>();
    queue = new ArrayDeque<>();
    indexMap = Maps.newHashMap();
  }

  // Construct the SPIG
  public void constructSPIG(SimpleSGraph spiGraph, ELGraph<LGVertex> query, ELGraph<SVertex> seed) {
    this.query = checkNotNull(query);
    qn = query.getNodeNum();
    Optional<Integer> id = pir.getId(checkNotNull(seed.getCam()));
    if (!id.isPresent()) {
      spiGraph.addVoidEdge(getSeedEdge(seed));
      return;
    }
    prepare(spiGraph);
    processSeed(seed, id.get());

    while (!queue.isEmpty()) {
      SimpleSN pre = queue.poll();
      ArraySet preSet = pre.getEdgeLabelSet();
      if (preSet.getSize() == query.getEdgeNum()) {
        break;
      }
      PIndexDataItem item = indexMap.get(pre.getGraphId());
      newSeedInfo = item.getInfo();
      tryConnection(pre, preSet);

      ImmutableTable<Integer, Integer, GTransform> newNodeTable = newSeedInfo.getNewNodeTable();
      newNodeTable.rowMap().forEach((i, labelMap) -> tryNewNode(pre, preSet, i, labelMap));
    }
  }

  private void tryConnection(SimpleSN pre, ArraySet preSet) {
    ImmutableList<GInfo.Connection> connectionList = newSeedInfo.getConnectionList();
    for (GInfo.Connection c : connectionList) {
      int src = pre.getQId(c.getSrc());
      int trg = pre.getQId(c.getTrg());
      int edgeLabel = query.getEdge(src, trg);
      if (edgeLabel > 0 && !preSet.contains(edgeLabel)) {
        ArraySet nowSet = preSet.add(edgeLabel);
        if (edgeLabelSet.add(nowSet)) { // check node by its edge label set
          SimpleSN now = pre.connectNodes(nowSet, c.getTrans());
          addToQueue(now);
        }
      }
    }
  }

  private void tryNewNode(SimpleSN pre, ArraySet preSet, int i, Map<Integer, GTransform> labelMap) {
    int qI = pre.getQId(i);
    for (int j = 0; j < qn; j++) {
      if (pre.getId(j) != -1) {
        continue;
      }
      int edgeLabel = query.getEdge(qI, j);
      if (edgeLabel > 0) {
        GTransform transform = labelMap.get(query.getNode(j).getLabel());
        if (transform != null) {
          ArraySet nowSet = preSet.add(edgeLabel);
          if (edgeLabelSet.add(nowSet)) { // check node by its edge label set
            SimpleSN now = pre.newNode(j, nowSet, transform);
            addToQueue(now);
          }
        }
      }
    }
  }

  private void addToQueue(SimpleSN ssn) {
    PIndexDataItem item = addToSpigGraph(ssn);
    GInfo info = item.getInfo();
    if (info != null && info.size() > 0) {
      queue.offer(ssn);
    }
  }

  private PIndexDataItem addToSpigGraph(SimpleSN ssn) {
    int id = ssn.getGraphId();
    PIndexDataItem dataItem = indexMap.get(id);
    if (dataItem == null) { // check if ssn is an new node in graph
      dataItem = pir.getPIndexDataItem(id);
      indexMap.put(id, dataItem);
      spiGraph.add(id, dataItem.getParent());
    }
    spiGraph.addEdgeSet(id, ssn.getEdgeLabelSet());
    return dataItem;
  }
}
