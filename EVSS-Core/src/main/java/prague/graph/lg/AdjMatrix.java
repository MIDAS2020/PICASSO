package prague.graph.lg;

import com.google.common.collect.ImmutableList;

/**
 * AdjMatrix represent edges, which is symmetrical
 */
public class AdjMatrix extends BaseAdjMatrix {

  private final int[] degrees;
  private final ImmutableList.Builder<Edge> edgeBuilder = ImmutableList.builder();
  private int edgeNum = 0;

  protected AdjMatrix(int size) {
    super(size);
    degrees = new int[size];
  }

  /**
   * add formulation sequence as the label of the edge; add two edges
   */
  @Override
  public void addEdge(int from, int to, int label) {
    super.addEdge(from, to, label);
    super.addEdge(to, from, label);
    edgeBuilder.add(new Edge(from, to, label));
    degrees[from]++;
    degrees[to]++;
    edgeNum++;
  }

  public int getEdgeNum() {
    return edgeNum;
  }

  public int getDegree(int id) {
    return degrees[id];
  }

  public ImmutableList<Edge> getEdges() {
    return edgeBuilder.build();
  }

}
