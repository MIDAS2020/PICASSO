package prague.algorithm;

import static com.google.common.base.Preconditions.*;

import prague.graph.Graph;
import prague.graph.Vertex;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;

/**
 * change graph node position in the graph
 *
 * Created by ch.wang on 24 Jan 2014.
 */
public class GraphRelabel {

  public static LinkGraph<LGVertex> relabel(Graph<? extends Vertex> graph, int[] newIds) {
    int n = graph.getNodeNum();
    checkArgument(n == newIds.length);
    int[] oldIds = invert(newIds);
    LinkGraph<LGVertex> rGraph = new LinkGraph<>(n);
    for (int i = 0; i < n; i++) {
      rGraph.addNode(new LGVertex(graph.getNode(oldIds[i]).getLabel()));
    }
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        if (graph.getEdge(oldIds[i], oldIds[j]) > 0) {
          rGraph.addEdge(i, j);
        }
      }
    }
    return rGraph;
  }

  public static int[] invert(int[] newIds){
    int n = newIds.length;
    int[] oldIds = new int[n];
    for (int i = 0; i < n; i++) {
      oldIds[newIds[i]] = i;
    }
    return oldIds;
  }
}
