package prague.query;

import static com.google.common.base.Preconditions.*;

import java.util.List;

import prague.model.RunResult;
import prague.result.Record;

/**
 * Statistics
 *
 * Created by ch.wang on 17 May 2014.
 */
public class Statistics {

  private long srt;
  private int sigma;
  private int size;
  private int n = 0;
  private long[] time;
  private long[] candidates;

  public void addRecords(List<Record> records) {
    int m = records.size();
    if (n++ == 0) {
      time = new long[m];
      candidates = new long[m];
      for (int i = 0; i < m; i++) {
        Record record = records.get(i);
        time[i] = record.getTime();
        candidates[i] = record.getExactSize();
      }
    } else {
      checkState(m == time.length);
      for (int i = 0; i < m; i++) {
        Record record = records.get(i);
        time[i] += record.getTime();
        checkState(candidates[i] == record.getExactSize());
      }
    }
  }

  public void doStatistics() {
    for (int i = 0; i < time.length; i++) {
      time[i] /= n;
    }
    srt /= n;
  }

  public void addResult(RunResult result) {
    if (n == 0) {
      srt = result.getSrt();
      sigma = result.getSigma();
      size = result.getSize();
    } else {
      srt += result.getSrt();
      checkState(sigma == result.getSigma());
      checkState(size == result.getSize());
    }
  }

  public long getSrt() {
    return srt;
  }

  public int getSigma() {
    return sigma;
  }

  public int getSize() {
    return size;
  }

  public long[] getTime() {
    return time;
  }

  public long[] getCandidates() {
    return candidates;
  }
}
