package prague.spig;

import static com.google.common.base.Preconditions.*;

import java.util.Arrays;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

/**
 * ArraySet
 *
 * Created by ch.wang on 24 Apr 2014.
 */
public class ImmutableIntSet implements ArraySet {

  private static final int[] EMPTY = new int[0];
  private final int[] array;

  /**
   * Cache the hash code for the ArraySet
   */
  private int hash; // Default to 0

  public ImmutableIntSet(@Nonnull int[] array) {
    this.array = checkNotNull(array);
  }

  public static ArraySet of() {
    return new ImmutableIntSet(EMPTY);
  }

  public static ArraySet of(int node) {
    return new ImmutableIntSet(new int[]{node});
  }

  @Override
  public int getSize() {
    return array.length;
  }

  @Override
  public boolean contains(int node) {
    int i = Arrays.binarySearch(array, node);
    return i >= 0;
  }

  @Override
  public ArraySet add(int node) {
    int i = Arrays.binarySearch(array, node);
    checkArgument(i < 0, "already in: %s", node);
    int ip = -1 - i;
    int[] result = new int[array.length + 1];
    System.arraycopy(array, 0, result, 0, ip);
    result[ip] = node;
    System.arraycopy(array, ip, result, ip + 1, array.length - ip);
    return new ImmutableIntSet(result);
  }

  @Override
  public ArraySet remove(int node) {
    int i = Arrays.binarySearch(array, node);
    checkArgument(i >= 0, "not in: %s", node);
    int[] result = new int[array.length - 1];
    System.arraycopy(array, 0, result, 0, i);
    System.arraycopy(array, i + 1, result, i, array.length - i - 1);
    return new ImmutableIntSet(result);
  }

  @Override
  public int max() {
    if (array.length == 0) {
      return -1;
    }
    return array[array.length - 1];
  }

  @Override
  public IntStream stream() {
    return Arrays.stream(array);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ImmutableIntSet arraySet = (ImmutableIntSet) o;
    return Arrays.equals(array, arraySet.array);
  }

  @Override
  public int hashCode() {
    if (hash == 0) {
      hash = Arrays.hashCode(array);
    }
    return hash;
  }

  @Override
  public String toString() {
    return Arrays.toString(array);
  }
}
