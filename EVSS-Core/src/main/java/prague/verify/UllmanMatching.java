/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prague.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import prague.graph.Graph;
import prague.graph.Vertex;
import prague.graph.lg.ELGraph;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;

/**
 * This object uses Ullman subgraph isomorphism algorithm to find all isomorphic subgraph 
 * of a given graph
 * @author kaihuang
 */
public class UllmanMatching {
     /**
     * The set of nodes within the supergraph matching to nodes in the subgraph
     * The nodes are added in backward-order.
     * Notice that only the first match is returned since this implementation realizes
     * the decision version of subgraph isomorphism problem.
     */
    private int[][] nodeset;
    public int[][] getNodeset() {
        return nodeset;
    }
    
    private void createNodeset() {
        if (leafMatches == null) {
            return;
        }
        int count = 0;
        for(VertexMatch leafMatch : leafMatches){
            if(leafMatch.depth+1 == SIZE)
               count++; 
        }
        int[][] nodeset = new int[count][];
        int i = 0;
        for (VertexMatch leafMatch : leafMatches) {
            
            if(leafMatch.depth + 1 < SIZE) continue;
            
            nodeset[i] = new int[leafMatch.depth + 1];
            VertexMatch match = leafMatch;
            for (int j = nodeset[i].length - 1; j >= 0; j--) {
                assert j == match.depth;
                nodeset[i][j] = match.label;
                match = match.parent;
            }
            
            assert match.parent == null;
            i++;
        }
        //added
        Set<List> setList = new HashSet<List>();
        List<Integer> indexList = new ArrayList<Integer>();
        for(int k1=0;k1<nodeset.length;k1++){
            List templist = new ArrayList();
            for(int k2=0;k2<nodeset[k1].length;k2++){
                templist.add(nodeset[k1][k2]);
            }
           Collections.sort(templist);
           if(setList.add(templist)){
               indexList.add(k1);
           }
        }
        this.nodeset = new int[setList.size()][SIZE];
        int index = 0;
        for(int k1=0;k1<nodeset.length;k1++){
            if(indexList.contains(k1)){
                this.nodeset[index] = nodeset[k1];
                index++;
            }
        }
        System.out.println(this.nodeset.toString());
    }
    
    private ArrayList<VertexMatch> leafMatches;
    private boolean flag = false;
    private int  SIZE ;        
    public boolean verify(ELGraph<? extends Vertex>  subgraph,  Graph<Vertex> supergraph) {
        //initialize
        leafMatches = new ArrayList<>();
        int superSize = supergraph.getNodeNum();//node number of graph
        int[] F = new int[superSize];
        Arrays.fill(F, 0);
        int subSize = subgraph.getNodeNum();//node number of query
        SIZE = subSize;
        int[] H = new int[subSize];
        Arrays.fill(H, 0);
        
        int[][] M = new int[subSize][superSize];
        for (int i = 0; i < subSize; i++) {
            for (int j = 0; j < superSize; j++) {
                if (subgraph.getNode(i).getDegree() <= supergraph.getNode(j).getDegree()
                        && subgraph.getNode(i).getLabel() == supergraph.getNode(j).getLabel()) {
                    M[i][j] = 1;
                } else {
                    M[i][j] = 0;
                }
            }
        }
        UllmanAlgo(F, H, subgraph, supergraph, new VertexMatch(-1, null, -1), M);
        createNodeset();
        //return (!leafMatches.isEmpty());
        return flag;
    }

    //the Ullman's algorithm
    private void UllmanAlgo(int[] F, int[] H, ELGraph<? extends Vertex>  subgraph, Graph<Vertex> supergraph, VertexMatch last, int[][] M) {
        int superSize = supergraph.getNodeNum();
        int subSize = subgraph.getNodeNum();
        int depth = last.depth + 1;
        if (depth == subSize) {//check all the nodes of query
           // leafMatches.add(last);
           flag = true;
        } else {
            for (int k = 0; k < superSize; k++) {
                if (F[k] == 0 && refine(subgraph, supergraph, depth, k, M)) {
                    H[depth] = k;
                    F[k] = 1;

                    int[][] M1 = new int[subSize][superSize];
                    for (int n = 0; n < subSize; n++) {
                        System.arraycopy(M[n], 0, M1[n], 0, superSize);
                    }
                    for (int m = 0; m < superSize; m++) {
                        if (m != k) {
                            M[depth][m] = 0;
                        }
                    }
                    VertexMatch cur = new VertexMatch(k, last, depth);
                    leafMatches.add(cur);
                    UllmanAlgo(F, H, subgraph, supergraph, cur, M);
                    M = M1;
                    F[k] = 0;
                }
            }//end for
        }//end else
    }

    private Collection<Vertex> getNeighbors(int nid, ELGraph<? extends Vertex> q) {
        //    System.out.append("getNeighbor nid="+nid);
        ArrayList<Vertex> neighbor = new ArrayList<Vertex>(q.getNodeNum());
        for (int i = 0; i < q.getNodeNum(); i++) {
            if (q.getEdge(i, nid) > 0) {
                //      System.out.println("find neighbor="+i);
                neighbor.add(q.getNode(i));
            }
        }
        Collections.sort(neighbor, new NodeComparator());
        return neighbor;
    }

    //refine process
    private boolean refine(ELGraph<? extends Vertex> q, Graph<Vertex>  g, int d, int k, int[][] M) {
        HashSet<Integer> visited = new HashSet<Integer>(g.getNodeNum());

        if (M[d][k] == 1) {
            Collection<Vertex> neighbors = getNeighbors(d, q);
            for (Vertex neighbor : neighbors) {
                int x = neighbor.getId();
                int y;
                for (y = 0; y < g.getNodeNum(); y++) {
                    if (!visited.contains(y) && g.getEdge(y, k) == 1 && M[x][y] == 1) {
                        visited.add(y);
                        break;
                    }
                }//end y
                if (y == g.getNodeNum()) {
                    M[d][k] = 0;
                    //    System.out.println("ret y=nnum");
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
    
    private static class VertexMatch {
        private int label;
        private VertexMatch parent;
        private int depth;

        public VertexMatch(int label, VertexMatch parent, int depth) {
            this.label = label;
            this.parent = parent;
            this.depth = depth;
        }
    }
}
