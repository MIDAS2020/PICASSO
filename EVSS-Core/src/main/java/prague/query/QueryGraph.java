package prague.query;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * query graph
 *
 * Created by ch.wang on 07 Mar 2014.
 */
public class QueryGraph {

  private final ListMultimap<Integer, Integer> adj = ArrayListMultimap.create();

  private final Set<QueryEdge> visitedEdge = new HashSet<>();

  private final Set<Integer> visited = new HashSet<>();

  public void newEdge(int a, int b) {
    adj.put(a, b);
    adj.put(b, a);
  }

  private void dfs(int x, ImmutableList.Builder<QueryEdge> edges) {
    visited.add(x);
    for (int i : adj.get(x)) {
      QueryEdge edge = new QueryEdge(x, i);
      if (!visitedEdge.contains(edge)) {
        edges.add(edge);
        visitedEdge.add(edge);
      }
      if (!visited.contains(i)) {
        dfs(i, edges);
      }
    }
  }

  public List<QueryEdge> getList() {
    if (adj.isEmpty()) {
      return ImmutableList.of();
    }
    int x;
    if (visitedEdge.isEmpty()) {
      x = Iterables.get(adj.keySet(), 0);
    } else {
      x = Iterables.get(visitedEdge, 0).getSrc();
    }
    ImmutableList.Builder<QueryEdge> edges = ImmutableList.builder();
    dfs(x, edges);
    visited.clear();
    return edges.build();
  }

  public boolean removeEdge(@Nonnull QueryEdge edge) {
    checkNotNull(edge);
    adj.remove(edge.getSrc(), edge.getTrg());
    adj.remove(edge.getTrg(), edge.getSrc());
    return visitedEdge.remove(edge);
  }

  public boolean hasPending() {
    return visitedEdge.size() * 2 != adj.size();
  }

  public boolean visitedEdge(@Nonnull QueryEdge edge) {
    return visitedEdge.contains(checkNotNull(edge));
  }

  public boolean isVisitedEmpty() {
    return visitedEdge.isEmpty();
  }
}
