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
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import prague.result.ChooseResultsInterface;

/**
 *
 * @author kaihuang
 */
public class HistoryUI extends javax.swing.JFrame {

    private int runtimes;
    private Vector<List<Integer>> vectorIdList = new Vector<List<Integer>>();
    private JPanel totalContainer;
    private JPanel toppanel;
    private JPanel selectPanel = null;
    private JPanel resultPanel = null;
    private JScrollPane scrollContainer;
    private JPanel panelContainer;
    private static int LONG = 1000;
    private static int WIDTH = 500;
    private static int ScrollLONG = 1000;
    private static int ScrollWIDTH = 400;
    private static int EachLONG = 500;
    private static int EachWIDTH = 400;
    private String time1 = "";
    private String time2 = "";
    private String oper = "";
    private Vector<ChooseResultsInterface> vectorcr = new Vector<ChooseResultsInterface>();
    private int showid;
    private String title = "";

    /**
     * Creates new form HistoryUI
     */
    public HistoryUI() {
        initComponents();
    }

    public HistoryUI(Vector<List<Integer>> idlistvector, int t, Vector<ChooseResultsInterface> cr) {
        initComponents();
        vectorIdList = idlistvector;
        runtimes = t;
        vectorcr = cr;
        initGUI();
    }

    public void initGUI() {

        //1. Label L1
        JLabel L1 = new JLabel("Results in:", JLabel.CENTER);
        L1.setFont(new Font("TimesRoman", 0, 17));
        L1.setMinimumSize(new Dimension(80, 30));
        L1.setMaximumSize(new Dimension(80, 30));
        L1.setPreferredSize(new Dimension(80, 30));
        Vector<String> topList = new Vector<String>();
        for (int i = 1; i <= runtimes; i++) {
            topList.addElement("Timestamp " + String.valueOf(i));
        }
        //2. topComboBox
        JComboBox topComboBox = new JComboBox<>(topList);
        //((JLabel) topComboBox.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
        topComboBox.setBorder(BorderFactory.createEmptyBorder());
        topComboBox.setMinimumSize(new Dimension(110, 30));
        topComboBox.setMaximumSize(new Dimension(110, 30));
        topComboBox.setPreferredSize(new Dimension(110, 30));
        time1 = (String) topComboBox.getItemAt(topComboBox.getSelectedIndex());
        topComboBox.addItemListener(e -> {
            time1 = (String) topComboBox.getItemAt(topComboBox.getSelectedIndex());
        });
        //3. opComboBox 
        Vector<String> operation = new Vector<String>();
        operation.addElement("");
        operation.addElement("In");
        operation.addElement("Not In");
        JComboBox<String> opComboBox = new JComboBox<>(operation);
        opComboBox.setMinimumSize(new Dimension(80, 30));
        opComboBox.setMaximumSize(new Dimension(80, 30));
        opComboBox.setPreferredSize(new Dimension(80, 30));
        oper = (String) opComboBox.getItemAt(opComboBox.getSelectedIndex());
        opComboBox.addItemListener(e -> {
            oper = (String) opComboBox.getItemAt(opComboBox.getSelectedIndex());
        });
        //4. topComboBox2 
        Vector<String> topList2 = new Vector<String>();
        topList2.addElement("");
        for (int i = 1; i <= runtimes; i++) {
            topList2.addElement("Timestamp " + String.valueOf(i));
        }
        JComboBox topComboBox2 = new JComboBox<>(topList2);
        //((JLabel) topComboBox2.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
        topComboBox2.setBorder(BorderFactory.createEmptyBorder());
        topComboBox2.setMinimumSize(new Dimension(110, 30));
        topComboBox2.setMaximumSize(new Dimension(110, 30));
        topComboBox2.setPreferredSize(new Dimension(110, 30));
        time2 = (String) topComboBox2.getItemAt(topComboBox2.getSelectedIndex());
        topComboBox2.addItemListener(e -> {
            time2 = (String) topComboBox2.getItemAt(topComboBox2.getSelectedIndex());
        });
        //5.runButton
        JButton runButton = new JButton("Run");
        runButton.setMinimumSize(new Dimension(80, 30));
        runButton.setMaximumSize(new Dimension(80, 30));
        runButton.setPreferredSize(new Dimension(80, 30));
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                List<Integer> resultIdList = new ArrayList<Integer>();
                List<Integer> resultIdList1 = new ArrayList<Integer>();
                List<Integer> resultIdList2 = new ArrayList<Integer>();
                JPanel subview = new JPanel();
                subview.setLayout(new BoxLayout(subview, BoxLayout.X_AXIS));
                if (time1.length() != 0) {
                    String P1 = "Timestamp\\s+(\\d+)";
                    Pattern r = Pattern.compile(P1);
                    Matcher m = r.matcher(time1);
                    if (m.find()) {
                        int index = Integer.parseInt(m.group(1));
                        System.out.println("index:" + index);
                        resultIdList1 = vectorIdList.get(index - 1);
                    }
                }
                if (time2.length() != 0) {
                    String P2 = "Timestamp\\s+(\\d+)";
                    Pattern r = Pattern.compile(P2);
                    Matcher m = r.matcher(time2);
                    if (m.find()) {
                        int index = Integer.parseInt(m.group(1));
                        System.out.println("index:" + index);
                        resultIdList2 = vectorIdList.get(index - 1);
                    }
                }
                if (oper.length() == 0) {
                    resultIdList = resultIdList1;
                } else if (oper.equals("In")) {
                    for (Integer t : resultIdList1) {
                        if (resultIdList2.contains(t)) {
                            resultIdList.add(t);
                        }
                    }
                } else {
                    for (Integer t : resultIdList1) {
                        if (resultIdList2.contains(t)) {

                        } else {
                            resultIdList.add(t);
                        }
                    }
                }
                Vector<Integer> idL = new Vector<>(resultIdList);
                JComboBox selIdComboBox = new JComboBox<>(idL);
                //((JLabel) selIdComboBox.getRenderer()).setHorizontalAlignment(JLabel.RIGHT);
                selIdComboBox.setBorder(BorderFactory.createEmptyBorder());
                selIdComboBox.setMinimumSize(new Dimension(80, 30));
                selIdComboBox.setMaximumSize(new Dimension(80, 30));
                selIdComboBox.setPreferredSize(new Dimension(80, 30));
                if (idL.size() != 0) {
                    subview.removeAll();
                    panelContainer.removeAll();
                    // panelContainer.removeAll();
                    showid = (int) selIdComboBox.getItemAt(0);
                    for (int i = 0; i < runtimes; i++) {
                        title = "Timestamp:" + String.valueOf(i + 1);
                        ChooseResultsInterface crs = vectorcr.get(i);
                        if (crs.getList().contains(showid)) {
                            ZGRViewer viewer = new ZGRViewer(crs, i);
                            JPanel pane = viewer.getPanelView();
                            pane.setPreferredSize(new Dimension(EachLONG, EachWIDTH));
                            JScrollPane jp = new JScrollPane(pane);
                            if (i != (runtimes - 1)) {
                                jp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), title, TitledBorder.CENTER, TitledBorder.TOP, new Font("TimesRoman", Font.PLAIN, 20)));
                            } else {
                                jp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED, 2), title, TitledBorder.CENTER, TitledBorder.TOP, new Font("TimesRoman", Font.PLAIN, 20)));
                            }
                            //jp.setSize(new Dimension(EachLONG - 50, EachWIDTH - 50));
                            //jp.setMinimumSize(new Dimension(EachLONG - 50, EachWIDTH - 50));
                            // jp.setMaximumSize(new Dimension(EachLONG - 50, EachWIDTH - 50));
                            jp.setPreferredSize(new Dimension(EachLONG, EachWIDTH - 50));
                            jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
                            jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getValue() / 2);
                            subview.add(jp);
                            File dotFile = crs.selectedResults(showid,null);
                            viewer.gvLdr.loadFile(dotFile, DOTManager.NEATO_PROGRAM, false);
                        } else {
                            JPanel pane = new JPanel();
                            JLabel Ltip = new JLabel("Empty!", JLabel.CENTER);
                            Ltip.setFont(new Font("TimesRoman", Font.PLAIN, 18));
                            Ltip.setMaximumSize(new Dimension(EachLONG, EachWIDTH));
                            Ltip.setMinimumSize(new Dimension(EachLONG, EachWIDTH));
                            Ltip.setPreferredSize(new Dimension(EachLONG, EachWIDTH));

                            pane.add(Ltip);
                            pane.setForeground(Color.GRAY);
                            JScrollPane jp = new JScrollPane(pane);
                            if (i != (runtimes - 1)) {
                                jp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), title, TitledBorder.CENTER, TitledBorder.TOP, new Font("TimesRoman", Font.PLAIN, 20)));
                            } else {
                                jp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED, 2), title, TitledBorder.CENTER, TitledBorder.TOP, new Font("TimesRoman", Font.PLAIN, 20)));
                            }
                            //jp.setSize(new Dimension(EachLONG - 50, EachWIDTH - 50));
                            //jp.setMinimumSize(new Dimension(EachLONG - 50, EachWIDTH - 50));
                            //jp.setMaximumSize(new Dimension(EachLONG - 50, EachWIDTH - 50));
                            jp.setPreferredSize(new Dimension(EachLONG, EachWIDTH - 50));
                            jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
                            jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getValue() / 2);
                            subview.add(jp);
                        }

                    }
                    panelContainer.add(subview);
                    panelContainer.setPreferredSize(new Dimension(EachLONG * runtimes, EachWIDTH));
                    panelContainer.validate();
                    panelContainer.repaint();
                    scrollContainer.setViewportView(panelContainer);
                    scrollContainer.validate();
                    scrollContainer.repaint();
                    scrollContainer.getHorizontalScrollBar().setValue(scrollContainer.getHorizontalScrollBar().getMaximum());
                    scrollContainer.getHorizontalScrollBar().setValue(scrollContainer.getHorizontalScrollBar().getValue());

                }
                selIdComboBox.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        subview.removeAll();
                        panelContainer.removeAll();
                        // panelContainer.removeAll();
                        showid = (int) selIdComboBox.getItemAt(selIdComboBox.getSelectedIndex());
                        for (int i = 0; i < runtimes; i++) {
                            title = "Timestamp:" + String.valueOf(i + 1);
                            ChooseResultsInterface crs = vectorcr.get(i);
                            if (crs.getList().contains(showid)) {
                                ZGRViewer viewer = new ZGRViewer(crs, i);
                                JPanel pane = viewer.getPanelView();
                                pane.setPreferredSize(new Dimension(EachLONG, EachWIDTH));
                                JScrollPane jp = new JScrollPane(pane);
                                if (i != (runtimes - 1)) {
                                    jp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), title, TitledBorder.CENTER, TitledBorder.TOP, new Font("TimesRoman", Font.PLAIN, 20)));
                                } else {
                                    jp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED, 2), title, TitledBorder.CENTER, TitledBorder.TOP, new Font("TimesRoman", Font.PLAIN, 20)));
                                }
                                //jp.setSize(new Dimension(EachLONG - 50, EachWIDTH - 50));
                                //jp.setMinimumSize(new Dimension(EachLONG - 50, EachWIDTH - 50));
                                // jp.setMaximumSize(new Dimension(EachLONG - 50, EachWIDTH - 50));
                                jp.setPreferredSize(new Dimension(EachLONG, EachWIDTH - 50));
                                jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
                                jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getValue() / 2);
                                subview.add(jp);
                                File dotFile = crs.selectedResults(showid,null);
                                viewer.gvLdr.loadFile(dotFile, DOTManager.NEATO_PROGRAM, false);
                            } else {
                                JPanel pane = new JPanel();
                                JLabel Ltip = new JLabel("Empty!", JLabel.CENTER);
                                Ltip.setFont(new Font("TimesRoman", Font.PLAIN, 18));
                                Ltip.setMaximumSize(new Dimension(EachLONG, EachWIDTH));
                                Ltip.setMinimumSize(new Dimension(EachLONG, EachWIDTH));
                                Ltip.setPreferredSize(new Dimension(EachLONG, EachWIDTH));

                                pane.add(Ltip);
                                pane.setForeground(Color.GRAY);
                                JScrollPane jp = new JScrollPane(pane);
                                if (i != (runtimes - 1)) {
                                    jp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), title, TitledBorder.CENTER, TitledBorder.TOP, new Font("TimesRoman", Font.PLAIN, 20)));
                                } else {
                                    jp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED, 2), title, TitledBorder.CENTER, TitledBorder.TOP, new Font("TimesRoman", Font.PLAIN, 20)));
                                }
                                //jp.setSize(new Dimension(EachLONG - 50, EachWIDTH - 50));
                                //jp.setMinimumSize(new Dimension(EachLONG - 50, EachWIDTH - 50));
                                //jp.setMaximumSize(new Dimension(EachLONG - 50, EachWIDTH - 50));
                                jp.setPreferredSize(new Dimension(EachLONG, EachWIDTH - 50));
                                jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getMaximum());
                                jp.getVerticalScrollBar().setValue(jp.getVerticalScrollBar().getValue() / 2);
                                subview.add(jp);
                            }

                        }
                        panelContainer.add(subview);
                        panelContainer.setPreferredSize(new Dimension(EachLONG * runtimes, EachWIDTH));
                        panelContainer.validate();
                        panelContainer.repaint();
                        scrollContainer.setViewportView(panelContainer);
                        scrollContainer.validate();
                        scrollContainer.repaint();
                        scrollContainer.getHorizontalScrollBar().setValue(scrollContainer.getHorizontalScrollBar().getMaximum());
                        scrollContainer.getHorizontalScrollBar().setValue(scrollContainer.getHorizontalScrollBar().getValue());

                    }
                });

                //selectPanel.setVisible(false);
                resultPanel.removeAll();
                JLabel Ltotal = new JLabel("Total:", JLabel.CENTER);
                Ltotal.setFont(new Font("TimesRoman", 0, 17));
                Ltotal.setMinimumSize(new Dimension(80, 30));
                Ltotal.setMaximumSize(new Dimension(80, 30));
                Ltotal.setPreferredSize(new Dimension(80, 30));

                JLabel Lcount = new JLabel(String.valueOf(resultIdList.size()), JLabel.LEFT);
                Lcount.setFont(new Font("TimesRoman", 0, 17));
                Lcount.setMinimumSize(new Dimension(80, 30));
                Lcount.setMaximumSize(new Dimension(80, 30));
                Lcount.setPreferredSize(new Dimension(80, 30));

                JLabel L2 = new JLabel("Graph ID:", JLabel.CENTER);
                L2.setFont(new Font("TimesRoman", 0, 17));
                L2.setMinimumSize(new Dimension(80, 30));
                L2.setMaximumSize(new Dimension(80, 30));
                L2.setPreferredSize(new Dimension(80, 30));
                resultPanel.add(Ltotal);
                resultPanel.add(Lcount);
                resultPanel.add(L2);
                resultPanel.add(selIdComboBox);

                toppanel.validate();
                toppanel.repaint();
            }
        });
        toppanel = new JPanel();
        toppanel.setLayout(new BorderLayout());

        selectPanel = new JPanel();
        selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.X_AXIS));
        selectPanel.add(L1);
        selectPanel.add(topComboBox);
        selectPanel.add(opComboBox);
        selectPanel.add(topComboBox2);
        selectPanel.add(runButton);
        toppanel.add(selectPanel, BorderLayout.WEST);

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.X_AXIS));
        toppanel.add(resultPanel, BorderLayout.EAST);

        toppanel.validate();
        toppanel.repaint();

        panelContainer = new JPanel();
        panelContainer.setPreferredSize(new Dimension(ScrollLONG, ScrollWIDTH));
        panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.X_AXIS));
        scrollContainer = new JScrollPane();
        scrollContainer.setViewportView(panelContainer);
        scrollContainer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollContainer.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollContainer.getHorizontalScrollBar().setValue(scrollContainer.getHorizontalScrollBar().getMaximum());
        scrollContainer.getHorizontalScrollBar().setValue(scrollContainer.getHorizontalScrollBar().getValue());

        totalContainer = new JPanel();
        totalContainer.setLayout(new BoxLayout(totalContainer, BoxLayout.Y_AXIS));
        totalContainer.add(toppanel);
        totalContainer.add(scrollContainer);

        totalContainer.validate();
        totalContainer.repaint();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new Dimension(LONG, WIDTH));
        setContentPane(totalContainer);
        setVisible(true);
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
            java.util.logging.Logger.getLogger(HistoryUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HistoryUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HistoryUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HistoryUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HistoryUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
