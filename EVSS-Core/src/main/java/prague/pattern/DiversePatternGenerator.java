package prague.pattern;

import com.google.common.collect.ImmutableListMultimap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import prague.index.PragueIndex;

/**
 * freq pattern extractor
 *
 * Created by ch.wang on 27 Feb 2014.
 */
public class DiversePatternGenerator implements PatternExtractor {

  private final Logger logger = LogManager.getLogger(this);

  private final int minSize;
  private final int maxSize;
  private final int limit;
  private final TEDPatternGenerator pg;
  private final PragueIndex pi;

  public DiversePatternGenerator(int minSize, int maxSize, int limit,
                              PragueIndex pi, TEDPatternGenerator pg) {
    this.minSize = minSize;
    this.maxSize = maxSize;
    this.limit = limit;
    this.pg = pg;
    this.pi = pi;
  }

  @Override
  public void extractPatterns() throws Exception {
    logger.info("extractTEDPatterns begin");

   // ImmutableListMultimap<Integer, LinkGraph<LGVertex>> freqGraphs = pi.getFreqGraphs();
    
     ImmutableListMultimap<Integer, LinkGraph<LGVertex>> tedGraphs = pi.getTEDGraphs();

    logger.info("extractTEDPatterns end");
    // Variable to control the number of patterns of each size
    for (int edgeNum = minSize; edgeNum <= maxSize; edgeNum++) {
    //  logger.info("edgeNum:" + edgeNum);
    // here limit can be ignored, since limit is max number of total patterns instead of single size
      tedGraphs.get(edgeNum).stream().limit(limit).forEach(pg::registerPattern);
    //  logger.info(edgeNum);
    }
  }
}
