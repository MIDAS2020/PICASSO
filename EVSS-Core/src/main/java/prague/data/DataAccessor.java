package prague.data;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.helpers.Strings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

import prague.graph.ImmutableGraph;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import prague.util.IntegerListHelper;
import wch.guava2.base.Timer;

public class DataAccessor {

  private final Logger logger = LogManager.getLogger(this);
  private final IntegerListHelper ilh = new IntegerListHelper(' ');
  private String strLine;

  private void readEdge(String line, ImmutableGraph.Builder graph) {
    List<Integer> strNode = ilh.splitInteger(line, 1);
    int e1 = strNode.get(0);
    int e2 = strNode.get(1);
    graph.addEdge(e1, e2);
  }

  private ImmutableGraph processGraph(BufferedReader br, int graphId) throws IOException {
    ImmutableGraph.Builder graphBuilder = ImmutableGraph.builder(graphId);
    while ((strLine = br.readLine()) != null) {
      if (strLine.startsWith("v")) {
        List<Integer> nodeLine = ilh.splitInteger(strLine, 2);
        graphBuilder.addNode(nodeLine.get(0));
      } else if (strLine.startsWith("e")) {
        readEdge(strLine, graphBuilder);
      } else if (strLine.startsWith("x")) {
        List<Integer> idSet = ilh.splitInteger(strLine, 1);
        graphBuilder.setIdList(idSet);
      } else if (strLine.startsWith("t")) {
        break;
      } else if (Strings.isEmpty(strLine)) {
        strLine = null;
        break;
      } else {
        throw new RuntimeException("wrong format");
      }
    }
    return graphBuilder.build();
  }

  // The data set is stored by ALGraph structure
  public DataSet readDataSet(File fin) throws IOException {
    logger.info("reading " + fin.getAbsolutePath());
    checkArgument(fin.exists(), "data set file not exists: %s", fin.getCanonicalPath());
    Timer timer = new Timer(fin.getName());
    ImmutableList.Builder<ImmutableGraph> graphsBuilder = ImmutableList.builder();
    try (BufferedReader br = Files.newReader(fin, Charsets.US_ASCII)) {
      int graphId = 0;

      while ((strLine = br.readLine()) != null) {
        if (strLine.startsWith("t #")) {
          do {
            ImmutableGraph graph = processGraph(br, graphId++);
            graphsBuilder.add(graph);
          } while (strLine != null);
        } else {
          checkState(Strings.isEmpty(strLine), "wrong format");
        }
      }
      DataSet dataSet = new DataSet(graphsBuilder.build());
      logger.info(timer.time() + ", size: " + dataSet.getGraphSet().size());
      return dataSet;
    }
  }

  public void appendToDataSet(BufferedWriter bw, LinkGraph<LGVertex> graph, int id)
      throws IOException {
    int vertexNum = graph.getNodeNum();
    bw.write("t # " + id + " " + vertexNum);
    bw.newLine();

    for (int i = 0; i < vertexNum; i++) {
      int nodeLabel = graph.getNode(i).getLabel();
      bw.write("v " + i + " " + nodeLabel);
      bw.newLine();
    }

    for (int i = 0; i < vertexNum; i++) {
      for (int j = i; j < vertexNum; j++) {
        if (graph.getEdge(i, j) == 1) {
          bw.write("e " + i + " " + j + " " + 0);
          bw.newLine();
        }
      }
    }
    bw.newLine();
  }
}
