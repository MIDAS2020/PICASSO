package prague.query;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import prague.data.DataParams;
import prague.index.PragueIndex;
import prague.model.QueryInterface;
import prague.model.RunResult;
import prague.result.Record;
import prague.spig.SimpleQueryEngine;

/**
 * QueryTest
 *
 * Created by ch.wang on 16 May 2014.
 */
public class QueryTest {

  private static final int RUN_TIMES = 3;
  private final List<TQuery> testQueries = new ArrayList<>();
  private final DataParams dp;
  private PragueIndex pi;
  private QueryInterface qi;
  private final QueryTestProperties qp = new QueryTestProperties();
  private final Map<String, Statistics> sMap = new TreeMap<>();

  private BufferedWriter bufferedWriter;

  public QueryTest() {
    dp = new DataParams("AIDS", 40, "0.1", 8, 3);
  }

  public static void main(String[] args) throws IOException {
    QueryTest qt = new QueryTest();
    qt.loadTests();
    qt.test();
  }

  public void loadTests() throws IOException {
    File dir = new File("query");
    File[] files = dir.listFiles();
    checkState(files != null);
    for (File file : files) {
      TQuery tq = new TQuery(file);
      testQueries.add(tq);
    }
  }

  private QueryEdge getEdge(String[] split) {
    int a = Integer.parseInt(split[1]);
    int b = Integer.parseInt(split[2]);
    return new QueryEdge(a, b);
  }

  public void test() throws IOException {
    for (int i = 0; i < RUN_TIMES; i++) {
      pi = new PragueIndex(dp, qp);
      pi.getDataSet();
      qi = new QueryInterfaceImpl(pi);
      testQueries.forEach(this::runOnce);
    }
    bufferedWriter = Files.newWriter(new File("report.txt"), Charsets.US_ASCII);
    sMap.forEach((tid, s) -> {
      s.doStatistics();
      output(String.format("#%s: (%d) %dms %d", tid, s.getSigma(), s.getSrt(), s.getSize()));
      output(Arrays.toString(s.getTime()));
      output(Arrays.toString(s.getCandidates()));
    });
    bufferedWriter.close();
  }

  private void output(String s) {
    System.out.println(s);
    try {
      bufferedWriter.write(s);
      bufferedWriter.newLine();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void runOnce(TQuery tQuery) {
    String tid = tQuery.getId();
    Statistics statistics = sMap.get(tid);
    if (statistics == null) {
      statistics = new Statistics();
      sMap.put(tid, statistics);
    }
    List<Record> records = new ArrayList<>();
    SimpleQueryEngine sqe = new SimpleQueryEngine(pi);
    BuildQuery bq = new BuildQuery(sqe);
    sqe.setQueryBuilder(bq);
    for (String s : tQuery.getQueries()) {
      String[] split = s.split(" ");
      switch (split[0]) {
        case "n":
          int id = Integer.parseInt(split[1]);
          int label = Integer.parseInt(split[2]);
          bq.recordNode(id, label);
          break;
        case "e":
          records.add(bq.recordEdge(getEdge(split)));
          break;
        case "r":
          records.add(bq.deleteEdge(getEdge(split)));
          break;
        case "run":
          qp.setSimilar(true);
          RunResult result = qi.run(bq);
          statistics.addResult(result);
          break;
      }
    }
    statistics.addRecords(records);
  }
}
