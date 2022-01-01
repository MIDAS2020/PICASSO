/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.claribole.zgrviewer;

import gblend.GBlendView;
import gblend.widget.QueryGraphScene;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.netbeans.api.visual.widget.ConnectionWidget;
import prague.query.QueryEdge;
import prague.result.ChooseResultsInterface;
import prague.result.Record;

/**
 *
 * @author kaihuang
 */
public class ExploreTEDUI extends javax.swing.JFrame implements ActionListener {

    //private JScrollPane mainScrollContainer;
    private JPanel panelContainer;
    //private static int LONG = 550;
   // private static int WIDTH = 800;
    private static int CLONG = 60;
    private static int MLONG = 70;
    private static int LLONG = 100;
    private int selecttopk;
    private String similartopk;
    private int selectid;
    private int topkmax;
    private int count;
    ////////////////
    private JPanel mainPanel;
    private JScrollPane contentPanel;
    private JButton B1, B2;
    private List<Integer> idList;
    private ChooseResultsInterface crs;
    private JComboBox<Integer> topComboBox;
    private boolean changed = true;
    private DefaultCategoryDataset dataset;
    private ChartPanel chartPanel = null;
    private JPanel statisContainer;
    private Vector<List<Integer>> vectorList;
    private Vector<JPanel> panelRecord = new Vector<JPanel>();
    private boolean isSimilar;
    // GBlendView gblendview;
    private Vector<JScrollPane> statisContainerRecord;
    private int runtime;
    //private static int STALONG = 300;
    //private static int STAWIDTH = 500;
    private JScrollPane bottomContainer;
    Vector<GBlendView> gblendViewRecord;
    
    
    private static int Long_each = 400;
    private static int Width_each = 450;
    private static int Long_panelContainer = Long_each*3 + 50;
    private static int Width_panelContainer = Width_each + 150;
    private static int Long_bottomContainer = Long_panelContainer;
    private static int Width_bottomContainer = Width_panelContainer; 
    

    class CompareResult {

