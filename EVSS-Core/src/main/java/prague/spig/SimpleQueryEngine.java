package prague.spig;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.ImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import gblend.exactquery.QueryType;
import java.util.Collections;
import prague.algorithm.EdgeStore;
import prague.graph.Graph;
import prague.graph.Vertex;
import prague.graph.lg.ELGraph;
import prague.graph.lg.LGVertex;
import prague.index.PragueIndex;
import prague.index.repository.PIndexRepository;
import prague.result.MatchedGraph;
import prague.verify.VF2;
import wch.guava2.base.Timer;

/**
 * SimpleQueryEngine
 *
 * Created by ch.wang on 15 Mar 2014.
 */
public class SimpleQueryEngine extends AbstractQueryEngine {

	private final PIndexRepository pir;
	private final SimpleSGraph sGraph = new SimpleSGraph();
	private final SpindleGraphCreator graphCreator;

	public SimpleQueryEngine(PragueIndex pi) {
		super(pi);
		pir = pi.getPIndexRepository();
		pir.preLoad(pi.getParameters().getB());
		graphCreator = new SpindleGraphCreator(pir);
	}

	@Override
	protected void executeExact() {
		ELGraph<LGVertex> query = qb.getWholeQuery(); // the original query
														// structure
		graphCreator.constructSPIG(sGraph, query, qb.getSeed());
		countCandidates();
	}

	private void countCandidates() {
		logger.info(sGraph);
		exactSize = getCandidateIds().size();
	}

	@Override
	public List<Integer> getCandidateIds() {
		ImmutableList<Integer> leaves = sGraph.getLeaves();
		List<Integer> delIds = pir.getDelIds(leaves);
		return delIds;
	}

	@Override
	public QueryType getQueryType() {
		int level = qb.getWholeQuery().getEdgeNum();
		boolean hasLevel = sGraph.hasLevel(level);
		return hasLevel ? QueryType.Frequent : QueryType.CommonInfrequent;
	}

	@Override
	public List<Graph<Vertex>> getCandidateGraph() {
		List<Integer> candidateIds = getCandidateIds();
		List<Graph<Vertex>> alGraphs = pi.fetchFromMem(candidateIds);
		return alGraphs;
	}

	@Override
	// Suggestion is not implemented
	public int modifySuggestion() {
		
		return 0;
	}

	@Override
	public void queryModification(int edgeLabel) {
		sGraph.deleteEdge(edgeLabel);
		countCandidates();
	}

	@Override
	public List<MatchedGraph> getSimilarResult() {
		ELGraph<LGVertex> query = qb.getWholeQuery();
		SimilarResultVerify verify;
		verify = new UseExactVerify(query);

		SGraphExtractor extractor = new SGraphExtractor(query);
		int sigma = pi.getSigma(query);
		logger.info("query sigma: " + sigma);
		List<SGraphExtractor.ExtractResult> sGraphList = ImmutableList
				.of(new SGraphExtractor.ExtractResult(BitArraySet.of(), -1,
						sGraph));
		List<MatchedGraph> returnList = new ArrayList<MatchedGraph>();
		for (int i = 1; i <= sigma; i++) {
			Timer timer = new Timer("extract sigma " + i);
			sGraphList = extractor.extract(sGraphList);
			logger.info(timer.time() + ", size: " + sGraphList.size());

			List<MatchedGraph> matchedGraphs = verify.verify(i, sGraphList);
			if (matchedGraphs != null && matchedGraphs.size() > 0) {
				for (int k = 0; k < matchedGraphs.size(); k++) {
					returnList.add(matchedGraphs.get(k));
				}
			}
			if (i == sigma) {
				// if (!matchedGraphs.isEmpty()) {
				return returnList;
			}
			// }
		}
		return ImmutableList.of();
	}

	private interface SimilarResultVerify {

		List<MatchedGraph> verify(int sigma,
				List<SGraphExtractor.ExtractResult> sGraphList);
	}

	private class UseExactVerify implements SimilarResultVerify {

		private final ELGraph<LGVertex> query;
		private final EdgeStore edgeStore;

		private UseExactVerify(ELGraph<LGVertex> query) {
			this.query = query;
			edgeStore = new EdgeStore(query);
		}

		@Override
		public List<MatchedGraph> verify(int sigma,
				List<SGraphExtractor.ExtractResult> sGraphList) {
			int edgeNum = query.getEdgeNum() - sigma;
			Map<Integer, MatchedGraph> mgMap = new HashMap<>();
			sGraphList.forEach(s -> {
				int oldCount = mgMap.size();
				EdgeStore.GraphAndTrans gt = edgeStore.getGraph(s.getSet(),
						s.getI());
				VF2 verifier = new VF2(gt.getGraph());
				SimpleSGraph sGraph = s.getSGraph();
				ImmutableList<Integer> leaves = sGraph.getLeaves();
				boolean isFree = sGraph.hasLevel(edgeNum);
				pir.getDelIds(leaves)
						.stream()
						.filter(id -> !mgMap.containsKey(id))
						.forEach(
								id -> {
									if (verifier.verify(pi.fetchFromMem(id))) {
										int[] nodeSet = verifier.getNodeSet();
										nodeSet = EdgeStore.helpTrans(
												gt.getTrans(), nodeSet);
										MatchedGraph mg = new MatchedGraph(id,
												sigma, nodeSet);
										mgMap.put(id, mg);
									} else {
										checkState(!isFree);
									}
								});
				int add = mgMap.size() - oldCount;
				if (add > 0) {
					String f = isFree ? "free" : "need";
					logger.info(String.format("%s (%s) get new: %d",
							s.getSet(), f, add));
				}
			});

			return ImmutableList.copyOf(mgMap.values());
		}
	}
}
