package prague.graph.lg;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;

public class BaseAdjMatrix {

  protected final int size;
  private final int[][] adjMat;
  private SetMultimap<Integer, Integer> ins = HashMultimap.create();

  protected BaseAdjMatrix(int size) {
    this.size = size;
    adjMat = new int[size][size];
  }

  public int getNodeNum() {
    return size;
  }

  /**
   * add formulation sequence as the label of the edge
   *
   * @param from  node
   * @param to    node
   * @param label label
   */
  void addEdge(int from, int to, int label) {
    checkArgument(from != to && adjMat[from][to] == 0);
    adjMat[from][to] = label;
    ins.put(from, to);
  }

  public void addEdge(int from, int to) {
    addEdge(from, to, 1);
  }

  public int getEdge(int from, int to) {
    return adjMat[from][to];
  }

  public ImmutableSet<Integer> getIn(int id) {
    checkElementIndex(id, size);
    return ImmutableSet.copyOf(ins.get(id));
  }
}
