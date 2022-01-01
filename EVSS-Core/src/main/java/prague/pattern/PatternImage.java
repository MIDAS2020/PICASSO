package prague.pattern;

import java.io.File;

import javax.annotation.Nonnull;

/**
 * pattern image
 *
 * Created by ch.wang on 03 Mar 2014.
 */
public class PatternImage implements Comparable<PatternImage> {

  private final File file;
  private final int score;
  private final int id;

  PatternImage(File file, int score, int id) {
    this.file = file;
    this.score = score;
    this.id = id;
  }

  @Override
  public int compareTo(@Nonnull PatternImage o) {
    return -(score - o.score);
  }

  public File getFile() {
    return file;
  }

  public int getId() {
    return id;
  }
}
