package prague.pattern;

import javax.annotation.Nonnull;

/**
 * 
 * @author Andy
 */
class Location implements Comparable<Location> {

  public int index;
  public double x;
  public double y;

  public Location() {
    index = -1;
    x = 0;
    y = 0;
  }

  @Override
  public int compareTo(@Nonnull Location o) {
    return index - o.index;
  }
}
