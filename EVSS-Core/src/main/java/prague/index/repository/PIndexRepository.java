package prague.index.repository;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import prague.index.data.PIndexDataItem;
import prague.index.jdbc.PIndexAccessor;
import prague.util.IntHelper;
import wch.guava2.base.Timer;

/**
 * p index repository
 *
 * Created by ch.wang on 13 Mar 2014.
 */
public class PIndexRepository {

  private final Logger logger = LogManager.getLogger(this);

  private final PIndexAccessor piAccessor;
  private Map<Integer, PIndexDataItem> itemMap;
  private Map<String, Integer> edgeMap;
  private final LoadingCache<Integer, int[]> delIdsCache;
  private Map<Integer, int[]> delIdsMap;

  public PIndexRepository(File pIndex) {
    try {
      piAccessor = new PIndexAccessor(pIndex);
    } catch (IOException | SQLException e) {
      throw new RuntimeException(e);
    }
    reload();
    delIdsCache = CacheBuilder.newBuilder()
        .maximumSize(100)
        .build(CacheLoader.from(this::selectDelIds));
  }

  public void reload(){
    try {
      Timer timer = new Timer("pre load seed");
      itemMap = piAccessor.selectAll();
      ImmutableMap.Builder<String, Integer> builder = ImmutableMap.builder();
      for (PIndexDataItem item : itemMap.values()) {
        if (item.getSize() == 1) {
          builder.put(item.getCam(), item.getId());
        }
      }
      edgeMap = builder.build();
      logger.info(timer.time());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public PIndexAccessor getPiAccessor() {
    return piAccessor;
  }

  @Nonnull
  public Optional<Integer> getId(@Nonnull String cam) {
    Integer id = edgeMap.get(checkNotNull(cam));
    return Optional.fromNullable(id);
  }

  public PIndexDataItem checkedSelect(int id) {
    try {
      Optional<PIndexDataItem> item = piAccessor.select(id);
      checkArgument(item.isPresent(), "id not found: %s", id);
      PIndexDataItem dataItem = item.get();
      return dataItem;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public Optional<PIndexDataItem> selectCam(String cam) {
    try {
      Optional<PIndexDataItem> item = piAccessor.selectCam(cam);
      return item;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public PIndexDataItem checkedSelectCam(String cam) {
    Optional<PIndexDataItem> item = selectCam(cam);
    checkArgument(item.isPresent(), "cam not found: %s", cam);
    PIndexDataItem dataItem = item.get();
    return dataItem;
  }

  @Nonnull
  public int[] selectDelIds(int id) {
    try {
      return piAccessor.selectDelIds(id);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Nonnull
  public List<Integer> getDelIds(@Nonnull List<Integer> ids) {
    checkNotNull(ids);
    List<int[]> gIdSets = ids.stream().map(this::getDelIds).collect(Collectors.toList());
    List<Integer> gIdResults = IntHelper.mergeIntersect(gIdSets);
    return gIdResults;
  }

  public void preLoad(int maxEdgeNum) {
    if (delIdsMap != null) {
      return;
    }
    try {
      Timer timer = new Timer("pre load " + maxEdgeNum);
      delIdsMap = piAccessor.selectMDelIds(maxEdgeNum);
      logger.info(timer.time() + ", size: " + delIdsMap.size());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public PIndexDataItem getPIndexDataItem(int id) {
    PIndexDataItem item = itemMap.get(id);
    checkState(item != null);
    return item;
  }

  public int[] getDelIds(int id) {
    checkArgument(id > 0);
    if (delIdsMap != null) {
      int[] delIds = delIdsMap.get(id);
      if (delIds != null) {
        return delIds;
      }
    }
    return delIdsCache.getUnchecked(id);
  }

  public void addInfo(int id, String info) {
    try {
      piAccessor.addInfo(id, info);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void addParent(int id, Collection<Integer> parent) {
    try {
      PIndexDataItem dataItem = checkedSelect(id);
      TreeSet<Integer> parentSet = new TreeSet<>(dataItem.getParent());
      parentSet.addAll(parent);
      String parentString = PIndexDataItem.joinParent(parentSet);
      piAccessor.updateParent(id, parentString);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
