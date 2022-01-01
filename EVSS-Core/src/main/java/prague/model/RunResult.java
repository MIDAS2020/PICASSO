package prague.model;

import java.util.List;

import prague.result.MatchedGraph;

/**
 * RunResult
 *
 * Created by ch.wang on 17 May 2014.
 */
public class RunResult {

  private final List<MatchedGraph> matchedGraphs;
  private final int size;
  private final int sigma;
  private final long srt;

  public RunResult(List<MatchedGraph> matchedGraphs, int size, int sigma, long srt) {
    this.matchedGraphs = matchedGraphs;
    this.size = size;
    this.sigma = sigma;
    this.srt = srt;
  }

  public List<MatchedGraph> getMatchedGraphs() {
    return matchedGraphs;
  }

  public int getSize() {
    return size;
  }

  public int getSigma() {
    return sigma;
  }

  public long getSrt() {
    return srt;
  }
}
