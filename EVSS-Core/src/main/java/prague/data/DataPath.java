package prague.data;

import java.io.File;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataPath {

    private final Logger logger = LogManager.getLogger(this);
  private final DataParams dp;
  private final File dir;
  private final File dirA;

  public DataPath(DataParams dp) {
    this.dp = dp;
    dir = new File("data/" + dp.getName() + "/" + dp.getDataSize() + "k");
    dirA = new File(dir, dp.getA());
  }

  // public File getDir() {
  // return dir;
  // }

  public File getDataSet() {
    String filename = dp.getName() + dp.getDataSize() + "k";
    File fin = new File(dir, filename);
    return fin;
  }

  public File getIIndex(int i) {
    File fin = new File(dirA, dp.getA() + "Iindex" + i);
    return fin;
  }

  public File getIIndexFgMap(int i) {
    File fin = new File(dirA, "IIndex" + i + ".fg");
    return fin;
  }

  public File getFrequentSet() {
    logger.info("getFrequentSet from : "+dp.getA() + dp.getName() + dp.getDataSize() + "k");
    File fin = new File(dirA, dp.getA() + dp.getName() + dp.getDataSize() + "k");
    return fin;
  }
  
 
  public File getFrequentSetCam() {
    File fin = new File(dirA, dp.getA() + dp.getName() + dp.getDataSize() + "k.cam");
    return fin;
  }
  
   public File getTEDSet() {
    logger.info("getTEDSet from : "+dp.getA() + dp.getName() + dp.getDataSize() + "kTED");
    File fin = new File(dirA, dp.getA() + dp.getName() + dp.getDataSize() + "kTED");
    return fin;
  }

   public File getTEDSetCam() {
    File fin = new File(dirA, dp.getA() + dp.getName() + dp.getDataSize() + "kTED.cam");
    return fin;
  }

  public File getMFIndex() {
    File fin = new File(dirA, dp.getB() + "MFIndex");
    return fin;
  }

  public File getDFIndex() {
    File fin = new File(dirA, dp.getB() + "DFIndex");
    return fin;
  }

  public File getPIndex() {
    File fin = new File(dirA, "PIndex");
    return fin;
  }

  public File getPIndexFgMap(int i) {
    File fin = new File(dirA, "PIndex" + i + ".fg");
    return fin;
  }

  public String getPLog() {
    File fin = new File(dirA, "IndexBuilder");
    try {
      return fin.getCanonicalPath();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public File getPatterns() {
    File fin = new File(dir, "patterns/patterns");
    return fin;
  }
  
  public File getTEDPatterns() {
    File fin = new File(dir, "patterns/patterns");
    return fin;
  }

  public File getPatternsVertexLoc() {
    File fin = new File(dir, "patterns/vertex_locations");
    return fin;
  }
  
   public File getTEDPatternsVertexLoc() {
    File fin = new File(dir, "patterns/vertex_locations");
    return fin;
  }

  public File getPatternsThumbnails() {
    File fin = new File(dir, "patterns/thumbnails");
    return fin;
  }
}
