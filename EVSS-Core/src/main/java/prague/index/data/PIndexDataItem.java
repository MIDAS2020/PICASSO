package prague.index.data;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import prague.index.model.GInfo;

/**
 * represent index item in the database
 *
 * Created by ch.wang on 17 Feb 2014.
 */
public class PIndexDataItem {

  private static final Joiner joiner = Joiner.on(",");
  private static final Splitter SPLITTER = Splitter.on(",").omitEmptyStrings();

  private final int id;
  private final String cam;
  private final PItemType type;
  private final int size;
  private final GInfo info;
  private final List<Integer> parent;
  private int[] delIds;

  public PIndexDataItem(int id, String cam, PItemType type, int size, GInfo info, String parent) {
    this.id = id;
    this.cam = cam;
    this.type = type;
    this.size = size;
    this.info = info;
    this.parent = SPLITTER.splitToList(parent).stream().map(Integer::parseInt)
        .collect(Collectors.toList());
  }

  public static String joinParent(SortedSet<Integer> set) {
    return joiner.join(set);
  }

  public int getId() {
    return id;
  }

  public String getCam() {
    return cam;
  }

  public PItemType getType() {
    return type;
  }

  public int getSize() {
    return size;
  }

  // null and empty are not same, null means not built yet
  public GInfo getInfo() {
    return info;
  }

  public List<Integer> getParent() {
    return parent;
  }

  @Nonnull
  public int[] getDelIds() {
    return checkNotNull(delIds);
  }

  public void setDelIds(@Nonnull int[] delIds) {
    this.delIds = checkNotNull(delIds);
  }

}
