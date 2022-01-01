/*
 * Copyright 2009, Center for Advanced Information Systems,Nanyang Technological University
 * 
 * File name: GBlendApp.java
 * 
 * Abstract: The main class of GBlend
 * 
 * Current Version: 0.1 Author: Jin Changjiu Modified Date: Jun.16,2009
 */
package gblend;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import gblend.db.DbManager;

/**
 * The main class of the application.
 */
public class GBlendApp extends SingleFrameApplication {

  /**
   * A convenient static getter for the application instance.
   *
   * @return the instance of PVGQApp
   */
  public static GBlendApp getApplication() {
    return Application.getInstance(GBlendApp.class);
  }

  public static DbManager getDbManager() {
    return DbManager.getInstance();
  }

  /**
   * Main method launching the application.
   */
  public static void main(String[] args) {
    launch(GBlendApp.class, args);
  }

  /**
   * At startup create and show the main frame of the application.
   */
  @Override
  protected void startup() {
    show(new GBlendView(this));
  }
}
