package prague.graph;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import prague.util.IntSet;
import wch.guava2.collect.Immutables;

/**
 * Immutable graph
 *
 * Created by ch.wang on 07 Apr 2014.
 */
public class ImmutableGraph implements Graph<Vertex> {

  private final int graphId;
  private final ImmutableList<ImmutableVertex> vertexes;
  private final ImmutableSetMultimap<Integer, Integer> edges;
  private final int edgeNum;
  private final IntSet idList;
  private String cam;

  private ImmutableGraph(int graphId, ImmutableList<Integer> nodes,
                         ImmutableSetMultimap<Integer, Integer> edges,
                         List<Integer> idList) {
    this.graphId = graphId;
    this.edges = edges;
    edgeNum = edges.size() / 2;
    this.idList = idList == null ? null : new IntSet(idList);
    vertexes = Immutables.toImmutableList(nodes, ImmutableVertex::new);
  }

  public static Builder builder(int graphId) {
    return new Builder(graphId);
  }

  @Override
  public int getNodeNum() {
    return vertexes.size();
  }

  @Override
  public int getEdgeNum() {
    return edgeNum;
  }

  @Override
  public ImmutableList<? extends Vertex> getNodes() {
    return vertexes;
  }

  @Override
  public Vertex getNode(int index) {
    return vertexes.get(index);
  }

  @Override
  public int getDegree(int id) {
    return edges.get(id).size();
  }

  @Override
  public int getEdge(int from, int to) {
    return edges.containsEntry(from, to) ? 1 : 0;
  }

  @Override
  public ImmutableSet<Integer> getIn(int id) {
    return edges.get(id);
  }

  @Override
  public int getGraphId() {
    return graphId;
  }

  @Nonnull
  @Override
  public String getCam() {
    checkState(cam != null, "cam not set");
    return cam;
  }

  @Override
  public void setCam(@Nonnull String cam) {
    checkState(this.cam == null, "cam already set: %s", this.cam);
    this.cam = checkNotNull(cam);
  }

  @Override
  public IntSet getIdSet() {
    return idList;
  }

  @Override
  public List<Integer> getIdList() {
    return idList == null ? null : idList.getList();
  }

  public static class Builder {

    private final int graphId;

    private final ImmutableList.Builder<Integer> nodes = ImmutableList.builder();

    private final ImmutableSetMultimap.Builder<Integer, Integer> edges =
        ImmutableSetMultimap.builder();

    private List<Integer> idList;

    Builder(int graphId) {
      this.graphId = graphId;
    }

    public void addNode(int label) {
      nodes.add(label);
    }

    public void addEdge(int from, int to) {
      edges.put(from, to);
      edges.put(to, from);
    }

    public void setIdList(List<Integer> gIds) {
      idList = gIds;
    }

    public ImmutableGraph build() {
      ImmutableGraph graph = new ImmutableGraph(graphId, nodes.build(), edges.build(), idList);
      return graph;
    }

  }

  private class ImmutableVertex implements Vertex {

    private final int id;
    private final int label;

    public ImmutableVertex(int id, int label) {
      this.id = id;
      this.label = label;
    }

    @Override
    public int getLabel() {
      return label;
    }

    @Override
    public int getId() {
      return id;
    }

    @Override
    public int getDegree() {
      return edges.get(id).size();
    }

    @Override
    public Set<Integer> getIn() {
      return edges.get(id);
    }
  }
}
