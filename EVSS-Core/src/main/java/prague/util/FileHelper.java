package prague.util;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

/**
 * file helper
 *
 * Created by ch.wang on 27 Feb 2014.
 */
public class FileHelper {

  public static void touchEmpty(File file) throws IOException {
    Files.createParentDirs(file);
    Files.write("", file, Charsets.US_ASCII);
  }

  private static void recursiveDelete(File dir) throws IOException {
    File[] files = dir.listFiles();
    if (files == null) {
      return;
    }
    for (File file : files) {
      if (file.isDirectory()) {
        recursiveDelete(file);
      }
      java.nio.file.Files.delete(file.toPath());
    }
  }

  public static void emptyDir(File dir) throws IOException {
    if (!dir.exists()) {
      checkState(dir.mkdirs(), "make dirs fail: %s", dir.getCanonicalPath());
      return;
    }
    recursiveDelete(dir);
  }
}
