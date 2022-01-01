package prague.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * test ArrayStringConverter
 *
 * Created by ch.wang on 21 Feb 2014.
 */
public class ArrayStringConverterTest {

  private final ArrayStringConverter asc = new ArrayStringConverter();

  @Test
  public void testConvert() throws Exception {
    assertEquals("102", asc.convert(new int[]{1, 0, 2}));
  }

  @Test
  public void testDoBackward() throws Exception {
    assertArrayEquals(new int[]{1, 0, 2}, asc.reverse().convert("102"));
  }
}
