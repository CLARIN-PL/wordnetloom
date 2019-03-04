package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.listeners;

import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.GraphController;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.Node;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * class VisualisationGraphMouseListener implements GraphMouseListener and handles mouse
 * actions in visualisation panel
 */
public class VisualisationGraphMouseListener implements GraphMouseListener<Node> {

    private GraphController controller;

    public void setController(GraphController controller) {
        this.controller = controller;
    }

    @Override
    public void graphClicked(Node v, MouseEvent me) {
    }

    @Override
    public void graphPressed(Node v, MouseEvent me) {
    }

    @Override
    public void graphReleased(Node v, MouseEvent me) {
        /* the 'everything wrecking jumping graphs' ultimate problem solver */

        // remember node location
        Point2D p1 = (Point2D) controller.getLayout().transform(v).clone();

        // propagate event to nodes
        v.mouseClick(me, controller);

        // find new node location
        Point2D p2 = (Point2D) controller.getLayout().transform(v).clone();

        controller.getVisualizationViewer().getRenderContext().
                getMultiLayerTransformer().getTransformer(Layer.LAYOUT)
                .translate(p1.getX() - p2.getX(), p1.getY() - p2.getY());
        if (me.getButton() == MouseEvent.BUTTON1) {
            controller.vertexSelectionChange(v);
        }
    }

}
