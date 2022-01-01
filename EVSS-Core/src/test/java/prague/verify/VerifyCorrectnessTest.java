package prague.verify;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

import prague.data.DataParams;
import prague.graph.Graph;
import prague.graph.ImmutableGraph;
import prague.graph.Vertex;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import prague.index.PragueIndex;
import wch.guava2.base.Timer;

/**
 * VerifyCorrectnessTest
 *
 * Created by ch.wang on 08 May 2014.
 */
public class VerifyCorrectnessTest {

  private ImmutableList<ImmutableGraph> graphSet;

  public static void main(String[] args) throws IOException {
    VerifyCorrectnessTest test = new VerifyCorrectnessTest();
    test.run();
  }

  public void run() throws IOException {
    @SuppressWarnings("deprecation")
    List<Function<Graph<? extends Vertex>, Verifier>> verifierList =
        ImmutableList.<Function<Graph<? extends Vertex>, Verifier>>
            of(VF2::new);

    DataParams dp = new DataParams("AIDS", 40, "0.1", 8, 3);
    PragueIndex pi = new PragueIndex(dp, null);
    graphSet = pi.getDataSet().getGraphSet();
    ImmutableList<LinkGraph<LGVertex>> freqGraphs = pi.getFreqGraphsAsList();
    int total = freqGraphs.size();
    for (LinkGraph<LGVertex> query : freqGraphs) {
      if (query.getEdgeNum() == 0) {
        continue;
      }
      int queryId = query.getGraphId();
      System.out.print(String.format("processing %d of %d", queryId, total));
      verifierList.stream().forEach(v -> test(v.apply(query), query));
      System.out.println();
    }
    System.out.println("finished");
  }

  private void test(Verifier verifier, Graph<? extends Vertex> query) {
    test(verifier.getClass(), query, 0, (q, s) -> verifier.verify(q),
         c -> query.getIdSet().contains(c), verifier::getNodeSet);
  }

  private void test(Class verifier, Graph<? extends Vertex> query, int sigma,
                    BiPredicate<Graph<? extends Vertex>, Integer> verifyResult,
                    Predicate<Integer> exceptAnswer,
                    Supplier<int[]> matchedNodes) {
    int queryId = query.getGraphId();
    Timer timer = new Timer(verifier.getName());
    for (ImmutableGraph graph : graphSet) {
      boolean result = verifyResult.test(graph, sigma);
      boolean except = exceptAnswer.apply(graph.getGraphId());
      if (result && !except) {
        System.out.println(Arrays.toString(matchedNodes.get()));
      }
      checkState(result == except, "%s error query: %s, graph: %s, sigma: %s, except: %s",
                 verifier.getName(), queryId, graph.getGraphId(), sigma, except);
    }
    System.out.print(" " + timer.time());
  }
}
