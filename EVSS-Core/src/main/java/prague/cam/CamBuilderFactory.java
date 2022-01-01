package prague.cam;

public class CamBuilderFactory {

  public static CamBuilderInterface getCamBuilder() {
    return new CombineCamBuilder();
  }

  public static CamReaderInterface getCamReader() {
    return new CombineCamReader();
  }
}
