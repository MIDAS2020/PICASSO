package prague.index;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import prague.data.DataAccessor;
import prague.data.DataParams;
import prague.data.DataPath;
import prague.data.DataSet;
import prague.data.FrequentFileProcessor;
import prague.data.tedFileProcessor;
import prague.data.QueryProperties;
import prague.graph.Graph;
import prague.graph.ImmutableGraph;
import prague.graph.LabelEdge;
import prague.graph.Vertex;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import prague.index.repository.PIndexRepository;
import wch.guava2.base.Timer;
import wch.guava2.collect.Immutables;

/**
 * common data accessor
 *
 * Created by ch.wang on 09 Jan 2014.
 */
public class PragueIndex {

  private final Logger logger = LogManager.getLogger(this);

  private final DataParams parameters;
  private final DataPath path;
  private final DataAccessor da = new DataAccessor();
  private final FrequentFileProcessor ffp;
  private final QueryProperties qp;

  private final tedFileProcessor ted;
  
  private DataSet ds;
  private ImmutableMap<LabelEdge, int[]> allEdges;
  private PIndexRepository pir;

  public PragueIndex(DataParams parameters, QueryProperties qp) {
    this.parameters = parameters;
    path = new DataPath(parameters);
    ffp = new FrequentFileProcessor(path);
    this.qp = qp;
    
    ted = new tedFileProcessor(path);
  }

  public DataParams getParameters() {
    return parameters;
  }

  public DataPath getPath() {
    return path;
  }

  public ImmutableListMultimap<Integer, LinkGraph<LGVertex>> getFreqGraphs() throws IOException {
    return ffp.getFreqGraphs();
  }

  public ImmutableList<LinkGraph<LGVertex>> getFreqGraphsAsList() throws IOException {
    return ffp.getFreqGraphsAsList();
  }

  public int getMaxFreqFragmentSize() throws IOException {
    return ffp.getMaxFreqFragmentSize();
  }

  public ImmutableSet<String> getFreqCamSet() throws IOException {
    return ffp.getCamSet();
  }
  
  public ImmutableListMultimap<Integer, LinkGraph<LGVertex>> getTEDGraphs() throws IOException {
    return ted.getTEDGraphs();
  }

  public ImmutableList<LinkGraph<LGVertex>> getTEDGraphsAsList() throws IOException {
    return ted.getTEDGraphsAsList();
  }

  public int getMaxTEDFragmentSize() throws IOException {
    return ted.getMaxTEDFragmentSize();
  }

  public ImmutableSet<String> getTEDCamSet() throws IOException {
    return ted.getTEDCamSet();
  }
  

  public DataSet getDataSet() {
    if (ds == null) {
      //read the graphs from disk to memory
      File fin = path.getDataSet();
      try {
        ds = da.readDataSet(fin); // store graph by ALGraph
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return ds;
  }

  private Set<LabelEdge> getEdgeMap(ImmutableGraph graph) {
    ImmutableSet.Builder<LabelEdge> builder = ImmutableSet.builder();
    int n = graph.getNodeNum();
    for (int i = 0; i < n; i++) {
      int iLabel = graph.getNode(i).getLabel();
      for (int j = i + 1; j < n; j++) {
        if (graph.getEdge(i, j) > 0) {
          LabelEdge se = new LabelEdge(iLabel, graph.getNode(j).getLabel());
          builder.add(se);
        }
      }
    }
    return builder.build();
  }

  private ListMultimap<LabelEdge, Integer> getEdgeMap(ImmutableList<ImmutableGraph> graphSet) {
    ListMultimap<LabelEdge, Integer> map = ArrayListMultimap.create();
    for (ImmutableGraph graph : graphSet) {
      int graphId = graph.getGraphId();
      Set<LabelEdge> edgeMap = getEdgeMap(graph);
      edgeMap.forEach(e -> map.put(e, graphId));
    }
    return map;
  }

  public ImmutableMap<LabelEdge, int[]> getEdges() {
    if (allEdges != null) {
      return allEdges;
    }
    Timer timer = new Timer("getEdges");
    ImmutableList<ImmutableGraph> graphSet = getDataSet().getGraphSet();
    ListMultimap<LabelEdge, Integer> map = getEdgeMap(graphSet);
    allEdges = Immutables.buildImmutableMap(builder -> {
      for (LabelEdge edge : map.keySet()) {
        builder.put(edge, Ints.toArray(map.get(edge)));
      }
    });
    logger.info(timer.time());
    return allEdges;
  }

  public int aSize() {
    DataSet ds = getDataSet();
    int aSize = (int) (parameters.getAValue() * ds.getGraphSet().size());
    return aSize;
  }

  // fetch graphs from the memory
  public List<Graph<Vertex>> fetchFromMem(Collection<Integer> list) {
    List<Graph<Vertex>> candidateSet = Lists.newArrayListWithCapacity(list.size());
    for (int graphId : list) {
      Graph<Vertex> graph = fetchFromMem(graphId);
      candidateSet.add(graph);
    }
    return candidateSet;
  }

  // fetch single graph from memory
  public ImmutableGraph fetchFromMem(int id) {
    ImmutableGraph graph = getDataSet().getGraphSet().get(id);
    return graph;
  }

  public int getSigma(@Nonnull LinkGraph<? extends Vertex> query) {
    checkNotNull(query);
    return Math.min(query.getEdgeNum() - 1, parameters.getSigma());
  }

  public PIndexRepository getPIndexRepository() {
    if (pir == null) {
      File pIndex = path.getPIndex();
      pir = new PIndexRepository(pIndex);
    }
    return pir;
  }

  public QueryProperties getQP() {
    return qp;
  }

}
