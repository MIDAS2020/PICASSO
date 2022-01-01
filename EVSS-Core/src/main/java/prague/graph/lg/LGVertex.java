/*
 * Copyright 2009, Center for Advanced Information Systems,Nanyang Technological University
 * 
 * File name: LGVertex.java
 * 
 * Abstract: The vertex structure in query graph
 * 
 * Current Version: 0.1 Author: Jin Changjiu Modified Date: Jun.16,2009
 */

package prague.graph.lg;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Objects;

import java.util.Set;

import prague.graph.Vertex;

/**
 * @author cjjin
 */
public class LGVertex implements Vertex {

  private final int label;
  private AdjMatrix graph;
  private int id = -1;

  public LGVertex(int label) {
    this.label = label;
  }

  public LGVertex(Vertex v) {
    label = v.getLabel();
  }

  @Override
  public int getLabel() {
    return label;
  }

  @Override
  public int getId() {
    checkState(id >= 0);
    return id;
  }

  @Override
  public int getDegree() {
    checkState(id >= 0);
    return graph.getDegree(id);
  }

  @Override
  public Set<Integer> getIn() {
    checkState(id >= 0);
    return graph.getIn(id);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("id", id)
        .add("label", label)
        .toString();
  }

  void addToGraph(AdjMatrix graph, int id){
    checkArgument(this.graph == null, "node already in some graph");
    this.graph = graph;
    this.id = id;
  }
}
