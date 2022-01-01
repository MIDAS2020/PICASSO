package prague.index.data;

import com.google.common.base.Charsets;
import com.google.common.collect.SetMultimap;
import com.google.common.io.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import prague.graph.Graph;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import wch.guava2.base.Throwables2;

/**
 * File helper
 *
 * Created by ch.wang on 12 Jan 2014.
 */
public class DifIndexAccessor {

  public static void writeFgMap(File file, SetMultimap<String, LinkGraph<LGVertex>> fgMap)
      throws IOException {
    try (BufferedWriter bw = Files.newWriter(file, Charsets.US_ASCII)) {
      for (String cam : fgMap.keySet()) {
        bw.write(cam);
        bw.write(" :");
        fgMap.get(cam).stream().map(Graph::getGraphId).sorted()
            .forEach(fg -> Throwables2.propagate(() -> bw.write(" " + fg)));
        bw.newLine();
      }
    }
  }
}
