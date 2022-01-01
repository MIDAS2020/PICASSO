/*
 * Copyright 2009, Center for Advanced Information Systems,Nanyang Technological University
 * 
 * File name: BuildIndexTask.java
 * 
 * Abstract:
 * 
 * Current Version: 0.1 Author: Jin Changjiu Modified Date: Jun.16,2009
 */

package gblend.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.application.Task;

import gblend.GBlendApp;
import prague.index.IndexBuilder;
import prague.index.PragueIndex;
import prague.model.QueryEngineInterface;

/**
 * @author cjjin
 */

public class BuildIndexTask extends Task<Void, QueryEngineInterface> {

  private final Logger logger = LogManager.getLogger(this);
  private final PragueIndex pragueIndex;

  public BuildIndexTask(PragueIndex pragueIndex) {
    super(GBlendApp.getApplication());
    this.pragueIndex = pragueIndex;
  }

  @Override
  protected Void doInBackground() throws Exception {
    logger.info("begin");
    setMessage("Building Index: ");

    IndexBuilder indexBuilder = new IndexBuilder(pragueIndex);
    indexBuilder.prepareIndex();

    logger.info("end");
    return null;
  }

}
