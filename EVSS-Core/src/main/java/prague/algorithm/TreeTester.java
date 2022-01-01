package prague.algorithm;

import prague.graph.Graph;
import prague.graph.Vertex;

/**
 * TreeTester
 *
 * Created by ch.wang on 18 Jan 2014.
 */
public class TreeTester {

  private int n;
  private boolean[] visited;
  private Graph<? extends Vertex> matrix;
  private int remainder;

  private boolean dfs(int x, int p) {
    visited[x] = true;
    remainder--;
    for (int i = 0; i < n; i++) {
      if (i != p && matrix.getEdge(x, i) != 0) {
        if (visited[i] || !dfs(i, x)) {
          return false;
        }
      }
    }
    return true;
  }

  public boolean isTree(Graph<? extends Vertex> graph) {
    n = graph.getNodeNum();
    remainder = n;
    visited = new boolean[n];
    matrix = graph;
    return dfs(0, -1) && remainder == 0;
  }
}
