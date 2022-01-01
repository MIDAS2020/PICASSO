package prague.util;

import static org.junit.Assert.*;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

import java.util.List;

/**
 * MergeIntersectTest
 *
 * Created by ch.wang on 27 Mar 2014.
 */
public class MergeIntersectTest {

  @Test
  public void testMergeIntersect() throws Exception {
    List<Integer> a = ImmutableList.of(1, 2, 3);
    List<Integer> b = ImmutableList.of(3, 4, 5);
    List<Integer> c = ImmutableList.of(3);
    List<Integer> result = MergeIntersect.mergeIntersect(ImmutableList.of(a, b, c));
    assertEquals(ImmutableList.of(3), result);
  }

  @Test
  public void testOne() throws Exception {
    List<Integer> a = ImmutableList.of(1, 2, 3);
    List<Integer> result = MergeIntersect.mergeIntersect(ImmutableList.of(a));
    assertEquals(ImmutableList.of(1, 2, 3), result);
  }

  @Test()
  public void testZero() throws Exception {
    List<Integer> result = MergeIntersect.mergeIntersect(ImmutableList.of());
    assertEquals(ImmutableList.<Integer>of(), result);
  }
}
