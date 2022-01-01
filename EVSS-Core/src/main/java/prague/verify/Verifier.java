package prague.verify;

import prague.graph.Graph;
import prague.graph.Vertex;

/**
 * Verifier
 *
 * Created by ch.wang on 08 May 2014.
 */
public interface Verifier {

  boolean verify(Graph<? extends Vertex> g);

  int[] getNodeSet();
}
