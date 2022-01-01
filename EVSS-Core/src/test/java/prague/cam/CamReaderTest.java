package prague.cam;

import static org.junit.Assert.*;

import org.junit.Test;

import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;

/**
 * Test cam reader
 *
 * Created by ch.wang on 23 Jan 2014.
 */
public class CamReaderTest {

  @Test
  public void testReadCam() throws Exception {
    String cam = "(3)1(0)10(0)010(0)0011(0)";
    CamReader cr = new CamReader();
    LinkGraph<LGVertex> graph = cr.readCam(cam);
    assertEquals(cam, graph.getCam());
    assertEquals(5, graph.getNodeNum());
    assertEquals(3, graph.getNode(0).getLabel());
    assertEquals(1, graph.getEdge(0, 1));
    assertEquals(1, graph.getEdge(0, 2));
  }
}
