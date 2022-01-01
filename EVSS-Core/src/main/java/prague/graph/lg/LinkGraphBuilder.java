package prague.graph.lg;

import java.util.ArrayList;
import java.util.List;

/**
 * link graph builder Created by ch.wang on 22 Jan 2014.
 */
public class LinkGraphBuilder<V extends LGVertex> {

  private final List<V> vertexes = new ArrayList<>();
  private final List<Edge> edges = new ArrayList<>();

  private LinkGraphBuilder() {
  }

  public static <E extends LGVertex> LinkGraphBuilder<E> create() {
    return new LinkGraphBuilder<>();
  }

  public int addNode(V node) {
    int id = vertexes.size();
    vertexes.add(node);
    return id;
  }

  public void addEdge(int from, int to) {
    Edge edge = new Edge(from, to);
    edges.add(edge);
  }

  public LinkGraph<V> build() {
    LinkGraph<V> graph = new LinkGraph<>(vertexes.size());
    vertexes.forEach(graph::addNode);
    edges.forEach(e -> graph.addEdge(e.getSrc(), e.getTrg()));
    return graph;
  }

  public V getNode(int i) {
    return vertexes.get(i);
  }
}
