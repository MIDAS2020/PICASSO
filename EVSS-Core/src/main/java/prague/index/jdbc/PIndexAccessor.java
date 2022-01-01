package prague.index.jdbc;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import prague.index.data.PIndexDataItem;
import prague.index.data.PItemType;
import prague.index.model.GInfo;
import prague.util.IntegerListHelper;

/**
 * communicate with the database
 *
 * Created by ch.wang on 17 Feb 2014.
 */
public class PIndexAccessor implements AutoCloseable {

  private static final String CREATE_TABLE_SQL = "create table p_index(\n"
                                                 + "  id int generated always as identity primary key,\n"
                                                 + "  cam varchar(500) unique,\n"
                                                 + "  type char(1) not null,\n"
                                                 + "  edge_num int not null,\n"
                                                 + "  info long varchar,\n"
                                                 + "  parent varchar(200) not null default '',\n"
                                                 + "  del_ids clob\n"
                                                 + ")";

  private static final String CREATE_SIZE_INDEX_SQL = "create index i_size on p_index (edge_num)";

  private static final String CREATE_PROPERTIES_SQL = "create table properties(\n"
                                                      + "  id varchar(50) primary key,\n"
                                                      + "  info varchar(50) not null\n"
                                                      + ")";

  private static final String SELECT_CLAUSE = "select id, cam, type, edge_num, info, parent ";
  private static final String SELECT_SQL = SELECT_CLAUSE
                                           + "from p_index where id = ?";
  private static final String SELECT_CAM_SQL = SELECT_CLAUSE
                                               + "from p_index where cam = ?";
  private static final String SELECT_M_SQL = SELECT_CLAUSE + ", del_ids "
                                             + "from p_index where edge_num <= ?";
  private static final String SELECT_ALL_SQL = SELECT_CLAUSE
                                               + "from p_index";

  private static final String SELECT_DEL_IDS_SQL = "select del_ids from p_index where id = ?";
  private static final String SELECT_M_DEL_IDS_SQL = "select id, del_ids from p_index "
                                                     + "where edge_num <= ?";
  private static final String INSERT_WITH_DEL_SQL =
      "insert into p_index (cam, type, edge_num, del_ids)\n"
      + "values (?, ?, ?, ?)";
  private static final String INSERT_SQL = "insert into p_index (cam, type, edge_num)\n"
                                           + "values (?, ?, ?)";
  private static final String SELECT_IDENTITY_SQL = "select IDENTITY_VAL_LOCAL() from p_index";
  private static final String ADD_DEL_IDS_SQL = "update p_index set del_ids = ? where id = ?";
  private static final String ADD_INFO_SQL = "update p_index set info = ? where id = ?";
  private static final String UPDATE_PARENT_SQL = "update p_index set parent = ? where id = ?";

  private static final String SELECT_PROPERTY_SQL = "select info from properties where id = ?";
  private static final String INSERT_PROPERTY_SQL = "insert into properties (id, info)\n"
                                                    + "values (?, ?)";
  private final PreparedStatement selectStmt;
  private final PreparedStatement selectCamStmt;
  private final PreparedStatement selectMStmt;
  private final PreparedStatement selectAllStmt;
  private final PreparedStatement selectDelIdsStmt;
  private final PreparedStatement selectMDelIdsStmt;
  private final PreparedStatement insertWithDelStmt;
  private final PreparedStatement insertStmt;
  private final PreparedStatement selectIdentityStmt;
  private final PreparedStatement addDelIdsStmt;
  private final PreparedStatement addInfoStmt;
  private final PreparedStatement updateParentStmt;

  private final PreparedStatement selectPropertyStmt;
  private final PreparedStatement insertPropertyStmt;

  private final IntegerListHelper ilh = new IntegerListHelper(',');

  private Connection con;

  public PIndexAccessor(File path) throws IOException, SQLException {
    String dirName = path.getCanonicalPath();
    if (!path.exists()) {
      con = DriverManager.getConnection("jdbc:derby:" + dirName + ";create=true");
      Statement stmt = con.createStatement();
      stmt.executeUpdate(CREATE_TABLE_SQL);
      stmt.executeUpdate(CREATE_SIZE_INDEX_SQL);
      stmt.executeUpdate(CREATE_PROPERTIES_SQL);
      stmt.close();
    } else {
      con = DriverManager.getConnection("jdbc:derby:" + dirName);
    }
    selectStmt = con.prepareStatement(SELECT_SQL);
    selectCamStmt = con.prepareStatement(SELECT_CAM_SQL);
    selectMStmt = con.prepareStatement(SELECT_M_SQL);
    selectAllStmt = con.prepareStatement(SELECT_ALL_SQL);
    selectDelIdsStmt = con.prepareStatement(SELECT_DEL_IDS_SQL);
    selectMDelIdsStmt = con.prepareStatement(SELECT_M_DEL_IDS_SQL);
    insertWithDelStmt = con.prepareStatement(INSERT_WITH_DEL_SQL);
    insertStmt = con.prepareStatement(INSERT_SQL);
    selectIdentityStmt = con.prepareStatement(SELECT_IDENTITY_SQL);
    addDelIdsStmt = con.prepareStatement(ADD_DEL_IDS_SQL);
    addInfoStmt = con.prepareStatement(ADD_INFO_SQL);
    updateParentStmt = con.prepareStatement(UPDATE_PARENT_SQL);
    selectPropertyStmt = con.prepareStatement(SELECT_PROPERTY_SQL);
    insertPropertyStmt = con.prepareStatement(INSERT_PROPERTY_SQL);
  }

  private int selectIdentity() throws SQLException {
    ResultSet rs = selectIdentityStmt.executeQuery();
    checkState(rs.next(), "select identity error");
    int id = rs.getInt(1);
    return id;
  }

