/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.claribole.zgrviewer;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author kaihuang
 */
public class KCoresBarChart extends javax.swing.JFrame {

    private DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    private ChartPanel chartPanel = null;
    private JPanel statisContainer;
    private Vector<List<Integer>> vectorIdList;
    private List<String> list1 = new ArrayList();
    private List<String> list3 = new ArrayList();
    private String fileName = "kcores.txt";
    private String corenessFileName = "coreness.txt";
    private List<String> corenesslist1 = new ArrayList();
    private List<String> corenesslist2 = new ArrayList();
     private List<String> corenesslist3 = new ArrayList();
    /**
     * Creates new form KCoresBarChart
     */
    public KCoresBarChart() {
        initComponents();
    }

    public KCoresBarChart(Vector<List<Integer>> v) {
        initComponents();
        String line = "";
        try {
            list1 = new ArrayList();
            list3 = new ArrayList();
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            line = in.readLine();// first line
            int count = 0;
            while (line != null) {
                String[] temp = line.trim().split("\\s+");
                if (temp.length == 2) {
                    list1.add(temp[0]);
                    //System.out.println(temp[0]);
                } else if (temp.length == 4) {
                    list3.add(temp[0]);
                    //System.out.println(temp[0]);
                } else {

                }
                line = in.readLine();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            corenesslist1 = new ArrayList();
            corenesslist2 = new ArrayList();
            corenesslist3 = new ArrayList();
            BufferedReader in = new BufferedReader(new FileReader(corenessFileName));
            line = in.readLine();// first line
            int count = 0;
            while (line != null) {
                String[] temp = line.trim().split("\\s+");
                if (temp.length == 5) {
                    corenesslist1.add(temp[1]);
                    corenesslist2.add(temp[2]);
                    corenesslist3.add(temp[3]);
                    
                } else {

                }
                line = in.readLine();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        vectorIdList = v;
        createBarChartPane();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new Dimension(550, 550));
        setContentPane(statisContainer);
        
        setTitle("KCores");
        
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/gblend/resources/bar.png")).getImage());
        
        setVisible(true);
    }

    public void createBarChartPane() {
        statisContainer = new JPanel();
        statisContainer.removeAll();
        statisContainer.setLayout(new BoxLayout(statisContainer, BoxLayout.Y_AXIS));
        JPanel selectPanel = new JPanel();
        selectPanel.setMaximumSize(new Dimension(400, 50));
        selectPanel.setMinimumSize(new Dimension(400, 50));
        selectPanel.setPreferredSize(new Dimension(400, 50));
        selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.X_AXIS));
        Vector<String> topList = new Vector<String>();
        topList.addElement("");
        for (int i = 1; i <= vectorIdList.size(); i++) {
            topList.addElement("Timestamp " + String.valueOf(i));
        }
        JLabel toplabel = new JLabel("Select:", JLabel.RIGHT);
        toplabel.setMinimumSize(new Dimension(80, 30));
        toplabel.setMaximumSize(new Dimension(80, 30));
        toplabel.setPreferredSize(new Dimension(80, 30));
        JComboBox topComboBox = new JComboBox<>(topList);
        topComboBox.setBorder(BorderFactory.createEmptyBorder());
        topComboBox.setMinimumSize(new Dimension(80, 30));
        topComboBox.setMaximumSize(new Dimension(80, 30));
        topComboBox.setPreferredSize(new Dimension(80, 30));
        topComboBox.addItemListener(e -> {
            String time1 = (String) topComboBox.getItemAt(topComboBox.getSelectedIndex());
            List<Integer> resultIdList = new ArrayList<Integer>();
            if (time1.length() != 0) {
                String P1 = "Timestamp\\s+(\\d+)";
                Pattern r = Pattern.compile(P1);
                Matcher m = r.matcher(time1);
                if (m.find()) {
                    int index = Integer.parseInt(m.group(1));
                    System.out.println("index:" + index);
                    resultIdList = vectorIdList.get(index - 1);
                }
            }
            int k2size = 0;
            int k3size = 0;
            for (int i = 0; i < resultIdList.size(); i++) {
                if (!list1.contains(resultIdList.get(i))) {
                    k2size++;
                }
                if (list3.contains(resultIdList.get(i))) {
                    k3size++;
                }
            }
            dataset.addValue(k2size, "Number of KCores", "K=" + ((Integer) 2).toString());
            dataset.addValue(k3size, "Number of KCores", "K=" + ((Integer) 3).toString());
            JFreeChart barChart = ChartFactory.createBarChart(
                    "Number of KCores", "KCores", "Number", dataset, PlotOrientation.VERTICAL, true, true, true);

            chartPanel = new ChartPanel(barChart);
            chartPanel.setMaximumSize(new Dimension(400, 400));
            chartPanel.setMinimumSize(new Dimension(400, 400));
            chartPanel.setPreferredSize(new Dimension(400, 400));

            selectPanel.removeAll();
            JLabel idlabel = new JLabel("GraphId:", JLabel.RIGHT);
            idlabel.setMinimumSize(new Dimension(80, 30));
            idlabel.setMaximumSize(new Dimension(80, 30));
            idlabel.setPreferredSize(new Dimension(80, 30));
            Vector<String> idStrList = new Vector<String>();
            idStrList.add("");
            for (int k = 0; k < resultIdList.size(); k++) {
                idStrList.add(String.valueOf(resultIdList.get(k)));
            }
            JComboBox idComboBox = new JComboBox<>(idStrList);
            idComboBox.setBorder(BorderFactory.createEmptyBorder());
            idComboBox.setMinimumSize(new Dimension(110, 30));
            idComboBox.setMaximumSize(new Dimension(110, 30));
            idComboBox.setPreferredSize(new Dimension(110, 30));
            idComboBox.addItemListener(f -> {
                String graphid = (String) idComboBox.getItemAt(idComboBox.getSelectedIndex());
                if (graphid.length() != 0) {
                    //int id =  Integer.parseInt(graphid);
                    DefaultCategoryDataset subdataset = new DefaultCategoryDataset();
                    /*
                    if (list1.contains(graphid)) {
                        subdataset.addValue(0, "Number of Coreness", "Coreness=" + ((Integer) 2).toString());
                    } else {
                        subdataset.addValue(1, "Number of Coreness", "Coreness=" + ((Integer) 2).toString());
                    }
                    if (list3.contains(graphid)) {
                        subdataset.addValue(1, "Number of Coreness", "Coreness=" + ((Integer) 3).toString());
                    } else {
                        subdataset.addValue(0, "Number of Coreness", "Coreness=" + ((Integer) 3).toString());
                    }*/
                    int id = Integer.parseInt(graphid);
                    subdataset.addValue(Integer.parseInt(corenesslist1.get(id)), "Number of Coreness", "Coreness=1");
                    subdataset.addValue(Integer.parseInt(corenesslist2.get(id)), "Number of Coreness", "Coreness=2");
                    subdataset.addValue(Integer.parseInt(corenesslist3.get(id)), "Number of Coreness", "Coreness=3");
                    
                    
                    JFreeChart idBarChart = ChartFactory.createBarChart(
                            "Number of Coreness of Graph" + graphid, "Coreness", "Number", subdataset, PlotOrientation.VERTICAL, true, true, true);
                    chartPanel.removeAll();
                    chartPanel = new ChartPanel(idBarChart);
                    chartPanel.setMaximumSize(new Dimension(400, 400));
                    chartPanel.setMinimumSize(new Dimension(400, 400));
                    chartPanel.setPreferredSize(new Dimension(400, 400));
                    statisContainer.removeAll();
                    statisContainer.add(selectPanel);
                    statisContainer.add(chartPanel);
                    statisContainer.validate();
                    statisContainer.repaint();
                }
            });
            selectPanel.add(toplabel);
            selectPanel.add(topComboBox);
            selectPanel.add(idlabel);
            selectPanel.add(idComboBox);
            selectPanel.validate();
            selectPanel.repaint();

            statisContainer.removeAll();
            statisContainer.add(selectPanel);
            statisContainer.add(chartPanel);
            statisContainer.validate();
            statisContainer.repaint();

        });
        selectPanel.add(toplabel);
        selectPanel.add(topComboBox);
        statisContainer.add(selectPanel);
        statisContainer.validate();
        statisContainer.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(KCoresBarChart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(KCoresBarChart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(KCoresBarChart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(KCoresBarChart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new KCoresBarChart().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
