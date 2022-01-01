package prague.spig;

import static com.google.common.base.Preconditions.*;

import java.util.BitSet;
import java.util.stream.IntStream;

/**
 * BitArraySet
 *
 * Created by ch.wang on 10 May 2014.
 */
public class BitArraySet implements ArraySet {

  private BitSet bitSet = new BitSet();
  private int size = 0;

  /**
   * Cache the hash code for the ArraySet
   */
  private int hash; // Default to 0

  private BitArraySet(BitSet bitSet, int size) {
    this.bitSet = bitSet;
    this.size = size;
  }

  public static ArraySet of() {
    BitSet bitSet = new BitSet();
    return new BitArraySet(bitSet, 0);
  }

  public static ArraySet of(int node) {
    BitSet bitSet = new BitSet();
    bitSet.set(node);
    return new BitArraySet(bitSet, 1);
  }

  @Override
  public int getSize() {
    return size;
  }

  @Override
  public boolean contains(int node) {
    return bitSet.get(node);
  }

  @Override
  public ArraySet add(int node) {
    checkArgument(!bitSet.get(node), "already in: %s", node);
    BitSet newSet = (BitSet) bitSet.clone();
    newSet.set(node);
    return new BitArraySet(newSet, size + 1);
  }

  @Override
  public ArraySet remove(int node) {
    checkArgument(bitSet.get(node), "not in: %s", node);
    BitSet newSet = (BitSet) bitSet.clone();
    newSet.clear(node);
    return new BitArraySet(newSet, size - 1);
  }

  @Override
  public int max() {
    return bitSet.length() - 1;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BitArraySet that = (BitArraySet) o;
    return size == that.size && bitSet.equals(that.bitSet);
  }

  @Override
  public int hashCode() {
    if (hash == 0) {
      hash = bitSet.hashCode();
    }
    return hash;
  }

  @Override
  public String toString() {
    return bitSet.toString();
  }

  @Override
  public IntStream stream() {
    return bitSet.stream();
  }
}
