package gblend.db;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * @author cjjin
 */
public class DbManager {

  public static final String NO_DATABASE_INFO = "No Database Selected";
  private static final String[] STRINGS = new String[0];
  private static DbManager instance = null;

  private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
  private final DbPanel view;
  private final DefaultComboBoxModel<String> databaseListComboBoxModel;
  private final DatabaseList databaseList = new DatabaseList();
  // bound properties
  private String currentDb;

  private DbManager(DbPanel view) {
    // local parameters
    this.view = view;

    databaseListComboBoxModel = new DefaultComboBoxModel<>(STRINGS);
    view.getDatabaseListComboBox().setModel(databaseListComboBoxModel);

    // bind the currentDb property with the database list combo box
    // the event is fired by selecting a different item in the combo box
    view.getDatabaseListComboBox().addItemListener(new DatabaseListComboBoxItemListener());

    // bind databaseList with the combo box
    databaseList.addListDataListener(new DatabaseListDataListener());

    // bind the currentDb property with the combo box
    // the event is fired from other class
    changeSupport.addPropertyChangeListener(new CurrentDbPropertyChangeListener());

    // set database combo box view and action
    view.getDatabaseListComboBox().setRenderer(new DbComboBoxRenderer());
  }

  public static DbManager getInstance() {
    if (instance == null) {
      instance = new DbManager(new DbPanel());
    }
    return instance;
  }

  public DbPanel getView() {
    return view;
  }

  public void addDatabase(String db) {
    databaseList.addElement(db);
    this.setCurrentDb(db);
  }

  public void setCurrentDb(String currentDb) {
    String oldDb = this.currentDb;
    this.currentDb = currentDb;
    changeSupport.firePropertyChange("currentDb", oldDb, currentDb);
  }

  private class DatabaseListComboBoxItemListener implements ItemListener {

    @Override
    public void itemStateChanged(ItemEvent e) {
      Object item = e.getItem();
      if (item instanceof String) {
        setCurrentDb((String) item);
      } else {
        setCurrentDb(null);
      }
    }

  }

  // database list data lister
  // this listener is invoked whenever a new database is added
  // or an old database is removed
  private class DatabaseListDataListener implements ListDataListener {

    @Override
    public void intervalAdded(ListDataEvent e) {
      int index0 = e.getIndex0();
      int index1 = e.getIndex1();

      for (int i = index0; i <= index1; i++) {
        // DatabaseInfo db = (DatabaseInfo) databaseList.get(i);
        // addDefaultSchemaTree(db);

        databaseListComboBoxModel.addElement(databaseList.get(i));
      }
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
      String[] removedObjects = databaseList.getRemovedObjects();

      if (removedObjects.length == 0) {
        return;
      }
      setCurrentDb(null);

      for (String obj : removedObjects) {
        databaseListComboBoxModel.removeElement(obj);
      }
    }

    @Override
    public void contentsChanged(ListDataEvent e) {

    }
  }

  private class CurrentDbPropertyChangeListener implements PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      String propertyName = evt.getPropertyName();
      if ("currentDb".equals(propertyName)) {
        String db = (String) evt.getNewValue();

        if (db == null) {
          // DefaultMutableTreeNode root = new DefaultMutableTreeNode(SCHEMA_TREE_ROOT);
          // root.add(new DefaultMutableTreeNode(NO_DATABASE_INFO));
          // DefaultTreeModel treeModel = new DefaultTreeModel(root);
          // getView().getSchemaTree().setModel(treeModel);
          getView().getDatabaseListComboBox().setSelectedIndex(0);
        } else {
          // databaseListComboBoxModel.setSelectedItem(db);
          getView().getDatabaseListComboBox().setSelectedItem(db);
        }

        // getView().getSchemaTree().setModel(schemaTreeMapper.get(db));
        // db.setSchemaAvailable(true);
      }
    }
  }
}
