package prague.cam;

import com.google.common.primitives.Ints;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import prague.algorithm.GraphRelabel;
import prague.graph.Graph;
import prague.graph.Vertex;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import prague.nauty.Nauty;

/**
 * NautyCamBuilder
 *
 * Created by ch.wang on 18 Mar 2014.
 */
public class NautyCamBuilder implements CamBuilderInterface {
private final Logger logger = LogManager.getLogger(this);
	private final FastCamBuilder fcb = new FastCamBuilder();

	@Override
	public String buildCam(Graph<? extends Vertex> g) {
		buildCamWithNewIds(g);
		return g.getCam();
	}

	@Override
	public int[] buildCamWithNewIds(Graph<? extends Vertex> g) {
                //logger.info("****NautyCamBuilder in****" + g.getNodeNum());
		Nauty nauty = new Nauty();
                // logger.info("****00000****");
		int n = g.getNodeNum();
               // logger.info("****11111****");
		for (int i = 0; i < n; i++) {
			nauty.addNode(g.getNode(i).getLabel());
			for (int j = i + 1; j < n; j++) {
				if (g.getEdge(i, j) > 0) {
					nauty.addEdge(i, j);
				}
			}
		}
               // logger.info("****222222****");
		int[] oldIds = callNauty(nauty);
		int[] newIds = GraphRelabel.invert(oldIds);
		LinkGraph<LGVertex> relabel = GraphRelabel.relabel(g, newIds);
                //logger.info("****3333333****");
		String cam = fcb.buildCam(relabel);
                //logger.info("****444444444****");
		g.setCam(cam);
                //logger.info("****NautyCamBuilder out****" + g.getNodeNum());
		return newIds;
	}

	private int[] callNauty(Nauty nauty) {
		try {
                      // logger.info("****callNauty1111****");
			List<Integer> canonical = nauty.getCanonical();
                       //  logger.info("****callNauty22222****");
			int[] oldIds = Ints.toArray(canonical);
			return oldIds;
		} catch (Exception e) {
			throw new RuntimeException("call nauty error", e);
		}
	}

	@Override
	public String buildCam(int label1, int label2) {
		int min = Math.min(label1, label2);
		int max = Math.max(label1, label2);
		String cam = "(" + min + ")1(" + max + ")";
		return cam;
	}

	@Override
	public int compare(int label1, int label2) {
		return label1 - label2;
	}
}
