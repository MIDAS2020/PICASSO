/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: InfoChart.java
 *
 * Abstract: Construct charts for the pre query time and current candidate size Current Version: 0.1
 * Author: Jin Changjiu Modified Date: Jun.3,2010
 */
package gblend.result;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;

import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import prague.result.Record;

public class InfoChart extends JFrame {

  private static final Font FONT = new Font("SansSerif", Font.BOLD, 12);
  private static final long serialVersionUID = 1L;

  /**
   */
  public InfoChart() {
    super("Real Time Query Charts");
    RefineryUtilities.centerFrameOnScreen(this);
    setMinimumSize(new Dimension(360, 360));
    setSize(480, 380);
  }

  private static JFreeChart createDataSet(List<Record> dataRecord) {
    DefaultCategoryDataset timeResult = new DefaultCategoryDataset();
    DefaultCategoryDataset candidateResult = new DefaultCategoryDataset();
    String series1 = "Query Time";
    String series2 = "Exact Candidates";
    String series3 = "Similarity Candidates";

    for (Record r : dataRecord) {
      String name = r.getName();
      timeResult.addValue(r.getTime(), series1, name);
      candidateResult.addValue(r.getExactSize(), series2, name);
      if (r.getSimSize() > 0) {
        candidateResult.addValue(r.getSimSize(), series3, name);
      }
    }

    return createChart(timeResult, candidateResult);
  }

  private static CategoryPlot createCandidatePlot(CategoryDataset candidateDataSet,
                                                  ValueAxis candidateAxis) {
    BarRenderer renderer = new BarRenderer();
    renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    // label the points
    renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
    //renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,
    //                                                                TextAnchor.TOP_CENTER));
    renderer.setBaseItemLabelsVisible(true);
    candidateAxis.setUpperMargin(0.2);
    CategoryPlot candidateSubPlot = new CategoryPlot(candidateDataSet, null,
                                                     candidateAxis, renderer);
    candidateSubPlot.setDomainGridlinesVisible(true);
    return candidateSubPlot;
  }

  /**
   * Creates a chart.
   *
   * @return A chart.
   */
  private static JFreeChart createChart(CategoryDataset timeDataSet,
                                        CategoryDataset candidateDataSet) {

    NumberAxis timeAxis = new NumberAxis("Response Time (ms)");
    timeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    LineAndShapeRenderer renderer1 = new LineAndShapeRenderer();
    renderer1.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
    CategoryPlot timeSubPlot = new CategoryPlot(timeDataSet, null, timeAxis, renderer1);
    timeSubPlot.setDomainGridlinesVisible(true);

    NumberAxis candidateAxis = new NumberAxis("Candidates");
    candidateAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    CategoryPlot candidateSubPlot = createCandidatePlot(candidateDataSet, candidateAxis);

    CategoryAxis stepAxis = new CategoryAxis("Query Formulation Steps");
    CombinedCategoryPlot plot = new CombinedCategoryPlot(stepAxis);

    plot.add(timeSubPlot, 2, timeAxis);
    plot.add(candidateSubPlot, 1, candidateAxis);

    JFreeChart result = new JFreeChart("", FONT, plot, true);

    return result;
  }

  /**
   * @return A panel.
   */
  private static JPanel createDataPanel(List<Record> dataRecord) {
    JFreeChart chart = createDataSet(dataRecord);
    return new ChartPanel(chart);
  }

  public void set(List<Record> dataRecord) {
    JPanel chartPanel = createDataPanel(dataRecord);
    chartPanel.setVisible(true);
    setContentPane(chartPanel);
    repaint();
    //setVisible(true);
    setVisible(false);
  }

}
