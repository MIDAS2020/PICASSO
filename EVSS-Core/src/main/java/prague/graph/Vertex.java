package prague.graph;

import java.util.Set;

/**
 * Vertex interface
 *
 * Created by ch.wang on 07 Apr 2014.
 */
public interface Vertex {

  int getLabel();

  int getId();

  int getDegree();

  Set<Integer> getIn();
}
