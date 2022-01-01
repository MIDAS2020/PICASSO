package prague.query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import gblend.exactquery.ExactVerify;
import gblend.exactquery.QueryType;
import java.util.HashSet;
import prague.graph.lg.ELGraph;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import prague.index.PragueIndex;
import prague.model.QueryBuilder;
import prague.model.QueryEngineInterface;
import prague.model.QueryInterface;
import prague.model.RunResult;
import prague.result.MatchedGraph;
import wch.guava2.base.Timer;

/**
 * QueryInterfaceImpl
 *
 * Created by ch.wang on 04 May 2014.
 */
public class QueryInterfaceImpl implements QueryInterface {

    private final Logger logger = LogManager.getLogger(this);

    private final PragueIndex pi;
    private final ExactVerify ev = new ExactVerify();

    public QueryInterfaceImpl(PragueIndex pi) {
        this.pi = pi;
    }

    private List<MatchedGraph> getExactResult(QueryType queryType, QueryEngineInterface qe,
            LinkGraph<LGVertex> query) {
        List<MatchedGraph> mGraphs = new ArrayList<>();
        if (queryType == QueryType.CommonInfrequent) { // for the common infrequent
            mGraphs = ev.verify(query, qe);// verify the exact results from
        } else if (queryType == QueryType.Frequent
                || queryType == QueryType.Dif) { // without verification
            Collection<Integer> candidateIds = qe.getCandidateIds();
            for (int candidateId : candidateIds) {
                MatchedGraph mg = new MatchedGraph(candidateId, 0, null);
                mGraphs.add(mg);
            }
        }
        return mGraphs;
    }

    @Override
    public RunResult run(QueryBuilder bq) {
        QueryEngineInterface qe = bq.getQueryEngine();
        ELGraph<LGVertex> query = bq.getWholeQuery();
        if (query.getEdgeNum() == 0) {
            return null;
        }

        Timer timer = new Timer("System Response");

        QueryType queryType = qe.getQueryType();
        /**
         * record the matched subgraphs in the results
         */
        List<MatchedGraph> mGraphs = getExactResult(queryType, qe, query);

        int exact = mGraphs.size();
        int sigma = 0;
        logger.info("Exact result size: " + exact);

        logger.info("Similar Panel State:" + pi.getQP().isSimilar());

        if (exact == 0 && pi.getQP().isSimilar()) {
            mGraphs = qe.getSimilarResult();
            for (int i = 0; i < mGraphs.size() - 1; i++) {
                for (int j = mGraphs.size() - 1; j > i; j--) {
                    if (mGraphs.get(j).getId()== mGraphs.get(i).getId()) {
                        mGraphs.remove(j);
                    }
                }
            }
            if (!mGraphs.isEmpty()) {
                sigma = mGraphs.get(0).getSigma();
            }
            logger.info("Similar result size: " + mGraphs.size());
        }

        RunResult runResult = new RunResult(mGraphs, mGraphs.size(), sigma, timer.getMsTime());
        return runResult;
    }
}
