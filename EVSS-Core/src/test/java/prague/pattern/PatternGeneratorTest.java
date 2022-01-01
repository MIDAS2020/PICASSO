package prague.pattern;

import org.junit.Test;

import prague.data.DataParams;
import prague.index.PragueIndex;

public class PatternGeneratorTest {

  @Test
  public void test() throws Exception {
    PatternSetInfo info = new PatternSetInfo(3, 0, 0, 1, 1);
    DataParams dp = new DataParams("AIDS", 10, "0.1", 8, 3);
    PragueIndex pi = new PragueIndex(dp, null);
    PatternGenerator pg = new PatternGenerator(info, pi);
    pg.execute();
  }

}
