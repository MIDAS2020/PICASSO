/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prague.model;

import prague.graph.lg.ELGraph;
import prague.graph.lg.LGVertex;
import prague.query.SVertex;

public interface QueryBuilder {

  ELGraph<SVertex> getSeed();

  ELGraph<LGVertex> getWholeQuery();

  QueryEngineInterface getQueryEngine();

  void recordQuery();
}

