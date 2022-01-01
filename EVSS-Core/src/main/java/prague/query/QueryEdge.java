package prague.query;

import com.google.common.base.Objects;

/**
 * @author ibmuser
 */
public class QueryEdge {

  private final int src;
  private final int trg;

  public QueryEdge(int n1, int n2) {
    if (n1 < n2) {
      src = n1;
      trg = n2;
    } else {
      src = n2;
      trg = n1;
    }
  }

  public boolean isEnd(int node) {
    return node == src || node == trg;
  }

  public int getSrc() {
    return src;
  }

  public int getTrg() {
    return trg;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(src, trg);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof QueryEdge)) {
      return false;
    }
    QueryEdge b = (QueryEdge) o;
    return src == b.src && trg == b.trg;
  }

  @Override
  public String toString() {
    return String.format("(%d, %d)", src, trg);
  }
}
