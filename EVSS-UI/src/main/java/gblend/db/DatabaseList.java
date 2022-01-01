package gblend.db;

import javax.swing.DefaultListModel;

/**
 * @author zhouyong
 */
class DatabaseList extends DefaultListModel<String> {

  private static final long serialVersionUID = 1L;
  private static final String[] STRINGS = new String[0];

  private String[] removedObjects = STRINGS;

  @Override
  public String remove(int index) {
    String obj = super.remove(index);
    if (obj != null) {
      removedObjects = new String[]{obj};
    }
    return obj;
  }

  @Override
  public void removeAllElements() {
    throw new UnsupportedOperationException(
        "Method: removeAllElements is not supported. Use clear()");
  }

  @Override
  public boolean removeElement(Object obj) {
    if (this.contains(obj)) {
      removedObjects = new String[]{(String)obj};
    }
    boolean removed = super.removeElement(obj);
    return removed;
  }

  @Override
  public void removeElementAt(int index) {
    throw new UnsupportedOperationException(
        "Method: removeElementAt is not supported. Use remove(int index)");
  }

  @Override
  public void removeRange(int fromIndex, int toIndex) {
    removedObjects = new String[toIndex - fromIndex + 1];
    for (int i = 0; i < removedObjects.length; i++) {
      removedObjects[i] = super.get(fromIndex + i);
    }
    super.removeRange(fromIndex, toIndex);
  }

  public String[] getRemovedObjects() {
    return removedObjects;
  }

}
