package prague.spig;

import java.util.Arrays;

import prague.index.model.GOperation;
import prague.index.model.GTransform;

/**
 * Simple SpiNode
 *
 * Created by ch.wang on 13 Mar 2014.
 */
public class SimpleSN {

  private final int[] qIds;
  private final ArraySet edgeSet;
  private final int graphId;
  private final int[] ids;

  public SimpleSN(int querySize, int[] qIds, ArraySet edgeSet, int graphId) {
    this.qIds = qIds;
    ids = new int[querySize];
    Arrays.fill(ids, -1);
    for (int id = 0; id < qIds.length; id++) {
      ids[qIds[id]] = id;
    }
    this.edgeSet = edgeSet;
    this.graphId = graphId;
  }

  public ArraySet getEdgeLabelSet() {
    return edgeSet;
  }

  public int getNodeNum() {
    return qIds.length;
  }

  public int getQId(int i) {
    return qIds[i];
  }

  public int getId(int j) {
    return ids[j];
  }

  public SimpleSN newNode(int qid, ArraySet newEdgeSet, GTransform transform) {
    int[] newQIds = GOperation.doNewNodeTrans(qIds, qid, transform.getNewIds());
    SimpleSN newSSN = new SimpleSN(ids.length, newQIds, newEdgeSet, transform.getId());
    return newSSN;
  }

  public SimpleSN connectNodes(ArraySet newEdgeSet, GTransform transform) {
    int[] newQIds = GOperation.doConnectionTrans(qIds, transform.getNewIds());
    SimpleSN newSSN = new SimpleSN(ids.length, newQIds, newEdgeSet, transform.getId());
    return newSSN;
  }

  public int getGraphId() {
    return graphId;
  }

}
