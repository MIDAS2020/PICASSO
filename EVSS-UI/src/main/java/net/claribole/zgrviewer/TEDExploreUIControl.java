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
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
/**
 *
 * @author kaihuang
 */
public class TEDExploreUIControl extends javax.swing.JFrame {

    private JPanel totalContainer;
    private JScrollPane scrollContainer;
    private JScrollPane bottomContainer;
    private JPanel panelContainer;
    private JPanel upContainer;
    private JPanel statisContainer;
    private DefaultCategoryDataset dataset;
    private ChartPanel chartPanel;
   
    
    private static int Long_each = 400;
    private static int Width_each = 450;
    private static int Long_panelContainer = Long_each*3 + 50;
    private static int Width_panelContainer = Width_each + 150;
    private static int Long_bottomContainer = Long_panelContainer;
    private static int Width_bottomContainer = Width_panelContainer; 
    
    
    //private static int LONG = 550;
    //private static int WIDTH = 800;
    //private static int STALONG = 300;
    //private static int STAWIDTH = 500;
    
     private Vector<List<Integer>> vectorIdList = new Vector<List<Integer>>();
    private Vector<List<Integer>> vectorList = new Vector<List<Integer>>();
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

    public TEDExploreUIControl() {
        initComponents();
        initGUI();
    }
    public int getReRuntime(){
        return reRuntime;
    }
    public void addControlPanel(GBlendView gblendview,ChooseResultsInterface crs, int runtimes, boolean isSimilar) {
       
        gblendViewRecord.add(gblendview);
        runtime = runtimes;
        //for cr 
        vectorcr.add(crs);
        //for recording id list
        vectorIdList.add(crs.getList());
        //for size distribution
        vectorList.add(crs.getListSize());
        
        
        
        
        
        
        //ExploreUI ui = new ExploreUI(bottomContainer,runtime,statisContainerRecord,gblendViewRecord,panelRecord, panelContainer, dataset, statisContainer, vectorList, isSimilar);
        
        ExploreTEDUI ui = new ExploreTEDUI(bottomContainer,runtime,statisContainerRecord,gblendViewRecord,panelRecord, panelContainer, dataset, statisContainer, vectorList, isSimilar);
        
        panelContainer = ui.addResultPanel(crs, runtimes);
        
        
        scrollContainer.setViewportView(panelContainer);
        scrollContainer.validate();
        scrollContainer.repaint();
        scrollContainer.getHorizontalScrollBar().setValue(scrollContainer.getHorizontalScrollBar().getMaximum());
        scrollContainer.getHorizontalScrollBar().setValue(scrollContainer.getHorizontalScrollBar().getValue());

        
        upContainer.validate();
        upContainer.repaint();
        
        
        //statisContainer.add(drawspane);
        
        statisContainer.setPreferredSize(new Dimension(Long_bottomContainer, Width_bottomContainer));
        statisContainer.validate();
        statisContainer.repaint();
        bottomContainer.setPreferredSize(new Dimension(Long_bottomContainer, Width_bottomContainer));
        bottomContainer.validate();
        bottomContainer.repaint();
        bottomContainer.getHorizontalScrollBar().setValue(bottomContainer.getHorizontalScrollBar().getMaximum());
        bottomContainer.getHorizontalScrollBar().setValue(bottomContainer.getHorizontalScrollBar().getValue());
        
        //if (runtimes <= 2) {
        //    setSize(new Dimension(runtimes * LONG, WIDTH));
       // }

        dataset = ui.getDataset();
        if (runtimes == 1) {
            System.out.println("**********runtimes:"+runtimes);
            setVisible(true);
        }
        //setVisible(true);
        
         addWindowListener(new WindowAdapter() {  public void windowClosing(WindowEvent e) {  
         // super.windowClosing(e);  
          reRuntime = 100000;
          super.windowClosing(e);  
        }  
         });   
    }

