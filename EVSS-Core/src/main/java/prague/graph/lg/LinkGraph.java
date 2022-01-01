package prague.graph.lg;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import prague.graph.Graph;
import prague.util.IntSet;

public class LinkGraph<V extends LGVertex> extends AdjMatrix implements Graph<V> {

  private final List<V> vertices;
  private int graphId = -1;
  private String cam;
  private IntSet idList;

  private LinkGraph(int size, List<V> vertices) {
    super(size);
    this.vertices = vertices;
  }

  public LinkGraph(int size) {
    this(size, new ArrayList<>());
  }

  public void addNode(V node) {
    int idx = vertices.size();
    checkElementIndex(idx, size);
    node.addToGraph(this, idx);
    vertices.add(node);
  }

  @Override
  public ImmutableList<V> getNodes() {
    return ImmutableList.copyOf(vertices);
  }

  @Override
  public V getNode(int index) {
    return vertices.get(index);
  }

  @Override
  public int getGraphId() {
    checkState(graphId != -1, "graph id not set");
    return graphId;
  }

  public void setGraphId(int gid) {
    checkArgument(gid >= 0);
    checkState(graphId == -1, "graph id already set: %s", gid);
    graphId = gid;
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
    return idList.getList();
  }

  public void setIdList(@Nonnull Collection<Integer> gIds) {
    idList = new IntSet(checkNotNull(gIds));
  }

  public void copyIdList(Graph<?> other) {
    idList = other.getIdSet();
  }

}
