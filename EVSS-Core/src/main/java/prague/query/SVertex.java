/*
 * Copyright 2009, Center for Advanced Information Systems,Nanyang Technological University
 * 
 * File name: SVertex.java
 * 
 * Abstract: The vertex structure in query graph
 * 
 * Current Version: 0.1 Author: Jin Changjiu Modified Date: Jun.16,2009
 */

package prague.query;

import prague.graph.lg.LGVertex;

/**
 * @author cjjin
 */
public class SVertex extends LGVertex {

  /**
   * node in seed attached the id in query
   */
  private final int qId;

  public SVertex(int label, int qId) {
    super(label);
    this.qId = qId;
  }

  public int getQId() {
    return qId;
  }
}
