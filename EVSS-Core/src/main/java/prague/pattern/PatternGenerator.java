package prague.pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import prague.data.DataAccessor;
import prague.data.DataPath;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import prague.index.PragueIndex;
import prague.result.DotGenerator;
import prague.util.FileHelper;
import wch.guava2.base.Timer;

/**
 * @author Andy
 */
public class PatternGenerator {

	private static final File tmpSvgFile = new File("tmp_pattern.svg");
	private final Logger logger = LogManager.getLogger(this);
	private final DataAccessor dataAccessor = new DataAccessor();
	private final PragueIndex pi;
	private final BufferedWriter patternsBw;
	private final BufferedWriter vertexLocBw;
	private final File thumbnailDir;
	private final PatternSetInfo patternSetInfo;
	private final Timer generatePatternImageTimer = Timer
			.createUnstarted("generatePatternImage");
	private final Timer writePatternStructureTimer = Timer
			.createUnstarted("writeStructure");
	private final Timer getVertexLocationTimer = Timer
			.createUnstarted("getVertexLocation");
	private final Timer writeVertexLocationTimer = Timer
			.createUnstarted("writeVertexLocation");
	private int patternId;

	public PatternGenerator(PatternSetInfo patternSetInfo, PragueIndex pi)
			throws IOException {
		logger.info("new PatternGenerator");
		patternId = -1;
		this.patternSetInfo = patternSetInfo;
		this.pi = pi;
		DataPath path = pi.getPath();

		thumbnailDir = path.getPatternsThumbnails();
		FileHelper.emptyDir(thumbnailDir);
		patternsBw = Files.newWriter(path.getPatterns(), Charsets.US_ASCII);
		vertexLocBw = Files.newWriter(path.getPatternsVertexLoc(),
				Charsets.US_ASCII);

		createPatternFolders();
	}

	public void execute() throws Exception {
		Timer timer = new Timer("Pattern generation");
		extractFreqPatterns(patternSetInfo.getMinSize(),
				patternSetInfo.getMaxSize(), patternSetInfo.getFreqNum());

		patternsBw.close();
		vertexLocBw.close();

		logger.info(timer.time());
		logger.info(generatePatternImageTimer.time());
		logger.info(writePatternStructureTimer.time());
		logger.info(getVertexLocationTimer.time());
		logger.info(writeVertexLocationTimer.time());
	}

	private void extractFreqPatterns(int minSize, int maxSize, int limit)
			throws Exception {
		FreqPatternGenerator fpg = new FreqPatternGenerator(minSize, maxSize,
				limit, pi, this);
		fpg.extractPatterns();
	}

	void registerPattern(LinkGraph<LGVertex> pattern) {
		patternId++;

		try {
			logger.info("registerPattern");
			generatePatternImage(pattern, patternId);
			writeStructure(pattern, patternId);
			writeVertexLocation(tmpSvgFile, patternId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void generatePatternImage(LinkGraph<LGVertex> pattern, int patternId)
			throws IOException {
		generatePatternImageTimer.start();

		// 产生dot文件
		DotGenerator.formatPattern(pattern, patternId);
		File imageDir = new File(thumbnailDir, "S" + pattern.getEdgeNum());
		File imageFile = new File(imageDir, Integer.toString(patternId)
				+ ".png");
		Files.createParentDirs(imageFile);
                
		// Windows:
                String path = System.getProperty("user.dir");
		String originalFile = path + "\\temp_pattern.dot";
		 String pngCmd = "cmd /c dot -Tpng " +originalFile +" -o " +
		 imageFile.getCanonicalPath();
		 String svgCmd =
		 "cmd /c dot -Tsvg "+ originalFile +" -o tmp_pattern.svg";

		// MAC by xm:，利用dot转换成图
//		String path = System.getProperty("user.dir");
//		String originalFile = path + "/temp_pattern.dot";
//		logger.info("path:" + path);
//		logger.info("dpath:" + imageFile.getCanonicalPath());

		// imageFile.getCanonicalPath()
//		String[] pngCmd = new String[] {
//				"/bin/bash",
//				"-c",
//				"dot -Tpng " + originalFile + " -o "
//						+ imageFile.getCanonicalPath() };
//		String[] svgCmd = new String[] { "/bin/bash", "-c",
//				"dot -Tsvg " + originalFile + " -o " + "tmp_pattern.svg" };
//		logger.info("cmd:" + path + "/temp_pattern.dot");

		try {
                    logger.info(pngCmd);
			Process pngProcess = Runtime.getRuntime().exec(pngCmd);
                        BufferedReader br = new BufferedReader(new InputStreamReader(pngProcess.getInputStream()));
                        String line;
                        while ( (line=br.readLine()) != null){
                        logger.info("dsdada"+line);}
			pngProcess.waitFor();
                    logger.info(svgCmd);
                    
			logger.info("end png gengerate");
			Process svgProcess = Runtime.getRuntime().exec(svgCmd);
			svgProcess.waitFor();
			logger.info("end svg gengerate:");
			generatePatternImageTimer.stop();
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

	private void writeVertexLocation(File svgFile, int entryOrder)
			throws Exception {
		getVertexLocationTimer.start();
		List<Location> locationList = SvgHelper.getLocations(svgFile);
		getVertexLocationTimer.stop();

		writeVertexLocationTimer.start();
		vertexLocBw.write(entryOrder + ":");
		vertexLocBw.newLine();

		Collections.sort(locationList);
		double cx = 0;
		double cy = 0;

		for (Location loc : locationList) {
			cx += loc.x;
			cy += loc.y;
		}

		cx /= locationList.size();
		cy /= locationList.size();

		for (Location loc : locationList) {
			long dx = Math.round(loc.x - cx);
			vertexLocBw.write(dx + " ");
			long dy = Math.round(loc.y - cy);
			vertexLocBw.write(dy + " ");
		}
		vertexLocBw.newLine();

		writeVertexLocationTimer.stop();
	}

	private void writeStructure(LinkGraph<LGVertex> pattern, int patternId)
			throws IOException {
		writePatternStructureTimer.start();
		dataAccessor.appendToDataSet(patternsBw, pattern, patternId);
		writePatternStructureTimer.stop();
	}

	// Create folders to contain images of generated patterns
	// which are categorized based on number of edges
	private void createPatternFolders() {
		int minimumSize = patternSetInfo.getMinSize();
		int maximumSize = patternSetInfo.getMaxSize();

		for (int i = minimumSize; i <= maximumSize; i++) {
			File subDir = new File(thumbnailDir, "S" + i);
			boolean result = subDir.mkdirs();
		}
	}
}
