package prague.graph;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;

import javax.annotation.Nonnull;

import prague.util.IntSet;

/**
 * Graph
 *
 * Created by ch.wang on 07 Apr 2014.
 */
public interface Graph<V extends Vertex> {

  int getNodeNum();

  int getEdgeNum();

  ImmutableList<? extends Vertex> getNodes();

  V getNode(int index);

  int getDegree(int id);

  int getEdge(int from, int to);

  ImmutableSet<Integer> getIn(int id);

  int getGraphId();

  @Nonnull
  String getCam();

  void setCam(@Nonnull String cam);

  IntSet getIdSet();

  List<Integer> getIdList();
}
