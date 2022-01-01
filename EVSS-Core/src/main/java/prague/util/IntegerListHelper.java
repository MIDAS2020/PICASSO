package prague.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;

import java.util.Collections;
import java.util.List;

/**
 * convert integer list
 *
 * Created by ch.wang on 17 Feb 2014.
 */
public class IntegerListHelper {

  private final Joiner joiner;
  private final Splitter splitter;

  public IntegerListHelper(char separator) {
    joiner = Joiner.on(separator);
    splitter = Splitter.on(separator).omitEmptyStrings();
  }

  public IntegerListHelper(String separator) {
    joiner = Joiner.on(separator);
    splitter = Splitter.on(separator).omitEmptyStrings();
  }

  public static List<Integer> asUnmodifiableList(int[] array) {
    return Collections.unmodifiableList(Ints.asList(array));
  }

  private ImmutableList<Integer> getIntegers(Iterable<String> items) {
    ImmutableList.Builder<Integer> intList = ImmutableList.builder();
    for (String item : items) {
      intList.add(Integer.parseInt(item));
    }
    return intList.build();
  }

  public List<Integer> splitInteger(String intString) {
    Iterable<String> items = splitter.split(intString);
    return getIntegers(items);
  }

  public List<Integer> splitInteger(String intString, int start) {
    Iterable<String> items = splitter.split(intString);
    Iterable<String> skipped = Iterables.skip(items, start);
    return getIntegers(skipped);
  }

  public int[] splitIntegerToArray(String intString) {
    List<Integer> items = splitInteger(intString);
    int[] intArray = Ints.toArray(items);
    return intArray;
  }

  public String join(List<?> list) {
    return joiner.join(list);
  }
}
