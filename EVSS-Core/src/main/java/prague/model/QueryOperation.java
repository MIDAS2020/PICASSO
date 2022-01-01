package prague.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import prague.query.QueryEdge;
import prague.result.Record;

/**
 * query operation
 *
 * Created by ch.wang on 05 Mar 2014.
 */
public interface QueryOperation {

  void recordNode(int id, int label);

  Record recordEdge(QueryEdge edge);

  @Nullable
  Record deleteEdge(@Nonnull QueryEdge edge);

  QueryEdge getModifySuggestion();

  void deleteNode(int id);

  QueryBuilder getQueryBuilder();

  boolean testConnection();

  int getNextEdgeLabel();
}
