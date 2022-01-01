package prague.algorithm;

import java.util.BitSet;
import java.util.Set;

import javax.annotation.Nonnull;

import prague.graph.Vertex;
import prague.graph.lg.AdjMatrix;
import prague.graph.lg.ELGraph;
import prague.spig.ArraySet;

/**
 * ConnectionTester
 *
 * Created by ch.wang on 12 Jan 2014.
 */
public class ELConnectionTester {

  private int n;
  private boolean[] visited;
  private AdjMatrix matrix;
  private BitSet remainder;
  private boolean changed;

  private void dfs(int x) {
    if (visited[x]) {
      return;
    }
    visited[x] = true;
    for (int i = 0; i < n; i++) {
      int edge = matrix.getEdge(x, i);
      if (edge > 0 && remainder.get(edge)) {
        remainder.clear(edge);
        changed = true;
        dfs(i);
      }
    }
  }

  private BitSet getBitSet(@Nonnull Set<Integer> set) {
    BitSet bitSet = new BitSet();
    set.forEach(bitSet::set);
    return bitSet;
  }

  public boolean isConnected(ELGraph<? extends Vertex> graph, @Nonnull ArraySet banEdges) {
    BitSet banSet = new BitSet();
    banEdges.stream().forEach(banSet::set);
    return isConnected(graph, banSet);
  }

  public boolean isConnected(ELGraph<? extends Vertex> graph, @Nonnull Set<Integer> banEdges) {
    return isConnected(graph, getBitSet(banEdges));
  }

  private boolean isConnected(ELGraph<? extends Vertex> graph, @Nonnull BitSet banEdges) {
    n = graph.getNodeNum();
    remainder = graph.getEdgeLabelSet();
    remainder.andNot(banEdges);
    visited = new boolean[n];
    matrix = graph;
    changed = false;
    for (int i = 0; i < n && !changed; i++) {
      dfs(i);
    }
    return remainder.isEmpty();
  }
}
