package prague.spig;

import static org.junit.Assert.*;

import org.junit.Test;

public class ImmutableIntSetTest {

  @Test
  public void testAdd() {
    ArraySet arraySet = new ImmutableIntSet(new int[]{1, 2, 4});
    ArraySet added = arraySet.add(3);
    assertEquals(new ImmutableIntSet(new int[]{1, 2, 3, 4}), added);
  }

  @Test(expected = RuntimeException.class)
  public void testAddException() throws Exception {
    ArraySet arraySet = new ImmutableIntSet(new int[]{1, 2, 3});
    arraySet.add(3);
  }

  @Test
  public void testRemove() {
    ArraySet arraySet = new ImmutableIntSet(new int[]{1, 2, 4});
    ArraySet result = arraySet.remove(4);
    assertEquals(new ImmutableIntSet(new int[]{1, 2}), result);
  }

  @Test(expected = RuntimeException.class)
  public void testRemoveException() throws Exception {
    ArraySet arraySet = new ImmutableIntSet(new int[]{1, 2, 3});
    arraySet.remove(4);
  }
}
