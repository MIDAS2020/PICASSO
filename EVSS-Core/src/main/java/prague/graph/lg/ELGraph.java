package prague.graph.lg;

import static com.google.common.base.Preconditions.*;

import java.util.BitSet;

/**
 * ELGraph
 *
 * Created by ch.wang on 13 Jan 2014.
 */
public class ELGraph<V extends LGVertex> extends LinkGraph<V> {

  private final BitSet eLabelSet = new BitSet();

  public ELGraph(int size) {
    super(size);
  }

  @Override
  public void addEdge(int from, int to, int label) {
    checkArgument(!eLabelSet.get(label), "label already exists: %s", label);
    super.addEdge(from, to, label);
    eLabelSet.set(label);
  }

  @Deprecated
  @Override
  public void addEdge(int from, int to) {
    throw new UnsupportedOperationException("not supported");
  }

  public BitSet getEdgeLabelSet() {
    return (BitSet) eLabelSet.clone();
  }

  public boolean containEdgeLabel(int label) {
    return eLabelSet.get(label);
  }

    public void addEdge(int i, int i0, String s) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
