package prague.util;

import static com.google.common.base.Preconditions.*;

import com.google.common.primitives.Ints;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * IntSet
 *
 * Created by ch.wang on 09 Apr 2014.
 */
public class IntSet {

  private final List<Integer> list;

  public IntSet(@Nonnull Collection<Integer> collection) {
    checkNotNull(collection);
    int n = collection.size();
    int[] elements = new int[n];
    int i = 0;
    for (int item : collection) {
      elements[i++] = item;
    }
    Arrays.sort(elements);
    list = Collections.unmodifiableList(Ints.asList(elements));
  }

  public boolean contains(int key) {
    return Collections.binarySearch(list, key) >= 0;
  }

  public List<Integer> getList() {
    return list;
  }
}
