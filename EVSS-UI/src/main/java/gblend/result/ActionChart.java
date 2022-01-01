package gblend.result;

import java.awt.BorderLayout;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Dimension;
import java.awt.Font;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class ActionChart extends JFrame {

  private static final Font FONT = new Font("SansSerif", Font.BOLD, 12);
  private static final long serialVersionUID = 1L;
  private final List<ActionRecord> records = new ArrayList<>();
  private Instant last = null;
  private double queryformulationTime = 0;
  private int    querysteps = 0;

  /**
   */
  public ActionChart() {
    super("Real Time Action Charts");
    setMinimumSize(new Dimension(360, 360));
    setSize(750, 500);
  }

  /**
   * Creates a chart.
   *
   * @return A chart.
   */
  private static JFreeChart createChart(CategoryDataset timeDataSet) {

    NumberAxis timeAxis = new NumberAxis("Interval Time (s)");
    timeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    LineAndShapeRenderer renderer = new LineAndShapeRenderer();
    renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    CategoryAxis stepAxis = new CategoryAxis("Action Steps");
    CategoryPlot plot = new CategoryPlot(timeDataSet, stepAxis, timeAxis, renderer);
    plot.setDomainGridlinesVisible(true);

    JFreeChart result = new JFreeChart("", FONT, plot, true);

    return result;
  }

  private JFreeChart createDataSet() {
    DefaultCategoryDataset timeResult = new DefaultCategoryDataset();
    String series1 = "Interval Time";
    //queryformulationTime = 0;
    
    for (ActionRecord r : records) {
      String name = r.getName();
      double tmptime = r.getInterval() / 1000.0;
      timeResult.addValue(tmptime, series1, name);
      
      //queryformulationTime += tmptime;
    }

    return createChart(timeResult);
  }

  /**
   * @return A panel.
   */
  private JPanel createDataPanel() {
    JFreeChart chart = createDataSet();
    return new ChartPanel(chart);
  }

  public void set(String name) {
    Instant now = Instant.now();
    if (last != null) {
      long interval = Duration.between(last, now).toMillis();
      records.add(new ActionRecord(name, interval));
      queryformulationTime += interval/1000;
      
      JPanel totalpanel = new JPanel();
      JPanel labelpanel = new JPanel();
      
      JLabel label1 = new JLabel();
      String str1 = "Time: " + queryformulationTime + "(s)";
      label1.setText(str1);
      label1.setFont(new Font("times new roman",Font.BOLD,16));
      label1.setMinimumSize(new Dimension(80, 30));
      label1.setMaximumSize(new Dimension(80, 30));
      
      JLabel label2 = new JLabel();
      int step = records.size()+1;
      String str2 = "Steps: " + step;
      label2.setText(str2);
      label2.setFont(new Font("times new roman",Font.BOLD,16));
      label2.setMinimumSize(new Dimension(80, 30));
      label2.setMaximumSize(new Dimension(80, 30));
      
      labelpanel.add(label1);
      labelpanel.add(new JSeparator(SwingConstants.VERTICAL));
      labelpanel.add(Box.createHorizontalStrut(10));
      labelpanel.add(label2);
      totalpanel.add(labelpanel);
      
      
      JPanel chartPanel = createDataPanel();
      chartPanel.setVisible(true);
      
      totalpanel.add(chartPanel);
      
      //setLayout(new BorderLayout());
      setPreferredSize(new Dimension(500, 500));
      setContentPane(totalpanel);
      repaint();
      setVisible(true);
      //setVisible(false);
    }
    last = now;
    
    if(records!=null )
      System.out.println("Number of steps:" + records.size()+1);
  }

  private class ActionRecord {

    private final String name;
    private final double interval;

    private ActionRecord(String name, double interval) {
      this.name = name;
      this.interval = interval;
    }

    public String getName() {
      return name;
    }

    public double getInterval() {
      return interval;
    }
  }
}
