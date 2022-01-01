package prague.cam;

import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;

/**
 * combine normal cam reader and tree cam reader
 *
 * Created by ch.wang on 23 Jan 2014.
 */
public class CombineCamReader implements CamReaderInterface {

  private final CamReader camReader = new CamReader();
  private final TreeCamReader treeCamReader = new TreeCamReader();

  @Override
  public LinkGraph<LGVertex> readCam(String cam) {
    if (cam.startsWith("T")) {
      return treeCamReader.readCam(cam);
    }
    return camReader.readCam(cam);
  }
}
