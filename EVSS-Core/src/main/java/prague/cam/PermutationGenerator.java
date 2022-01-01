package prague.cam;

import java.util.NoSuchElementException;

public class PermutationGenerator {

  private final int n;
  private final int[] p;
  private boolean first = true;
  private boolean has = false;

  public PermutationGenerator(int n) {
    this.n = n;
    p = new int[n];
    for (int i = 0; i < n; i++) {
      p[i] = i;
    }
  }

  private void swap(int i, int j) {
    int t = p[i];
    p[i] = p[j];
    p[j] = t;
  }

  public boolean hasNext() {
    if (first) {
      first = false;
      return has = true;
    }
    int i = n - 2;
    while (i >= 0 && p[i] >= p[i + 1]) {
      i--;
    }
    if (i < 0) {
      return has = false;
    }
    int j = n - 1;
    while (p[j] <= p[i]) {
      j--;
    }
    swap(i, j);
    int m = n;
    while (++i < --m) {
      swap(i, m);
    }
    return has = true;
  }

  public int[] get() {
    if (!has) {
      throw new NoSuchElementException("empty");
    }
    return p;
  }
}
