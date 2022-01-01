package prague.cam;

import prague.algorithm.TreeTester;
import prague.graph.Graph;
import prague.graph.Vertex;

/**
 * combine normal cam builder and tree cam builder
 *
 * Created by ch.wang on 18 Jan 2014.
 */
public class CombineCamBuilder implements CamBuilderInterface {

  private final CamBuilderInterface camBuilder = new NautyCamBuilder();
  private final TreeCamBuilder treeCamBuilder = new TreeCamBuilder();
  private final TreeTester treeTester = new TreeTester();

  @Override
  public String buildCam(Graph<? extends Vertex> g) {
    if (g.getEdgeNum() > 1 && treeTester.isTree(g)) {
      return treeCamBuilder.buildCam(g);
    }
    return camBuilder.buildCam(g);
  }

  @Override
  public int[] buildCamWithNewIds(Graph<? extends Vertex> g) {
    if (g.getEdgeNum() > 1 && treeTester.isTree(g)) {
      return treeCamBuilder.buildCamWithNewIds(g);
    }
    return camBuilder.buildCamWithNewIds(g);
  }

  @Override
  public String buildCam(int label1, int label2) {
    return camBuilder.buildCam(label1, label2);
  }

  @Override
  public int compare(int label1, int label2) {
    return camBuilder.compare(label1, label2);
  }
}
