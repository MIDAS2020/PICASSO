package prague.pattern;

/**
 *
 * @author Andy
 */
public class PatternSetInfo {
  private final int freqNum;
  private final int difNum;
  private final int nifNum;

  private final int minSize;
  private final int maxSize;
  private int  numberOfPatterns = 10;

  public PatternSetInfo(int freqNum, int difNum, int nifNum, int minSize, int maxSize) {
    this.freqNum = freqNum;
    this.difNum = difNum;
    this.nifNum = nifNum;

    this.minSize = minSize;
    this.maxSize = maxSize;
  }
  
  public PatternSetInfo(int freqNum, int difNum, int nifNum, int minSize, int maxSize, int  num) {
    this.freqNum = freqNum;
    this.difNum = difNum;
    this.nifNum = nifNum;

    this.minSize = minSize;
    this.maxSize = maxSize;
    this.numberOfPatterns = num;
  }

  public int getMinSize() {
    return minSize;
  }

  public int getMaxSize() {
    return maxSize;
  }

  public int getFreqNum() {
    return freqNum;
  }

  public int getDifNum() {
    return difNum;
  }

  public int getNifNum() {
    return nifNum;
  }
  
   public int getNumberOfPatterns() {
    return numberOfPatterns;
  }
}
