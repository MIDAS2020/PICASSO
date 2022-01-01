package prague.cam;

import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;

/**
 * The interface of cam reader
 *
 * Created by ch.wang on 23 Jan 2014.
 */
public interface CamReaderInterface {
  LinkGraph<LGVertex> readCam(String cam);
}
