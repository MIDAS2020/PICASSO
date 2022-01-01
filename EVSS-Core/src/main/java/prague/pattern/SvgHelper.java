package prague.pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class SvgHelper {

  public static List<Location> getLocations(File svgFile) throws DocumentException, SAXException {
    SAXReader reader = new SAXReader();
    reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    Document doc = reader.read(svgFile);
    @SuppressWarnings("unchecked")
    List<Node> list = doc.selectNodes("/svg/*[name()='g']/*[name()='g'][starts-with(@id,'node')]");
    List<Location> locations = new ArrayList<>();
    for (Node node : list) {
      int index = Integer.parseInt(node.valueOf("*[name()='title']"));
      Node text = node.selectSingleNode("*[name()='text']");
      double x = Double.parseDouble(text.valueOf("@x"));
      double y = Double.parseDouble(text.valueOf("@y"));
      Location loc = new Location();
      loc.index = index;
      loc.x = x;
      loc.y = y;
      locations.add(loc);
    }
    return locations;
  }
}
