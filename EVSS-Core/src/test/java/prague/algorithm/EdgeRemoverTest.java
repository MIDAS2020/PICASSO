package prague.algorithm;

import static org.junit.Assert.*;

import org.junit.Test;

import prague.cam.CamBuilderFactory;
import prague.cam.CamBuilderInterface;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;

/**
 * EdgeRemoverTest
 *
 * Created by ch.wang on 12 Jan 2014.
 */
public class EdgeRemoverTest {

  @Test
  public void testRemoveEdge() throws Exception {
    LinkGraph<LGVertex> graph = new LinkGraph<>(3);
//    LGVertex v0 = new LGVertex(0);
//    graph.addNode(v0);
//    LGVertex v1 = new LGVertex(1);
//    graph.addNode(v1);
//    LGVertex v2 = new LGVertex(2);
//    graph.addNode(v2);
//    graph.addEdge(0, 1);
//    graph.addEdge(1, 2);
//    CamBuilderInterface cb = CamBuilderFactory.getCamBuilder();
//    EdgeRemover edgeRemover = new EdgeRemover(graph);
//    LinkGraph<LGVertex> subgraph = edgeRemover.removeEdge(0, 1);
//    assertEquals(2, subgraph.getNodeNum());
//    assertEquals(1, subgraph.getEdgeNum());
//    String cam = cb.buildCam(subgraph);
//    assertEquals("(1)1(2)", cam);
//    LinkGraph<LGVertex> subgraph1 = edgeRemover.removeEdge(1, 2);
//    assertEquals(2, subgraph1.getNodeNum());
//    assertEquals(1, subgraph1.getEdgeNum());
//    String cam1 = cb.buildCam(subgraph1);
//    assertEquals("(0)1(1)", cam1);
  }
}
