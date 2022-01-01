package prague.cam;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.NoSuchElementException;

public class PermutationGeneratorTest {

  @Test
  public void test() {
    PermutationGenerator pg = new PermutationGenerator(3);
    assertEquals(true, pg.hasNext());
    assertArrayEquals(new int[]{0, 1, 2}, pg.get());
    assertEquals(true, pg.hasNext());
    assertArrayEquals(new int[]{0, 2, 1}, pg.get());
    assertEquals(true, pg.hasNext());
    assertArrayEquals(new int[]{1, 0, 2}, pg.get());
    assertEquals(true, pg.hasNext());
    assertArrayEquals(new int[]{1, 2, 0}, pg.get());
    assertEquals(true, pg.hasNext());
    assertArrayEquals(new int[]{2, 0, 1}, pg.get());
    assertEquals(true, pg.hasNext());
    assertArrayEquals(new int[]{2, 1, 0}, pg.get());
    assertEquals(false, pg.hasNext());
  }

  @Test(expected = NoSuchElementException.class)
  public void testException() {
    PermutationGenerator pg = new PermutationGenerator(3);
    pg.get();
  }
} 
