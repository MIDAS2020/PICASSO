package prague.index;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;

import prague.cam.CamBuilderFactory;
import prague.cam.CamBuilderInterface;
import prague.graph.lg.Edge;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import prague.index.data.PItemType;
import prague.index.model.GInfo;
import prague.index.model.GOperation;
import prague.index.model.GTransform;
import prague.index.repository.PIndexRepository;
import prague.query.QueryEdge;
import prague.query.QueryGraph;

/**
 * SubgraphChecker
 *
 * Created by ch.wang on 25 May 2014.
 */
class SubgraphChecker {

  private final CamBuilderInterface cb = CamBuilderFactory.getCamBuilder();
  private final PIndexRepository pir;
  private LinkGraph<LGVertex> graph;
  private int id;
  private int[] qIds;

  public SubgraphChecker(PIndexRepository pir) {
    this.pir = pir;
  }

  public boolean check(LinkGraph<LGVertex> graph) {
    this.graph = graph;
    ImmutableList<Edge> edges = graph.getEdges();
    for (int i = 0; i < edges.size(); i++) {
      if (!check(i, edges)) {
        return false;
      }
    }
    return true;
  }

  private boolean check(int x, ImmutableList<Edge> edges) {
    QueryGraph queryGraph = new QueryGraph();
    for (int i = 0; i < edges.size(); i++) {
      if (i != x) {
        Edge edge = edges.get(i);
        queryGraph.newEdge(edge.getSrc(), edge.getTrg());
      }
    }
    List<QueryEdge> list = queryGraph.getList();
    return queryGraph.hasPending() || check(list);
  }

  private void prepareSeed(QueryEdge seed) {
    int src = seed.getSrc();
    int trg = seed.getTrg();
    int label1 = graph.getNode(src).getLabel();
    int label2 = graph.getNode(trg).getLabel();
    String cam = cb.buildCam(label1, label2);
    if (cb.compare(label1, label2) <= 0) {
      qIds = new int[]{src, trg};
    } else {
      qIds = new int[]{trg, src};
    }
    Optional<Integer> oId = pir.getId(cam);
    checkState(oId.isPresent());
    id = oId.get();
  }

  private boolean check(List<QueryEdge> list) {
    int[] ids = new int[graph.getNodeNum()];
    prepareSeed(list.get(0));
    for (int i = 1; i < list.size(); i++) {
      updateIds(ids);
      QueryEdge edge = list.get(i);
      int src = edge.getSrc();
      int trg = edge.getTrg();
      checkState(ids[src] >= 0 || ids[trg] >= 0);
      GInfo info = pir.getPIndexDataItem(id).getInfo();
      if (ids[src] >= 0 && ids[trg] >= 0) {
        connection(info, ids[src], ids[trg]);
      } else {
        if (ids[src] >= 0) {
          newNode(info, ids[src], trg);
        } else {
          newNode(info, ids[trg], src);
        }
      }
      if (id == -1 || pir.getPIndexDataItem(id).getType() != PItemType.F) {
        return false;
      }
    }
    return true;
  }

  private void connection(GInfo info, int id1, int id2) {
    String op = GOperation.connectNodes(id1, id2);
    GTransform transform = info.doOperation(op);
    if (transform == null) {
      id = -1;
      return;
    }
    id = transform.getId();
    qIds = GOperation.doConnectionTrans(qIds, transform.getNewIds());
  }

  private void newNode(GInfo info, int nodeId, int qid) {
    int label = graph.getNode(qid).getLabel();
    String op = GOperation.newNode(nodeId, label);
    GTransform transform = info.doOperation(op);
    if (transform == null) {
      id = -1;
      return;
    }
    id = transform.getId();
    qIds = GOperation.doNewNodeTrans(qIds, qid, transform.getNewIds());
  }

  private void updateIds(int[] ids) {
    Arrays.fill(ids, -1);
    for (int i = 0; i < qIds.length; i++) {
      ids[qIds[i]] = i;
    }
  }
}
