package prague.model;

import java.util.Collection;
import java.util.List;

import gblend.exactquery.QueryType;
import prague.graph.Graph;
import prague.graph.Vertex;
import prague.index.PragueIndex;
import prague.result.MatchedGraph;

/**
 * QueryEngineInterface
 *
 * Created by ch.wang on 14 Mar 2014.
 */
public interface QueryEngineInterface {

  PragueIndex getPi();

  // the main exact query algorithm
  long executeQuery();

  Collection<Integer> getCandidateIds();

  QueryType getQueryType();

  List<Graph<Vertex>> getCandidateGraph();

  int modifySuggestion();

  void queryModification(int edgeLabel);

  int getExactSize();

  int getSimilarSize();

  List<MatchedGraph> getSimilarResult();
}
