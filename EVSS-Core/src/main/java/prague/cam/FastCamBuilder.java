package prague.cam;

import prague.graph.Graph;
import prague.graph.Vertex;

/**
 * fast cam builder
 *
 * Created by ch.wang on 12 Mar 2014.
 */
public class FastCamBuilder {

  public String buildCam(Graph<? extends Vertex> g) {
    StringBuilder cam = new StringBuilder();
    int n = g.getNodeNum();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < i; j++) {
        cam.append(g.getEdge(i, j) != 0 ? "1" : "0");
      }
      cam.append("(");
      cam.append(g.getNode(i).getLabel());
      cam.append(")");
    }
    return cam.toString();
  }
}
