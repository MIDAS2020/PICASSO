/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: BuildQuery.java
 *
 * Abstract: Build the Query Graph
 *
 * Current Version: 0.1 Author: Jin Changjiu Modified Date: Jun.3,2010
 */

package prague.query;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import prague.algorithm.ELConnectionTester;
import prague.cam.CamBuilderFactory;
import prague.cam.CamBuilderInterface;
import prague.graph.lg.ELGraph;
import prague.graph.lg.LGVertex;
import prague.model.QueryBuilder;
import prague.model.QueryEngineInterface;
import prague.model.QueryOperation;
import prague.result.Record;
import wch.guava2.base.Timer;

/**
 * @author cjjin
 */
public class BuildQuery implements QueryOperation, QueryBuilder {

	private final Logger logger = LogManager.getLogger(this);

	private final CamBuilderInterface cb = CamBuilderFactory.getCamBuilder();
	private final QueryEngineInterface qe;
	private final Map<Integer, QueryNode> qns = Maps.newHashMap(); // store all
																	// the query
																	// nodes
	private final BiMap<QueryEdge, Integer> edgeMap = HashBiMap.create();
	private final ELConnectionTester connectionTester = new ELConnectionTester();
	private final QueryRecorder qr = new QueryRecorder();
	private int nextEdgeLabel = 1;
	private ELGraph<LGVertex> wholeQuery;
	private ELGraph<SVertex> seed = null; // the newest edge
	private Map<Integer, Integer> qIds;

	public BuildQuery(QueryEngineInterface qe) {
		this.qe = qe;
		formQuery();
	}

	/**
	 * record the nodes added on the Query panel
	 */
	@Override
	public void recordNode(int id, int label) {
		checkArgument(!qns.containsKey(id));
		qr.recordNode(id, label);
		QueryNode node = new QueryNode(id, label);
		qns.put(id, node);
	}

	/**
	 * record the edges added on the panel
	 */
	@Override
	public Record recordEdge(QueryEdge edge) {
		checkArgument(!edgeMap.containsKey(edge), "edge already added: %s",
				edge);
		qr.recordEdge(edge);
		edgeMap.put(edge, nextEdgeLabel);
		int i = nextEdgeLabel++;
		logger.info("Edge: " + i);

		formQuery(); // construct the query
		constructSeed(); // construct the new edge as a seed graph

		// invoke the exact query and spindle graph
		long finalTime = qe.executeQuery();

		Record r = new Record(String.valueOf(i), i, edge);
		r.insertRecord(finalTime, qe.getExactSize(), qe.getSimilarSize());

		return r;
	}

	@Override
	public QueryEdge getModifySuggestion() {
		// int suggestedEdge = qe.modifySuggestion();
		// if (suggestedEdge == 0) {
		// return null;
		// }
		logger.info("BuildQuery");
		int id = Collections.max(edgeMap.values());
		QueryEdge sedge = edgeMap.inverse().get(id);
		
		if (sedge == null) {
			logger.info("suggested edgeId" + id);
			logger.info("suggested edge==null");
		} else {
			logger.info("suggested edgeId" + id);
		}
		return sedge;
	}

	@Override
	public void deleteNode(int id) {
		qns.remove(id);
	}

	@Override
	public QueryBuilder getQueryBuilder() {
		return this;
	}

	@Override
	public boolean testConnection() {
		return connectionTester.isConnected(wholeQuery, ImmutableSet.of());
	}

	@Override
	public int getNextEdgeLabel() {
		return nextEdgeLabel;
	}

	private int checkEdge(Integer edgeId, QueryEdge edge) {
		checkArgument(edgeId != null, "edge not exists: %s", edge);
		return edgeId;
	}

	@Nonnull
	@Override
	public Record deleteEdge(@Nonnull QueryEdge edge) {
		checkNotNull(edge);
		qr.deleteEdge(edge);
		int edgeId = checkEdge(edgeMap.remove(edge), edge);

		formQuery(); // reconstruct the new query
		Timer eqTimer = new Timer("queryModification");
		// modification based on spindle shaped graph
		qe.queryModification(edgeId);

		logger.info(eqTimer.time());
		long finalTime = eqTimer.getMsTime();

		Record r = new Record("r" + edgeId, -1, null);
		r.insertRecord(finalTime, qe.getExactSize(), qe.getSimilarSize());

		return r;
	}

	// formulate the new query graph
	private void formQuery() {
		Set<Integer> qnSet = edgeMap.keySet().stream()
				.flatMapToInt(e -> IntStream.of(e.getSrc(), e.getTrg()))
				.boxed().collect(Collectors.toSet());
		qIds = Maps.newHashMap();
		// copy the previous node
		wholeQuery = new ELGraph<>(qnSet.size());

		for (int id : qnSet) {
			QueryNode node = qns.get(id);
			LGVertex v = new LGVertex(node.getLabel());
			wholeQuery.addNode(v);
			qIds.put(id, v.getId());
		}
		edgeMap.forEach((e, i) -> wholeQuery.addEdge(qIds.get(e.getSrc()),
				qIds.get(e.getTrg()), i));
	}

	private SVertex getNode(int id) {
		QueryNode qn = qns.get(id);
		int label = qn.getLabel();
		SVertex v = new SVertex(label, qIds.get(id));
		return v;
	}

	/**
	 * construct the new edge as the seed of SPIG
	 */
	private void constructSeed() {
		int id = Collections.max(edgeMap.values());

		QueryEdge lastEdge = edgeMap.inverse().get(id);
		SVertex v1 = getNode(lastEdge.getSrc());
		SVertex v2 = getNode(lastEdge.getTrg());
		int label1 = v1.getLabel();
		int label2 = v2.getLabel();
		seed = new ELGraph<>(2);
		if (cb.compare(label1, label2) < 0) {
			seed.addNode(v1);
			seed.addNode(v2);
		} else {
			seed.addNode(v2);
			seed.addNode(v1);
		}
		seed.addEdge(0, 1, id); // set label on the edge

		String cam = cb.buildCam(label1, label2);
		seed.setCam(cam);
	}

	@Nonnull
	@Override
	public ELGraph<LGVertex> getWholeQuery() {
		return wholeQuery;
	}

	@Override
	public QueryEngineInterface getQueryEngine() {
		return qe;
	}

	@Override
	public void recordQuery() {
		qr.record();
	}

	/**
	 * return the newest edge
	 */
	@Override
	public ELGraph<SVertex> getSeed() {
		return seed;
	}
}
