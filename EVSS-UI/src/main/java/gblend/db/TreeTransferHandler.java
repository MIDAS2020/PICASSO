package gblend.db;

/**
 *
 * @author cjjin
 */

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

class TreeTransferHandler extends TransferHandler {

  private static final long serialVersionUID = 1L;

  /**
   * Bundle up the data for export.
   */
  @Override
  protected Transferable createTransferable(JComponent c) {
    JTree jt = (JTree) c;
    TreePath[] paths = jt.getSelectionPaths();
    if (paths != null) {
      int pathCount = paths[0].getPathCount();
      if (pathCount == 2) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
        String nodeLabel = node.toString();
        return new StringSelection(nodeLabel);
      }
    }
    return null;
  }

  /**
   * The list handles both copy and move actions.
   */
  @Override
  public int getSourceActions(JComponent c) {
    return COPY_OR_MOVE;
  }

  /**
   * We only support importing strings.
   */
  @Override
  public boolean canImport(TransferHandler.TransferSupport support) {
    // disable import
    return false;
  }
}
