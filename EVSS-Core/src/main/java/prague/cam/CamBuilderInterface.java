package prague.cam;

import prague.graph.Graph;
import prague.graph.Vertex;

public interface CamBuilderInterface {

  String buildCam(Graph<? extends Vertex> g);

  int[] buildCamWithNewIds(Graph<? extends Vertex> g);

  // CAUTION! different cam builder will produce different result
  String buildCam(int label1, int label2);

  int compare(int label1, int label2);
}
