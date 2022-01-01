package gblend.widget;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gblend.GBlendView;
import gblend.exactquery.QueryType;
import gblend.result.InfoChart;
import prague.model.QueryBuilder;
import prague.model.QueryEngineInterface;
import prague.model.QueryOperation;
import prague.query.QueryEdge;
import prague.result.Record;

/**
 * query controller
 *
 * Created by ch.wang on 05 Mar 2014.
 */
public class QueryController {

	private final QueryOperation queryOperation;
	private final GBlendView view;
	private final InfoChart chart = new InfoChart();
	private final List<Record> stepRecord = new ArrayList<>();
	private final EdgeResultRecorder edgeResultRecorder;
	private String lastDelete;
         
	public QueryController(GBlendView view, QueryOperation queryOperation,
			EdgeResultRecorder edgeResultRecorder) {
		this.view = view;
		this.queryOperation = queryOperation;
		this.edgeResultRecorder = edgeResultRecorder;
	}
        //added
        public List<Record> getStepRecord(){
            return stepRecord;
        }
	public QueryBuilder getQueryBuilder() {
		return queryOperation.getQueryBuilder();
	}

	public void recordNode(int id, int label) {
		queryOperation.recordNode(id, label);
	}

	private int selectDialogue() {
		//String message = "Current query has no exact Results";
                String message = "Current query may have no exact Results";
		String title = "Query Options";
		int optionType = JOptionPane.YES_NO_OPTION;
		int messageType = JOptionPane.INFORMATION_MESSAGE;
		Object[] options = { "Modify the Query", "Similarity Search" };
		int n = 0;
		n = JOptionPane.showOptionDialog(null, message, title, optionType,
				messageType, null, options, options[0]);
		Logger logger = LogManager.getLogger(this);
		logger.info("seelcted option:" + n);
		// int n = JOptionPane.showConfirmDialog(null,
		// message, title,
		// JOptionPane.YES_NO_OPTION);
		return n;
	}

	private void processRecords(Record record) {
		stepRecord.add(record);
		if (record.getEdge() != null) {
			edgeResultRecorder.record(record.getI(), record.getEdge());
		}
		chart.set(stepRecord);
	}
        
	public QueryEdge recordEdge(QueryEdge edge) {
		Record record = queryOperation.recordEdge(edge);
		processRecords(record);
		boolean isExact = !view.getSimilar();
		// this OptionDialogue only appears once
		QueryEngineInterface queryEngine = queryOperation.getQueryBuilder()
				.getQueryEngine();
		Logger logger = LogManager.getLogger(this);
		logger.info("queryEngine.getQueryType()!!!="
				+ queryEngine.getQueryType());
		logger.info("queryEngine.getQueryType()!!!=" + QueryType.Similarity);
		// we can not run verify in each step.
		if (isExact && queryEngine.getQueryType() == QueryType.CommonInfrequent) {
			int n = selectDialogue();
			if (n == 0) {
				return edge; // compute the modification
			} else if (n == 1) {
				view.setSimilar(true); // invoke the similarity search
			}
		}
		return null;
	}
        
	public boolean deleteEdge(@Nonnull QueryEdge edge) {
		Record record = queryOperation.deleteEdge(edge);
		if (record == null) {
			view.showError("query graph must be connected");
			return false;
		}
		processRecords(record);
		lastDelete = record.getName();
		return true;
	}
        
	public String getLastDelete() {
		return lastDelete;
	}

	public int getNextEdgeLabel() {
		return queryOperation.getNextEdgeLabel();
	}

	public QueryEdge getModifySuggestion() {
		return queryOperation.getModifySuggestion();
	}

	public void deleteNode(int id) {
		queryOperation.deleteNode(id);
	}

	public boolean testConnection() {
		return queryOperation.testConnection();
	}

	@FunctionalInterface
	public interface EdgeResultRecorder {

		void record(int i, QueryEdge edge);
	}
}
