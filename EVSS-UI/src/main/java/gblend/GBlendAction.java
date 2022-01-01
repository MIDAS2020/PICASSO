package gblend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

import gblend.db.AddDBTask;
import gblend.db.BuildIndexTask;
import gblend.db.LoadingDialog;
import gblend.pattern.DnDImage;
import gblend.pattern.DnDTable;
import gblend.result.ChooseResults;
import gblend.widget.QueryGraphScene;
import prague.data.DataParams;
import prague.data.QueryProperties;
import prague.graph.lg.ELGraph;
import prague.graph.lg.LGVertex;
import prague.index.PragueIndex;
import prague.model.QueryBuilder;
import prague.model.QueryEngineInterface;
import prague.model.QueryInterface;
import prague.model.QueryOperation;
import prague.model.RunResult;
import prague.pattern.PatternImage;
import prague.pattern.PatternManager;
import prague.query.BuildQuery;
import prague.query.QueryInterfaceImpl;
import prague.result.ChooseResultsInterface;
import prague.result.MatchedGraph;
import prague.spig.SimpleQueryEngine;
import prague.verify.DataSetVerify;
import wch.guava2.base.Timer;

/**
 * GBlendAction
 *
 * Created by ch.wang on 29 Mar 2014.
 */
class GBlendAction {

	private final Logger logger = LogManager.getLogger(this);

	private final GBlendView view;
	private final NormalRun normalRun = new NormalRun();
	private final DataSetVerify dsv = new DataSetVerify();
	private final SequenceIterateRun sequenceIterateRun = new SequenceIterateRun();
	private QueryInterface qi;

	public GBlendAction(GBlendView view) {
		this.view = view;
	}

        public void addDatabase(DataParams parameters, QueryProperties qp) {
                //logger.info("parameters.getName(): " + parameters.getName());
		AddDBTask task = new AddDBTask(parameters.getName());
		task.addPropertyChangeListener(new LoadingDialog(view.getFrame(), task));
		task.execute();
		view.pragueIndex = new PragueIndex(parameters, qp);
		view.patternManager = new PatternManager(view.pragueIndex.getPath());
		view.drawPatternSizeButtons();
		qi = new QueryInterfaceImpl(view.pragueIndex);
	}

	BuildIndexTask buildIndex() {
		BuildIndexTask task = new BuildIndexTask(view.pragueIndex);
		task.addPropertyChangeListener(new LoadingDialog(view.getFrame(), task));
		return task;
	}

	void addQuery() {
		QueryOperation queryOperation;
		SimpleQueryEngine sqe = new SimpleQueryEngine(view.pragueIndex);
		BuildQuery bq = new BuildQuery(sqe);
		sqe.setQueryBuilder(bq);
		queryOperation = bq;
		view.scene = new QueryGraphScene(view, queryOperation);
	}

	void run(QueryBuilder bq) {
		normalRun.run(bq);
	}
        
        void TEDrun(QueryBuilder bq) {
		normalRun.TEDrun(bq);
	}

	void sequenceIterate(QueryBuilder bq) {
		sequenceIterateRun.run(bq);
	}

	DnDTable getImageTable(String patternType, List<PatternImage> imageList) {
		String[] columnNames = new String[1];
		for (int i = 0; i < columnNames.length; i++) {
			columnNames[i] = "";
		}

		Object[][] data = getImageArray(patternType, imageList);

		DnDTable table = new DnDTable(columnNames, data, patternType);
		return table;
	}

	private Object[][] getImageArray(String patternType,
			List<PatternImage> imageList) {
		Object[][] imageArray = new Object[imageList.size()][1];

		for (int i = 0; i < imageList.size(); i++) {
			int patId = imageList.get(i).getId();
			DnDImage tmpButton = new DnDImage(patternType, imageList.get(i)
					.getFile(), patId);
			imageArray[i][0] = tmpButton;
		}

		return imageArray;
	}

	private abstract class AbstractRun {

		QueryBuilder bq;
		QueryEngineInterface qe;
		ELGraph<LGVertex> query;
		List<MatchedGraph> mGraphs;
		boolean isSimilar;

		protected abstract void action();

		public final void run(QueryBuilder bq) {
			this.bq = bq;
			qe = bq.getQueryEngine();
			query = bq.getWholeQuery();
			isSimilar = false;
			if (query.getEdgeNum() == 0) {
				return;
			}

			Timer timer = new Timer("System Response");

			action();

			String srTime = timer.time();
			logger.info(srTime);

			// prepare the graph view
			ChooseResultsInterface cr = new ChooseResults(qe, mGraphs, query);
			view.afterRun(mGraphs.size(), isSimilar, srTime, cr);
		}
                
                public final void TEDrun(QueryBuilder bq) {
			this.bq = bq;
			qe = bq.getQueryEngine();
			query = bq.getWholeQuery();
			isSimilar = false;
			if (query.getEdgeNum() == 0) {
				return;
			}

			Timer timer = new Timer("System Response");

			action();

			String srTime = timer.time();
			logger.info(srTime);

			// prepare the graph view
			ChooseResultsInterface cr = new ChooseResults(qe, mGraphs, query);
			view.afterTEDRun(mGraphs.size(), isSimilar, srTime, cr);
		}
	}

	private class NormalRun extends AbstractRun {

		@Override
		protected void action() {
			logger.info("NormalRun!!!!!!");
			RunResult result = qi.run(bq);
			mGraphs = result.getMatchedGraphs();
			isSimilar = result.getSigma() > 0;
		}
	}

	protected class SequenceIterateRun extends AbstractRun {

		@Override
		protected void action() {
			mGraphs = dsv.verify(query, qe.getPi().getDataSet());

			int exact = mGraphs.size();
			logger.info("Exact result size: " + exact);
		}
	}
}
