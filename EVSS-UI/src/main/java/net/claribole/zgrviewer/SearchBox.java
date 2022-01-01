/*
 * FILE: SearchBox.java DATE OF CREATION: Thu Jan 09 15:47:07 2003 Copyright (c) Emmanuel Pietriga,
 * 2002. All Rights Reserved Copyright (c) INRIA, 2004-2011. All Rights Reserved Licensed under the
 * GNU LGPL. For full terms see the file COPYING. $Id: SearchBox.java 4942 2013-02-21 17:26:22Z
 * epietrig $
 */
/*
 * Modified by: Jin Changjiu Modified Date: Dec.05,2009
 */

package net.claribole.zgrviewer;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import prague.result.ChooseResultsInterface;

class SearchBox extends JFrame implements ActionListener {

  private static final long serialVersionUID = 1L;

  private static final int FRAME_WIDTH = 250; // 300
  private static final int FRAME_HEIGHT = 100; // 110

  private GVLoader gvl; // added
  private int cbId = 0; // added

  private JButton prevBt, nextBt;
  private Vector<Integer> idList; // added

  private JComboBox<Integer> idComboBox; // added
  private JLabel posLabel; // added
  private ChooseResultsInterface cr; // added
  private boolean changed = true; // added

  SearchBox(GVLoader gvloader, ChooseResultsInterface crs) {
    super();
    gvl = gvloader;
    cr = crs;
    Container cp = getContentPane();
    cp.setLayout(new GridLayout(2, 1));
    JPanel p1 = new JPanel();
    JPanel p2 = new JPanel();
    cp.add(p1);
    cp.add(p2);
    p1.add(new JLabel("Result Graph:")); // changed

    // changed
    idList = new Vector<>(crs.getList());
    idComboBox = new JComboBox<>(idList);
    idComboBox.setSize(100, 60);

    idComboBox.addItemListener(e -> {
      if (changed) {
        cbId = idComboBox.getSelectedIndex();
        display(cbId);
      }
    });

    p1.add(idComboBox);

    posLabel = new JLabel();
    p1.add(posLabel);

    prevBt = new JButton("Previous");
    p2.add(prevBt);
    prevBt.addActionListener(this);
    nextBt = new JButton("Next");
    p2.add(nextBt);
    nextBt.addActionListener(this);
    // window
    WindowListener w0 = new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        dispose();
      }
    };
    addWindowListener(w0);
    setTitle("Choose Results"); // changed
    pack();
    setSize(FRAME_WIDTH, FRAME_HEIGHT);
    setResizable(false);
    setVisible(true);

    posLabel.setText(1 + "/" + idList.size());
    File dotFile = cr.selectedResults(-1,null);
    gvl.loadFile(dotFile, DOTManager.NEATO_PROGRAM, false);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == prevBt) {
      if (cbId > 0) {
        cbId--;
      } else {
        return;
      }
    } else if (e.getSource() == nextBt) {
      if (cbId + 1 < idList.size()) {
        cbId++;
      } else {
        return;
      }
    }
    changed = false;
    idComboBox.setSelectedIndex(cbId);
    changed = true; // Because the generated graph from itemStateChanged is smaller
    display(cbId);
  }

  private void display(int cbId) {
    posLabel.setText(cbId + 1 + "/" + idList.size());
    try {
      File dotFile = cr.selectedResults(idList.get(cbId),null);
      gvl.reloadFile(dotFile);
    } catch (Exception e1) {
      throw new RuntimeException(e1);
    }
  }
}
