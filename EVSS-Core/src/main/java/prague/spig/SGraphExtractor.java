package prague.spig;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import prague.algorithm.ELConnectionTester;
import prague.graph.lg.ELGraph;
import prague.graph.lg.LGVertex;

/**
 * SGraphExtractor
 *
 * Created by ch.wang on 01 May 2014.
 */
class SGraphExtractor {

  private final ELGraph<LGVertex> query;
  private final ELConnectionTester tester = new ELConnectionTester();

  public SGraphExtractor(ELGraph<LGVertex> query) {
    this.query = query;
  }

  private Map<ArraySet, SimpleSGraph> extract(SimpleSGraph sGraph, int largestEdge, int sigma,
                                              ArraySet banEdges) {
    Map<ArraySet, SimpleSGraph> result = Maps.newHashMap();
    result.put(banEdges, sGraph);
    if (sigma == 0) {
      return result;
    }
    for (int i = 1; i <= largestEdge; i++) {
      if (!query.containEdgeLabel(i)) {
        continue;
      }
      ArraySet newBanEdges = banEdges.add(i);
      if (tester.isConnected(query, newBanEdges)) {
        SimpleSGraph dGraph = new SimpleSGraph(sGraph);
        dGraph.deleteEdge(i);
        result.putAll(extract(dGraph, i - 1, sigma - 1, newBanEdges));
      }
    }
    return result;
  }

  public List<ExtractResult> extract(List<ExtractResult> preGraphs) {
    BitSet edgeLabelSet = query.getEdgeLabelSet();
    Set<ArraySet> visited = new HashSet<>();
    List<ExtractResult> result = new ArrayList<>();
    preGraphs.forEach(p -> {
      ArraySet preSet = p.getSet();
      for (int i = edgeLabelSet.nextSetBit(0); i >= 0; i = edgeLabelSet.nextSetBit(i + 1)) {
        if (!preSet.contains(i)) {
          ArraySet nowSet = preSet.add(i);
          if (visited.add(nowSet) && tester.isConnected(query, nowSet)) {
            SimpleSGraph nowGraph = new SimpleSGraph(p.getSGraph());
            nowGraph.deleteEdge(i);
            result.add(new ExtractResult(nowSet, i, nowGraph));
          }
        }
      }
    });
    return result;
  }

  public static class ExtractResult {

    private final ArraySet set;
    private final int i;
    private final SimpleSGraph sGraph;

    public ExtractResult(ArraySet set, int i, SimpleSGraph sGraph) {
      this.set = set;
      this.i = i;
      this.sGraph = sGraph;
    }

    public ArraySet getSet() {
      return set;
    }

    public int getI() {
      return i;
    }

    public SimpleSGraph getSGraph() {
      return sGraph;
    }
  }
}
