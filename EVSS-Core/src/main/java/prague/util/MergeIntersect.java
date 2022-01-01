package prague.util;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * MergeIntersect
 *
 * Created by ch.wang on 27 Mar 2014.
 */
public class MergeIntersect {

  public static List<Integer> mergeIntersect(List<List<Integer>> sortedLists) {
    checkNotNull(sortedLists);
    int n = sortedLists.size();
    if (n == 0) {
      return ImmutableList.of();
    }
    int[] index = new int[n];
    int[] size = new int[n];
    for (int i = 0; i < n; i++) {
      size[i] = sortedLists.get(i).size();
    }
    ImmutableList.Builder<Integer> result = ImmutableList.builder();
    int first = 0;
    OUTER:
    while (index[first] < size[first]) {
      int max = sortedLists.get(first).get(index[first]);
      for (int i = (first + 1) % n; i != first; i = (i + 1) % n) {
        List<Integer> listJ = sortedLists.get(i);
        while (index[i] < size[i] && listJ.get(index[i]) < max) {
          index[i]++;
        }
        if (index[i] == size[i]) {
          break OUTER;
        } else if (listJ.get(index[i]) > max) {
          max = listJ.get(index[i]);
          first = i;
        }
      }
      result.add(max);
      for (int i = 0; i < n; i++) {
        index[i]++;
      }
    }
    return result.build();
  }
}
