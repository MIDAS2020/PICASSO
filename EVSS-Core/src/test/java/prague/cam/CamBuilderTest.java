package prague.cam;

import static org.junit.Assert.*;

import org.junit.Test;

import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;

public class CamBuilderTest {

  private final CamBuilder cb = new CamBuilder();
  private final CamReader cr = new CamReader();

  @Test
  public void testBuildCam() throws Exception {
//    LinkGraph<LGVertex> graph = new LinkGraph<>(3);
//    LGVertex o1 = new LGVertex(2);
//    graph.addNode(o1);
//    LGVertex o2 = new LGVertex(2);
//    graph.addNode(o2);
//    LGVertex c = new LGVertex(1);
//    graph.addNode(c);
//    graph.addEdge(0, 2);
//
//    String cam = cb.buildCam(graph);
//    assertEquals("(2)1(1)00(2)", cam);
  }

  @Test
  public void test1Edge() {
//    LinkGraph<LGVertex> graph = new LinkGraph<>(2);
//    LGVertex o1 = new LGVertex(2);
//    graph.addNode(o1);
//    LGVertex o2 = new LGVertex(10);
//    graph.addNode(o2);
//    graph.addEdge(0, 1);
//
//    String cam = cb.buildCam(graph);
//    assertEquals("(2)1(10)", cam);
  }

  @Test
  public void testBuildCamWithNewIds() {
//    String cam = "(0)1(2)11(1)";
//    LinkGraph<LGVertex> graph = cr.readCam(cam);
//    TestGraph tg = new TestGraph(graph, "(2)1(1)11(0)");
//    int[] newIds = cb.buildCamWithNewIds(tg);
//    assertArrayEquals(new int[]{2, 0, 1}, newIds);
  }
}
