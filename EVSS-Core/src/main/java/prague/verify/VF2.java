package prague.verify;

import com.google.common.collect.ComparisonChain;
import com.google.common.primitives.Ints;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import prague.graph.Graph;
import prague.graph.Vertex;

/**
 * VF2
 *
 * Created by ch.wang on 08 May 2014.
 */
public class VF2 implements Verifier {

  private static final int NULL_NODE = -1;
  private final Graph<? extends Vertex> query;

  private final int qn;
  private final int[] qo;
  private final int[] qCore;
  private final int[] qT;
  private int gn;
  private int[] go;
  private int[] gCore;
  private int[] gT;

  private Graph<? extends Vertex> graph;

  public VF2(Graph<? extends Vertex> q) {
    this.query = q;
    qo = new NodeCompare(q).getOrder();
    qn = q.getNodeNum();
    qCore = new int[qn];
    qT = new int[qn];
  }

  @Override
  public boolean verify(Graph<? extends Vertex> g) {
    this.graph = g;
    go = new NodeCompare(g).getOrder();
    gn = g.getNodeNum();
    gCore = new int[gn];
    gT = new int[gn];
    Arrays.fill(qCore, NULL_NODE);
    Arrays.fill(gCore, NULL_NODE);
    Arrays.fill(qT, Integer.MAX_VALUE);
    Arrays.fill(gT, Integer.MAX_VALUE);
    return match(0);
  }

  private boolean checkNodes(int q, int g) {
    Vertex gNode = graph.getNode(g);
    Vertex qNode = query.getNode(q);
    return gNode.getLabel() == qNode.getLabel() && gNode.getDegree() >= qNode.getDegree();
  }

  private boolean match(int depth) {
    if (depth == qn) {
      return true;
    }
    if (depth == 0) {
      int q = qo[0];
      for (int j = 0; j < gn; j++) {
        int g = go[j];
        if (checkNodes(q, g) && tryP(depth, q, g)) {
          return true;
        }
      }
    } else {
      for (int i = 0; i < qn; i++) {
        int q = qo[i];
        if (qT[q] < depth && qCore[q] == NULL_NODE) {
          return tryQ(depth, q);
        }
      }
    }
    return false;
  }

  private boolean tryQ(int depth, int q) {
    for (int j = 0; j < gn; j++) {
      int g = go[j];
      if (checkNodes(q, g) && gT[g] < depth && gCore[g] == NULL_NODE && tryP(depth, q, g)) {
        return true;
      }
    }
    return false;
  }

  private boolean tryP(int depth, int q, int g) {
    Set<Integer> qIn = query.getIn(q);
    Set<Integer> gIn = graph.getIn(g);

    int ct = 0;
    int cr = 0;

    for (int qm : qIn) {
      if (qCore[qm] != NULL_NODE) {
        if (!gIn.contains(qCore[qm])) {
          return false;
        }
      } else if (qT[qm] < depth) {
        ct++;
      } else {
        cr++;
      }
    }

    for (int gm : gIn) {
      if (gCore[gm] == NULL_NODE) {
        if (gT[gm] < depth) {
          ct--;
        } else {
          cr--;
        }
      }
    }

    return ct <= 0 && ct + cr <= 0 && tryAdd(depth, q, g, qIn, gIn);
  }

  private boolean tryAdd(int depth, int q, int g, Set<Integer> qIn, Set<Integer> gIn) {
    qCore[q] = g;
    gCore[g] = q;
    qIn.stream().filter(qm -> qT[qm] > depth).forEach(qm -> qT[qm] = depth);
    gIn.stream().filter(gm -> gT[gm] > depth).forEach(gm -> gT[gm] = depth);

    if (match(depth + 1)) {
      return true;
    }

    qCore[q] = NULL_NODE;
    gCore[g] = NULL_NODE;
    qIn.stream().filter(qm -> qT[qm] == depth).forEach(qm -> qT[qm] = Integer.MAX_VALUE);
    gIn.stream().filter(gm -> gT[gm] == depth).forEach(gm -> gT[gm] = Integer.MAX_VALUE);
    return false;
  }

  @Override
  public int[] getNodeSet() {
    return qCore;
  }

  private class NodeCompare implements Comparator<Integer> {

    private final Graph<? extends Vertex> g;
    private final int[] order;

    public NodeCompare(Graph<? extends Vertex> g) {
      this.g = g;
      int n = g.getNodeNum();
      order = new int[n];
      for (int i = 0; i < n; i++) {
        order[i] = i;
      }
      Ints.asList(order).sort(this);
    }

    public int[] getOrder() {
      return order;
    }

    @Override
    public int compare(Integer i, Integer j) {
      Vertex e1 = g.getNode(i);
      Vertex e2 = g.getNode(j);
      return ComparisonChain.start()
          .compare(e2.getLabel(), e1.getLabel())
          .compare(e2.getDegree(), e1.getDegree())
          .compare(e1.getId(), e2.getId())
          .result();
    }
  }
}
