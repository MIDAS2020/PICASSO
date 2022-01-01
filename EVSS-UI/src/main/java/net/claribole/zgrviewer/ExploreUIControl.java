/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.claribole.zgrviewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import prague.result.ChooseResultsInterface;
import gblend.GBlendView;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
/**
 *
 * @author kaihuang
 */
public class ExploreUIControl extends javax.swing.JFrame {

    private JPanel totalContainer;
    private JScrollPane scrollContainer;
    private JScrollPane bottomContainer;
    private JPanel panelContainer;
    private JPanel statisContainer;
    private DefaultCategoryDataset dataset;
    private ChartPanel chartPanel;
    private Vector<List<Integer>> vectorIdList = new Vector<List<Integer>>();
    private Vector<List<Integer>> vectorList = new Vector<List<Integer>>();
    private static int LONG = 550;
    private static int WIDTH = 800;
    private static int STALONG = 300;
    private static int STAWIDTH = 500;

    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem2_1;
     private javax.swing.JMenuItem jMenuItem2_2;
    private javax.swing.JMenuItem jMenuItem3;
    private Vector<JPanel> panelRecord = new Vector<JPanel>();
    private Vector<JScrollPane> statisContainerRecord = new Vector<JScrollPane>();
    private Vector<GBlendView>  gblendViewRecord = new Vector<GBlendView>();
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private Vector<ChooseResultsInterface> vectorcr = new Vector<ChooseResultsInterface>();
    private int runtime;
    private int reRuntime=1;

