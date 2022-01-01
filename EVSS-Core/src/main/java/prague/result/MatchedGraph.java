/*
 * Copyright 2009, Center for Advanced Information Systems,Nanyang Technological University
 * 
 * File name: MatchedGraph.java
 * 
 * Abstract: Keep the matched subgraph part of the result graph
 * 
 * Current Version: 0.1 Author: Jin Changjiu Modified Date: Sep.22,2009
 */

package prague.result;

import static com.google.common.base.Preconditions.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author cjjin
 */
public class MatchedGraph {

  private final int id;
  private final int sigma;
  private int[] nodeSet;

  public MatchedGraph(int id, int sigma, @Nullable int[] nodeSet) {
    this.id = id;
    this.sigma = sigma;
    this.nodeSet = nodeSet == null ? null : nodeSet.clone();
  }

  public int getSigma() {
    return sigma;
  }

  @Nullable
  public int[] getNodeSet() {
    return nodeSet;
  }

  public void setNodeSet(@Nonnull int[] nodeSet) {
    this.nodeSet = checkNotNull(nodeSet).clone();
  }

  public int getId() {
    return id;
  }
}
