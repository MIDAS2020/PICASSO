package prague.data;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;

import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;

/**
 * use to load graph from file
 *
 * Created by ch.wang on 17 Jan 2014.
 */
public class GraphLoader {

  public static LinkGraph<LGVertex> load(File file) throws IOException {
    try (BufferedReader br = Files.newReader(file, Charsets.US_ASCII)) {
      String line = br.readLine();
      List<String> labels = Splitter.on(" ").splitToList(line);
      LinkGraph<LGVertex> graph = new LinkGraph<>(labels.size());
      for (String label : labels) {
        graph.addNode(new LGVertex(Integer.parseInt(label)));
      }
      while ((line = br.readLine()) != null) {
        List<String> edge = Splitter.on(" ").splitToList(line);
        graph.addEdge(Integer.parseInt(edge.get(0)), Integer.parseInt(edge.get(1)));
      }
      return graph;
    }
  }
}
