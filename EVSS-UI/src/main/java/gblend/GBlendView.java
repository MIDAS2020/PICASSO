/*
 * Copyright 2009, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: GBlendView.java
 *
 * Abstract: The application's main frame
 *
 * Current Version: 0.1 Author: Jin Changjiu Modified Date: Feb 28,2009
 */
package gblend;

import net.claribole.zgrviewer.ZGRViewer;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import gblend.db.AddDbDialog;
import gblend.db.LoadingDialog;
import gblend.pattern.DnDTable;
import gblend.pattern.GenPatternsDialog;
import gblend.pattern.GenTEDPatternsDialog;
import gblend.pattern.PatternGenerateTask;
import gblend.pattern.TEDPatternGenerateTask;
import gblend.query.ButtonTabComponent;
import gblend.result.ChooseDataSetResults;

import gblend.widget.QueryController;
import gblend.widget.QueryGraphScene;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.openide.util.Exceptions;
import prague.data.DataParams;
import prague.data.QueryProperties;
import prague.index.PragueIndex;
import prague.model.QueryEngineInterface;
import prague.pattern.Pattern;
import prague.pattern.PatternImage;
import prague.pattern.PatternManager;
import prague.pattern.PatternSetInfo;
import prague.query.QueryEdge;
import prague.result.ChooseResultsInterface;
import prague.result.Record;
import net.claribole.zgrviewer.ZGRViewerList;

public class GBlendView extends FrameView {

    private static final String PLEASE_SELECT_DATABASE_FIRST = "Please select the Database first!";
    private static final String QUERY_CONNECTION_MESSAGE = "The query graph must be connected";

    private final ActionListener myActionListener = e -> {
        JButton button = (JButton) e.getSource();
        String patternType = button.getText();

        if (this.patternManager.isLoaded(patternType)) {
            putUpPatterns(patternType);
        }
    };
    private final GBlendAction action;
    private int runtimes = 0;
    public QueryGraphScene scene;
    PragueIndex pragueIndex;
    PatternManager patternManager;
    private boolean databaseLoaded = false;
    private boolean indexBuilt = false;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private int tabNum = 0;
    private int finalNum = 0;
    private ChooseResultsInterface cr;
    private ZGRViewer viewer = null;
    private ZGRViewerList viewerList = new ZGRViewerList();
    private JPanel databasePanel;
    private JScrollPane drawPane;
    private JPanel patternPanel;
    private JScrollPane patternScrollPane;
    private JToolBar patternToolbar;
    private JPanel queryPanel;
    private JToggleButton similarToggleButton;
    private JDialog aboutBox;
    private int drawwidth;
    private int drawheight;

    public GBlendView(SingleFrameApplication app) {
        super(app);
        initComponents();
        initFrame();
        ResourceMap resourceMap = getResourceMap();
        super.getFrame().setIconImage(resourceMap.getImageIcon("logo").getImage());
        action = new GBlendAction(this);
    }

