package prague.index.model;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Splitter;

import java.util.List;

import prague.util.ArrayStringConverter;

/**
 * GTransform
 *
 * Created by ch.wang on 21 Feb 2014.
 */
public class GTransform {

  private static final ArrayStringConverter ASC = new ArrayStringConverter();
  private final int id;
  private final int[] newIds;
  private final String newIdString;

  public GTransform(int id, int[] newIds) {
    this.id = id;
    this.newIds = newIds;
    this.newIdString = ASC.convert(newIds);
  }

  public GTransform(String newIdString) {
    List<String> split = Splitter.on(" ").splitToList(newIdString);
    checkArgument(split.size() == 2, "must split into 2 part");
    this.id = Integer.parseInt(split.get(0));
    this.newIds = ASC.reverse().convert(split.get(1));
    this.newIdString = newIdString;
  }

  public int getId() {
    return id;
  }

  public int[] getNewIds() {
    return newIds;
  }

  public String getNewIdString() {
    return newIdString;
  }

  @Override
  public String toString() {
    return id + " " + newIdString;
  }
}
