package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.renderes;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.BasicEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.BasicEdgeRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public class AstrideLabelRenderer<V, E> implements Renderer.EdgeLabel<V, E> {

    @Override
    public void labelEdge(RenderContext<V, E> rc, Layout<V, E> layout, E e, String label) {
        if (label == null || label.length() == 0) {
            return;
        }

        Graph<V, E> graph = layout.getGraph();
        // don't draw edge if either incident vertex is not drawn
        Pair<V> endpoints = graph.getEndpoints(e);
        if (endpoints == null) {
            return;
        }

        V v2 = endpoints.getFirst();
        V v1 = endpoints.getSecond();

        if (v1 == null || v2 == null) {
            return;
        }

        if (!rc.getEdgeIncludePredicate().evaluate(Context.getInstance(graph, e))) {
            return;
        }

        if (!rc.getVertexIncludePredicate().evaluate(Context.getInstance(graph, v1))
                || !rc.getVertexIncludePredicate().evaluate(Context.getInstance(graph, v2))) {
            return;
        }

        Point2D p1 = layout.transform(v1);
        Point2D p2 = layout.transform(v2);
        p1 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p1);
        p2 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p2);
        float x1 = (float) p1.getX();
        float y1 = (float) p1.getY();
        float x2 = (float) p2.getX();
        float y2 = (float) p2.getY();

        GraphicsDecorator g = rc.getGraphicsContext();
        float dx = x2 - x1;
        float dy = y2 - y1;
        double totalLength = Math.sqrt(dx * dx + dy * dy);

        Shape destVertexShape
                = rc.getVertexShapeTransformer().transform(v2);

        Shape edgeShape = rc.getEdgeShapeTransformer().transform(Context.getInstance(graph, e));

        BasicEdgeRenderer<V, E> ber = new BasicEdgeRenderer<>();

        AffineTransform xf = AffineTransform.getTranslateInstance(x2, y2);
        destVertexShape = xf.createTransformedShape(destVertexShape);

        AffineTransform xff = AffineTransform.getTranslateInstance(x1, y1);

        float thetaRadians = (float) Math.atan2(dy, dx);
        xff.rotate(thetaRadians);
        xff.scale(totalLength, 1.0);
        edgeShape = xff.createTransformedShape(edgeShape);

        AffineTransform att = ber.getEdgeArrowRenderingSupport()
                .getArrowTransform(rc, new GeneralPath(edgeShape), destVertexShape);

        BasicEdgeLabelRenderer<V, E> belr = new BasicEdgeLabelRenderer<>();
        Component component = belr.prepareRenderer(rc, rc.getEdgeLabelRenderer(), label,
                rc.getPickedEdgeState().isPicked(e), e);

        Dimension d = component.getPreferredSize();

        AffineTransform old = g.getTransform();
        AffineTransform xform = new AffineTransform(old);

        xform.concatenate(att);

        double pad = 5.0;

        if (dx < 0) {
            xform.rotate(Math.PI);
            if (dy > 0) {
                xform.translate(pad, -d.height);
            } else {
                xform.translate(pad, 0);
            }
        } else if (dy > 0) {
            xform.translate(-d.width - pad, -d.height);

        } else {
            xform.translate(-d.width - pad, 0);
        }
        g.setTransform(xform);
        g.draw(component, rc.getRendererPane(), 0, 0, d.width, d.height, true);

        g.setTransform(old);
    }

}