    private void initFrame() {
        databasePanel.add(GBlendApp.getDbManager().getView());
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = GBlendApp.getApplication().getMainFrame();
            aboutBox = new GBlendAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        GBlendApp.getApplication().show(aboutBox);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        JSplitPane mainSplitPane = new JSplitPane();
        databasePanel = new JPanel();
        queryPanel = new JPanel();
        drawPane = new JScrollPane();
        JToolBar mainToolBar = new JToolBar();
        JButton addDatabaseButton = new JButton();
        JToolBar.Separator jSeparator1 = new JToolBar.Separator();
        JButton indexButton = new JButton();
        JButton addQueryButton = new JButton();
        JToolBar.Separator jSeparator2 = new JToolBar.Separator();
        JButton runButton = new JButton();
        JButton TEDrunButton = new JButton();
        JToolBar.Separator jSeparator3 = new JToolBar.Separator();
        JButton resultButton = new JButton();
        JToolBar.Separator jSeparator4 = new JToolBar.Separator();
        similarToggleButton = new JToggleButton();
        JButton genPatternsButton = new JButton();
        JButton genTEDPatternsButton = new JButton();
        JButton sequenceIterateButton = new JButton();
        patternPanel = new JPanel();
        patternToolbar = new JToolBar();
        patternScrollPane = new JScrollPane();
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu();
        JMenuItem openFileMenuItem = new JMenuItem();
        JMenuItem saveFileMenuItem = new JMenuItem();
        JMenuItem exitMenuItem = new JMenuItem();
        JMenu dbMenu = new JMenu();
        JMenuItem dbMenuItem = new JMenuItem();
        JMenuItem showDataSetItem = new JMenuItem();
        JMenuItem showFrequentsItem = new JMenuItem();
        JMenuItem indexMenuItem = new JMenuItem();
        JMenu queryMenu = new JMenu();
        JMenuItem newQueryMenuItem = new JMenuItem();
        JMenuItem runQueryMenuItem = new JMenuItem();
        JMenuItem recordQueryMenuItem = new JMenuItem();
        JMenu viewMenu = new JMenu();
        JMenuItem resultMenuItem = new JMenuItem();
        JMenuItem interactionViewMenuItem = new JMenuItem();
        JMenuItem zoomInMenuItem = new JMenuItem();
        JMenuItem zoomOutMenuItem = new JMenuItem();
        JMenu helpMenu = new JMenu();
        JMenuItem aboutMenuItem = new JMenuItem();
        JPanel statusPanel = new JPanel();
        JSeparator statusPanelSeparator = new JSeparator();

        mainPanel.setName("mainPanel");
        mainPanel.setLayout(new BorderLayout());

        mainSplitPane.setName("mainSplitPane");

        databasePanel.setMinimumSize(new Dimension(150, 0));
        databasePanel.setName("databasePanel");
        databasePanel.setPreferredSize(new Dimension(150, 300));
        databasePanel.setLayout(new BorderLayout());
        mainSplitPane.setLeftComponent(databasePanel);

        queryPanel.setName("queryPanel");
        queryPanel.setLayout(new BorderLayout());

        drawPane.setBorder(BorderFactory.createTitledBorder( null, "Query Designer", TitledBorder.CENTER, TitledBorder.TOP, new Font("times new roman",Font.BOLD,16), Color.BLACK));
        //drawPane.setMinimumSize(new Dimension(300, 300));
        drawPane.setName("drawPane");
        drawPane.setPreferredSize(new Dimension(1000, 800));
        queryPanel.add(drawPane, BorderLayout.PAGE_START);

        mainSplitPane.setRightComponent(queryPanel);

        mainPanel.add(mainSplitPane, BorderLayout.CENTER);

        mainToolBar.setFloatable(false);
        mainToolBar.setRollover(true);
        mainToolBar.setName("mainToolBar");

        ActionMap actionMap = Application.getInstance().getContext().getActionMap(GBlendView.class, this);
        addDatabaseButton.setAction(actionMap.get("addDatabase"));
        addDatabaseButton.setFocusable(false);
        addDatabaseButton.setHorizontalTextPosition(SwingConstants.CENTER);
        addDatabaseButton.setName("addDatabaseButton");
        addDatabaseButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        mainToolBar.add(addDatabaseButton);

        //jSeparator1.setName("jSeparator1"); 
        // mainToolBar.add(jSeparator1);
        indexButton.setAction(actionMap.get("buildIndex"));
        indexButton.setFocusable(false);
        indexButton.setHorizontalTextPosition(SwingConstants.CENTER);
        indexButton.setName("indexButton");
        indexButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        mainToolBar.add(indexButton);

        //jSeparator2.setName("jSeparator2"); 
        // mainToolBar.add(jSeparator2);
        addQueryButton.setAction(actionMap.get("addQuery"));
        addQueryButton.setFocusable(false);
        addQueryButton.setHorizontalTextPosition(SwingConstants.CENTER);
        addQueryButton.setName("addQueryButton");
        addQueryButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        mainToolBar.add(addQueryButton);

        //jSeparator3.setName("jSeparator3"); 
        //mainToolBar.add(jSeparator3);
        runButton.setAction(actionMap.get("run"));
        runButton.setFocusable(false);
        runButton.setHorizontalTextPosition(SwingConstants.CENTER);
        runButton.setName("runButton");
        runButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        mainToolBar.add(runButton);
        
      

        
        //jSeparator4.setName("jSeparator4"); 
        // mainToolBar.add(jSeparator4);
        resultButton.setAction(actionMap.get("showResult"));
        resultButton.setFocusable(false);
        resultButton.setHorizontalTextPosition(SwingConstants.CENTER);
        resultButton.setName("resultButton");
        resultButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        // mainToolBar.add(resultButton);

        similarToggleButton.setIcon(new ImageIcon(getClass().getResource("/gblend/resources/similar.png")));
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(GBlendView.class);
        similarToggleButton.setToolTipText(resourceMap.getString("similarToggleButton.toolTipText"));
        similarToggleButton.setFocusable(false);
        similarToggleButton.setHorizontalTextPosition(SwingConstants.CENTER);
        similarToggleButton.setName("similarToggleButton");
        similarToggleButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        mainToolBar.add(similarToggleButton);

        genPatternsButton.setAction(actionMap.get("generatePatterns"));
        genPatternsButton.setFocusable(false);
        genPatternsButton.setHorizontalTextPosition(SwingConstants.CENTER);
        genPatternsButton.setName("genPatternsButton");
        genPatternsButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        mainToolBar.add(genPatternsButton);
        
        genTEDPatternsButton.setAction(actionMap.get("generateTEDPatterns"));
        genTEDPatternsButton.setFocusable(false);
        genTEDPatternsButton.setHorizontalTextPosition(SwingConstants.CENTER);
        genTEDPatternsButton.setName("genTEDPatternsButton");
        genTEDPatternsButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        mainToolBar.add(genTEDPatternsButton);

        
        
        TEDrunButton.setAction(actionMap.get("TEDrun"));
        TEDrunButton.setFocusable(false);
        TEDrunButton.setHorizontalTextPosition(SwingConstants.CENTER);
        TEDrunButton.setName("TEDrunButton");
        TEDrunButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        mainToolBar.add(TEDrunButton);
        
        sequenceIterateButton.setAction(actionMap.get("sequenceIterate"));
        sequenceIterateButton.setFocusable(false);
        sequenceIterateButton.setHorizontalTextPosition(SwingConstants.CENTER);
        sequenceIterateButton.setName("sequenceIterateButton");
        sequenceIterateButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        //mainToolBar.add(sequenceIterateButton);

        mainPanel.add(mainToolBar, BorderLayout.PAGE_START);
        patternPanel.setBorder(BorderFactory.createTitledBorder( null, "Pattern Set", TitledBorder.CENTER, TitledBorder.TOP, new Font("times new roman",Font.BOLD,16), Color.BLACK));
        patternPanel.setName("patternPanel");
        patternPanel.setPreferredSize(new Dimension(200, 400));
        patternPanel.setLayout(new BorderLayout());

        patternToolbar.setFloatable(false);
        patternToolbar.setRollover(true);
        patternToolbar.setMaximumSize(new Dimension(200, 200));
        patternToolbar.setMinimumSize(new Dimension(25, 25));
        patternToolbar.setName("patternToolbar");
        patternToolbar.setPreferredSize(new Dimension(25, 25));
        patternPanel.add(patternToolbar, BorderLayout.PAGE_START);

        patternScrollPane.setName("patternScrollPane");
        patternPanel.add(patternScrollPane, BorderLayout.CENTER);

        mainPanel.add(patternPanel, BorderLayout.EAST);

        menuBar.setName("menuBar");

        ResourceBundle bundle = ResourceBundle.getBundle("gblend/resources/GBlendView");
        fileMenu.setText(bundle.getString("fileMenu.text"));
        fileMenu.setFont(new Font("times new roman",Font.BOLD,14));
        fileMenu.setName("fileMenu");

        openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
        openFileMenuItem.setText(bundle.getString("openFileMenuItem.text"));
        openFileMenuItem.setFont(new Font("times new roman",Font.BOLD,14));
        openFileMenuItem.setName("openFileMenuItem");
        fileMenu.add(openFileMenuItem);

        saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        saveFileMenuItem.setText(bundle.getString("saveFileMenuItem.text"));
        saveFileMenuItem.setFont(new Font("times new roman",Font.BOLD,14));
        saveFileMenuItem.setName("saveFileMenuItem");
        fileMenu.add(saveFileMenuItem);

        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        exitMenuItem.setText(bundle.getString("exitMenuItem.text"));
        exitMenuItem.setFont(new Font("times new roman",Font.BOLD,14));
        exitMenuItem.setName("exitMenuItem");
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        dbMenu.setText(bundle.getString("dbMenu.text"));
        dbMenu.setFont(new Font("times new roman",Font.BOLD,14));
        dbMenu.setName("dbMenu");

        dbMenuItem.setAction(actionMap.get("addDatabase"));
        dbMenuItem.setText(bundle.getString("dbMenuItem.text"));
        dbMenuItem.setFont(new Font("times new roman",Font.BOLD,14));
        dbMenuItem.setName("dbMenuItem");
        dbMenu.add(dbMenuItem);

        showDataSetItem.setAction(actionMap.get("showDataSet"));
        showDataSetItem.setText(bundle.getString("showDataSetItem.text"));
        showDataSetItem.setFont(new Font("times new roman",Font.BOLD,14));
        showDataSetItem.setName("showDataSetItem");
        dbMenu.add(showDataSetItem);

        showFrequentsItem.setAction(actionMap.get("showFrequents"));
        showFrequentsItem.setText(bundle.getString("showFrequentsItem.text"));
        showFrequentsItem.setFont(new Font("times new roman",Font.BOLD,14));
        showFrequentsItem.setName("showFrequentsItem");
        dbMenu.add(showFrequentsItem);

        indexMenuItem.setAction(actionMap.get("buildIndex"));
        indexMenuItem.setText(bundle.getString("indexMenuItem.text"));
        indexMenuItem.setFont(new Font("times new roman",Font.BOLD,14));
        indexMenuItem.setName("indexMenuItem");
        dbMenu.add(indexMenuItem);

        menuBar.add(dbMenu);

        queryMenu.setText(bundle.getString("queryMenu.text"));
        queryMenu.setFont(new Font("times new roman",Font.BOLD,14));
        queryMenu.setName("queryMenu");

        newQueryMenuItem.setAction(actionMap.get("addQuery"));
        newQueryMenuItem.setText(bundle.getString("newQueryMenuItem.text"));
        newQueryMenuItem.setFont(new Font("times new roman",Font.BOLD,14));
        newQueryMenuItem.setName("newQueryMenuItem");
        queryMenu.add(newQueryMenuItem);

        runQueryMenuItem.setAction(actionMap.get("run"));
        runQueryMenuItem.setText(bundle.getString("runQueryMenuItem.text"));
        runQueryMenuItem.setFont(new Font("times new roman",Font.BOLD,14));
        runQueryMenuItem.setName("runQueryMenuItem");
        queryMenu.add(runQueryMenuItem);

        recordQueryMenuItem.setAction(actionMap.get("recordQuery"));
        recordQueryMenuItem.setText(bundle.getString("recordQueryMenuItem.text"));
        recordQueryMenuItem.setFont(new Font("times new roman",Font.BOLD,14));
        recordQueryMenuItem.setName("recordQueryMenuItem");
        queryMenu.add(recordQueryMenuItem);

        menuBar.add(queryMenu);

        viewMenu.setText(bundle.getString("viewMenu.text"));
        viewMenu.setFont(new Font("times new roman",Font.BOLD,14));
        viewMenu.setName("viewMenu");

        resultMenuItem.setAction(actionMap.get("showResult"));
        resultMenuItem.setText(bundle.getString("resultMenuItem.text"));
        resultMenuItem.setFont(new Font("times new roman",Font.BOLD,14));
        resultMenuItem.setName("resultMenuItem");
        //viewMenu.add(resultMenuItem);

        interactionViewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        interactionViewMenuItem.setText(bundle.getString("interactionViewMenuItem.text"));
        interactionViewMenuItem.setFont(new Font("times new roman",Font.BOLD,14));
        interactionViewMenuItem.setName("interactionViewMenuItem");
        viewMenu.add(interactionViewMenuItem);

        zoomInMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        zoomInMenuItem.setText(bundle.getString("zoomInMenuItem.text"));
        zoomInMenuItem.setFont(new Font("times new roman",Font.BOLD,14));
        zoomInMenuItem.setName("zoomInMenuItem");
        viewMenu.add(zoomInMenuItem);

        zoomOutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        zoomOutMenuItem.setText(bundle.getString("zoomOutMenuItem.text"));
        zoomOutMenuItem.setFont(new Font("times new roman",Font.BOLD,14));
        zoomOutMenuItem.setName("zoomOutMenuItem");
        viewMenu.add(zoomOutMenuItem);

        //menuBar.add(viewMenu);

        helpMenu.setText(bundle.getString("helpMenu.text"));
        helpMenu.setFont(new Font("times new roman",Font.BOLD,14));
        helpMenu.setName("helpMenu");

        aboutMenuItem.setAction(actionMap.get("showAboutBox"));
        aboutMenuItem.setText(bundle.getString("aboutMenuItem.text"));
        aboutMenuItem.setFont(new Font("times new roman",Font.BOLD,14));
        aboutMenuItem.setName("aboutMenuItem");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel");

        statusPanelSeparator.setName("statusPanelSeparator");

        GroupLayout statusPanelLayout = new GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
                statusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(statusPanelSeparator, GroupLayout.DEFAULT_SIZE, 712, Short.MAX_VALUE)
        );
        statusPanelLayout.setVerticalGroup(
                statusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(statusPanelLayout.createSequentialGroup()
                                .addComponent(statusPanelSeparator, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
                                .addGap(17, 17, 17))
        );
      
        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
        
        //added
        this.getFrame().setPreferredSize(new Dimension(1200,750));
        this.getFrame().setMinimumSize(new Dimension(1200,750));
        this.getFrame().setMaximumSize(new Dimension(1200,750));
        this.getFrame().setResizable(false);
       
    }
     @Action
    public void addDatabase() {
        // Show dialog to get parameters
        AddDbDialog dialog = new AddDbDialog(getFrame());
        GBlendApp.getApplication().show(dialog);
        if (!dialog.isOk()) {
            return;
        }
        databaseLoaded = true;
        DataParams parameters = dialog.getParameters();
        action.addDatabase(parameters, new GQueryProperties());
    }

