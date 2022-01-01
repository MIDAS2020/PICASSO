package prague.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * test IntCharConverter
 *
 * Created by ch.wang on 21 Feb 2014.
 */
public class IntCharConverterTest {

  private final IntCharConverter icc = new IntCharConverter();

  @Test
  public void testDoForward() throws Exception {
    assertEquals(Character.valueOf('1'), icc.convert(1));
  }

  @Test
  public void testDoBackward() throws Exception {
    assertEquals(Integer.valueOf(1), icc.reverse().convert('1'));
  }
}
