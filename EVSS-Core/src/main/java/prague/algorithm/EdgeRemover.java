package prague.algorithm;

import static com.google.common.base.Preconditions.*;

import java.util.Arrays;

import prague.graph.Graph;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;

/**
 * EdgeRemover
 *
 * Created by ch.wang on 12 Jan 2014.
 */
class EdgeRemover {

  private final Graph<?> graph;
  private final int[] nId;

  public EdgeRemover(Graph<?> graph) {
    this.graph = graph;
    nId = new int[graph.getNodeNum()];
  }

  private LinkGraph<LGVertex> getGraph(int gSize, int s, int t) {
    LinkGraph<LGVertex> subgraph = new LinkGraph<>(gSize);
    Arrays.fill(nId, -1);
    for (int oId = 0; oId < graph.getNodeNum(); oId++) {
      if ((oId == s || oId == t) && graph.getDegree(oId) == 1) {
        continue;
      }
      LGVertex node = new LGVertex(graph.getNode(oId).getLabel());
      subgraph.addNode(node);
      nId[oId] = node.getId();
      for (int k = 0; k < oId; k++) {
        if (!(k == s && oId == t) && graph.getEdge(k, oId) > 0) {
          subgraph.addEdge(nId[k], nId[oId]);
        }
      }
    }
    return subgraph;
  }

  public LinkGraph<LGVertex> removeEdge(int x, int y) {
    checkArgument(graph.getEdge(x, y) != 0, "edge does not exists: (%s, %s)", x, y);
    int gSize = graph.getNodeNum();
    if (graph.getDegree(x) == 1) {
      gSize--;
    }
    if (graph.getDegree(y) == 1) {
      gSize--;
    }
    LinkGraph<LGVertex> subgraph = getGraph(gSize, Math.min(x, y), Math.max(x, y));
    return subgraph;
  }

  public int[] getNewId() {
    return nId;
  }
}