package prague.index;

import com.google.common.collect.ImmutableList;

import java.util.List;

import prague.data.DataParams;
import wch.guava2.collect.Immutables;

/**
 * AutoBuild
 *
 * Created by ch.wang on 26 May 2014.
 */
public class AutoBuild {

  public static void main(String[] args) {
    ImmutableList<DataParams> dps = Immutables.buildImmutableList(
        builder -> ImmutableList.of(10, 20, 50, 100, 200)
            .forEach(s -> builder.add(new DataParams("Syn", s, "0.1", 8, 2)))
    );
    AutoBuild ab = new AutoBuild();
    ab.build(dps);
  }

  public void build(List<DataParams> dps) {
    dps.forEach(dp -> {
      PragueIndex pi = new PragueIndex(dp, null);
      IndexBuilder indexBuilder = new IndexBuilder(pi);
      try {
        indexBuilder.prepareIndex();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

  }
}
