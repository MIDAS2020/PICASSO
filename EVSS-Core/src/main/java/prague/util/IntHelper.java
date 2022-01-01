package prague.util;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * IntHelper
 *
 * Created by ch.wang on 01 May 2014.
 */
public class IntHelper {

  @Nonnull
  public static List<Integer> mergeIntersect(@Nonnull List<int[]> sortedArrays) {
    checkNotNull(sortedArrays);
    int n = sortedArrays.size();
    if (n == 0) {
      return ImmutableList.of();
    }
    int[] index = new int[n];
    int[] size = new int[n];
    for (int i = 0; i < n; i++) {
      size[i] = sortedArrays.get(i).length;
    }
    ImmutableList.Builder<Integer> result = ImmutableList.builder();
    int first = 0;
    outer:
    while (index[first] < size[first]) {
      int max = sortedArrays.get(first)[index[first]];
      for (int i = (first + 1) % n; i != first; i = (i + 1) % n) {
        int[] listJ = sortedArrays.get(i);
        while (index[i] < size[i] && listJ[index[i]] < max) {
          index[i]++;
        }
        if (index[i] == size[i]) {
          break outer;
        } else if (listJ[index[i]] > max) {
          max = listJ[index[i]];
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
