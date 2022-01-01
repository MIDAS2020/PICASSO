package gblend.db;

import org.jdesktop.application.Task;

import gblend.GBlendApp;

/**
 * Build the LHS panel for drag-drop
 *
 * Add database name to the list of database
 *
 * @author cjjin
 */
public class AddDBTask extends Task<Void, DatabaseInfo> {

  private final String databaseInfo;
  private final LoadSchema load = new LoadSchema();

  public AddDBTask(String databaseInfo) {
    super(GBlendApp.getApplication());
    this.databaseInfo = databaseInfo;
  }

  @Override
  protected Void doInBackground() throws Exception {
    setMessage("Adding Database: " + databaseInfo);

    // Build the panel on LHS for drag-drop
    load.loadDB(databaseInfo, GBlendApp.getDbManager().getView().getTree());

    // Add DB name to the list of database
    GBlendApp.getDbManager().addDatabase(databaseInfo);
    return null;
  }
}
