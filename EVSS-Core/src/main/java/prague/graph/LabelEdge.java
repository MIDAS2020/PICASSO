package prague.graph;

/**
 * LabelEdge
 *
 * Created by ch.wang on 26 Mar 2014.
 */
public class LabelEdge {

  private final int fromLabel;
  private final int toLabel;

  public LabelEdge(int fromLabel, int toLabel) {
    this.fromLabel = Math.min(fromLabel, toLabel);
    this.toLabel = Math.max(fromLabel, toLabel);
  }

  public int getFromLabel() {
    return fromLabel;
  }

  public int getToLabel() {
    return toLabel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    LabelEdge se = (LabelEdge) o;
    return fromLabel == se.fromLabel && toLabel == se.toLabel;
  }

  @Override
  public int hashCode() {
    int result = 31 * fromLabel + toLabel;
    return result;
  }
}
