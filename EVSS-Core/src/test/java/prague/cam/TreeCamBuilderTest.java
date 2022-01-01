package prague.cam;

import static org.junit.Assert.*;

import org.junit.Test;

import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;

/**
 * test
 *
 * Created by ch.wang on 18 Jan 2014.
 */
public class TreeCamBuilderTest {

  private final TreeCamReader reader = new TreeCamReader();
  private final TreeCamBuilder builder = new TreeCamBuilder();

  @Test
  public void testBuildCam() throws Exception {
    LinkGraph<LGVertex> graph = new LinkGraph<>(3);
    LGVertex o1 = new LGVertex(2);
    graph.addNode(o1);
    LGVertex o2 = new LGVertex(2);
    graph.addNode(o2);
    LGVertex c = new LGVertex(1);
    graph.addNode(c);
    graph.addEdge(0, 2);

    String cam = builder.buildCam(graph);
    assertEquals("T(1|(2))", cam);
  }

  @Test
  public void testBuildCamWithNewIds() {
    String cam = "T(0|(0),(0|(4|(0))))";
    LinkGraph<LGVertex> graph = reader.readCam(cam);
    TestGraph tg = new TestGraph(graph, "T(0|(0),(0|(4|(0))))");
    int[] newIds = builder.buildCamWithNewIds(tg);
    assertEquals(cam, graph.getCam());
    for (int i = 0; i < 5; i++) {
      assertEquals(i, newIds[i]);
    }
  }

  @Test
  public void testBuildCamWithNewIds2() {
    String cam = "T(0|(0),(0|(0),(4)))";
    LinkGraph<LGVertex> graph = reader.readCam(cam);
    TestGraph tg = new TestGraph(graph, "T(0|(0),(0|(0)),(4))");
    int[] newIds = builder.buildCamWithNewIds(tg);
    assertArrayEquals(new int[]{2, 3, 0, 1, 4}, newIds);
  }

  @Test
  public void testBuildCamWithNewIds3() {
    String cam = "T(0|(2),(0),(1))";
    LinkGraph<LGVertex> graph = reader.readCam(cam);
    TestGraph tg = new TestGraph(graph, "T(0|(0),(1),(2))");
    int[] newIds = builder.buildCamWithNewIds(tg);
    assertArrayEquals(new int[]{0, 3, 1, 2}, newIds);
  }
}
