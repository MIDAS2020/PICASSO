/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 * 
 * File name: LoadSchema.java
 * 
 * Abstract: Load the unique labels in the database
 * 
 * Current Version: 0.1 Author: Jin Changjiu Modified Date: Mar.3, 2010
 */
package gblend.db;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * 
 * @author cjjin
 */
class LoadSchema {

  private static DefaultMutableTreeNode top; // tree of labels on LHS of GUI

  /**
   * 
   * @param dbName database information
   * @param tree to display the list of labels on LHS
   */
  public void loadDB(String dbName, JTree tree) {
    // Set the root of JTree
    top = new DefaultMutableTreeNode(dbName);
    // Add node to the 2nd layer of JTree
    String realLabels[] = DatabaseInfo.getLabels();

    for (String label : realLabels) {
      DefaultMutableTreeNode uniLabel = new DefaultMutableTreeNode(label);
      top.add(uniLabel);
    }
    // cell render
    tree.setModel(new DefaultTreeModel(top));
    tree.setTransferHandler(new TreeTransferHandler()); // DragAndDrop
    tree.setCellRenderer(new NodeCellRenderer());
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
  }

  public static boolean isDataSource(DefaultMutableTreeNode node) {
    return node == top;
  }

}
