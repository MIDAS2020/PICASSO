package prague.result;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ImmutableMap;

import java.util.BitSet;

import prague.graph.Vertex;
import prague.graph.lg.ELGraph;
import prague.graph.lg.LinkGraph;
import wch.guava2.collect.Immutables;

/**
 * matched checker
 *
 * Created by ch.wang on 10 Mar 2014.
 */
public class MatchedChecker {

  private static final int EMPTY_NODE = -1;

  private final LinkGraph<? extends Vertex> query;
  private final ImmutableMap<Integer, Integer> nodeSet;
  private final BitSet edges;
  private final int sigma;

  public MatchedChecker(ELGraph<? extends Vertex> query, MatchedGraph matchedGraph) {
    this.query = checkNotNull(query);
    edges = query.getEdgeLabelSet();
    sigma = matchedGraph.getSigma();
    int[] matchedNodes = checkNotNull(matchedGraph.getNodeSet());
    nodeSet = Immutables.buildImmutableMap(nodeBuilder -> {
      for (int i = 0; i < matchedNodes.length; i++) {
        int matchedNode = matchedNodes[i];
        if (matchedNode != EMPTY_NODE) {
          nodeBuilder.put(matchedNode, i);
        }
      }
    });
    //System.out.println("matchedEdge:"+edges);
    //System.out.println("matchedNode:"+nodeSet.toString());
  }
  //added 
  public ImmutableMap<Integer, Integer> getNodeSet(){
      return nodeSet;
  }
          
  public boolean checkNode(int node) {
    return nodeSet.containsKey(node);
  }

  public int checkEdge(int from, int to) {
    Integer i = nodeSet.get(from);
    Integer j = nodeSet.get(to);
    if (i == null || j == null) {
      return 0;
    }
    int edgeLabel = query.getEdge(i, j);
    if (edgeLabel > 0) {
      edges.clear(edgeLabel);
    }
    return edgeLabel;
  }

  public BitSet getUnUsedEdges() {
   // System.out.println("getUnUsedEdges:"+sigma+"&&&&&&"+edges.cardinality());
    checkState(sigma == edges.cardinality());
    return edges;
  }
}
