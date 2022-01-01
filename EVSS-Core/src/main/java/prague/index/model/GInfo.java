package prague.index.model;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * storage the graph info
 *
 * Created by ch.wang on 21 Feb 2014.
 */
public class GInfo {

  private static final Joiner.MapJoiner MAP_JOINER = Joiner.on(",").withKeyValueSeparator("=>");
  private static final Splitter.MapSplitter MAP_SPLITTER = Splitter.on(",").omitEmptyStrings()
      .withKeyValueSeparator("=>");
  private static final Pattern connectionPattern = Pattern.compile("C(\\d+)-(\\d+)");
  private static final Pattern newNodePattern = Pattern.compile("N(\\d+)\\((\\d+)\\)");
  private final ImmutableList<Connection> connectionList;
  private final ImmutableTable<Integer, Integer, GTransform> newNodeTable;
  private final ImmutableMap<String, GTransform> operationMap;

  public GInfo(String info) {
    Map<String, String> infoMap = MAP_SPLITTER.split(info);

    ImmutableList.Builder<Connection> connectionBuilder = ImmutableList.builder();
    ImmutableTable.Builder<Integer, Integer, GTransform> newNodeBuilder = ImmutableTable.builder();
    ImmutableMap.Builder<String, GTransform> operationBuilder = ImmutableMap.builder();

    infoMap.forEach((op, t) -> {
      GTransform transform = new GTransform(t);
      operationBuilder.put(op, transform);
      checkState(matchConnection(op, transform, connectionBuilder)
                 || matchNewNode(op, transform, newNodeBuilder));
    });

    connectionList = connectionBuilder.build();
    newNodeTable = newNodeBuilder.build();
    operationMap = operationBuilder.build();
  }

  public static String getInfoString(List<GOperation> gOperations) {
    String info = MAP_JOINER.join(gOperations);
    return info;
  }

  public String getInfoString() {
    String info = MAP_JOINER.join(operationMap);
    return info;
  }

  private boolean matchConnection(String op, GTransform transform,
                                  ImmutableList.Builder<Connection> connectionBuilder) {
    Matcher matcher = connectionPattern.matcher(op);
    if (matcher.matches()) {
      Connection c = new Connection(Integer.parseInt(matcher.group(1)),
                                    Integer.parseInt(matcher.group(2)),
                                    transform);
      connectionBuilder.add(c);
      return true;
    }
    return false;
  }

  private boolean matchNewNode(String op, GTransform transform,
                               ImmutableTable.Builder<Integer, Integer, GTransform> builder) {
    Matcher matcher = newNodePattern.matcher(op);
    if (matcher.matches()) {
      builder.put(Integer.parseInt(matcher.group(1)),
                  Integer.parseInt(matcher.group(2)),
                  transform);
      return true;
    }
    return false;
  }

  public int size() {
    return operationMap.size();
  }

  public ImmutableList<Connection> getConnectionList() {
    return connectionList;
  }

  public ImmutableTable<Integer, Integer, GTransform> getNewNodeTable() {
    return newNodeTable;
  }

  public GTransform doOperation(String operation) {
    return operationMap.get(operation);
  }

  public static class Connection {

    private final int src;
    private final int trg;
    private final GTransform trans;

    public Connection(int src, int trg, GTransform trans) {
      this.src = src;
      this.trg = trg;
      this.trans = trans;
    }

    public int getSrc() {
      return src;
    }

    public int getTrg() {
      return trg;
    }

    public GTransform getTrans() {
      return trans;
    }
  }
}
