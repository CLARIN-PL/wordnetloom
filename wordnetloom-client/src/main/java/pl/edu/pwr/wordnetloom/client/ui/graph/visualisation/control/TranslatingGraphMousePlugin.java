package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.control;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.Edge;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.Node;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 * TranslatingGraphMousePlugin is a combination of
 * TranslatingGraphMousePlugin and AnimatedPickingGraphMousePlugin when
 * vertex is clicked it is centered when mouse is dragged, screen translates
 */
public class TranslatingGraphMousePlugin extends edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin {

    public TranslatingGraphMousePlugin() {
        super(InputEvent.BUTTON2_MASK);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TRANSLATING PART
        VisualizationViewer<Node, Edge> vv = (VisualizationViewer<Node, Edge>) e.getSource();
        boolean accepted = checkModifiers(e);
        down = e.getPoint();
        if (accepted) {
            vv.setCursor(cursor);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TRANSLATING PART
        final VisualizationViewer<Node, Edge> vv = (VisualizationViewer<Node, Edge>) e.getSource();
        if (down != null) {
            vv.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        down = null;
    }

}
