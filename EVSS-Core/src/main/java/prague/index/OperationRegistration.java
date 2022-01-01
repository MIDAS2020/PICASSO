package prague.index;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import prague.index.data.PIndexDataItem;
import prague.index.model.GInfo;
import prague.index.model.GOperation;
import prague.index.model.GTransform;
import prague.index.repository.PIndexRepository;

/**
 * register operation
 *
 * Created by ch.wang on 21 Feb 2014.
 */
class OperationRegistration {

  private final PIndexRepository indexRepository;

  private ImmutableListMultimap.Builder<Integer, GOperation> operationBuilder =
      ImmutableListMultimap.builder();

  private ImmutableSetMultimap.Builder<Integer, Integer> parentBuilder =
      ImmutableSetMultimap.builder();

  private final Map<String, Integer> idMap = Maps.newHashMap();
  private final Set<Integer> freqSet = new HashSet<>();

  public OperationRegistration(PIndexRepository indexRepository) {
    this.indexRepository = indexRepository;
    operationBuilder = operationBuilder.orderValuesBy(Ordering.natural());
    parentBuilder = parentBuilder.orderValuesBy(Ordering.natural());
  }

  public void registerFreq(String fgCam, int id) {
    idMap.put(fgCam, id);
    freqSet.add(id);
  }

  private int getId(String cam) {
    Integer newId = idMap.get(cam);
    if (newId != null) {
      return newId;
    }
    Optional<PIndexDataItem> item = indexRepository.selectCam(cam);
    checkArgument(item.isPresent(), "cam not registered: %s", cam);
    PIndexDataItem dataItem = item.get();
    idMap.put(cam, dataItem.getId());
    return dataItem.getId();
  }

  public void register(String fgCam, String op, String newCam, int[] newIdString) {
    int newId = getId(newCam);
    GTransform transform = new GTransform(newId, newIdString);
    GOperation operation = new GOperation(op, transform);
    Integer fgId = idMap.get(fgCam);
    checkArgument(fgId != null, "cam not registered: %s", fgCam);
    operationBuilder.put(fgId, operation);
    parentBuilder.put(newId, fgId);
  }

  public void buildAndWrite() {
    ImmutableListMultimap<Integer, GOperation> operationMap = operationBuilder.build();
    for (int fgId : freqSet) {
      ImmutableList<GOperation> gOperations = operationMap.get(fgId);
      String infoString = GInfo.getInfoString(gOperations);
      indexRepository.addInfo(fgId, infoString);
    }

    ImmutableSetMultimap<Integer, Integer> parentMap = parentBuilder.build();
    for (int newId : parentMap.keySet()) {
      ImmutableSet<Integer> pis = parentMap.get(newId);
      indexRepository.addParent(newId, pis);
    }
  }
}
