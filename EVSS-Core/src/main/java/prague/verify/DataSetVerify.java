package prague.verify;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;

import prague.data.DataSet;
import prague.graph.ImmutableGraph;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import prague.result.MatchedGraph;

/**
 * DataSetVerify
 *
 * Created by ch.wang on 03 May 2014.
 */
public class DataSetVerify {

  public List<MatchedGraph> verify(LinkGraph<LGVertex> query, DataSet ds) {
    ImmutableList<ImmutableGraph> candidateGraphs = ds.getGraphSet();
    return candidateGraphs.parallelStream().map(g -> {
      VF2 verify = new VF2(query);
      if (verify.verify(g)) {
        MatchedGraph mg = new MatchedGraph(g.getGraphId(), 0, verify.getNodeSet());
        return mg;
      }
      return null;
    }).filter(mg -> mg != null).collect(Collectors.toList());
  }
}
