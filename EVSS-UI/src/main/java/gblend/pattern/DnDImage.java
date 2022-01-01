package gblend.pattern;

import com.google.common.io.Files;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class DnDImage extends ImageIcon {

  private static final int SIZE = 150;

  final int patternId;
  private final File url;

  public DnDImage(String patternType, File url, int patternId) {
    super();
    this.url = url;
    this.patternId = patternId;

    try {
      BufferedImage image = ImageIO.read(url);
      image = resizeImage(image, SIZE, getHeight(patternType));
      setImage(image);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static int getHeight(String patternType) {
    int size = Integer.parseInt(patternType.substring(1));
    return SIZE + 20 * size;
  }

  /*------------------------------Utilities----------------------------------*/
  private BufferedImage resizeImage(BufferedImage image, int width, int height) throws IOException {
    if (image.getWidth() == width && image.getHeight() == height) {
      return image;
    }
    int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
    File resizedFile = getRFile();
    BufferedImage resizedImage;
    if (resizedFile.exists()) {
      resizedImage = ImageIO.read(resizedFile);
    } else {
      resizedImage = new BufferedImage(width, height, type);
      Graphics2D g = resizedImage.createGraphics();
      g.setComposite(AlphaComposite.Src);
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                         RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.drawImage(image, 0, 0, width, height, null);
      g.dispose();

      ImageIO.write(resizedImage, "png", resizedFile);
    }

    return resizedImage;
  }

  private File getRFile() {
    File dir = url.getParentFile();
    String fileName = Files.getNameWithoutExtension(url.getName()) + "r.png";
    return new File(dir, fileName);
  }
}

