package prague.result;

import static org.junit.Assert.*;

import com.google.common.io.Files;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import prague.graph.lg.ELGraph;
import prague.graph.lg.LGVertex;

public class DotGeneratorTest {

  @Test
  public void formatMatchedGraphTest() throws URISyntaxException, IOException {
    ELGraph<LGVertex> query = new ELGraph<>(3);
    LGVertex a0 = new LGVertex(0);
    query.addNode(a0);
    LGVertex a1 = new LGVertex(1);
    query.addNode(a1);
    LGVertex a2 = new LGVertex(0);
    query.addNode(a2);
    query.addEdge(0, 1, 1);
    query.setGraphId(0);
    MatchedGraph mg = new MatchedGraph(0, 0, new int[]{0, 1});
    MatchedChecker checker = new MatchedChecker(query, mg);
   // File actualFile = DotGenerator.formatMatchedGraph(query, checker);
   // File exceptedFile = new File(getClass().getResource("/formatMatchedGraphTest.dot").toURI());
    //assertTrue(Files.equal(exceptedFile, actualFile));
  }
}
