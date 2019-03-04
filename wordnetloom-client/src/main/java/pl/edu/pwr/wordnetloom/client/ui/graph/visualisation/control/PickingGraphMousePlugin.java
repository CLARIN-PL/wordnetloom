package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.control;

import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.Edge;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 * PickingGraphMousePlugin is an extension of PickingGraphMousePlugin
 * Cursor changed... why JUNG do not allow this?
 */
public class  PickingGraphMousePlugin extends edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin<Node, Edge> {

        public PickingGraphMousePlugin() {
            super(InputEvent.BUTTON1_MASK, InputEvent.BUTTON1_MASK | InputEvent.CTRL_MASK);
            cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
        }

        public void mouseEntered(MouseEvent e) {
            JComponent c = (JComponent) e.getSource();
            //  ViWordNetService s = (ViWordNetService) graphController.getWorkbench().getService(Service.class.getName());
            //  if (s != null && !s.isMakeRelationModeOn()) {
            //      c.setCursor(cursor);
            //  }
        }

        public void mouseExited(MouseEvent e) {
            JComponent c = (JComponent) e.getSource();
            //    ViWordNetService s = (ViWordNetService) graphController.getWorkbench().getService(Service.class.getName());
            //  if (s != null && !s.isMakeRelationModeOn()) {
            //     c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            // }
        }
    }