package prague.spig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import prague.data.QueryProperties;
import prague.index.PragueIndex;
import prague.model.QueryBuilder;
import prague.model.QueryEngineInterface;
import wch.guava2.base.Timer;

/**
 * AbstractQueryEngine
 *
 * Created by ch.wang on 05 May 2014.
 */
public abstract class AbstractQueryEngine implements QueryEngineInterface {

  protected final Logger logger = LogManager.getLogger(this);

  protected final PragueIndex pi;
  protected final QueryProperties qp;
  protected QueryBuilder qb;
  protected int exactSize;
  protected int similarSize;

  protected AbstractQueryEngine(PragueIndex pi) {
    this.pi = pi;
    qp = pi.getQP();
  }

  @Override
  public PragueIndex getPi() {
    return pi;
  }

  public void setQueryBuilder(QueryBuilder qb) {
    this.qb = qb;
  }

  protected abstract void executeExact();

  protected void executeSimilar() {
  }

  // the main exact query algorithm
  @Override
  public final long executeQuery() {
    boolean isExact = !qp.isSimilar();
    logger.info("isExact = " + isExact);
    Timer eqTimer = new Timer("executeExact");
    // invoke the exact query and spindle graph
    executeExact();
    logger.info("Query type = " + getQueryType());
    logger.info("Candidate Size = " + exactSize);
    // building
    logger.info(eqTimer.time());
    long finalTime = eqTimer.getMsTime();

    similarSize = 0;
    if (!isExact) { // start the similarity search
      eqTimer = new Timer("executeSimilar");
      executeSimilar();
      logger.info(eqTimer.time());
      finalTime += eqTimer.getMsTime();
    }
    return finalTime;
  }

  @Override
  public int getExactSize() {
    return exactSize;
  }

  @Override
  public int getSimilarSize() {
    return similarSize;
  }

}