    public ExploreUIControl() {
        initComponents();
        initGUI();
    }
    public int getReRuntime(){
        return reRuntime;
    }
    public void addControlPanel(GBlendView gblendview,ChooseResultsInterface crs, int runtimes, JPanel drawpane, boolean isSimilar) {
       
       //added
        //drawpane.setPreferredSize(new Dimension(STALONG+100, STAWIDTH));
        drawpane.setPreferredSize(new Dimension(800, 700));
        JScrollPane drawspane = new JScrollPane(drawpane);
        drawspane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                "Query:" + String.valueOf(runtimes), TitledBorder.CENTER,
                TitledBorder.TOP, new Font("TimesRoman", Font.PLAIN, 20)));
        drawspane.setPreferredSize(new Dimension(500, 400));
        
        gblendViewRecord.add(gblendview);
        statisContainerRecord.add(drawspane);
        runtime = runtimes;
        //for cr 
        vectorcr.add(crs);
        //for recording id list
        vectorIdList.add(crs.getList());
        //for size distribution
        vectorList.add(crs.getListSize());
        ExploreUI ui = new ExploreUI(bottomContainer,runtime,statisContainerRecord,gblendViewRecord,panelRecord, panelContainer, dataset, statisContainer, vectorList, isSimilar);
        panelContainer = ui.addResultPanel(crs, runtimes);
        scrollContainer.setViewportView(panelContainer);
        scrollContainer.validate();
        scrollContainer.repaint();
        scrollContainer.getHorizontalScrollBar().setValue(scrollContainer.getHorizontalScrollBar().getMaximum());
        scrollContainer.getHorizontalScrollBar().setValue(scrollContainer.getHorizontalScrollBar().getValue());

     
        
        statisContainer.add(drawspane);
        statisContainer.setPreferredSize(new Dimension(STALONG * runtimes, STAWIDTH));
        statisContainer.validate();
        statisContainer.repaint();
        bottomContainer.setPreferredSize(new Dimension(STALONG * runtimes, STAWIDTH));
        bottomContainer.validate();
        bottomContainer.repaint();
        bottomContainer.getHorizontalScrollBar().setValue(bottomContainer.getHorizontalScrollBar().getMaximum());
        bottomContainer.getHorizontalScrollBar().setValue(bottomContainer.getHorizontalScrollBar().getValue());
        
        if (runtimes <= 2) {
            setSize(new Dimension(runtimes * LONG, WIDTH));
        }

        dataset = ui.getDataset();
        if (runtimes == 1) {
            System.out.println("**********runtimes:"+runtimes);
            setVisible(true);
        }
        
         addWindowListener(new WindowAdapter() {  public void windowClosing(WindowEvent e) {  
         // super.windowClosing(e);  
          reRuntime = 100000;
          super.windowClosing(e);  
        }  
         });  
    }

    public void initGUI() {
        //1. MenuBar
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem2_1 = new javax.swing.JMenuItem();
        jMenuItem2_2 = new javax.swing.JMenuItem();
        jMenu1.setText("Analysis");
        jMenuItem1.setText("Exploration History");
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gblend/resources/view.png")));
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new HistoryUI(vectorIdList, runtime, vectorcr);
            }
        });
        jMenu1.add(jMenuItem1);
        jMenuBar1.add(jMenu1);

        jMenu2.setText("View");
        jMenuItem2.setText("Numbers and Sizes");
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gblend/resources/analysis.png")));
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new BarChart(dataset, vectorList);
            }
        });
        jMenu2.add(jMenuItem2);
        jMenuItem2_1.setText("KCores");
        jMenuItem2_1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gblend/resources/bar.png")));
        jMenuItem2_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new KCoresBarChart(vectorIdList);
            }
        });
        jMenu2.add(jMenuItem2_1);
     
        jMenuItem2_2.setText("Degrees and Labels");
        jMenuItem2_2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gblend/resources/piechart.png")));
        jMenuItem2_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new DegreesLabelsChart(vectorIdList);
            }
        });
        jMenu2.add(jMenuItem2_2);
        jMenuBar1.add(jMenu2);
           
        jMenu3.setText("Help");
        jMenuItem3.setText("About");
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gblend/resources/info.png")));
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new AboutDialog();
            }
        });
        jMenu3.add(jMenuItem3);
        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jToolBar1.setRollover(true);
        jToolBar1.setToolTipText("");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gblend/resources/view.png"))); // NOI18N
        jButton1.setToolTipText("View History");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {

            }
        });
        jToolBar1.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gblend/resources/analysis.png"))); // NOI18N
        jButton2.setToolTipText("Analysis");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setMaximumSize(new java.awt.Dimension(27, 23));
        jButton2.setMinimumSize(new java.awt.Dimension(27, 23));
        jButton2.setPreferredSize(new java.awt.Dimension(27, 23));
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton2);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gblend/resources/info.png"))); // NOI18N
        jButton3.setToolTipText("About");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton3);

        //2. scrollContainer
        panelContainer = new JPanel();
        panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.X_AXIS));
        scrollContainer = new JScrollPane();
        scrollContainer.setViewportView(panelContainer);
        scrollContainer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollContainer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //3. statisContainer
        statisContainer = new JPanel();
        statisContainer.setLayout(new BoxLayout(statisContainer, BoxLayout.X_AXIS));
        dataset = new DefaultCategoryDataset();
        // JFreeChart barChart = ChartFactory.createBarChart("Numbers","Run times","Candidates",dataset,PlotOrientation.VERTICAL,true, true, false);
        // chartPanel = new ChartPanel(barChart);
        // statisContainer.add(chartPanel);
        bottomContainer = new JScrollPane(statisContainer);
        // bottomContainer.setViewportView(statisContainer);
        bottomContainer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        bottomContainer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        totalContainer = new JPanel();
        totalContainer.setLayout(new BoxLayout(totalContainer, BoxLayout.Y_AXIS));
        //totalContainer.add(jToolBar1, BorderLayout.NORTH);
        totalContainer.add(scrollContainer);
        totalContainer.add(bottomContainer);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new Dimension(LONG, WIDTH));
        setContentPane(totalContainer);
        // setVisible(true);
        addWindowListener(new WindowAdapter() {  public void windowClosing(WindowEvent e) {  
         // super.windowClosing(e);  
          reRuntime = 100000;
          super.windowClosing(e);  
        }  
         });   

    }

    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Results Exploration Wall");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 776, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 401, Short.MAX_VALUE)
        );

        pack();
    }
}