    public void initGUI() {
        totalContainer = new JPanel();
        totalContainer.setLayout(new BoxLayout(totalContainer, BoxLayout.Y_AXIS));
        //1. set upContainer
        upContainer = new JPanel();
        //upContainer.setLayout(new BoxLayout(upContainer, BoxLayout.X_AXIS));
        upContainer.setLayout(new BorderLayout());
        
        JToolBar mainToolBar = new JToolBar();
        //mainToolBar.setFloatable(false);
        //mainToolBar.setRollover(true);
        //mainToolBar.setName("mainToolBar");
        
        JButton showTEDButton = new JButton();
        showTEDButton.setFocusable(false);
        showTEDButton.setHorizontalTextPosition(SwingConstants.LEFT);
        showTEDButton.setName("Show TED");
        showTEDButton.setText("TED");
        showTEDButton.setVerticalTextPosition(SwingConstants.CENTER);
        showTEDButton.setToolTipText("Show TED");
        showTEDButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gblend/resources/smalllogo.png")));
        showTEDButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new TEDExplorer(vectorIdList);
            }
        });
        showTEDButton.setMaximumSize(new java.awt.Dimension(87, 25));
        showTEDButton.setMinimumSize(new java.awt.Dimension(87, 25));
        mainToolBar.add(showTEDButton);
        
        mainToolBar.addSeparator(new Dimension(30, 25));

        
        JButton showKCOREButton = new JButton();
        showKCOREButton.setFocusable(false);
        showKCOREButton.setHorizontalTextPosition(SwingConstants.LEFT);
        showKCOREButton.setName("Show KCores");
        showKCOREButton.setText("KCores");
        showKCOREButton.setVerticalTextPosition(SwingConstants.CENTER);
        showKCOREButton.setToolTipText("Show KCores");
        showKCOREButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gblend/resources/bar.png")));
        showKCOREButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new KCoresBarChart(vectorIdList);
            }
        });
        showKCOREButton.setMaximumSize(new java.awt.Dimension(87, 25));
        showKCOREButton.setMinimumSize(new java.awt.Dimension(87, 25));
        mainToolBar.add(showKCOREButton);
        
        mainToolBar.addSeparator(new Dimension(30, 25));
        
        JButton showDegreeButton = new JButton();
        showDegreeButton = new JButton();
        showDegreeButton.setFocusable(false);
        showDegreeButton.setHorizontalTextPosition(SwingConstants.LEFT);
        showDegreeButton.setName("Show Distribution");
        showDegreeButton.setText("Distribution");
        showDegreeButton.setVerticalTextPosition(SwingConstants.CENTER);
        showDegreeButton.setToolTipText("Show Distribution");
        showDegreeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gblend/resources/piechart.png")));
        showDegreeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new BarChart(dataset, vectorList);
            }
        });
        showDegreeButton.setMaximumSize(new java.awt.Dimension(87, 25));
        showDegreeButton.setMinimumSize(new java.awt.Dimension(87, 25));
        mainToolBar.add(showDegreeButton);
        
        mainToolBar.addSeparator(new Dimension(30, 25));
        
        upContainer.add(mainToolBar, BorderLayout.PAGE_START);
        
        //upContainer.add(mainToolBar);
        
        
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
        bottomContainer.setVisible(false);
     
        //totalContainer.add(jToolBar1, BorderLayout.NORTH);
        
        totalContainer.add(upContainer);
        totalContainer.add(scrollContainer);
        totalContainer.add(bottomContainer);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        setContentPane(totalContainer);
        
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/gblend/resources/explorerwall.png")).getImage());
        
        if(bottomContainer.isVisible())
           setSize(new Dimension(Long_panelContainer, Width_panelContainer + Width_bottomContainer));
        else 
           setSize(new Dimension(Long_panelContainer, Width_panelContainer)); 
        
        addWindowListener(new WindowAdapter() {  public void windowClosing(WindowEvent e) {  
         // super.windowClosing(e);  
          reRuntime = 100000;
          super.windowClosing(e);  
        }  
         });   

    }

    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Query Results Explorer");

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
