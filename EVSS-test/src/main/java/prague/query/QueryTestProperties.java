package prague.query;

import prague.data.QueryProperties;

/**
 * QueryTestProperties
 *
 * Created by ch.wang on 16 May 2014.
 */
public class QueryTestProperties implements QueryProperties {

  private boolean similar;

  @Override
  public boolean isSimilar() {
    return similar;
  }

  public void setSimilar(boolean similar) {
    this.similar = similar;
  }

  @Override
  public boolean usePreLoad() {
    return true;
  }

  @Override
  public boolean useSimilarVerify() {
    return false;
  }
}
