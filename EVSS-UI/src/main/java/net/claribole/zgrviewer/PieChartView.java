/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.claribole.zgrviewer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author kaihuang
 */
public class PieChartView extends ApplicationFrame    
                                implements ChartMouseListener{
    public PieChartView(String title) {
        super(title);
          // create a dataset...   
        DefaultPieDataset data = new DefaultPieDataset();   
        data.setValue("Java", new Double(43.2));   
        data.setValue("Visual Basic", new Double(8.0));   
        data.setValue("C/C++", new Double(17.5));   
   
        // create the chart...   
        JFreeChart chart = ChartFactory.createPieChart(   
            "Pie Chart Demo 1",  // chart title   
            data,                // data   
            true,                // include legend   
            true,   
            false   
        );   
        // add the chart to a panel...   
        ChartPanel chartPanel = new ChartPanel(chart, false, false, false,    
                false, false);   
        chartPanel.addChartMouseListener(this);   
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));   
        setContentPane(chartPanel);   
    }
    @Override
    public void chartMouseClicked(ChartMouseEvent event) {
          ChartEntity entity = event.getEntity();   
        if (entity != null) {   
            System.out.println("Mouse clicked: " + entity.toString());   
        }   
        else {   
            System.out.println("Mouse clicked: null entity.");   
        }      
    }
    @Override
    public void chartMouseMoved(ChartMouseEvent event) {
     int x = event.getTrigger().getX();   
        int y = event.getTrigger().getY();   
        ChartEntity entity = event.getEntity();   
        if (entity != null) {   
            System.out.println(   
                "Mouse moved: " + x + ", " + y + ": " + entity.toString()   
            );   
        }   
        else {   
            System.out.println(   
                "Mouse moved: " + x + ", " + y + ": null entity."   
            );   
        }      
    }
    public  void generatePieChart(String title) {   
        PieChartView demo = new PieChartView(title);   
        demo.pack();   
        RefineryUtilities.centerFrameOnScreen(demo); 
        demo.setDefaultCloseOperation(HIDE_ON_CLOSE);
        demo.setVisible(true);   
    }   
}
