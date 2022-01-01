package gblend.result;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import prague.graph.ImmutableGraph;
import prague.index.PragueIndex;
import prague.result.ChooseResultsInterface;
import prague.result.DotGenerator;

/**
 * ChooseDataSetResults
 *
 * Created by ch.wang on 09 May 2014.
 */
public class ChooseDataSetResults implements ChooseResultsInterface {

  private final PragueIndex pi;

  public ChooseDataSetResults(PragueIndex pi) {
    this.pi = pi;
  }

  @Override
  public File selectedResults(int cbId,List<Integer> missEdges) {
    if (cbId == -1) {
      cbId = 0;
    }
    ImmutableGraph candidateGraph = pi.fetchFromMem(cbId);
    File f = DotGenerator.formatMatchedGraph(candidateGraph, null,missEdges);
    return f;
  }

  @Override
  public List<Integer> getList() {
    int size = pi.getDataSet().getGraphSet().size();
    return IntStream.range(0, size).boxed().collect(Collectors.toList());
  }

    @Override
    public List<Integer> getListSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<File> selectAllmatches(int graphId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void selectedResults2(int cbId, List<Integer> missEdges, List<String> theMissedEdgeLabel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
