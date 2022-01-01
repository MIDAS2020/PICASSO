/*
 * Copyright 2010, Center for Advanced Information Systems, Nanyang Technological University
 * 
 * File name: ChooseResult.java
 * 
 * Abstract: Construct the selected result graph with its matched subgraph for creating the DOT file
 * 
 * Current Version: 0.1 Author: Jin Changjiu Modified Date: Mar.3,2010
 */
package gblend.result;

/**
 *
 * @author cjjin
 */
import static com.google.common.base.Preconditions.*;
import com.google.common.collect.ImmutableMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import prague.cam.FastCamBuilder;
import prague.graph.ImmutableGraph;
import prague.graph.Vertex;
import prague.graph.lg.ELGraph;
import prague.index.PragueIndex;
import prague.model.QueryEngineInterface;
import prague.result.ChooseResultsInterface;
import prague.result.DotGenerator;
import prague.result.MatchedChecker;
import prague.result.MatchedGraph;
import prague.verify.VF2;
import prague.verify.UllmanMatching;

public class ChooseResults implements ChooseResultsInterface {

    private final Logger logger = LogManager.getLogger(this);

    private final VF2 sv;
    private final List<Integer> idList;
    private final List<Integer> idListSize;
    private final List<Integer> missedges = new ArrayList<Integer>();
    private final List<MatchedGraph> matched;
    private final ELGraph<? extends Vertex> query;
    private final PragueIndex pi;

    public ChooseResults(QueryEngineInterface qe, List<MatchedGraph> matchedGraph,
            ELGraph<? extends Vertex> q) {
        pi = qe.getPi();
        query = q;
        sv = new VF2(query);
        matched = matchedGraph;
        idList = matchedGraph.stream().map(MatchedGraph::getId).sorted().collect(Collectors.toList());
        idListSize = new ArrayList<Integer>();
        for (int i = 0; i < idList.size(); i++) {
            ImmutableGraph candidateGraph = pi.fetchFromMem(idList.get(i));
            boolean flag = idListSize.add(candidateGraph.getEdgeNum());
        }
    }

    // is id in matched set?
    private MatchedGraph inMatchedSet(int id) {
        for (MatchedGraph mg : matched) {
            if (mg.getId() == id) {
                return mg;
            }
        }
        throw new RuntimeException("should in matched");
    }

    private boolean verify(MatchedGraph mg, ImmutableGraph candidateGraph) {
        boolean result = sv.verify(candidateGraph);
        if (result) {
            int[] nodeSet = sv.getNodeSet();
            mg.setNodeSet(nodeSet);
        }
        return result;
    }

    private void getMatchedGraph(MatchedGraph mg) {
        if (mg.getNodeSet() != null) {
            return;
        }
        int id = mg.getId();
        int sigma = mg.getSigma();
        logger.info(String.format("MatchedGraph: %d sigma: %s", id, sigma));
        checkState(sigma == 0);
        ImmutableGraph candidateGraph = pi.fetchFromMem(id);
        if (verify(mg, candidateGraph)) {
            logger.info("getMatchedGraph  matched found");
            return;
        }
        logger.info("getMatchedGraph  no matched found");
        FastCamBuilder cb = new FastCamBuilder();
        logger.error(cb.buildCam(candidateGraph));
        logger.error(cb.buildCam(query));
         logger.info("getMatchedGraph  return");
        throw new RuntimeException("unexpected");
    }

    // select All matches of a graph
    @Override
    public List<File> selectAllmatches(int graphId) {
        List<File> listFiles = new ArrayList<File>();
        if (graphId == -1) {
            graphId = idList.get(0);
        }
        ImmutableGraph candidateGraph = pi.fetchFromMem(graphId); //matched graph
        UllmanMatching ullman = new UllmanMatching();
        if (ullman.verify(query, candidateGraph)) {
            for (int i = 0; i < ullman.getNodeset().length; i++) {
                MatchedGraph mg = inMatchedSet(graphId);
                getMatchedGraph(mg);
                mg.setNodeSet(ullman.getNodeset()[i]);
                MatchedChecker matchedChecker = new MatchedChecker(query, mg);
                String filename = "graph" + String.valueOf(i) + ".dot";
                File f = DotGenerator.formatMatchedGraphWithName(candidateGraph, matchedChecker, null, filename);
                listFiles.add(f);

            }
          //  System.out.println("Matched:" + ullman.getNodeset().length);
        } else {
          //  System.out.println("No Match.");
        }

        return listFiles;
    }

    @Override
    public void selectedResults2(int cbId, List<Integer> missEdges, List<String> theMissedEdgeLabel) {

        if (cbId == -1) {
            cbId = idList.get(0);
        }
        MatchedGraph mg = inMatchedSet(cbId);
        getMatchedGraph(mg);
        MatchedChecker matchedChecker = new MatchedChecker(query, mg);
        // construct the dot file
        ImmutableGraph candidateGraph = pi.fetchFromMem(cbId);// candidateGraph is original  graph before matched
        File f = DotGenerator.formatMatchedGraph2(candidateGraph, matchedChecker, missEdges,theMissedEdgeLabel);
        //for(String str:theMissedEdgeLabel){
        //    System.out.println("str:"+str);
        //}
    }

    // select a result id to review from list
    @Override
    public File selectedResults(int cbId, List<Integer> missEdges) {

        if (cbId == -1) {
            cbId = idList.get(0);
        }
        MatchedGraph mg = inMatchedSet(cbId);
        getMatchedGraph(mg);
        MatchedChecker matchedChecker = new MatchedChecker(query, mg);
        // construct the dot file
        ImmutableGraph candidateGraph = pi.fetchFromMem(cbId);// candidateGraph is original  graph before matched
        /*
        if(missEdges!=null){
            missEdges.add(matchedChecker.getUnUsedEdges().cardinality());
            return null;
        }*/
        ////////////////////////////////////
        /*
        int[] nodeSet = mg.getNodeSet();
        for(int i = 0;i<nodeSet.length;i++){
            System.out.println(i + "to "+ nodeSet[i]);
        }
        System.out.println("ssssssssss"+query.getEdgeLabelSet());
        */
        ////////////////////////////////////
        File f = DotGenerator.formatMatchedGraph(candidateGraph, matchedChecker, missEdges);
        return f;
    }

    @Override
    public List<Integer> getList() {
        return idList;
    }

    @Override
    public List<Integer> getListSize() {
        return idListSize; //To change body of generated methods, choose Tools | Templates.
    }

}
