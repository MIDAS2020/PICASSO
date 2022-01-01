package prague.cam;

import com.google.common.collect.Ordering;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import prague.algorithm.GraphRelabel;
import prague.graph.Graph;
import prague.graph.Vertex;

public class CamBuilder implements CamBuilderInterface {

  private final Logger logger = LogManager.getLogger(this);
  private int[] newIds;

  private String getCam(Graph<? extends Vertex> g, int[] p) {
    StringBuilder cam = new StringBuilder();
    int n = g.getNodeNum();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < i; j++) {
        cam.append(g.getEdge(p[i], p[j]) != 0 ? "1" : "0");
      }
      cam.append("(");
      cam.append(g.getNode(p[i]).getLabel());
      cam.append(")");
    }
    return cam.toString();
  }

  private void calculateCam(Graph<? extends Vertex> g) {
      //logger.info("****buildCam****" + g.getNodeNum());

    if (g.getNodeNum() > 10) {
      logger.warn("graph size: " + g.getNodeNum());
    }
    String cam = "";
    PermutationGenerator pg = new PermutationGenerator(g.getNodeNum());
    while (pg.hasNext()) {
      String one = getCam(g, pg.get());
      if (one.compareTo(cam) > 0) {
        cam = one;
        newIds = GraphRelabel.invert(pg.get());
      }
    }
    // logger.info("****out buildCam****");
    g.setCam(cam);
  }

  @Override
  public String buildCam(Graph<? extends Vertex> g) {
    //  logger.info("****buildCam****" + g.getNodeNum());
    calculateCam(g);
    return g.getCam();
  }

  @Override
  public int[] buildCamWithNewIds(Graph<? extends Vertex> g) {
    //logger.info("****buildCamWithNewIds****" + g.getNodeNum());
    calculateCam(g);
    return newIds;
  }

  private String getCam(int label1, int label2) {
    return "(" + label1 + ")1(" + label2 + ")";
  }

  @Override
  public String buildCam(int label1, int label2) {
    String cam1 = getCam(label1, label2);
    String cam2 = getCam(label2, label1);
    return Ordering.natural().max(cam1, cam2);
  }

  @Override
  public int compare(int label1, int label2) {
    String cam1 = getCam(label1, label2);
    String cam2 = getCam(label2, label1);
    return -cam1.compareTo(cam2);
  }
}