    public void showError(String message) {
        
        JOptionPane.showMessageDialog(getFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Action
    public void addQuery() {
        if (!databaseLoaded || !indexBuilt) { // if didn't choose database, show this dialog
            String message = "Please select the Database and build the index firstly!";
            showError(message);
        } else {
            action.addQuery();
            drawPane.setViewportView(scene.getView());
            queryPanel.add(tabbedPane);
            tabbedPane.removeAll();
            String title = "Query " + (++tabNum);
            tabbedPane.add(title, drawPane);
            tabbedPane.setTabComponentAt(0, new ButtonTabComponent(tabbedPane));
            tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        }
    }

    @Action
    public void run() {
        if (scene == null) {
            return;
        }
        QueryController controller = scene.getController();
        if (!controller.testConnection()) {
            showError(QUERY_CONNECTION_MESSAGE);
            return;
        }
        action.run(controller.getQueryBuilder());
    }
    
     @Action
    public void TEDrun() {
        if (scene == null) {
            return;
        }
        QueryController controller = scene.getController();
        if (!controller.testConnection()) {
            showError(QUERY_CONNECTION_MESSAGE);
            return;
        }
        action.TEDrun(controller.getQueryBuilder());
    }

    //added 
    public QueryGraphScene getViewScene() {
        return scene;
    }

    public void setViewScene(QueryGraphScene s) {
        scene = s;
    }

    public int getDrawWidhth() {
        return drawwidth;
    }

    public int getDrawHeight() {
        return drawheight;
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

    void afterRun(int finalNum, boolean isSimilar, String srTime, ChooseResultsInterface cr) {
        this.finalNum = finalNum;
        this.cr = cr;
        int n = showResultOptionDialog(!isSimilar, srTime);
        // prepare the graph view
        if (n == JOptionPane.YES_OPTION && finalNum != 0) {
            runtimes++;
            drawwidth = drawPane.getWidth() - 10;
            //drawwidth = drawPane.getWidth()-5;
            drawheight = drawPane.getHeight() - 20;
            // drawheight = drawPane.getHeight() -5;
            JPanel p = screenShotPanel(scene, drawwidth, drawheight);
            viewerList.setZGRViewerList(this, cr, runtimes, p, this.getSimilar());
        }
    }
    
      void afterTEDRun(int finalNum, boolean isSimilar, String srTime, ChooseResultsInterface cr) {
        this.finalNum = finalNum;
        this.cr = cr;
        int n = showResultOptionDialog(!isSimilar, srTime);
        // prepare the graph view
        if (n == JOptionPane.YES_OPTION && finalNum != 0) {
            runtimes++;
            drawwidth = drawPane.getWidth() - 10;
            drawheight = drawPane.getHeight() - 20;
            JPanel p = screenShotPanel(scene, drawwidth, drawheight);
            
            viewerList.setZGRViewerListTED(this, cr, runtimes, p, this.getSimilar());
            
           // ZGRViewerList EachviewerList = new ZGRViewerList();
           // EachviewerList.setZGRViewerListTED(this, cr, runtimes, p, this.getSimilar());
        }
    }

    /*
  void afterRun(int finalNum, boolean isSimilar, String srTime, ChooseResultsInterface cr) {
    this.finalNum = finalNum;
    this.cr = cr;

    int n = showResultOptionDialog(!isSimilar, srTime);
    // prepare the graph view
    if (n == JOptionPane.YES_OPTION && finalNum != 0) {
      //viewer = new ZGRViewer(cr);
      boolean explore = true;
      viewer = new ZGRViewer(cr,explore);
    } else {
      viewer = null;
    }
  }
     */
    private int showResultOptionDialog(boolean isExact, String srTime) {
        String typeMsg = isExact ? "Exact Results Number: " : "Similar Results Number: ";
        String msg = typeMsg + finalNum + "\n\n" + srTime;
        String title = "Query Results";
        Object[] options = {"View Results", "Exit"};
        int ot = JOptionPane.YES_NO_OPTION;
        int mt = JOptionPane.QUESTION_MESSAGE;
        int n = JOptionPane.showOptionDialog(getFrame(), msg, title, ot, mt, null, options, options[0]);
        return n;
    }

    /*
  @Action
  public void showResult() {
    if (finalNum != 0) {
      if (viewer != null) {
        viewer.review();
      } else {
        viewer = new ZGRViewer(cr);
      }
    }
  }*/
    @Action
    public Task<Void, QueryEngineInterface> buildIndex() {
        if (!databaseLoaded) { // if didn't choose database, show this dialog
            showError(PLEASE_SELECT_DATABASE_FIRST);
            return null;
        }
        indexBuilt = true;
        return action.buildIndex();
    }

    @Action
    public Task generatePatterns() throws IOException {
        if (!databaseLoaded) { // if didn't choose database, show this dialog
            showError(PLEASE_SELECT_DATABASE_FIRST);
            return null;
        }
        GenPatternsDialog genPatternsDialog = new GenPatternsDialog(this.getFrame());
        GBlendApp.getApplication().show(genPatternsDialog);

        if (!genPatternsDialog.isOk()) {
            return null;
        }

        PatternSetInfo patternSetInfo = genPatternsDialog.getPatternSetInfo();
        PatternGenerateTask task = new PatternGenerateTask(this, patternSetInfo, pragueIndex);
        task.addPropertyChangeListener(new LoadingDialog(this.getFrame(), task));

        return task;
    }
    
    @Action
    public Task generateTEDPatterns() throws IOException {
        if (!databaseLoaded) { // if didn't choose database, show this dialog
            showError(PLEASE_SELECT_DATABASE_FIRST);
            return null;
        }
        GenTEDPatternsDialog genTEDPatternsDialog = new GenTEDPatternsDialog(this.getFrame());
        GBlendApp.getApplication().show(genTEDPatternsDialog);

        if (!genTEDPatternsDialog.isOk()) {
            return null;
        }

        PatternSetInfo patternSetInfo = genTEDPatternsDialog.getPatternSetInfo();
        TEDPatternGenerateTask task = new TEDPatternGenerateTask(this, patternSetInfo, pragueIndex);
        task.addPropertyChangeListener(new LoadingDialog(this.getFrame(), task));

        return task;
    }
    

    public Pattern getPattern(int id) {
        return patternManager.getPattern(id);
    }

    /* Draw buttons to choose the size of the patterns being displayed */
    public void drawPatternSizeButtons() {
        patternToolbar.removeAll();
        patternManager.preparePatterns();
        List<String> patternTypes = patternManager.getPatternTypes();
        if (patternTypes.isEmpty()) {
            return;
        }
        for (String patternType : patternTypes) {
            JButton jButton = new JButton();
            jButton.setText(patternType);
            jButton.setFont(new Font("times new roman",Font.BOLD,14));
            jButton.addActionListener(myActionListener);
            patternToolbar.add(jButton);
        }
        patternToolbar.repaint();
        putUpPatterns(patternTypes.get(0));
    }

    private void putUpPatterns(String patternType) {
        List<PatternImage> imageList = patternManager.getImages(patternType);
        DnDTable table = action.getImageTable(patternType, imageList);

        patternPanel.remove(patternScrollPane);
        patternScrollPane = new JScrollPane(table);
        patternScrollPane.setPreferredSize(new Dimension(200,100));
        patternPanel.add(patternScrollPane, BorderLayout.CENTER);

        patternPanel.revalidate();
    }

    @Action
    public void showDataSet() {
        if (!databaseLoaded) { // if didn't choose database, show this dialog
            showError(PLEASE_SELECT_DATABASE_FIRST);
            return;
        }
        ChooseResultsInterface cr = new ChooseDataSetResults(pragueIndex);
        viewer = new ZGRViewer(cr);
    }

    @Action
    public void showFrequents() {
    }

    @Action
    public void sequenceIterate() {
        if (scene == null) {
            return;
        }
        QueryController controller = scene.getController();
        if (!controller.testConnection()) {
            showError(QUERY_CONNECTION_MESSAGE);
            return;
        }
        action.sequenceIterate(controller.getQueryBuilder());
    }

    public boolean getSimilar() {
        return similarToggleButton.isSelected();
    }

    public void setSimilar(boolean similar) {
        similarToggleButton.setSelected(similar);
    }

    @Action
    public void recordQuery() {
        if (scene != null) {
            scene.getController().getQueryBuilder().recordQuery();
        }
    }

    private class GQueryProperties implements QueryProperties {

        @Override
        public boolean isSimilar() {
            return getSimilar();
        }

        @Override
        public boolean usePreLoad() {
            return true;
        }

        @Override
        public boolean useSimilarVerify() {
            return false;
        }
    }

}
