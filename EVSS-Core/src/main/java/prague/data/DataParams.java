package prague.data;

/**
 * @author cjjin
 */
public class DataParams {

  private final int dataSize;  // DATABASE SIZE
  private final String aString; // Support STRING 
  private final double a; //   SUPPORT INT
  private final int b;  //Memory Size
  private final String dbName;  // DATABASE NAME E.G., AIDS
  private final int s;   // SIGMMA

  public DataParams(String name, int k, String a, int b, int sigma) {
    dbName = name;
    dataSize = k;
    this.aString = a;
    this.a = Double.parseDouble(a);
    this.b = b;
    s = sigma;
  }

  public int getDataSize() {
    return dataSize;
  }

  public String getA() {
    return aString;
  }

  public double getAValue() {
    return a;
  }

  public int getB() {
    return b;
  }

  public String getName() {
    return dbName;
  }

  public int getSigma() {
    return s;
  }

}
