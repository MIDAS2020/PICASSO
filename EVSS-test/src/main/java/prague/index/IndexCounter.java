package prague.index;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;

import java.sql.SQLException;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import prague.data.DataParams;
import prague.index.data.PIndexDataItem;
import prague.index.data.PItemType;
import prague.index.model.GInfo;
import prague.index.repository.PIndexRepository;
import prague.util.IntegerListHelper;

/**
 * IndexCounter
 *
 * Created by ch.wang on 17 May 2014.
 */
public class IndexCounter {

  private final IntegerListHelper ilh = new IntegerListHelper(',');
  private final TreeMap<SizeAndType, Counts> map = new TreeMap<>();
  private final TreeMap<PItemType, Counts> typeMap = new TreeMap<>();
  private int delSize;

  public IndexCounter() throws SQLException {
    DataParams dp = new DataParams("AIDS", 40, "0.1", 8, 2);
    PragueIndex pi = new PragueIndex(dp, null);
    PIndexRepository pir = new PIndexRepository(pi.getPath().getPIndex());
    ImmutableMap<Integer, PIndexDataItem> itemMap = pir.getPiAccessor().selectM(99);
    System.out.println("Total: " + itemMap.size());
    int byteSize = itemMap.values().stream().mapToInt(this::count).sum();
    System.out.println("Total size: " + getM(byteSize));
    System.out.println("DelIds size: " + getM(delSize));
    map.forEach((st, c) -> System.out.println(st + "\t" + c));
    typeMap.forEach((st, c) -> System.out.println(st + "\t" + c));
  }

  public static void main(String[] args) throws SQLException {
    IndexCounter qt = new IndexCounter();
  }

  private static String getM(int b) {
    return String.format("%.1f", b / 1024.0 / 1024.0);
  }

  private int count(PIndexDataItem item) {
    int byteSize = 0;
    byteSize += 4;                                   // int id;
    byteSize += item.getCam().length();              // String cam;
    byteSize += 1;                                   // String type;
    byteSize += 4;                                   // int size;
    GInfo info = item.getInfo();
    int infoSize = 0;
    if (info != null) {
      byteSize += info.getInfoString().length();     // GInfo info;
      infoSize = info.size();
    }
    byteSize += ilh.join(item.getParent()).length(); // List<Integer> parent;
    String join = ilh.join(Ints.asList(item.getDelIds()));
    byteSize += join.length();                       // int[] delIds;
    delSize += join.length();
    Counts c = new Counts(byteSize, infoSize);
    map.merge(new SizeAndType(item.getSize(), item.getType()), c, (a, b) -> a.merge(b));
    typeMap.merge(item.getType(), c, (a, b) -> a.merge(b));
    return byteSize;
  }

  public static class SizeAndType implements Comparable<SizeAndType> {

    private final int size;
    private final PItemType type;

    public SizeAndType(int size, PItemType type) {
      this.size = size;
      this.type = type;
    }

    @Override
    public String toString() {
      return size + type.toString();
    }

    @Override
    public int compareTo(@Nonnull SizeAndType o) {
      return ComparisonChain.start().compare(size, o.size).compare(o.type, type).result();
    }
  }

  private static class Counts {

    private final int byteSize;
    private final int infoSize;
    private int size = 1;
    private int minInfoSize;
    private int maxInfoSize;

    private Counts(int byteSize, int infoSize) {
      this.byteSize = byteSize;
      this.infoSize = infoSize;
      minInfoSize = maxInfoSize = infoSize;
    }

    public Counts merge(Counts o) {
      if (o != null) {
        Counts c = new Counts(byteSize + o.byteSize, infoSize + o.infoSize);
        c.size = size + o.size;
        c.minInfoSize = Math.min(minInfoSize, o.minInfoSize);
        c.maxInfoSize = Math.max(maxInfoSize, o.maxInfoSize);
        return c;
      }
      return this;
    }

    @Override
    public String toString() {
      return String.format("%d\t%s\t%d-%d\t%.1f", size, getM(byteSize),
                           minInfoSize, maxInfoSize, (double) infoSize / size);
    }
  }
}
