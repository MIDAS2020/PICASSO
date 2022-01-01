package prague.algorithm;

import prague.graph.Vertex;
import prague.graph.lg.AdjMatrix;
import prague.graph.lg.LinkGraph;

/**
 * ConnectionTester
 *
 * Created by ch.wang on 12 Jan 2014.
 */
class ConnectionTester {

  private int n;
  private boolean[] visited;
  private AdjMatrix matrix;
  private int remainder;

  private void dfs(int x) {
    visited[x] = true;
    remainder--;
    for (int i = 0; i < n; i++) {
      if (matrix.getEdge(x, i) != 0 && !visited[i]) {
        dfs(i);
      }
    }
  }

  public boolean isConnected(LinkGraph<? extends Vertex> graph) {
    n = graph.getNodeNum();
    remainder = n;
    visited = new boolean[n];
    matrix = graph;
    dfs(0);
    return remainder == 0;
  }
}
