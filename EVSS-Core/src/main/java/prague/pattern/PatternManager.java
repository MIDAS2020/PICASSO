package prague.pattern;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import prague.data.DataAccessor;
import prague.data.DataPath;
import prague.graph.ImmutableGraph;
import wch.guava2.collectors.Collectors2;

/**
 * @author Andy
 */
public class PatternManager {

	private final Logger logger = LogManager.getLogger(this);

	private final DataPath path;
	private ArrayList<Pattern> patternList;
	private Map<String, List<PatternImage>> thumbnailTables;

	public PatternManager(DataPath path) {
		this.path = path;
	}

	public List<PatternImage> getImages(String patternType) {
		return thumbnailTables.get(patternType);
	}

	public boolean isLoaded(String patternType) {
		return thumbnailTables.containsKey(patternType);
	}

	public List<String> getPatternTypes() {
		return thumbnailTables.keySet().stream()
				.mapToInt(k -> Integer.parseInt(k.substring(1))).sorted()
				.mapToObj(k -> "S" + k).collect(Collectors2.toImmutableList());
	}

	/*
	 * Load structural information of the patterns including vertices' locations
	 * of each pattern
	 */
	public void preparePatterns() {
		try {
			loadPatterns();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		loadThumbnails();
	}

	private void loadPatterns() throws IOException {
		patternList = new ArrayList<>();

		DataAccessor da = new DataAccessor();
		File patternFile = path.getPatterns();
		if (!patternFile.exists()) {
			return;
		}
		List<ImmutableGraph> patternSet = da.readDataSet(patternFile)
				.getGraphSet();
		logger.info(patternSet.size() + " patterns loaded");

		File vertexLocFile = path.getPatternsVertexLoc();
		try (BufferedReader br = Files.newReader(vertexLocFile,
				Charsets.US_ASCII)) {
			String strLine;
			while ((strLine = br.readLine()) != null) {
				strLine = strLine.trim();
				int idx = Integer.parseInt(strLine.substring(0,
						strLine.length() - 1));
				strLine = br.readLine();
				String[] numbers = strLine.split("\\s");

				Pattern pattern = new Pattern(patternSet.get(idx));
				for (int i = 0; 2 * i < numbers.length; i++) {
					pattern.x[i] = Integer.parseInt(numbers[2 * i]);
					pattern.y[i] = Integer.parseInt(numbers[2 * i + 1]);
				}
				patternList.add(pattern);
			}
		}
	}

	private void loadThumbnails() {
		thumbnailTables = Maps.newHashMap();
		File thumbnailsDir = path.getPatternsThumbnails();
		File[] files = thumbnailsDir.listFiles();
		if (files == null) {
			return;
		}
		int count = 0;
		for (File subDir : files) {
			if (subDir.isDirectory()) {
				List<PatternImage> patternImageList = loadImagesFromFolder(subDir);
				thumbnailTables.put(subDir.getName(), patternImageList);
				count += patternImageList.size();
			}
		}

		logger.info(count + " pattern images loaded");
	}

	private List<PatternImage> loadImagesFromFolder(File dirPath) {
		logger.info("dirPath:" + dirPath);
		checkState(dirPath.isDirectory());
		File[] fileList = dirPath.listFiles();
		checkState(fileList != null, "dir not found");

		List<PatternImage> patternImages = new ArrayList<>();
		for (File file : fileList) {
			String fileName = Files.getNameWithoutExtension(file.getName());
			if (!fileName.endsWith("r") && !fileName.equals("")
					&& !fileName.equals(" ")) {
				int patId = Integer.parseInt(fileName);
				PatternImage pi = new PatternImage(file, 0, patId);
				patternImages.add(pi);
			}
		}
		Collections.sort(patternImages);

		return patternImages;
	}

	public Pattern getPattern(int id) {
		return patternList.get(id);
	}
}
