package prague.algorithm;

import static com.google.common.base.Preconditions.*;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import prague.graph.Graph;
import prague.graph.lg.ELGraph;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import prague.spig.ArraySet;
import prague.spig.BitArraySet;

/**
 * EdgeStore
 *
 * Created by ch.wang on 14 May 2014.
 */
public class EdgeStore {

  private final Map<Integer, Integer> srcMap = new HashMap<>();
  private final Map<Integer, Integer> trgMap = new HashMap<>();
  private final Map<ArraySet, GraphAndTrans> graphMap = new HashMap<>();

  public EdgeStore(ELGraph<LGVertex> query) {
    int n = query.getNodeNum();
    int[] identity = new int[n];
    for (int i = 0; i < n; i++) {
      identity[i] = i;
      for (int j = i + 1; j < n; j++) {
        int e = query.getEdge(i, j);
        if (e > 0) {
          srcMap.put(e, i);
          trgMap.put(e, j);
        }
      }
    }
    graphMap.put(BitArraySet.of(), new GraphAndTrans(query, identity));
  }

  public static int[] helpTrans(int[] preTrans, int[] trans) {
    int n = preTrans.length;
    int[] nowTrans = new int[n];
    for (int i = 0; i < n; i++) {
      nowTrans[i] = preTrans[i] != -1 ? trans[preTrans[i]] : -1;
    }
    return nowTrans;
  }

  public GraphAndTrans getGraph(ArraySet banSet, int i) {
    GraphAndTrans gt = graphMap.get(banSet);
    if (gt != null) {
      return gt;
    }
    ArraySet preSet = banSet.remove(i);
    gt = graphMap.get(preSet);
    checkState(gt != null);
    Graph<?> preGraph = gt.getGraph();
    int[] preTrans = gt.getTrans();
    Integer src = srcMap.get(i);
    Integer trg = trgMap.get(i);
    checkState(src != null && trg != null);
    int x = preTrans[src];
    int y = preTrans[trg];
    checkState(x >= 0 && y >= 0, "unexpected x: %s, y: %s", x, y);
    EdgeRemover edgeRemover = new EdgeRemover(preGraph);
    LinkGraph<LGVertex> nowGraph = edgeRemover.removeEdge(x, y);
    int[] trans = edgeRemover.getNewId();
    int[] nowTrans = helpTrans(preTrans, trans);
    GraphAndTrans value = new GraphAndTrans(nowGraph, nowTrans);
    graphMap.put(banSet, value);
    return value;
  }

  public static class GraphAndTrans {

    private final Graph<?> graph;
    private final int[] trans;

    public GraphAndTrans(@Nonnull Graph<?> graph, @Nonnull int[] trans) {
      this.graph = checkNotNull(graph);
      this.trans = checkNotNull(trans);
    }

    @Nonnull
    public Graph<?> getGraph() {
      return graph;
    }

    @Nonnull
    public int[] getTrans() {
      return trans;
    }
  }
}
