/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.claribole.zgrviewer;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.openide.util.Exceptions;

/**
 *
 * @author kaihuang
 */
public class DegreesLabelsChart extends javax.swing.JFrame {
    private DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    private ChartPanel chartPanel=null;
    private JPanel statisContainer;
    private Vector<List<Integer>> vectorList;
    private Vector<Vector<Integer>> data = new Vector<Vector<Integer>>();
    private Vector<Float> degreeVector= new Vector<Float>();
    private String degreeFileName = "avgdegrees.txt";
    private String labelFileName = "labelsdistribution.txt";
    private String[] vertexLable ={"C", "O", "Cu", "N", "S", "P", "Cl", "Zn", "B", "Br", "Co", "Mn", "As", "Al", "Ni", "Se",
	"Si", "V", "Sn", "I", "F", "Li", "Sb", "Fe", "Pd", "Hg", "Bi", "Na", "Ca", "Ti", "Ho", "Ge",
	"Pt", "Ru", "Rh", "Cr", "Ga", "K", "Ag", "Au", "Tb", "Ir", "Te", "Mg", "Pb", "W", "Cs", "Mo",
	"Re", "Cd", "Os", "Pr", "Nd", "Sm", "Gd", "Yb", "Er", "U", "Tl", "Ac"};
    /**
     * Creates new form DegreesLabelsChart
     */
    public DegreesLabelsChart() {
        initComponents();
    }
     public DegreesLabelsChart( Vector<List<Integer>> v) {
        initComponents();
        readData();
        vectorList = v;
        createBarChartPane();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	setSize(new Dimension(500, 500));
	setContentPane(statisContainer);
        setVisible(true);
    }
       public void readData(){
           BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(labelFileName));
            String   line;
            
        try {
            line = in.readLine(); // first line
              while (line != null) {
                String[] temp = line.trim().split("\\s+");
                if(temp.length !=12) continue;
                Vector<Integer> tempV = new Vector<Integer>();
                for(int k=1;k<12;k++) tempV.add(Integer.parseInt(temp[k]));
                data.add(tempV);
                line = in.readLine();
            }
            in.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
       }
    public void createBarChartPane(){
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(degreeFileName));
            String   line;
        try {
            line = in.readLine(); // first line
              while (line != null) {
                String[] temp = line.trim().split("\\s+");
                if(temp.length !=2) continue;
                int gid = Integer.parseInt(temp[0]);
                float degrees = Float.valueOf(temp[1]);
                degreeVector.add(degrees);
                line = in.readLine();
            }
            in.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        int runtimes = 1;
        for(List<Integer> iter : vectorList){
            float ans = 0.0f;
            for(int i=0;i<iter.size();i++){
                int graphId = iter.get(i);
                ans += degreeVector.elementAt(graphId);
            }
            dataset.addValue(ans/iter.size(), "Average Degrees", String.valueOf(runtimes));
            runtimes++;
        }
        JFreeChart barChart = ChartFactory.createBarChart(
            "Average Degrees","Timestamps","Average Degrees",dataset,PlotOrientation.VERTICAL,true, true, true);
       chartPanel = new ChartPanel(barChart);
       ChartMouseListener l = new ChartMouseListener() {
        @Override
        public void chartMouseMoved(ChartMouseEvent e) {
            ChartEntity entity = e.getEntity();  
            if (entity != null) {   
      
                System.out.println("Mouse clicked: " + entity.toString());   
            }   
            else {   
                System.out.println("Mouse clicked: null entity.");   
            }   
        }
        @Override
        public void chartMouseClicked(ChartMouseEvent e) {
            ChartEntity entity = e.getEntity();  
             if (entity != null) {   
               // new PieChartView(String.valueOf(runtimes)).generatePieChart(String.valueOf(runtimes)) ;
                String resultstr = entity.toString();
                String pattern = "columnKey=(\\d+),";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(resultstr);
                if (m.find()) {
                   String col = m.group(1);
                   int index = Integer.parseInt(col);
                   List<Integer> idL = vectorList.get(index-1);
                   HashMap<Integer, Integer> hashMap=new HashMap<Integer, Integer>();
                   int[] count={0,0,0,0,0,0,0,0,0,0,0};
                   for(int i=0;i<idL.size();i++) {
                       for(int j=0;j<11;j++){
                           count[j] += data.get(i).get(j);
                       }
                   }
                   for(int k=0;k<11;k++){
                        hashMap.put(k, count[k]);  
                   }
                   new degreePieChartJFrame(hashMap);
                }
               
               System.out.println("Mouse clicked: " + entity.toString());   
            }   
            else {   
                System.out.println("Mouse clicked: null entity.");   
            }   
        }
       };
       chartPanel.addChartMouseListener(l);
       statisContainer = new JPanel();
       statisContainer.removeAll();
       chartPanel.setMaximumSize(new Dimension(400,400));
       chartPanel.setMinimumSize(new Dimension(400,400));
       chartPanel.setPreferredSize(new Dimension(400,400));
       statisContainer.add(chartPanel);
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
            java.util.logging.Logger.getLogger(DegreesLabelsChart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DegreesLabelsChart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DegreesLabelsChart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DegreesLabelsChart.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DegreesLabelsChart().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