        Integer index;
        Integer missEdges;
        String theMissed;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == B1) {
            //if (count >= 0) {
            if (count * selecttopk + selectid > 0) {
                count--;
            } else {
                return;
            }
        } else if (e.getSource() == B2) {
            if (count + 1 < idList.size()) {
                count++;
            } else {
                return;
            }
        }
        mainPanel.remove(contentPanel);
        mainPanel = createContentPanel(mainPanel);
        mainPanel.validate();
        mainPanel.repaint();

    }

    public class ViewResult {

        public GVLoader gvLdr;
        public JPanel _panelView;
    }

    /**
     * Creates new form ExploreUI
     */
    public ExploreTEDUI() {
        // initComponents();
        // initGUI();
    }

    public ExploreTEDUI(JPanel jpane) {
        panelContainer = jpane;

    }

    public ExploreTEDUI(JScrollPane bottomCon, int t, Vector<JScrollPane> statisConRecord, Vector<GBlendView> gbviewRecord, Vector<JPanel> panelrecord, JPanel jpane, DefaultCategoryDataset data, JPanel staContainer, Vector<List<Integer>> vlist, boolean isSim) {
        gblendViewRecord = gbviewRecord;
        bottomContainer = bottomCon;
        runtime = t;
        statisContainerRecord = statisConRecord;
        // gblendview = gbview;
        isSimilar = isSim;
        panelRecord = panelrecord;
        panelContainer = jpane;
        panelContainer.removeAll();
        for (int i = 0; i < panelRecord.size(); i++) {
            JPanel p = panelRecord.get(i);
            p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Timestamp:" + String.valueOf(i + 1), TitledBorder.CENTER, TitledBorder.TOP, new Font("TimesRoman", Font.PLAIN, 20)));
            panelContainer.add(p);
        }
        panelContainer.revalidate();
        panelContainer.repaint();
        statisContainer = staContainer;
        dataset = data;
        vectorList = vlist;

    }

    public DefaultCategoryDataset getDataset() {
        return dataset;
    }

    public JPanel addResultPanel(ChooseResultsInterface cr, int runtimes) {
        crs = cr;
        idList = crs.getList();
        selecttopk = 5;  // initialize for every new panel  
        selectid = 0;
        topkmax = 10;
        count = 0;  // to select the count topk, for exaple 3rd top 5
        //added
        if (!isSimilar) {
            dataset.addValue(idList.size(), "Number of Exact Results", String.valueOf(runtimes));
        } else {
            dataset.addValue(idList.size(), "Number of Similar Results", String.valueOf(runtimes));
        }
        //mainpanel add topbar
        String title = "Timestamp:" + String.valueOf(runtimes);
        if (isSimilar) {
            title += "(Similarity Search)";
        }
        /*
           1.mainPanel
         */
        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED, 2), title, TitledBorder.CENTER, TitledBorder.TOP, new Font("TimesRoman", Font.PLAIN, 20)));
        
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));


        //mainPanel.setPreferredSize(new Dimension(LONG-100,WIDTH) );
        /*
           2.topBar
         */
        JPanel topBar = new JPanel();
        topBar.setLayout(new BoxLayout(topBar, BoxLayout.X_AXIS));
        JLabel L1 = new JLabel("Total:", JLabel.RIGHT);
        L1.setMinimumSize(new Dimension(20, 20));
        L1.setMaximumSize(new Dimension(CLONG, 20));

        JLabel L2 = new JLabel(String.valueOf(idList.size()), JLabel.LEFT);
        L2.setMinimumSize(new Dimension(20, 20));
        L2.setMaximumSize(new Dimension(CLONG, 20));

        Vector<Integer> topList = new Vector<Integer>();
        for (int i = 1; i <= topkmax; i++) {
            topList.addElement(new Integer(i));
        }
        topComboBox = new JComboBox<>(topList);
        ((JLabel) topComboBox.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
        topComboBox.setBorder(BorderFactory.createEmptyBorder());
        topComboBox.setMinimumSize(new Dimension(20, 20));
        topComboBox.setMaximumSize(new Dimension(CLONG, 20));
        topComboBox.setSelectedItem(selecttopk);
        topComboBox.addItemListener(e -> {
            if (changed) {
                selecttopk = topComboBox.getItemAt(topComboBox.getSelectedIndex());
                count = 0;
                mainPanel.remove(contentPanel);
                mainPanel = createContentPanel(mainPanel);
                mainPanel.validate();
                mainPanel.repaint();
            }
        });

        JLabel L3 = new JLabel("/Page");
        L3.setMinimumSize(new Dimension(MLONG, 20));
        L3.setMaximumSize(new Dimension(MLONG, 20));

        B1 = new JButton("Prev");
        B1.setMinimumSize(new Dimension(MLONG, 20));
        B1.setMaximumSize(new Dimension(MLONG, 20));
        B1.addActionListener(this);
        B2 = new JButton("Next");
        B2.setMinimumSize(new Dimension(MLONG, 20));
        B2.setMaximumSize(new Dimension(MLONG, 20));
        B2.addActionListener(this);

        JLabel L4 = new JLabel("GraphId:", JLabel.RIGHT);
        L4.setMinimumSize(new Dimension(CLONG, 20));
        L4.setMaximumSize(new Dimension(CLONG, 20));

        Vector<Integer> idL = new Vector<>(idList);
        JComboBox<Integer> idComboBox = new JComboBox<>(idL);
        idComboBox.setMinimumSize(new Dimension(CLONG, 20));
        idComboBox.setMaximumSize(new Dimension(CLONG, 20));
        idComboBox.addItemListener(e -> {
            selectid = idComboBox.getSelectedIndex();
            count = 0;
            mainPanel.remove(contentPanel);
            mainPanel = selectContentPanel(mainPanel, selectid);
            mainPanel.validate();
            mainPanel.repaint();
            // System.out.println(selectid);
        });
        JButton allMatchButton = new JButton("AllMatch");
        allMatchButton.setMinimumSize(new Dimension(LLONG, 20));
        allMatchButton.setMaximumSize(new Dimension(LLONG, 20));
        allMatchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPanel subview = new JPanel();
                //subview.setLayout(new BoxLayout(subview, BoxLayout.Y_AXIS));
                subview.setLayout(new BoxLayout(subview, BoxLayout.X_AXIS));
                List<File> dotFiles = crs.selectAllmatches(idList.get(selectid));
                for (int i = 0; i < dotFiles.size(); i++) {
                    ZGRViewer viewer = new ZGRViewer(crs, i);
                    JPanel pane = viewer.getPanelView();
                    pane.setPreferredSize(new Dimension(Long_each, Width_each));
                    JScrollPane jp = new JScrollPane(pane);
                    jp.setPreferredSize(new Dimension(Long_each, Width_each));
                    jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
                    jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getValue() / 2);
                    subview.add(jp);
                    //viewer.gvLdr.loadFile(dotFiles.get(i), DOTManager.NEATO_PROGRAM, false);
                    viewer.gvLdr.loadFile(dotFiles.get(i), DOTManager.DOT_PROGRAM, false);
                }
                mainPanel.remove(contentPanel);
                contentPanel.setViewportView(subview);
                mainPanel.add(contentPanel);
                mainPanel.validate();
                mainPanel.repaint();
            }
        });

        topBar.add(L1);
        topBar.add(L2);

        topBar.add(B1);
        topBar.add(topComboBox);
        //topBar.add(L3);
        topBar.add(B2);
        topBar.add(L4);
        topBar.add(idComboBox);
        if (!isSimilar) {
            topBar.add(allMatchButton);
        }
        if (isSimilar) {
            JLabel seltopk = new JLabel("", JLabel.RIGHT);
            seltopk.setMinimumSize(new Dimension(10, 20));
            seltopk.setMaximumSize(new Dimension(10, 20));

            Vector<String> similartopList = new Vector<String>();
            for (int i = 1; i <= 10; i++) {
                similartopList.addElement("Top" + String.valueOf(i));
            }
            JComboBox simComboBox = new JComboBox<>(similartopList);
            ((JLabel) simComboBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
            simComboBox.setBorder(BorderFactory.createEmptyBorder());
            simComboBox.setMinimumSize(new Dimension(20, 20));
            simComboBox.setMaximumSize(new Dimension(MLONG, 20));
            simComboBox.addItemListener(e -> {
                similartopk = (String) simComboBox.getItemAt(simComboBox.getSelectedIndex());

                int selectk = 0;
                String pattern = "Top(\\d+)";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(similartopk);
                if (m.find()) {
                    String col = m.group(1);
                    selectk = Integer.parseInt(col);
                }
                List<Integer> missEdges = new ArrayList<Integer>();
                List<String> theMissedEdgeLabel = new ArrayList<String>();
                List<CompareResult> compareEdges = new ArrayList<CompareResult>();
                for (int i = 0; i < idList.size(); i++) {
                    if (i > 300) {
                        continue;
                    }
                    //crs.selectedResults(idList.get(i), missEdges);
                    crs.selectedResults2(idList.get(i), missEdges, theMissedEdgeLabel);
                    if (missEdges.size() == i) {
                        //missEdges.add(0);
                        break;
                    }
                    CompareResult compareR = new CompareResult();
                    compareR.index = i;
                    compareR.missEdges = missEdges.get(i);
                    compareR.theMissed = theMissedEdgeLabel.get(i);
                    compareEdges.add(compareR);
                }
                if (missEdges.size() != 0) {

                    CompareResult[] tempArray = new CompareResult[compareEdges.size()];
                    for (int i = 0; i < compareEdges.size(); i++) {
                        tempArray[i] = compareEdges.get(i);
                    }
                    for (int i = 0; i < tempArray.length - 1; i++) {
                        for (int j = 0; j < tempArray.length - i - 1; j++) {
                            if (tempArray[j].missEdges > tempArray[j + 1].missEdges) {
                                CompareResult tmp = tempArray[j];
                                tempArray[j] = tempArray[j + 1];
                                tempArray[j + 1] = tmp;
                            }
                        }
                    }
                    List<Integer> selectedList = new ArrayList<Integer>();
                    List<String> selectedListMissEdges = new ArrayList<String>();
                    for (int i = 0; i < missEdges.size(); i++) {
                        if (i < selectk) {
                            // System.out.println("selected index of graph:" + tempArray[i].index);
                            selectedList.add(idList.get(tempArray[i].index));
                            selectedListMissEdges.add(tempArray[i].theMissed);
                        }
                    }
                    mainPanel.remove(contentPanel);
                    mainPanel = createContentPanel2(mainPanel, selectedList, selectedListMissEdges);
                    mainPanel.validate();
                    mainPanel.repaint();
                }

            });
            topBar.add(seltopk);
            topBar.add(simComboBox);
        }

        mainPanel.add(topBar);
        
        mainPanel.add(Box.createVerticalStrut(10));

        //mainpanel add contentpanel
        mainPanel = createContentPanel(mainPanel);
        
        panelRecord.addElement(mainPanel);
        panelContainer.add(mainPanel);
        panelContainer.validate();
        panelContainer.repaint();
        return panelContainer;
    }

    public JPanel createContentPanel(JPanel mainPanel) {
        //  3.contentPanel 
        contentPanel = new JScrollPane();
        contentPanel.setPreferredSize(new Dimension(Long_panelContainer, Width_panelContainer));
       
        //contentPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        contentPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
       
        
        JPanel subview = new JPanel();
        
        //subview.setLayout(new BoxLayout(subview, BoxLayout.Y_AXIS));
        subview.setLayout(new BoxLayout(subview, BoxLayout.X_AXIS));

        int startindex = count * selecttopk + selectid;
        int endindex = count * selecttopk + selecttopk + selectid;
        //System.out.println(startindex + "----" + endindex);
        if (startindex > idList.size()) {
            startindex = idList.size();
        }
        if (endindex > idList.size()) {
            endindex = idList.size();
        }
        if (startindex < 0) {
            startindex = 0;
        }
        if (endindex < 0) {
            endindex = 0;
        }
        if (!isSimilar) {
            for (int i = startindex; i < endindex; i++) {
                ZGRViewer viewer = new ZGRViewer(crs, i);
                JPanel pane = viewer.getPanelView();
                pane.setPreferredSize(new Dimension(Long_each, Width_each));
                JScrollPane jp = new JScrollPane(pane);
                jp.setPreferredSize(new Dimension(Long_each, Width_each));
                
                //jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
                //jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getValue() / 2);
               
                
                jp.getHorizontalScrollBar().setValue(jp.getHorizontalScrollBar().getMaximum());
                jp.getHorizontalScrollBar().setValue(jp.getHorizontalScrollBar().getValue()/2);
                
                subview.add(jp);
                File dotFile = crs.selectedResults(idList.get(i), null);
                
               // viewer.gvLdr.loadFile(dotFile, DOTManager.NEATO_PROGRAM, false);
                
                viewer.gvLdr.loadFile(dotFile, DOTManager.DOT_PROGRAM, false);
                
            }
        } else {
            for (int i = startindex; i < endindex; i++) {
                int index = i;
                ZGRViewer viewer = new ZGRViewer(crs, i);
                JPanel pane = viewer.getPanelView();
                pane.setPreferredSize(new Dimension(Long_each, Width_each));
                //////////////////////////////////////////////////////////
                JPanel pane2 = new JPanel();
                pane2.setPreferredSize(new Dimension(Long_each, Width_each));
                pane2.setLayout(new BoxLayout(pane2, BoxLayout.X_AXIS));
                JButton alignButton = new JButton("Align");
                alignButton.setMinimumSize(new Dimension(MLONG, 20));
                alignButton.setMaximumSize(new Dimension(MLONG, 20));
                alignButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                });
                pane2.add(alignButton);
                JPanel totalpane = new JPanel();
                totalpane.setPreferredSize(new Dimension(Long_each, Width_each));
                
                //totalpane.setLayout(new BoxLayout(totalpane, BoxLayout.Y_AXIS));
                totalpane.setLayout(new BoxLayout(totalpane, BoxLayout.X_AXIS));
                pane2.setVisible(false);
                
                
                totalpane.add(pane);
                totalpane.add(pane2);
                //////////////////////////////////////////////////////////
                JScrollPane jp = new JScrollPane(totalpane);
                jp.setPreferredSize(new Dimension(Long_each, Width_each));
                
                //jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
                //jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getValue() / 2);
                
                jp.getHorizontalScrollBar().setValue(jp.getHorizontalScrollBar().getMaximum());
                jp.getHorizontalScrollBar().setValue(jp.getHorizontalScrollBar().getValue()/2);
                
                subview.add(jp);
                File dotFile = crs.selectedResults(idList.get(i), null);
                //viewer.gvLdr.loadFile(dotFile, DOTManager.NEATO_PROGRAM, false);
                viewer.gvLdr.loadFile(dotFile, DOTManager.DOT_PROGRAM, false);
            }
        }

        //subview.setPreferredSize(new Dimension(LONG-100,WIDTH-50));
        contentPanel.setViewportView(subview);
        
        
        ///// only display one 
       // mainPanel.removeAll();
        
        
        mainPanel.add(contentPanel);
        //mainPanel.setPreferredSize(new Dimension(LONG-100,WIDTH-50));
        return mainPanel;

    }

    public JPanel screenShotPanel(QueryGraphScene scene, int drawwidth, int drawheight) {
        BufferedImage image = new BufferedImage(drawwidth, drawheight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = image.createGraphics();
        scene.paint(graphics2D);
        try {
            ImageIO.write(image, "jpeg", new File("screenshot.jpeg"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ImageIcon img = new ImageIcon(image);
        JPanel p = new JPanel();
        JLabel picLabel = new JLabel(img);
        p.add(picLabel);
        return p;
    }

    public JPanel highlightMissEdges(List<String> selectedListMissEdges, int alignIndex) {
        List<Record> records = gblendViewRecord.get(runtime - 1).scene.getController().getStepRecord();
        Map<QueryEdge, ConnectionWidget> edgeset = gblendViewRecord.get(runtime - 1).scene.getEdgeMap();
        if (selectedListMissEdges != null && selectedListMissEdges.size() > alignIndex) {
            String missing = selectedListMissEdges.get(alignIndex);
            if (missing != null && missing.length() > 0) {
                String[] missEdgeLabel = missing.replaceAll("\\{|\\}", "").split(",");
                if (missEdgeLabel != null && missEdgeLabel.length > 0) {
                    Map<QueryEdge, ConnectionWidget> tempset = new HashMap<QueryEdge, ConnectionWidget>();
                    for (int k = 0; k < missEdgeLabel.length; k++) {
                        for (int i = 0; i < records.size(); i++) {
                            Record r = records.get(i);
                            if (r.getEdge() != null && String.valueOf(r.getI()).equals(missEdgeLabel[k].trim())) {
                                //System.out.println(r.getEdge().getSrc()+" "+r.getEdge().getTrg()+ " "+ r.getTime() + " " + r.getName() + " " + r.getI());
                                for (Map.Entry<QueryEdge, ConnectionWidget> entry : edgeset.entrySet()) {
                                    if (entry.getKey().getSrc() == r.getEdge().getSrc() && entry.getKey().getTrg() == r.getEdge().getTrg()) {
                                        gblendViewRecord.get(runtime - 1).scene.updateEdgeColor(entry.getKey(), entry.getValue(), new Color(244, 72, 53));
                                        tempset.put(entry.getKey(), entry.getValue());
                                        break;
                                    }
                                }
                                break;
                            } else {
                               // System.out.println("edge null." + " "+ r.getTime() + " " + r.getName() + " " + r.getI());
                            }
                        }
                    }
                    System.out.println("screenShotPanel in time: "+ runtime);
                    JPanel p = screenShotPanel(gblendViewRecord.get(runtime - 1).scene,
                            gblendViewRecord.get(runtime - 1).getDrawWidhth(), gblendViewRecord.get(runtime - 1).getDrawHeight());
                    //restore 
                    for (Map.Entry<QueryEdge, ConnectionWidget> entry : tempset.entrySet()) {
                        gblendViewRecord.get(runtime - 1).scene.updateEdgeColor(entry.getKey(), entry.getValue(), Color.BLACK);
                    }
                    return p;
                }
            }
        }
        return null;
    }

    public JPanel createContentPanel2(JPanel mainPanel, List<Integer> selectedList, List<String> selectedListMissEdges) {
        //  3.contentPanel 
        contentPanel = new JScrollPane();
        contentPanel.setPreferredSize(new Dimension(Long_panelContainer, Width_panelContainer));
        contentPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JPanel subview = new JPanel();
        subview.setLayout(new BoxLayout(subview, BoxLayout.Y_AXIS));

        for (int i = 0; i < selectedList.size(); i++) {
            int alignIndex = i;
            ZGRViewer viewer = new ZGRViewer(crs, i);
            JPanel pane = viewer.getPanelView();
            pane.setPreferredSize(new Dimension(Long_each, Width_each));
            //////////////////////////////////////////////////////////
            JPanel pane2 = new JPanel();
            pane2.setPreferredSize(new Dimension(Long_each, Width_each));
            pane2.setLayout(new BoxLayout(pane2, BoxLayout.X_AXIS));
            JButton alignButton = new JButton("Align");
            alignButton.setMinimumSize(new Dimension(MLONG, 20));
            alignButton.setMaximumSize(new Dimension(MLONG, 20));
            alignButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    
                }
            });
            pane2.add(alignButton);
            pane2.setVisible(false);
            JPanel totalpane = new JPanel();
            totalpane.setPreferredSize(new Dimension(Long_panelContainer, Width_panelContainer));
            totalpane.setLayout(new BoxLayout(totalpane, BoxLayout.X_AXIS));
            totalpane.add(pane);
            totalpane.add(pane2);
            //////////////////////////////////////////////////////////
            JScrollPane jp = new JScrollPane(totalpane);
            jp.setPreferredSize(new Dimension(Long_panelContainer, Width_panelContainer));
            jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
            jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getValue() / 2);
            //jp.getHorizontalScrollBar().setValue(jp.getHorizontalScrollBar().getMaximum());
            //jp.getHorizontalScrollBar().setValue(jp.getHorizontalScrollBar().getValue()/2);
            subview.add(jp);
            File dotFile = crs.selectedResults(selectedList.get(i), null);
            //viewer.gvLdr.loadFile(dotFile, DOTManager.NEATO_PROGRAM, false);
            viewer.gvLdr.loadFile(dotFile, DOTManager.DOT_PROGRAM, false);
        }
        //subview.setPreferredSize(new Dimension(LONG-100,WIDTH-50));
        contentPanel.setViewportView(subview);
        
        
         ///// only display one 
        
         
       //mainPanel.removeAll();
        
        
        mainPanel.add(contentPanel);
        //mainPanel.setPreferredSize(new Dimension(LONG-100,WIDTH-50));
        return mainPanel;

    }

    public JPanel selectContentPanel(JPanel mainPanel, int selectId) {
        changed = false;
        selecttopk = 1;
        topComboBox.setSelectedIndex(0);
        changed = true;
        //  3.contentPanel 
        contentPanel = new JScrollPane();
        contentPanel.setPreferredSize(new Dimension(Long_panelContainer, Width_panelContainer));
        contentPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JPanel subview = new JPanel();
        subview.setLayout(new BoxLayout(subview, BoxLayout.Y_AXIS));

        int startindex = selectId;
        int endindex = selectId;
        if (startindex > idList.size()) {
            startindex = idList.size();
        }
        if (endindex > idList.size()) {
            endindex = idList.size();
        }
        if (startindex < 0) {
            startindex = 0;
        }
        if (endindex < 0) {
            endindex = 0;
        }
        for (int i = startindex; i <= endindex; i++) {
            int index = i;
            if (!isSimilar) {
                ZGRViewer viewer = new ZGRViewer(crs, i);
                JPanel pane = viewer.getPanelView();
                pane.setPreferredSize(new Dimension(Long_each, Width_each));
                //pane.setPreferredSize(new Dimension((int)(LONG*2),(int)(WIDTH*2)));
                JScrollPane jp = new JScrollPane(pane);
                jp.setPreferredSize(new Dimension(Long_each, Width_each));
                jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
                jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getValue() / 2);
                //jp.getHorizontalScrollBar().setValue(jp.getHorizontalScrollBar().getMaximum());
                //jp.getHorizontalScrollBar().setValue(jp.getHorizontalScrollBar().getValue()/2);
                subview.add(jp);
                File dotFile = crs.selectedResults(idList.get(i), null);
                //viewer.gvLdr.loadFile(dotFile, DOTManager.NEATO_PROGRAM, false);
                viewer.gvLdr.loadFile(dotFile, DOTManager.DOT_PROGRAM, false);
            } else {
                ZGRViewer viewer = new ZGRViewer(crs, i);
                JPanel pane = viewer.getPanelView();
                pane.setPreferredSize(new Dimension(Long_each, Width_each));
                //////////////////////////////////////////////////////////
                JPanel pane2 = new JPanel();
                pane2.setPreferredSize(new Dimension(Long_each, Width_each));
                pane2.setLayout(new BoxLayout(pane2, BoxLayout.X_AXIS));
                JButton alignButton = new JButton("Align");
                alignButton.setMinimumSize(new Dimension(MLONG, 20));
                alignButton.setMaximumSize(new Dimension(MLONG, 20));
                alignButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        List<Integer> missEdges = new ArrayList<Integer>();
                        List<String> theMissedEdgeLabel = new ArrayList<String>();
                        crs.selectedResults2(idList.get(index), missEdges, theMissedEdgeLabel);
                        if (missEdges.size() != 0) {
                        }
                    }
                });
                pane2.add(alignButton);
                 pane2.setVisible(false);
               JPanel totalpane = new JPanel();
              totalpane.setPreferredSize(new Dimension(Long_panelContainer, Width_panelContainer));
               totalpane.setLayout(new BoxLayout(totalpane, BoxLayout.X_AXIS));
                totalpane.add(pane);
                totalpane.add(pane2);
                //////////////////////////////////////////////////////////
                JScrollPane jp = new JScrollPane(totalpane);
                jp.setPreferredSize(new Dimension(Long_each, Width_each));
                jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
                jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getValue() / 2);
                //jp.getHorizontalScrollBar().setValue(jp.getHorizontalScrollBar().getMaximum());
                //jp.getHorizontalScrollBar().setValue(jp.getHorizontalScrollBar().getValue()/2);
                subview.add(jp);
                File dotFile = crs.selectedResults(idList.get(i), null);
               // viewer.gvLdr.loadFile(dotFile, DOTManager.NEATO_PROGRAM, false);
                viewer.gvLdr.loadFile(dotFile, DOTManager.DOT_PROGRAM, false);
            }

        }
        //subview.setPreferredSize(new Dimension(LONG,WIDTH));
        contentPanel.setViewportView(subview);
       
        
        
         ///// only display one 
        //mainPanel.removeAll();
        
        
        
        mainPanel.add(contentPanel);
        //mainPanel.setPreferredSize(new Dimension(LONG-50,WIDTH-50));
        return mainPanel;
    }

    /*
    public void initGUI(){
           panelContainer = new JPanel();
           panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.X_AXIS));
           panelContainer.setPreferredSize(new Dimension(LONG-50,selecttopk*WIDTH));
           mainScrollContainer = new JScrollPane();
           mainScrollContainer.setViewportView(panelContainer);
           setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	   setSize(new Dimension(LONG, WIDTH));
	   setContentPane(mainScrollContainer);
	   setVisible(true);
    }*/
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Query Results");

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
            java.util.logging.Logger.getLogger(ExploreTEDUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ExploreTEDUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ExploreTEDUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ExploreTEDUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ExploreTEDUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
