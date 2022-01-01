package prague.graph.lg;

/**
 * Edge
 *
 * Created by ch.wang on 25 May 2014.
 */
public class Edge {

  private final int src;
  private final int trg;
  private final int label;

  public Edge(int src, int trg) {
    this(src, trg, 1);
  }

  public Edge(int src, int trg, int label) {
    this.src = src;
    this.trg = trg;
    this.label = label;
  }

  public int getSrc() {
    return src;
  }

  public int getTrg() {
    return trg;
  }

  public int getLabel() {
    return label;
  }

  @Override
  public String toString() {
    return String.format("(%d,%d %d)", src, trg, label);
  }
}
