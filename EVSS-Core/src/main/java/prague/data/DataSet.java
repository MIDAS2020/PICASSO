package prague.data;

import com.google.common.collect.ImmutableList;

import prague.graph.ImmutableGraph;

public class DataSet {

  private final ImmutableList<ImmutableGraph> graphSet;

  DataSet(ImmutableList<ImmutableGraph> graphSet) {
    this.graphSet = graphSet;
  }

  public ImmutableList<ImmutableGraph> getGraphSet() {
    return graphSet;
  }
}
