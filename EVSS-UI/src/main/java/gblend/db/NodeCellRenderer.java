/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package gblend.db;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * @author cjjin
 */
class NodeCellRenderer extends DefaultTreeCellRenderer {

  private static final Font TOP_FONT = new Font("Tahoma", Font.BOLD, 12);
  private static final Font NODE_FONT = new Font("Tahoma", Font.PLAIN, 12);

  private static final long serialVersionUID = 1L;

  @Override
  public Icon getLeafIcon() {
    return null;
  }


  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                boolean expanded, boolean leaf, int row,
                                                boolean hasFocus) {
    super.getTreeCellRendererComponent(tree, value.toString().trim(),
                                       sel, expanded, leaf, row, hasFocus);
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

    if (LoadSchema.isDataSource(node)) {
      super.setFont(TOP_FONT);
      // super.setForeground(Color.ORANGE);
      super.setIcon(null);

      if (sel) {
        super.setOpaque(true);
        super.setBackground(Color.PINK);
      } else {
        super.setOpaque(false);
      }
    } else {
      super.setOpaque(false);
      super.setFont(NODE_FONT);
    }

    return this;
  }
}
