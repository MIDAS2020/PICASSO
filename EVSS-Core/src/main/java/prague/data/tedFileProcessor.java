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
public class tedFileProcessor {

  private final Logger logger = LogManager.getLogger(this);

  private final DataAccessor da = new DataAccessor();
  private final DataPath path;

  private ImmutableList<LinkGraph<LGVertex>> processedTEDGraphs;
  private ImmutableSet<String> camTEDSet;
  private ImmutableListMultimap<Integer, LinkGraph<LGVertex>> tedGraphMap;
  private int maxEdgeSize;

  public tedFileProcessor(DataPath path) {
    this.path = path;
  }

  public ImmutableListMultimap<Integer, LinkGraph<LGVertex>> getTEDGraphs() throws IOException {
    if (tedGraphMap == null) {
      buildTEDGraphs();
    }
    return tedGraphMap;
  }

  public ImmutableList<LinkGraph<LGVertex>> getTEDGraphsAsList() throws IOException {
    getTEDGraphs();
    return processedTEDGraphs;
  }

  public int getMaxTEDFragmentSize() throws IOException {
    getTEDGraphs();
    return maxEdgeSize;
  }

  public ImmutableSet<String> getTEDCamSet() throws IOException {
    getTEDGraphs();
    return camTEDSet;
  }

  private void readTEDCams(List<ImmutableGraph> freqGraphs, File camFile) throws IOException {
    Timer timer = new Timer("read ted cam file");
    List<String> camLines = Files.asCharSource(camFile, Charsets.US_ASCII).readLines();
    logger.info(timer.time());
    checkState(camLines.size() == freqGraphs.size(), "size not matched");
    timer = new Timer("make freq graph");
    TEDGraphBuilder builder = new TEDGraphBuilder();
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

  private void processTEDCam(ImmutableList<ImmutableGraph> tedGraphs) throws IOException {
    
    //File camFile = path.getFrequentSetCam();
    
    File camFile = path.getTEDSetCam();
    
    if (camFile.exists()) {
       readTEDCams(tedGraphs, camFile);
       logger.info("Found ted cam");
      return;
    }
    logger.info("processing ted cam");
    Timer timer = new Timer("processing ted cam");
    List<String> camLines = new ArrayList<>();
    TEDGraphBuilder builder = new TEDGraphBuilder();

    CamBuilderInterface cb = CamBuilderFactory.getCamBuilder();
    for (ImmutableGraph tedGraph : tedGraphs) {
      //logger.info("*****before buildCamWithNewIds****");
     // logger.info("*****before buildCamWithNewIds****"+ freqGraph.getNodeNum());
      int[] newIds = cb.buildCamWithNewIds(tedGraph);
      //logger.info("*****after buildCamWithNewIds****");
      LinkGraph<LGVertex> processedFreqGraph = GraphRelabel.relabel(tedGraph, newIds);
      processedFreqGraph.setCam(tedGraph.getCam());
      processedFreqGraph.copyIdList(tedGraph);
      camLines.add(tedGraph.getCam());
      builder.add(processedFreqGraph);
    }
    builder.build();

    Files.asCharSink(camFile, Charsets.US_ASCII).writeLines(camLines);
    logger.info(timer.time());
  }

  // read the frequent fragments from a file
  private void readTEDs() throws IOException {
    logger.info("readTEDs begin");
    
    //File fin = path.getFrequentSet();
    
    File fin = path.getTEDSet();
    
    ImmutableList<ImmutableGraph> tedGraphs = da.readDataSet(fin).getGraphSet();
    
    processTEDCam(tedGraphs);
    
    
    logger.info("readTEDs end");
  }

  private void buildTEDGraphs() throws IOException {
    readTEDs();
    maxEdgeSize = 0;
    ImmutableListMultimap.Builder<Integer, LinkGraph<LGVertex>> builder;
    builder = ImmutableListMultimap.builder();
    for (LinkGraph<LGVertex> tedGraph : processedTEDGraphs) {
      builder.put(tedGraph.getEdgeNum(), tedGraph);
      maxEdgeSize = Math.max(maxEdgeSize, tedGraph.getEdgeNum());
    }
    logger.info("max ted fragment size: " + maxEdgeSize);
    tedGraphMap = builder.build();
  }

  class TEDGraphBuilder {

    private final ImmutableList.Builder<LinkGraph<LGVertex>> graphBuilder = ImmutableList.builder();
    private final ImmutableSet.Builder<String> camBuilder = ImmutableSet.builder();
    private int nextId = 0;

    public void add(LinkGraph<LGVertex> graph) {
      graph.setGraphId(nextId++);
      graphBuilder.add(graph);
      camBuilder.add(graph.getCam());
    }

    public void build() {
      processedTEDGraphs = graphBuilder.build();
      camTEDSet = camBuilder.build();
    }
  }
}
