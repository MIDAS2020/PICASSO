package prague.cam;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import prague.graph.Graph;
import prague.graph.Vertex;

/**
 * build cam when it is tree
 *
 * Created by ch.wang on 18 Jan 2014.
 */
public class TreeCamBuilder implements CamBuilderInterface {

  private Graph<? extends Vertex> graph;
  private TreeNode[][] f;

  private TreeNode computeCam(int x, int p) {
    List<TreeNode> subNodes = new ArrayList<>();
    for (int u : graph.getIn(x)) {
      if (u == p) {
        continue;
      }
      subNodes.add(getCam(u, x));
    }
    Collections.sort(subNodes);
    TreeNode node = TreeNode.concat(x, graph.getNode(x).getLabel(), subNodes);
    return node;
  }

  private TreeNode getCam(int x, int p) {
    if (f[x][p] != null) {
      return f[x][p];
    }
    return f[x][p] = computeCam(x, p);
  }

  private TreeNode calculateCam(Graph<? extends Vertex> g) {
    this.graph = g;
    int n = g.getNodeNum();
    f = new TreeNode[n][n];
    List<TreeNode> nodes = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      TreeNode node = computeCam(i, -1);
      nodes.add(node);
    }
    TreeNode result = Collections.min(nodes);
    g.setCam("T" + result.cam);
    return result;
  }

  @Override
  public String buildCam(Graph<? extends Vertex> g) {
    calculateCam(g);
    return g.getCam();
  }

  @Override
  public int[] buildCamWithNewIds(Graph<? extends Vertex> g) {
    TreeNode result = calculateCam(g);
    int n = g.getNodeNum();
    int[] newIds = new int[n];
    List<Integer> nodeIds = result.nodeIds;
    for (int i = 0; i < nodeIds.size(); i++) {
      int id = nodeIds.get(i);
      newIds[id] = i;
    }
    return newIds;
  }

  @Deprecated
  @Override
  public String buildCam(int label1, int label2) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public int compare(int label1, int label2) {
    throw new UnsupportedOperationException();
  }

  private static class TreeNode implements Comparable<TreeNode> {

    public String cam;
    public final List<Integer> nodeIds = new ArrayList<>();

    private TreeNode(int id) {
      nodeIds.add(id);
    }

    @Override
    public int compareTo(@Nonnull TreeNode o) {
      return cam.compareTo(o.cam);
    }

    public static TreeNode concat(int id, int label, List<TreeNode> treeNodes) {
      TreeNode node = new TreeNode(id);

      StringBuilder cam = new StringBuilder();
      cam.append("(");
      cam.append(label);
      if (!treeNodes.isEmpty()) {
        cam.append("|");
        List<String> subCams = new ArrayList<>();
        for (TreeNode subNode : treeNodes) {
          subCams.add(subNode.cam);
          node.nodeIds.addAll(subNode.nodeIds);
        }
        cam.append(Joiner.on(",").join(subCams));
      }
      cam.append(")");
      node.cam = cam.toString();
      return node;
    }
  }
}
