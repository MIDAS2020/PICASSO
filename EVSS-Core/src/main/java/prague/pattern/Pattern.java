package prague.pattern;

import prague.graph.Graph;
import prague.graph.Vertex;

/**
 * pattern class
 *
 * Created by ch.wang on 03 Mar 2014.
 */
public class Pattern {

  public final int[] x;
  public final int[] y;
  private final Graph<? extends Vertex> graph;

  public Pattern(Graph<? extends Vertex> graph) {
    this.graph = graph;
    x = new int[graph.getNodeNum()];
    y = new int[graph.getNodeNum()];
  }

  public Graph<? extends Vertex> getGraph() {
    return graph;
  }
}
