package prague.pattern;

import static org.junit.Assert.*;

import org.dom4j.DocumentException;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

public class SvgHelperTest {

  private static final Double EPS = 1e-6;

  @Test
  public void test() throws DocumentException, URISyntaxException, SAXException {
    File svgFile = new File(getClass().getResource("/test.svg").toURI());
    List<Location> locations = SvgHelper.getLocations(svgFile);
    assertEquals(2, locations.size());
    Location l0 = locations.get(0);
    assertEquals(0, l0.index);
    assertEquals(74, l0.x, EPS);
    assertEquals(-140.3, l0.y, EPS);
    Location l1 = locations.get(1);
    assertEquals(1, l1.index);
    assertEquals(74, l1.x, EPS);
    assertEquals(-68.3, l1.y, EPS);
  }
}
