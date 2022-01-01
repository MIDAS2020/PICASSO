/*
 * Copyright 2010, Center for Advanced Information Systems, Nanyang Technological University
 * 
 * File name: ExactVerify.java
 * 
 * Abstract: Verify the exact results from candidates
 * 
 * Current Version: 0.1 Author: Jin Changjiu Modified Date: Feb.28,2010
 */

package gblend.exactquery;

import java.util.ArrayList;
import java.util.List;

import prague.graph.Graph;
import prague.graph.Vertex;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import prague.model.QueryEngineInterface;
import prague.result.MatchedGraph;
import prague.verify.VF2;

/**
 * @author cjjin
 */
public class ExactVerify {

  public List<MatchedGraph> verify(LinkGraph<LGVertex> query, QueryEngineInterface qe) {
    
    List<Graph<Vertex>> candidateGraphs = qe.getCandidateGraph();
    List<MatchedGraph> matchedGraph = new ArrayList<>();
    VF2 verify = new VF2(query);
    candidateGraphs.stream().filter(verify::verify).forEach(cGraph -> {
      MatchedGraph mg = new MatchedGraph(cGraph.getGraphId(), 0, verify.getNodeSet());
      matchedGraph.add(mg);
    });
    return matchedGraph;
  }

}
