package gblend.pattern;

import java.awt.Component;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

public class DnDTable extends JTable implements DragSourceListener, DragGestureListener {

  //JTable data
  private final Object[][] rows;
  private final String[] columns;

  private final DragSource dragSource;     //source processing <- (source component, drag listener)
  private final MyTransferHandler transferHandler;  //data carrier      <- (export, import)

  public DnDTable(String[] columnNames, Object[][] data, String patternType) {
    super(data, columnNames);

    rows = data;
    columns = columnNames;

    int height = DnDImage.getHeight(patternType);
    setRowHeight(height);

    //Table Render Model
    TableCellRenderer render = getDefaultRenderer(ImageIcon.class);
    setDefaultRenderer(ImageIcon.class, new ButtonRenderer(render));

    //Table Data Model
    setModel(new ImageModel());

    //Drag
    dragSource = new DragSource();
    dragSource.createDefaultDragGestureRecognizer(this,                       //JComponent
                                                  DnDConstants.ACTION_MOVE,   //Action
                                                  this);                      //DragGestureListener

    //Transfer
    transferHandler = new MyTransferHandler();
    this.setTransferHandler(transferHandler);
  }

  /*-------------------------DragGestureListener----------------------------*/
  @Override
  public void dragGestureRecognized(DragGestureEvent dge) {
    //Null data
    if (getValueAt(getSelectedRow(), getSelectedColumn()) == null) {
      return;
    }
    //Not desirable data type
    else if (!(getValueAt(getSelectedRow(), getSelectedColumn()) instanceof DnDImage)) {
      return;
    }

    JComponent tableComp = this;
    Transferable transferable = transferHandler.createTransferable(tableComp);

    dragSource.startDrag(dge,                        //event
                         DragSource.DefaultMoveDrop, //cursor
                         transferable,
                         this);                      //drag source listener
  }

  /*-------------------------DragSourceListener-----------------------------*/
  @Override
  public void dragEnter(DragSourceDragEvent dsde) {
  }

  @Override
  public void dragOver(DragSourceDragEvent dsde) {
  }

  @Override
  public void dragExit(DragSourceEvent dse) {
  }

  @Override
  public void dragDropEnd(DragSourceDropEvent dsde) {
  }

  @Override
  public void dropActionChanged(DragSourceDragEvent dsde) {
  }

  /*-------------------------TransferHandler--------------------------------*/
  class MyTransferHandler extends TransferHandler {

    //Export
    @Override
    public Transferable createTransferable(JComponent tableComponent) {
      DnDTable table = (DnDTable) tableComponent;
      int row = table.getSelectedRow();
      int col = table.getSelectedColumn();

      Object element = table.getValueAt(row, col);
      if (element instanceof DnDImage) {
        DnDImage image = (DnDImage) element;
        StringSelection strSelection = new StringSelection("Pattern" + image.patternId);
        return strSelection;
      }
      return null;
    }
  }

  /*-----------------------DataModel & DataRender----------------------------*/
  class ButtonRenderer implements TableCellRenderer {

    //rendering when data is not of the expected type
    private final TableCellRenderer defaultRenderer;

    public ButtonRenderer(TableCellRenderer defaultRenderer) {
      this.defaultRenderer = defaultRenderer;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row,
                                                   int column) {
      //Render expected type;
      if (value instanceof Component) {
        return (Component) value;
      }
      //Render unexpected type;
      return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                                                           column);
    }
  }//end ButtonRenderer

  private class ImageModel extends AbstractTableModel {

    @Override
    public int getRowCount() {
      return rows.length;
    }

    @Override
    public int getColumnCount() {
      return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      return rows[rowIndex][columnIndex];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }

    @Override
    public Class getColumnClass(int column) {
      return DnDImage.class;
    }

    @Override
    public String getColumnName(int column) {
      return columns[column];
    }
  }//end ButtonModel class
}

