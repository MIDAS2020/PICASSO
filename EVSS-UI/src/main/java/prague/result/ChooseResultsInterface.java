package prague.result;

import java.io.File;
import java.util.List;

/**
 * ChooseResultsInterface
 *
 * Created by ch.wang on 15 Jan 2014.
 */
public interface ChooseResultsInterface {

  // select a result id to review from list
  File selectedResults(int cbId,List<Integer> missEdges);
  
  List<Integer> getList();
  
  List<Integer> getListSize();
 
  List<File> selectAllmatches(int graphId);        

  void selectedResults2(int cbId,List<Integer> missEdges, List<String> theMissedEdgeLabel);
}
