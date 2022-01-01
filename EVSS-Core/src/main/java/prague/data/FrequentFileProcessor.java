package prague.data;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import prague.algorithm.GraphRelabel;
import prague.cam.CamBuilderFactory;
import prague.cam.CamBuilderInterface;
import prague.cam.CamReaderInterface;
import prague.graph.ImmutableGraph;
import prague.graph.lg.LGVertex;
import prague.graph.lg.LinkGraph;
import wch.guava2.base.Timer;

/**
 * CamFileProcessor
 *
 * Created by ch.wang on 03 May 2014.
 */
public class FrequentFileProcessor {

  private final Logger logger = LogManager.getLogger(this);

  private final DataAccessor da = new DataAccessor();
  private final DataPath path;

  private ImmutableList<LinkGraph<LGVertex>> processedFreqGraphs;
  private ImmutableSet<String> camSet;
  private ImmutableListMultimap<Integer, LinkGraph<LGVertex>> freqGraphMap;
  private int maxEdgeSize;

  public FrequentFileProcessor(DataPath path) {
    this.path = path;
  }

  public ImmutableListMultimap<Integer, LinkGraph<LGVertex>> getFreqGraphs() throws IOException {
    if (freqGraphMap == null) {
      buildFreqGraphs();
    }
    return freqGraphMap;
  }

  public ImmutableList<LinkGraph<LGVertex>> getFreqGraphsAsList() throws IOException {
    getFreqGraphs();
    return processedFreqGraphs;
  }

  public int getMaxFreqFragmentSize() throws IOException {
    getFreqGraphs();
    return maxEdgeSize;
  }

  public ImmutableSet<String> getCamSet() throws IOException {
    getFreqGraphs();
    return camSet;
  }

  private void readCams(List<ImmutableGraph> freqGraphs, File camFile) throws IOException {
    Timer timer = new Timer("read cam file");
    List<String> camLines = Files.asCharSource(camFile, Charsets.US_ASCII).readLines();
    logger.info(timer.time());
    checkState(camLines.size() == freqGraphs.size(), "size not matched");
    timer = new Timer("make freq graph");
    FreqGraphBuilder builder = new FreqGraphBuilder();
    CamReaderInterface camReader = CamBuilderFactory.getCamReader();
    for (int i = 0; i < freqGraphs.size(); i++) {
      String cam = camLines.get(i);
      LinkGraph<LGVertex> graph = camReader.readCam(cam);
      graph.copyIdList(freqGraphs.get(i));
      builder.add(graph);
    }
    builder.build();
    logger.info(timer.time());
  }

  private void processCam(ImmutableList<ImmutableGraph> freqGraphs) throws IOException {
    File camFile = path.getFrequentSetCam();
    if (camFile.exists()) {
      readCams(freqGraphs, camFile);
       logger.info("Found freq cam");
      return;
    }
    logger.info("processing freq cam");
    Timer timer = new Timer("processing freq cam");
    List<String> camLines = new ArrayList<>();
    FreqGraphBuilder builder = new FreqGraphBuilder();

    CamBuilderInterface cb = CamBuilderFactory.getCamBuilder();
    for (ImmutableGraph freqGraph : freqGraphs) {
      //logger.info("*****before buildCamWithNewIds****");
     // logger.info("*****before buildCamWithNewIds****"+ freqGraph.getNodeNum());
      int[] newIds = cb.buildCamWithNewIds(freqGraph);
      //logger.info("*****after buildCamWithNewIds****");
      LinkGraph<LGVertex> processedFreqGraph = GraphRelabel.relabel(freqGraph, newIds);
      processedFreqGraph.setCam(freqGraph.getCam());
      processedFreqGraph.copyIdList(freqGraph);
      camLines.add(freqGraph.getCam());
      builder.add(processedFreqGraph);
    }
    builder.build();

    Files.asCharSink(camFile, Charsets.US_ASCII).writeLines(camLines);
    logger.info(timer.time());
  }

  // read the frequent fragments from a file
  private void readFrequents() throws IOException {
    logger.info("readFrequents begin");
    File fin = path.getFrequentSet();
    ImmutableList<ImmutableGraph> freqGraphs = da.readDataSet(fin).getGraphSet();
    processCam(freqGraphs);
    logger.info("readFrequents end");
  }

  private void buildFreqGraphs() throws IOException {
    readFrequents();
    maxEdgeSize = 0;
    ImmutableListMultimap.Builder<Integer, LinkGraph<LGVertex>> builder;
    builder = ImmutableListMultimap.builder();
    for (LinkGraph<LGVertex> freqGraph : processedFreqGraphs) {
      builder.put(freqGraph.getEdgeNum(), freqGraph);
      maxEdgeSize = Math.max(maxEdgeSize, freqGraph.getEdgeNum());
    }
    logger.info("max freq fragment size: " + maxEdgeSize);
    freqGraphMap = builder.build();
  }

  class FreqGraphBuilder {

    private final ImmutableList.Builder<LinkGraph<LGVertex>> graphBuilder = ImmutableList.builder();
    private final ImmutableSet.Builder<String> camBuilder = ImmutableSet.builder();
    private int nextId = 0;

    public void add(LinkGraph<LGVertex> graph) {
      graph.setGraphId(nextId++);
      graphBuilder.add(graph);
      camBuilder.add(graph.getCam());
    }

    public void build() {
      processedFreqGraphs = graphBuilder.build();
      camSet = camBuilder.build();
    }
  }
}
