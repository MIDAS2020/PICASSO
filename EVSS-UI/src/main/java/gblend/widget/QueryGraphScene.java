/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: QueryGraphScene.java
 *
 * Abstract: Response the mouse actions on the plane, such as addition, deletion
 *
 * Current Version: 0.1 Author: Jin Changjiu Modified Date: Jun.16,2009
 */
package gblend.widget;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netbeans.api.visual.action.AcceptProvider;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.TwoStateHoverProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.action.WidgetAction.Chain;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

import java.awt.Color;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.lang.annotation.Target;
import java.util.Map;

import gblend.GBlendView;
import gblend.db.DatabaseInfo;
import gblend.result.ActionChart;
import prague.graph.Graph;
import prague.graph.Vertex;
import prague.model.QueryOperation;
import prague.pattern.Pattern;
import prague.query.QueryEdge;

/**
 * @author cjjin
 */
public class QueryGraphScene extends GraphScene.StringGraph {

    private static final Color RED = new Color(244, 72, 53);
    private static final Color NODE_COLOR = new Color(170, 226, 255);
    private final GBlendView view;
    private final LayerWidget mainLayer = new LayerWidget(this);
    private final LayerWidget connectionLayer = new LayerWidget(this);
    private final WidgetAction moveAction = ActionFactory.createMoveAction();
    private final WidgetAction hoverAction = ActionFactory.createHoverAction(new MyHoverProvider());
    private final DrawEdgeAction drawEdge = new DrawEdgeAction();
    private final QueryController controller;

    private final Map<Integer, NodeWidget> nodeSet = Maps.newHashMap();
    private final Map<QueryEdge, ConnectionWidget> edgeSet = Maps.newHashMap();

    private final ActionChart actionChart = new ActionChart();

    private int nextNodeId = 0;

    public QueryGraphScene(GBlendView view, QueryOperation queryOperation) {
        this.view = view;

        addChild(mainLayer);
        addChild(connectionLayer);

        getActions().addAction(hoverAction);
        getActions().addAction(ActionFactory.createZoomAction());
        getActions().addAction(ActionFactory.createPanAction());
        getActions().addAction(drawEdge);

        DragNodeAcceptProvider dragNodeAP = new DragNodeAcceptProvider();
        getActions().addAction(ActionFactory.createAcceptAction(dragNodeAP));
        createView();
        controller = new QueryController(view, queryOperation,
                (i, e) -> edgeAddLabel(edgeSet.get(e), String.valueOf(i)));
    }
    /////////////////////added/////////////////////////
    public Map<QueryEdge, ConnectionWidget> getEdgeMap() {
        return edgeSet;
    }
    public void updateEdgeColor(QueryEdge edge, ConnectionWidget conWidget,Color color ){
         conWidget.setLineColor(color);
    }

    public Map<Integer, NodeWidget> getNodeSet(){
        return nodeSet;
    }
    ///////////////////////////////////////////////////////
    private void edgeAddLabel(ConnectionWidget cw, String label) {
        LabelWidget labelWidget = new LabelWidget(QueryGraphScene.this, label);
        //System.out.println("!!!!!!!!!!!"+labelWidget.getLabel());
        //labelWidget.setBackground(RED);
        labelWidget.setOpaque(true);
        cw.addChild(labelWidget);
        cw.setConstraint(labelWidget,
                LayoutFactory.ConnectionWidgetLayoutAlignment.CENTER, 0.5f);
    }

    public QueryController getController() {
        return controller;
    }

    // Automatically called after addNode()
    @Override
    protected Widget attachNodeWidget(String label) {
        NodeWidget node = new NodeWidget(this, label, nextNodeId);
        nodeSet.put(nextNodeId++, node);
        Chain chain = node.getActions();
        chain.addAction(drawEdge);
        // mouse-dragged, the event is consumed while mouse is dragged:
        chain.addAction(moveAction);
        // mouse-over, the event is consumed while the mouse is over the widget:
        chain.addAction(hoverAction);
        mainLayer.addChild(node);
        int nodeLabel = DatabaseInfo.convert(label);
        controller.recordNode(node.getId(), nodeLabel);
        return node;
    }

