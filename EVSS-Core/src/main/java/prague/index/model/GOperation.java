package prague.index.model;

import com.google.common.base.Preconditions;

import java.util.Map;

import javax.annotation.Nonnull;

/**
 * GOperation
 *
 * Created by ch.wang on 21 Feb 2014.
 */
public class GOperation implements Comparable<GOperation>, Map.Entry<String, GTransform> {

  private final String operation;
  private final GTransform transform;

  public GOperation(String operation, GTransform transform) {
    this.operation = operation;
    this.transform = transform;
  }

  public static String newNode(int connectedTo, int newNodeLabel) {
    String op = String.format("N%d(%d)", connectedTo, newNodeLabel);
    return op;
  }

  public static String connectNodes(int first, int second) {
    int min = Math.min(first, second);
    int max = Math.max(first, second);
    String op = String.format("C%d-%d", min, max);
    return op;
  }

  public static int[] doNewNodeTrans(int[] qIds, int qid, int[] newIds) {
    int n = qIds.length;
    Preconditions.checkArgument(newIds.length == n + 1);
    int[] newQIds = new int[n + 1];
    for (int i = 0; i < n; i++) {
      newQIds[newIds[i]] = qIds[i];
    }
    newQIds[newIds[n]] = qid;
    return newQIds;
  }

  public static int[] doConnectionTrans(int[] qIds, int[] newIds) {
    int n = qIds.length;
    Preconditions.checkArgument(newIds.length == n);
    int[] newQIds = new int[n];
    for (int i = 0; i < n; i++) {
      newQIds[newIds[i]] = qIds[i];
    }
    return newQIds;
  }

  @Override
  public int compareTo(@Nonnull GOperation o) {
    return operation.compareTo(o.operation);
  }

  @Override
  public String getKey() {
    return operation;
  }

  @Override
  public GTransform getValue() {
    return transform;
  }

  @Deprecated
  @Override
  public GTransform setValue(GTransform value) {
    throw new UnsupportedOperationException();
  }
}
