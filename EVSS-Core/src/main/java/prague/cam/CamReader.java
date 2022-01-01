package prague.cam;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import prague.graph.lg.LinkGraphBuilder;

/**
 * Read cam built by CamBuilder
 *
 * Created by ch.wang on 23 Jan 2014.
 */
public class CamReader implements CamReaderInterface {

  private static final Pattern NODE_PATTERN = Pattern.compile("(\\d*)\\((\\d+)\\)");

  @Override
  public LinkGraph<LGVertex> readCam(String cam) {
    Matcher matcher = NODE_PATTERN.matcher(cam);
    LinkGraphBuilder<LGVertex> graphBuilder = LinkGraphBuilder.create();
    while (matcher.find()) {
      int label = Integer.parseInt(matcher.group(2));
      LGVertex vertex = new LGVertex(label);
      int id = graphBuilder.addNode(vertex);
      String edgeString = matcher.group(1);
      if (edgeString.length() != id) {
        throw new RuntimeException("bad cam format");
      }
      for (int i = 0; i < id; i++) {
        if (edgeString.charAt(i) == '1') {
          graphBuilder.addEdge(i, id);
        }
      }
    }
    LinkGraph<LGVertex> graph = graphBuilder.build();
    graph.setCam(cam);
    return graph;
  }

}
