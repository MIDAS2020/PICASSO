/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.claribole.zgrviewer;

import gblend.db.DatabaseInfo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
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
public class TEDExplorer extends javax.swing.JFrame {
    private JPanel statisContainer;
    private Vector<List<Integer>> vectorIdList;
    private List<Integer> resultIdList;
    private int selecttopk;
    private int minE;
    private int maxE;
    private int minSup;
    
    private JScrollPane bottomContainer;
            
    private static int Long_each = 400;
    private static int Width_each = 450;
    private static int Long_panelContainer = Long_each*3 + 50;
    private static int Width_panelContainer = Width_each + 150;
    private static int Long_bottomContainer = Long_panelContainer;
    private static int Width_bottomContainer = Width_panelContainer; 
    private static int CLONG = 60;
    private static int MLONG = 70;
    private static int LLONG = 100;
    private TEDSProcessor processor;
    private Arguments arguments ;
    private    File inFile ;
    private    File outFile ;
    private    FileReader reader;
    private FileWriter writer;
    private JLabel    totalCov;
    private double    CovValue;
    
    private ArrayList<Graph> Patterns;
    
    public TEDExplorer() {
        initComponents();
    }

    public TEDExplorer(Vector<List<Integer>> v) {
        
        initComponents();
        vectorIdList = v;
        
        statisContainer = new JPanel();
        statisContainer.removeAll();
        statisContainer.setLayout(new BoxLayout(statisContainer, BoxLayout.Y_AXIS));
        
        
        /// 1. top panel 
        ///////////////////////////////////////////////////////////////////////
        JPanel selectPanel = new JPanel();
        selectPanel.setLayout(new BorderLayout());
        //selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.X_AXIS));
        
        JToolBar mainToolBar = new JToolBar();
       // mainToolBar.setLayout(new BorderLayout());
        
        JLabel timelabel = new JLabel("Select:", JLabel.RIGHT);
        Vector<String> timeList = new Vector<String>();
        //timeList.addElement("");
        for (int i = vectorIdList.size(); i  >= 1 ; i--) {
            timeList.addElement("Timestamp " + String.valueOf(i));
        }
        timelabel.setMinimumSize(new Dimension(80, 30));
        timelabel.setMaximumSize(new Dimension(80, 30));
        timelabel.setPreferredSize(new Dimension(80, 30));
        mainToolBar.add(timelabel);
        resultIdList = new ArrayList<Integer>();
        if(vectorIdList!=null && vectorIdList.size() > 0)  resultIdList = vectorIdList.get(vectorIdList.size() -1 );      
        JComboBox timeComboBox = new JComboBox<>(timeList);
        timeComboBox.setBorder(BorderFactory.createEmptyBorder());
        timeComboBox.setMinimumSize(new Dimension(100, 30));
        timeComboBox.setMaximumSize(new Dimension(100, 30));
        timeComboBox.setPreferredSize(new Dimension(100, 30));
        timeComboBox.addItemListener(e -> {
            int id = timeComboBox.getSelectedIndex();
            resultIdList = vectorIdList.get(vectorIdList.size() - 1 - id);  
            createTEDPane();
            statisContainer.validate();
            statisContainer.repaint();
        });
        mainToolBar.add(timeComboBox); 
        
        mainToolBar.addSeparator(new Dimension(30, 0));
        
        ///////////////////////////////////////////////////////////////////////////////////
        JLabel toplabel = new JLabel("Top:", JLabel.RIGHT);
        Vector<Integer> topList = new Vector<Integer>();
        for (int i = 3; i <= 10; i++) {
            topList.addElement(i);
        }
        toplabel.setMinimumSize(new Dimension(80, 30));
        toplabel.setMaximumSize(new Dimension(80, 30));
        toplabel.setPreferredSize(new Dimension(80, 30));
        mainToolBar.add(toplabel);
        selecttopk = 3;     
        JComboBox topComboBox = new JComboBox<>(topList);
        topComboBox.setBorder(BorderFactory.createEmptyBorder());
        topComboBox.setMinimumSize(new Dimension(80, 30));
        topComboBox.setMaximumSize(new Dimension(80, 30));
        topComboBox.setPreferredSize(new Dimension(80, 30));
        topComboBox.addItemListener(e -> {
            Integer top = (Integer) topComboBox.getItemAt(topComboBox.getSelectedIndex());
            selecttopk = top;
            createTEDPane();
            statisContainer.validate();
            statisContainer.repaint();
                    
        });
        mainToolBar.add(topComboBox);
        
        mainToolBar.addSeparator(new Dimension(80, 0));
        
        //////////////////////////////////////////////////// 
        JLabel minlabel = new JLabel("MinE:", JLabel.RIGHT);
        Vector<Integer> minList = new Vector<Integer>();
        for (int i = 2; i <= 6; i++) {
            minList.addElement(i);
        }
        toplabel.setMinimumSize(new Dimension(80, 30));
        toplabel.setMaximumSize(new Dimension(80, 30));
        toplabel.setPreferredSize(new Dimension(80, 30));
        mainToolBar.add(minlabel);
        minE = 2;     
        JComboBox minComboBox = new JComboBox<>(minList);
        minComboBox.setBorder(BorderFactory.createEmptyBorder());
        minComboBox.setMinimumSize(new Dimension(80, 30));
        minComboBox.setMaximumSize(new Dimension(80, 30));
        minComboBox.setPreferredSize(new Dimension(80, 30));
        minComboBox.addItemListener(e -> {
            Integer mine = (Integer) minComboBox.getItemAt(minComboBox.getSelectedIndex());
            minE = mine;
            createTEDPane();
            statisContainer.validate();
            statisContainer.repaint();
                    
        });
        mainToolBar.add(minComboBox);
        
        mainToolBar.addSeparator(new Dimension(30, 0));
        
         //////////////////////////////////////////////////// 
        JLabel maxlabel = new JLabel("MaxE:", JLabel.RIGHT);
        Vector<Integer> maxList = new Vector<Integer>();
        maxList.addElement(4);
        maxList.addElement(2);
        maxList.addElement(3);
        for (int i = 5; i <= 10; i++) {
            maxList.addElement(i);
        }
        maxlabel.setMinimumSize(new Dimension(80, 30));
        maxlabel.setMaximumSize(new Dimension(80, 30));
        maxlabel.setPreferredSize(new Dimension(80, 30));
        mainToolBar.add(maxlabel);
        maxE = 4;     
        JComboBox maxComboBox = new JComboBox<>(maxList);
        maxComboBox.setBorder(BorderFactory.createEmptyBorder());
        maxComboBox.setMinimumSize(new Dimension(80, 30));
        maxComboBox.setMaximumSize(new Dimension(80, 30));
        maxComboBox.setPreferredSize(new Dimension(80, 30));
        maxComboBox.addItemListener(e -> {
            Integer maxe = (Integer) maxComboBox.getItemAt(maxComboBox.getSelectedIndex());
            maxE = maxe;
            createTEDPane();
            statisContainer.validate();
            statisContainer.repaint();
                    
        });
        mainToolBar.add(maxComboBox);
        
        mainToolBar.addSeparator(new Dimension(30, 0));
        
          //////////////////////////////////////////////////// 
        JLabel suplabel = new JLabel("MinSup:", JLabel.RIGHT);
        suplabel.setMinimumSize(new Dimension(80, 30));
        suplabel.setMaximumSize(new Dimension(80, 30));
        suplabel.setPreferredSize(new Dimension(80, 30));
        mainToolBar.add(suplabel);
        minSup = 1;     
        JTextField supField = new JTextField(1);
        supField.setText("1");
        supField.setMinimumSize(new Dimension(80, 30));
        supField.setMaximumSize(new Dimension(80, 30));
        supField.setPreferredSize(new Dimension(80, 30));
        supField.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                minSup = Integer.parseInt(supField.getText().trim());
                createTEDPane();
                statisContainer.validate();
                statisContainer.repaint();
            }
        });
        mainToolBar.add(supField);
        
        
        mainToolBar.addSeparator(new Dimension(40, 0));
        
        totalCov = new  JLabel("Edge Coverage", JLabel.RIGHT);
        totalCov.setMinimumSize(new Dimension(200, 30));
        totalCov.setMaximumSize(new Dimension(200, 30));
        totalCov.setPreferredSize(new Dimension(200, 30));
        mainToolBar.add(totalCov);
        
        
        selectPanel.add(mainToolBar);
        statisContainer.add(selectPanel);
        
        
       
        
        
        bottomContainer = new JScrollPane();
        bottomContainer.setPreferredSize(new Dimension(Long_bottomContainer, Width_bottomContainer));
        bottomContainer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        bottomContainer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        bottomContainer.setVisible(true);
        
        
        statisContainer.add(bottomContainer);
        
        createTEDPane();
        statisContainer.validate();
        statisContainer.repaint();
                
                
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new Dimension(Long_panelContainer, Width_panelContainer));
        setContentPane(statisContainer);
        
        setTitle("TED Explorer");
        
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/gblend/resources/smalllogo.png")).getImage());
        
        setVisible(true);
    }
    
    public void initilize(){
        processor = new TEDSProcessor();
        arguments = new Arguments();
        inFile = new File(arguments.inFilePath);
        outFile = new File(arguments.outFilePath);
        try {
            reader = new FileReader(inFile);
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        try {
            writer = new FileWriter(outFile);
                    } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        if(arguments.isSimplified) {
                 	System.out.println("Start TEDSSimplifiedProcessor...");
                 	System.out.println("Finished TEDSSimplifiedProcessor...");
         }else {
                	if(arguments.isLightVersion) {
                            System.out.println("Start TEDSLightProcessor...");
                            System.out.println("Finished TEDSLightProcessor...");
                        }else {
                            System.out.println("Start TEDSProcessor...");
                            try {
                                processor.readResultGraphs(resultIdList,reader, writer, arguments);
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                            System.out.println("Finished TEDSProcessor...");
                        }
        } 
    }

     private void read(FileReader is) throws IOException {
        Patterns = new ArrayList<Graph>();
        BufferedReader read = new BufferedReader(is);
        while (true) {
            Graph g = new Graph(false);
            read = g.read(read);
            if (g.isEmpty())
                break;
            Patterns.add(g);
        }
        read.close();
    }
     
     private  String getRealLabels(int l, int i) {
        String[] realLabels = DatabaseInfo.getLabels();
        String label = realLabels[l] + "_" + i;
        return label;
    }
     
    private  void printEdges(PrintStream p, Graph  graph, String[] realLabels, List<Integer> missEdges) {

        int n = graph.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                ArrayList<Edge> edge = graph.get(i).edge;
                for(Edge e: edge){
                   if (e.to == j) {
                    printEdge(p, realLabels, i, j);
                    break;
                   } 
                }
               
            }
        }
    }
 
    private  void printEdge(PrintStream p, String[] realLabels, int i, int j) {
        p.print(realLabels[i]);
        p.print(" -- ");
        p.print(realLabels[j]);
        p.println(";");
    }
     private File formatGraphWithName(Graph pattern, int patternid,  List<Integer> missEdges, String pathname){
        String output = pathname;
        File fOut = new File(output);
        try {
            // Connect print stream to the output stream
            try (PrintStream p = new PrintStream(new FileOutputStream(fOut))) {
                p.println("graph \"result\" {");
                p.println("graph [ fontname=\"Helvetica-Oblique\", fontsize=20,");
                int n = pattern.size();
                p.printf("label=\"\\n\\nPattern %d (%d,%d)\", ", patternid, n, pattern.getEdgeSize());
                p.println("size=\"4,4\" ];");
                p.println("node [ label=\"\\N\", shape=box, sides=4, color=cadetblue1,");
                p.println("style=filled, fontname=\"Helvetica-Outline\" ];");

                String[] realLabels = new String[n];
                for (int i = 0; i < n; i++) {
                    realLabels[i] = getRealLabels(pattern.get(i).label, i);
                    p.print(realLabels[i]);
                    //if (checker != null && checker.checkNode(i)) {
                    //    p.print(" [color=orange]");
                    //}
                    p.println(";");
                }

                printEdges(p, pattern, realLabels, missEdges);

                p.println("}");
            }
        } catch (IOException e) {
            throw new RuntimeException("formatDotFile error", e);
        }

        return fOut;
    }
        
    public void createTEDPane() {
        
        initilize();
        
        
        System.out.println("resultIdList:" + resultIdList.size());
        System.out.println("selecttopk:" + selecttopk);
        System.out.println("minE:" + minE);
        System.out.println("maxE:" + maxE);
        System.out.println("minSup:" + minSup);
        try {
          CovValue =   processor.run(resultIdList.size(), selecttopk, minE, maxE, minSup);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        try {
            reader.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        try {
            writer.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        
        FileReader temfilereader = null;
        File tempinFile = new  File(arguments.outFilePath);
        try {
            temfilereader = new FileReader(tempinFile);
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        try {      
            read(temfilereader);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        
        JPanel subview = new JPanel();
        subview.setLayout(new BoxLayout(subview, BoxLayout.X_AXIS));
        for (int i = 0; i < selecttopk; i++) {
                String filename = "graph" + String.valueOf(i) + ".dot";
                Graph pattern = Patterns.get(i);
                File f = formatGraphWithName(pattern, i ,  null, filename);
                ZGRViewer viewer = new ZGRViewer(false,true);
                JPanel pane = viewer.getPanelView();
                pane.setPreferredSize(new Dimension(Long_each, Width_each));
                JScrollPane jp = new JScrollPane(pane);
                jp.setPreferredSize(new Dimension(Long_each, Width_each));
                jp.getHorizontalScrollBar().setValue(jp.getHorizontalScrollBar().getMaximum());
                jp.getHorizontalScrollBar().setValue(jp.getHorizontalScrollBar().getValue()/2);
                subview.add(jp);
                File dotFile = f;
                viewer.gvLdr.loadFile(dotFile, DOTManager.DOT_PROGRAM, false);
            }
        bottomContainer.setViewportView(subview);
        
        

        DecimalFormat df = new DecimalFormat("#.000");
        String str = df.format(CovValue);
        totalCov.setText("Edge Coverage: " + "0"+str);
        totalCov.setFont(new Font("times new roman",Font.BOLD,18));
        //totalCov.setBackground(Color.RED);
        totalCov.setForeground(Color.RED);
        
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
            java.util.logging.Logger.getLogger(TEDExplorer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TEDExplorer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TEDExplorer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TEDExplorer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TEDExplorer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