    @Override
    protected Widget attachEdgeWidget(String edge) {
        ConnectionWidget connection = new ConnectionWidget(this);
        connection.setTargetAnchorShape(AnchorShape.NONE);
        //connection.setForeground(RED);
        //connection.setLineColor(RED);
        connectionLayer.addChild(connection);
        return connection;
    }

    @Override
    protected void attachEdgeSourceAnchor(String edge, String oldSourceNode,
            String sourceNode) {
        Widget w = sourceNode != null ? findWidget(sourceNode) : null;
        ((ConnectionWidget) findWidget(edge)).setSourceAnchor(AnchorFactory
                .createRectangularAnchor(w));
    }

    @Override
    protected void attachEdgeTargetAnchor(String edge, String oldTargetNode,
            String targetNode) {
        Widget w = targetNode != null ? findWidget(targetNode) : null;
        ((ConnectionWidget) findWidget(edge)).setTargetAnchor(AnchorFactory
                .createRectangularAnchor(w));
    }

    private NodeWidget addNodeWithLocation(Widget widget, String dragNode,
            Point point) {
        NodeWidget w = (NodeWidget) addNode(dragNode);
        // Local coordination system to the scene coordination system
        w.setPreferredLocation(widget.convertLocalToScene(point));
        return w;
    }

    private void connectQueryNode(NodeWidget n1, NodeWidget n2) {
        QueryEdge edge = new QueryEdge(n1.getId(), n2.getId());
        if (edgeSet.containsKey(edge)) {
            return;
        }
        ConnectionWidget conWidget = drawEdge(n1, n2);
        //conWidget.setLineColor(RED);

        edgeSet.put(edge, conWidget);
        QueryEdge sedge = controller.recordEdge(edge);

        Logger logger = LogManager.getLogger(this);
        // if one edge is suggested to users, change the nodes color
        if (sedge != null) {
            logger.info("QueryEdge Suggestion!=null");
            NodeWidget src = nodeSet.get(sedge.getSrc());
            NodeWidget trg = nodeSet.get(sedge.getTrg());
            logger.info("src:" + sedge.getSrc());
            logger.info("des:" + sedge.getTrg());
            src.setBackground(RED);
            trg.setBackground(RED);

        } else { // set the default colour back
            logger.info("QueryEdge Suggestion==null");
            mainLayer.getChildren().forEach(
                    node -> node.setBackground(NODE_COLOR));
        }
    }

    private ConnectionWidget drawEdge(NodeWidget widget1, NodeWidget widget2) {
        ConnectionWidget conWidget = new ConnectionWidget(this);
        conWidget.setSourceAnchor(AnchorFactory
                .createRectangularAnchor(widget1));
        conWidget.setTargetAnchor(AnchorFactory
                .createRectangularAnchor(widget2));
        conWidget.setTargetAnchorShape(AnchorShape.NONE);

        connectionLayer.addChild(conWidget);
        return conWidget;
    }

  

    private static class MyHoverProvider implements TwoStateHoverProvider {

        @Override
        public void unsetHovering(Widget widget) {
            // widget.setBackground(new Color(170, 226, 255));
            // widget.setForeground(Color.BLACK);
        }

        @Override
        public void setHovering(Widget widget) {
            // widget.setBackground(new Color(164, 211, 238));
            // widget.setForeground(Color.WHITE);
        }
    }

    private class DrawEdgeAction extends WidgetAction.Adapter {

        private NodeWidget w1 = null;
        private NodeWidget w2 = null;