  public int newItem(String cam, PItemType type, int size, List<Integer> idList)
      throws SQLException {
    insertWithDelStmt.setString(1, cam);
    insertWithDelStmt.setString(2, type.toString());
    insertWithDelStmt.setInt(3, size);
    String idListString = ilh.join(idList);
    insertWithDelStmt.setString(4, idListString);
    int updateCount = insertWithDelStmt.executeUpdate();
    if (updateCount != 1) {
      throw new RuntimeException();
    }
    int id = selectIdentity();
    return id;
  }

  public int newDifItem(String cam, int size) throws SQLException {
    insertStmt.setString(1, cam);
    insertStmt.setString(2, PItemType.D.toString());
    insertStmt.setInt(3, size);
    int updateCount = insertStmt.executeUpdate();
    if (updateCount != 1) {
      throw new RuntimeException();
    }
    int id = selectIdentity();
    return id;
  }

  private PIndexDataItem processItem(ResultSet rs) throws SQLException {
    int id = rs.getInt("id");
    String cam = rs.getString("cam");
    PItemType type = PItemType.valueOf(rs.getString("type"));
    int size = rs.getInt("edge_num");
    String info = rs.getString("info");
    String parent = rs.getString("parent");
    GInfo gInfo = info == null ? null : new GInfo(info);
    PIndexDataItem item = new PIndexDataItem(id, cam, type, size, gInfo, parent);
    return item;
  }

  private Optional<PIndexDataItem> processSelect(ResultSet rs) throws SQLException {
    if (!rs.next()) {
      return Optional.absent();
    }
    return Optional.of(processItem(rs));
  }

  public Optional<PIndexDataItem> select(int id) throws SQLException {
    selectStmt.setInt(1, id);
    ResultSet rs = selectStmt.executeQuery();
    return processSelect(rs);
  }

  public Optional<PIndexDataItem> selectCam(String cam) throws SQLException {
    selectCamStmt.setString(1, cam);
    ResultSet rs = selectCamStmt.executeQuery();
    return processSelect(rs);
  }

  private int[] processDelIds(ResultSet rs) throws SQLException {
    String delString = rs.getString("del_ids");
    int[] delIds = delString == null ? null : ilh.splitIntegerToArray(delString);
    return delIds;
  }

  public int[] selectDelIds(int id) throws SQLException {
    selectDelIdsStmt.setInt(1, id);
    ResultSet rs = selectDelIdsStmt.executeQuery();
    checkArgument(rs.next(), "selectDelIds not found id: %s", id);
    int[] delIds = processDelIds(rs);
    return delIds;
  }

  public ImmutableMap<Integer, int[]> selectMDelIds(int maxEdgeNum) throws SQLException {
    selectMDelIdsStmt.setInt(1, maxEdgeNum);
    ResultSet rs = selectMDelIdsStmt.executeQuery();
    ImmutableMap.Builder<Integer, int[]> builder = ImmutableMap.builder();
    while (rs.next()) {
      int id = rs.getInt("id");
      int[] delIds = processDelIds(rs);
      builder.put(id, delIds);
    }
    return builder.build();
  }

  public ImmutableMap<Integer, PIndexDataItem> selectM(int maxEdgeNum) throws SQLException {
    selectMStmt.setInt(1, maxEdgeNum);
    ResultSet rs = selectMStmt.executeQuery();
    ImmutableMap.Builder<Integer, PIndexDataItem> builder = ImmutableMap.builder();
    while (rs.next()) {
      PIndexDataItem item = processItem(rs);
      int[] delIds = processDelIds(rs);
      item.setDelIds(delIds);
      builder.put(item.getId(), item);
    }
    return builder.build();
  }

  public ImmutableMap<Integer, PIndexDataItem> selectAll() throws SQLException {
    ResultSet rs = selectAllStmt.executeQuery();
    ImmutableMap.Builder<Integer, PIndexDataItem> builder = ImmutableMap.builder();
    while (rs.next()) {
      PIndexDataItem item = processItem(rs);
      builder.put(item.getId(), item);
    }
    return builder.build();
  }

  public void addDelIds(int id, List<Integer> idList) throws SQLException {
    addDelIdsStmt.setInt(2, id);
    String idListString = ilh.join(idList);
    addDelIdsStmt.setString(1, idListString);
    int updateCount = addDelIdsStmt.executeUpdate();
    if (updateCount != 1) {
      throw new RuntimeException();
    }
  }

  public void addInfo(int id, String info) throws SQLException {
    addInfoStmt.setInt(2, id);
    addInfoStmt.setString(1, info);
    int updateCount = addInfoStmt.executeUpdate();
    if (updateCount != 1) {
      throw new RuntimeException();
    }
  }

  public void updateParent(int id, String parent) throws SQLException {
    updateParentStmt.setInt(2, id);
    updateParentStmt.setString(1, parent);
    int updateCount = updateParentStmt.executeUpdate();
    if (updateCount != 1) {
      throw new RuntimeException();
    }
  }

  public String selectProperty(String key) throws SQLException {
    selectPropertyStmt.setString(1, key);
    ResultSet rs = selectPropertyStmt.executeQuery();
    if (!rs.next()) {
      return null;
    }
    return rs.getString("info");
  }

  public void insertProperty(String key, String value) throws SQLException {
    insertPropertyStmt.setString(1, key);
    insertPropertyStmt.setString(2, value);
    int updateCount = insertPropertyStmt.executeUpdate();
    if (updateCount != 1) {
      throw new RuntimeException();
    }
  }

  @Override
  public void close() throws Exception {
    con.close();
  }

}
