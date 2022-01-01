package prague.algorithm;

import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;

/**
 * LargestSubgraphGenerator
 *
 * Created by ch.wang on 12 Jan 2014.
 */
public class LargestSubgraphGenerator {

  private final LinkGraph<?> graph;
  private final EdgeRemover edgeRemover;
  private final int n;
  private final ConnectionTester ct = new ConnectionTester();
  private int x = 0;
  private int y = 0;

  public LargestSubgraphGenerator(LinkGraph<?> graph) {
    this.graph = graph;
    n = graph.getNodeNum();
    edgeRemover = new EdgeRemover(graph);
  }

  private boolean next() {
    if (y + 1 < n) {
      y++;
      return true;
    }
    if (x + 2 < n) {
      x++;
      y = x + 1;
      return true;
    }
    return false;
  }

  public LinkGraph<LGVertex> nextGraph() {
    while (next()) {
      if (graph.getEdge(x, y) != 0) {
        LinkGraph<LGVertex> subgraph = edgeRemover.removeEdge(x, y);
        if (ct.isConnected(subgraph)) {
          return subgraph;
        }
      }
    }
    return null;
  }
}
