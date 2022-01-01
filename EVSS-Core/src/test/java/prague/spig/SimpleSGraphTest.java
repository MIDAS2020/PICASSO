package prague.spig;

import static org.junit.Assert.*;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

public class SimpleSGraphTest {

  @Test
  public void testLeaves() {
    SimpleSGraph sGraph = new SimpleSGraph();
    ArraySet as1 = BitArraySet.of(100);
    ArraySet as2 = BitArraySet.of(200);
    sGraph.add(1, ImmutableList.of());
    sGraph.addEdgeSet(1, as1);
    sGraph.add(2, ImmutableList.of(1));
    sGraph.addEdgeSet(2, as2);
    sGraph.add(3, ImmutableList.of(2));
    sGraph.addEdgeSet(3, as2);
    assertEquals(ImmutableList.of(3), sGraph.getLeaves());
    sGraph.deleteEdge(200);
    assertEquals(ImmutableList.of(1), sGraph.getLeaves());
  }
}