package prague.cam;

import static org.junit.Assert.*;

import org.junit.Test;

import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;

/**
 * test tree cam reader
 *
 * Created by ch.wang on 22 Jan 2014.
 */
public class TreeCamReaderTest {

  @Test
  public void testReadCam() throws Exception {
    String cam = "T(0|(0),(0))";
    TreeCamReader tcr = new TreeCamReader();
    LinkGraph<LGVertex> graph = tcr.readCam(cam);
    assertEquals(cam, graph.getCam());
    assertEquals(3, graph.getNodeNum());
    assertEquals(1, graph.getEdge(0, 1));
    assertEquals(1, graph.getEdge(0, 2));
  }

  @Test
  public void testReadCam2() throws Exception {
    String cam = "T(0|(0),(0|(0),(4)))";
    TreeCamReader tcr = new TreeCamReader();
    LinkGraph<LGVertex> graph = tcr.readCam(cam);
    assertEquals(cam, graph.getCam());
    assertEquals(5, graph.getNodeNum());
    assertEquals(1, graph.getEdge(0, 1));
    assertEquals(1, graph.getEdge(0, 2));
    assertEquals(1, graph.getEdge(2, 3));
    assertEquals(1, graph.getEdge(2, 4));
  }
}
