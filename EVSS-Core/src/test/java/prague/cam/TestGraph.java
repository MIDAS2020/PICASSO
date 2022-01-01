package prague.cam;

import static org.junit.Assert.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;

import javax.annotation.Nonnull;

import prague.graph.Graph;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import prague.util.IntSet;

/**
* Created by ch.wang on 12 May 2014.
*/
class TestGraph implements Graph<LGVertex> {

  private final LinkGraph<LGVertex> graph;
  private final String cam;

  public TestGraph(LinkGraph<LGVertex> graph, String cam) {
    this.graph = graph;
    this.cam = cam;
  }

  @Override
  public LGVertex getNode(int index) {
    return graph.getNode(index);
  }

  @Override
  public int getEdge(int from, int to) {
    return graph.getEdge(from, to);
  }

  @Override
  public int getNodeNum() {
    return graph.getNodeNum();
  }

  @Override
  public ImmutableSet<Integer> getIn(int id) {
    return graph.getIn(id);
  }

  @Override
  public ImmutableList<LGVertex> getNodes() {
    return graph.getNodes();
  }

  @Override
  public int getGraphId() {
    return graph.getGraphId();
  }

  @Nonnull
  @Override
  public String getCam() {
    return graph.getCam();
  }

  @Override
  public void setCam(@Nonnull String cam) {
    assertEquals(this.cam, cam);
  }

  @Override
  public IntSet getIdSet() {
    return graph.getIdSet();
  }

  @Override
  public List<Integer> getIdList() {
    return graph.getIdList();
  }

  @Override
  public int getEdgeNum() {
    return graph.getEdgeNum();
  }

  @Override
  public int getDegree(int id) {
    return graph.getDegree(id);
  }
}
