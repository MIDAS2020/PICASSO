package prague.spig;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.SetMultimap;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import wch.guava2.collect.ManyMany;
import wch.guava2.collect.OneMany;

/**
 * SimpleSGraph
 *
 * Created by ch.wang on 14 Mar 2014.
 */
class SimpleSGraph {

  private static final int ROOT = -1;
  private static final int ROOT_LEVEL = 0;
  private final OneMany<Integer, Integer> levelMap;
  private final ManyMany<Integer> edgeMap;
  private final SetMultimap<Integer, ArraySet> edgeSets;
  private final BitSet voidSet;
  private ImmutableList<Integer> result;
  private boolean modified = true;

  public SimpleSGraph() {
    levelMap = new OneMany<>();
    levelMap.put(ROOT_LEVEL, ROOT);
    edgeMap = new ManyMany<>();
    edgeSets = HashMultimap.create();
    voidSet = new BitSet();
  }

  public SimpleSGraph(SimpleSGraph other) {
    levelMap = new OneMany<>(other.levelMap);
    edgeMap = new ManyMany<>(other.edgeMap);
    edgeSets = HashMultimap.create(other.edgeSets);
    voidSet = (BitSet) other.voidSet.clone();
    result = other.result;
    modified = other.modified;
  }

  private void add(int pid, int id) {
    if (edgeMap.containsEntry(pid, id)) {
      return;
    }
    Integer pLevel = levelMap.getKey(pid);
    checkArgument(pLevel != null, "pid not in graph: %s", pid);
    modified = true;
    edgeMap.put(pid, id);
    int level = pLevel + 1;
    Integer oldLevel = levelMap.getKey(id);
    if (oldLevel == null) {
      levelMap.put(level, id);
    } else {
      checkState(oldLevel == level, "level error");
    }
  }

  public void add(int id, @Nonnull List<Integer> pIds) {
    checkArgument(id > 0);
    checkNotNull(pIds);
    if (pIds.isEmpty()) {
      pIds = ImmutableList.of(ROOT);
    }
    for (int pid : pIds) {
      add(pid, id);
    }
  }

  public void addVoidEdge(int edge) {
    checkArgument(edge > 0);
    voidSet.set(edge);
  }

  public void addEdgeSet(int id, @Nonnull ArraySet arraySet) {
    checkArgument(levelMap.containsValue(id), "id not in graph: %s", id);
    edgeSets.put(id, checkNotNull(arraySet));
  }

  public boolean hasLevel(int level) {
    return levelMap.containsKey(level);
  }

  public ImmutableList<Integer> getLeaves() {
    if (!voidSet.isEmpty()) {
      return ImmutableList.of();
    }
    if (!modified) {
      return result;
    }
    result = ImmutableList.copyOf(edgeMap.getSink());
    modified = false;
    return result;
  }

  private void remove(int id) {
    levelMap.removeValue(id);
    edgeSets.removeAll(id);
    ManyMany.InAndOut<Integer> io = edgeMap.removeAll(id);
    io.getOutSet().forEach(this::remove);
  }

  public void deleteEdge(int edge) {
    checkArgument(edge > 0);
    if (voidSet.get(edge)) {
      voidSet.clear(edge);
      return;
    }
    modified = true;
    Set<Integer> keySet = levelMap.getKeySet();
    for (int level : keySet) {
      if (level == ROOT_LEVEL) {
        continue;
      }
      for (int id : levelMap.getValues(level)) {
        Set<ArraySet> arraySets = edgeSets.get(id);
        if (arraySets.isEmpty()) {
          continue;
        }
        arraySets.removeIf(as -> as.contains(edge));
        if (arraySets.isEmpty()) {
          remove(id);
        }
      }
    }
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .add("nodes", edgeSets.keySet().size())
        .add("edgeSets", edgeSets.size())
        .toString();
  }
}
