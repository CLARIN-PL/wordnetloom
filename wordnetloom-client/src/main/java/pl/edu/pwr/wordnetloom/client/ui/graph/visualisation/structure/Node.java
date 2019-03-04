package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.GraphController;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public abstract class Node {

    private NodeDirection nodeDirection = null;

    public NodeDirection getNodeDirection() {
        return nodeDirection;
    }

    private Node node = null;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node, NodeDirection nodeDirection) {
        this.nodeDirection = nodeDirection;
        this.node = node;
    }

    /**
     * Get vertex shape (clickable area of the Vertex).
     *
     * @return shape
     */
    public abstract Shape getShape();

    public abstract void mouseClick(MouseEvent me, GraphController ui);

    /**
     * Transform an absolute screen point to relative vertex point.
     *
     * @param point - screen point
     * @param node - node to transform
     * @param vv - visualisation
     * @return Point2D --- vertex relative point.
     */
    public static Point absToVertexRel(Point point, Node node, VisualizationViewer<Node, Edge> vv) {
        Point2D p = vv.getGraphLayout().transform(node);
        Point2D ps2 = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(point);
        return new Point((int) (ps2.getX() - p.getX()), (int) (ps2.getY() - p.getY()));
    }

    private boolean marked = false;

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean state) {
        marked = state;
    }

    public abstract String getLabel();
}
