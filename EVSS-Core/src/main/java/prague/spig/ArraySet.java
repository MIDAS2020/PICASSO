package prague.spig;

import java.util.stream.IntStream;

/**
 * ArraySet
 *
 * Created by ch.wang on 10 May 2014.
 */
public interface ArraySet {

  int getSize();

  boolean contains(int node);

  ArraySet add(int node);

  ArraySet remove(int node);

  int max();

  IntStream stream();
}
