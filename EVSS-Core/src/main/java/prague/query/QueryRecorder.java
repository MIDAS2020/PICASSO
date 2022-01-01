package prague.query;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * QueryRecorder
 *
 * Created by ch.wang on 17 May 2014.
 */
class QueryRecorder {

  private static final File FILE = new File("query_record.txt");
  private List<String> records = new ArrayList<>();

  public void recordNode(int id, int label) {
    records.add(String.format("n %d %d", id, label));
  }

  public void recordEdge(QueryEdge edge) {
    records.add(String.format("e %d %d", edge.getSrc(), edge.getTrg()));
  }

  public void deleteEdge(QueryEdge edge) {
    records.add(String.format("r %d %d", edge.getSrc(), edge.getTrg()));
  }

  public void record() {
    try {
      Files.asCharSink(FILE, Charsets.US_ASCII).writeLines(records);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