        private void connect(NodeWidget n2) {
            try {
                actionChart.set(String.valueOf(controller.getNextEdgeLabel()));
                connectQueryNode(w1, n2);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public State mouseClicked(Widget widget, WidgetMouseEvent event) {
            if (!(widget instanceof NodeWidget)) { // not click on a node
                if (w1 != null) {
                    w1.setDefaultColor();
                    w1 = null;
                }
                if (w2 != null) {
                    w2.setDefaultColor();
                    w2 = null;
                }
                return State.REJECTED;
            }
            NodeWidget node = (NodeWidget) widget;
            int button = event.getButton();
            if (w1 == null && w2 == null) {
                if (button == MouseEvent.BUTTON1) {
                    w1 = node;
                    node.setBackground(Color.GREEN);
                } else if (button == MouseEvent.BUTTON3) {
                    w2 = node;
                    node.setBackground(Color.RED);
                }
            } else if (w1 != null) {
                w1.setDefaultColor();
                if (button == MouseEvent.BUTTON3 && w1 != node) {
                    connect(node);
                }
                w1 = null;
            } else { // w2 != null
                w2.setDefaultColor();
                if (button == MouseEvent.BUTTON1 && w2 != node) {
                    deleteEdge(w2, node);
                } else if (button == MouseEvent.BUTTON3 && w2 == node) {
                    deleteNode(w2);
                }

                w2 = null;
            }
            return State.CONSUMED;
        }

        private void deleteEdge(NodeWidget n1, NodeWidget n2) {
            try {
                QueryEdge edge = new QueryEdge(n1.getId(), n2.getId());
                if (!edgeSet.containsKey(edge)) {
                    return;
                }
                boolean canDelete = controller.deleteEdge(edge); // only the
                // edge is
                // deleted
                if (canDelete) {
                    ConnectionWidget conWidget = edgeSet.remove(edge);
                    connectionLayer.removeChild(conWidget);
                    actionChart.set(controller.getLastDelete());
                    //added 
                    deleteNode(n1);
                    deleteNode(n2);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void deleteNode(NodeWidget node) {
            try {
                int id = node.getId();
                if (!remainEdgeOnNode(id)) {
                    mainLayer.removeChild(node);
                    nodeSet.remove(id);
                    controller.deleteNode(id);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private boolean remainEdgeOnNode(int nodeId) {
            return Iterables.any(edgeSet.keySet(), edge -> edge.isEnd(nodeId));
        }
    }

    private class DragNodeAcceptProvider implements AcceptProvider {

        @Override
        public ConnectorState isAcceptable(Widget widget, Point point,
                Transferable transferable) {
            return ConnectorState.ACCEPT;
        }

        private void addPattern(Pattern pattern, Widget widget, Point point) {
            /* Draw nodes of the received pattern */
            Graph<? extends Vertex> graph = pattern.getGraph();
            int n = graph.getNodeNum();
            NodeWidget[] nodeWidgets = new NodeWidget[n];
            for (int i = 0; i < n; i++) {
                String label = DatabaseInfo.getLabels()[graph.getNode(i)
                        .getLabel()];

                int x = point.x + pattern.x[i];
                int y = point.y + pattern.y[i];
                Point newPoint = new Point(x, y);
                nodeWidgets[i] = addNodeWithLocation(widget, label, newPoint);
            }

            /* Draw edges of the received pattern */
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (graph.getEdge(i, j) > 0) {
                        connectQueryNode(nodeWidgets[i], nodeWidgets[j]);
                    }
                }
            }
        }

        @Override
        public void accept(Widget widget, Point point, Transferable transferable) {
            try {
                String dragNode = (String) transferable
                        .getTransferData(DataFlavor.stringFlavor);
                if (!dragNode.startsWith("Pattern")) {
                    addNodeWithLocation(widget, dragNode, point);
                } else { // If a new pattern is added
                    int patternId = Integer.parseInt(dragNode.substring(7));
                    Pattern pattern = view.getPattern(patternId);
                    int s = controller.getNextEdgeLabel();
                    addPattern(pattern, widget, point);
                    int e = controller.getNextEdgeLabel() - 1;
                    actionChart.set(String.format("%d-%d", s, e));
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            // System.out.println("Number of nodes: " +
            // queryBuilder.getQueryVertexSet().size());
            // System.out.println("Number of edges: " +
            // queryBuilder.getQueryEdgeSet().size());
            // System.out.println("Number of GUI nodes: " +
            // mainLayer.getChildren().size());
            // System.out.println("Number of GUI edges: " +
            // connectionLayer.getChildren().size());
        }
    }

}
