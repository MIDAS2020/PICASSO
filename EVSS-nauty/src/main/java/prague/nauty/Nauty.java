package prague.nauty;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.TreeMultimap;
import com.google.common.io.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.stream.Collectors;


/**
 * Nauty
 *
 * Created by ch.wang on 17 Mar 2014.
 */
public class Nauty {
  private static final String cmd;

  static {
    //File exeFile = copyFile("dreadnaut.exe");
    //copyFile("cygwin1.dll");
    //cmd = exeFile.getAbsolutePath();
    
    //modiifed 
     File exeFile = new File("dreadnaut.exe");
    cmd = exeFile.getAbsolutePath();
  
  
  }

  private static final Joiner NODES_JOINER = Joiner.on(", ");
  private static final Joiner PARTS_JOINER = Joiner.on(" | ");
  private static final Joiner NEIGHBORS_JOINER = Joiner.on(" ");
  private static final Splitter CANONICAL_SPLITTER = Splitter.on(" ").omitEmptyStrings();

  private final TreeMultimap<Integer, Integer> labelMap = TreeMultimap.create();
  private final ListMultimap<Integer, Integer> edgeMap = ArrayListMultimap.create();
  private int size = 0;

  private static File copyFile(String fileName) {
    //System.out.println("copyFile0000" );       
    File exeFile = new File(fileName);
   //  System.out.println("copyFile11111" );  
   try (InputStream file = Nauty.class.getResource("/" + fileName).openStream()) {
   //try (InputStream file = new FileInputStream("C:\\Users\\Kai\\Desktop\\"+fileName)) {
       // System.out.println("copyFile222" );  
       // System.out.println("file path:" +Nauty.class.getResource("/" + fileName).getPath());
       // System.out.println("file name:" +Nauty.class.getResource("/" + fileName).toString());
      Files.asByteSink(exeFile).writeFrom(file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return exeFile;
  }

  public void addNode(int label) {
    labelMap.put(label, size++);
  }

  public void addEdge(int from, int to) {
    edgeMap.put(from, to);
  }

  private void outputF(BufferedWriter bw) throws IOException {
    List<String> parts = new ArrayList<>();
    for (int label : labelMap.keySet()) {
      NavigableSet<Integer> nodes = labelMap.get(label);
      parts.add(NODES_JOINER.join(nodes));
    }
    bw.write("f=[");
    bw.write(PARTS_JOINER.join(parts));
    bw.write("]");
    bw.newLine();
  }

  private void output(OutputStream out) throws IOException {
    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out))) {
      bw.write("n=" + size + " c g");
      bw.newLine();
      for (int i = 0; i < size; i++) {
        List<Integer> neighbors = edgeMap.get(i);
        bw.write(NEIGHBORS_JOINER.join(neighbors));
        bw.write(i == size - 1 ? "." : ";");
        bw.newLine();
      }
      outputF(bw);
      bw.write("x");
      bw.newLine();
      bw.write("> canonical");
      bw.newLine();
      bw.write("b q");
      bw.newLine();
    }
  }

  private void exec() throws IOException, InterruptedException {
    Process exec = Runtime.getRuntime().exec(cmd);
    output(exec.getOutputStream());
    exec.waitFor();
//    InputStreamReader isr = new InputStreamReader(exec.getInputStream());
//    System.out.println(CharStreams.toString(isr));
//    InputStreamReader isrr = new InputStreamReader(exec.getErrorStream());
//    System.out.println(CharStreams.toString(isrr));
  }

  public List<Integer> getCanonical() throws Exception {
    //output(new FileOutputStream("nauty_input"));
    exec();
    String canonical = Files.readFirstLine(new File("canonical"), Charsets.US_ASCII);
    List<String> strings = CANONICAL_SPLITTER.splitToList(canonical);
    List<Integer> nodes = strings.stream().map(Integer::parseInt).collect(Collectors.toList());
    return nodes;
  }
}
