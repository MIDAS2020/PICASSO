/*
 * Copyright 2009, Center for Advanced Information Systems,Nanyang Technological University
 * 
 * File name: QueryNodeID.java
 * 
 * Abstract:
 * 
 * Current Version: 0.1 Author: Jin Changjiu Modified Date: Jun.16,2009
 */
package prague.query;

/**
 * @author cjjin
 */
class QueryNode {

  private final int id;
  private final int label;

  QueryNode(int id, int label) {
    this.id = id;
    this.label = label;
  }

  public int getId() {
    return id;
  }

  public int getLabel() {
    return label;
  }
}
