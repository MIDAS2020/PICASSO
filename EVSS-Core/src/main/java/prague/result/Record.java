/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 * 
 * File name: Record.java
 * 
 * Abstract: record the pre query time and current candidate size Current Version: 0.1 Author: Jin
 * Changjiu Modified Date: Jun.3,2010
 */
package prague.result;

import prague.query.QueryEdge;

/**
 * @author c4jin
 */
public class Record {

  private final String name;
  private final int i;
  private final QueryEdge edge;
  private long time = 0;
  private int exactSize = 0;
  private int simSize = 0;

  public Record(String name, int i, QueryEdge edge) {
    this.name = name;
    this.i = i;
    this.edge = edge;
  }

  public void insertRecord(long t, int e, int s) {
    time = t;
    exactSize = e;
    simSize = s;
  }

  public String getName() {
    return name;
  }

  public int getI() {
    return i;
  }

  public QueryEdge getEdge() {
    return edge;
  }

  public long getTime() {
    return time;
  }

  public int getExactSize() {
    return exactSize;
  }

  public int getSimSize() {
    return simSize;
  }

}
