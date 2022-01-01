package prague.cam;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import prague.graph.lg.LinkGraphBuilder;

/**
 * Convert cam build by TreeCamBuilder to LinkGraph
 *
 * Created by ch.wang on 22 Jan 2014.
 */
public class TreeCamReader implements CamReaderInterface {

  private static final Pattern NODE_PATTERN = Pattern.compile("\\((\\d+)(?:\\|(.+))?\\)");
  private LinkGraphBuilder<LGVertex> graphBuilder;

  private int dfs(String cam) {
    Matcher matcher = NODE_PATTERN.matcher(cam);
    checkState(matcher.matches(), "bad tree cam: %s", cam);
    int label = Integer.parseInt(matcher.group(1));
    LGVertex vertex = new LGVertex(label);
    int id = graphBuilder.addNode(vertex);
    String children = matcher.group(2);
    if (children != null) {
      for (String child : splitChildren(children)) {
        int cid = dfs(child);
        graphBuilder.addEdge(id, cid);
      }
    }
    return id;
  }

  @Override
  public LinkGraph<LGVertex> readCam(String cam) {
    checkArgument(cam.startsWith("T"), "not a tree cam: %s", cam);
    String cutCam = cam.substring(1);
    graphBuilder = LinkGraphBuilder.create();
    dfs(cutCam);
    LinkGraph<LGVertex> graph = graphBuilder.build();
    graph.setCam(cam);
    return graph;
  }

  private List<String> splitChildren(String children) {
    int leftParenthesis = 0;
    int lastPosition = 0;
    List<String> split = new ArrayList<>();
    for (int i = 0; i < children.length(); i++) {
      switch (children.charAt(i)) {
        case '(':
          leftParenthesis++;
          break;
        case ')':
          leftParenthesis--;
          break;
        case ',':
          if (leftParenthesis == 0) {
            String child = children.substring(lastPosition, i);
            split.add(child);
            lastPosition = i + 1;
          }
          break;
      }
    }
    checkState(leftParenthesis == 0);
    String child = children.substring(lastPosition);
    split.add(child);
    return split;
  }

}
