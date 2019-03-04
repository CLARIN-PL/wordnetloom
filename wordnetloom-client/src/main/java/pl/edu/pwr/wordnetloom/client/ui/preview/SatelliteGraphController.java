/*
    Copyright (C) 2011 Łukasz Jastrzębski, Paweł Koczan, Michał Marcińczuk,
                       Bartosz Broda, Maciej Piasecki, Adam Musiał,
                       Radosław Ramocki, Michał Stanek
    Part of the WordnetLoom

    This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the Free
Software Foundation; either version 3 of the License, or (at your option)
any later version.

    This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.

    See the LICENSE and COPYING files for more details.
 */
package pl.edu.pwr.wordnetloom.client.ui.preview;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.LayoutScalingControl;
import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.GraphController;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.decorators.*;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.renderes.VertexFillColor;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.Edge;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.Node;

import javax.inject.Singleton;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

@Singleton
public class SatelliteGraphController {

    private VisualizationViewer<Node, Edge> satellite;

    private VisualizationViewer<Node, Edge> vv;

    protected GraphController graph;

    private JPanel panel;

    private ScalingControl satelliteScaller = new LayoutScalingControl();

    private final static int WIDTH = 250;
    private final static int HEIGHT = 140;

    public void set(GraphController graph) {

        this.graph = graph;
        this.graph.addGraphChangeListener(this::refresh);

        vv = graph.getVisualizationViewer();

        satellite = new SatelliteVisualizationViewer<>(vv, new Dimension(WIDTH, HEIGHT));
        satellite.getRenderContext().setEdgeDrawPaintTransformer(
                new PickableEdgePaintTransformer<>(satellite.getPickedEdgeState(), Color.black, Color.cyan));

        satellite.getRenderContext().setVertexFillPaintTransformer(new VertexFillColor(vv.getPickedVertexState(), graph.getRootNode()));
        satellite.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<>());

        satellite.scaleToLayout(satelliteScaller);
        panel.add(satellite);
    }

    public JPanel getSatelliteGraphViewer() {
        this.panel = new JPanel();
        this.panel.setSize(WIDTH + 10, HEIGHT + 10);
        return panel;
    }

    /**
     * refresh satellite view
     * <p>
     * TODO: do it better than recreating satellite, probably wont fix TODO:
     * visualisation should be centered in the view, centered at the center of the view
     * not at the root or selected node, probably done, need testing TODO: visualisation
     * should be resized to fill full size of view, something is wrong when
     * visualisation scale is >1, fixed by double scaling
     */
    public void refresh() {
        panel.removeAll();

        // refresh satellite
        refreshSatellite();
        // scale
        // if layout was scaled, scale it to it original size
        if (vv.getRenderContext().getMultiLayerTransformer().getTransformer(
                Layer.LAYOUT).getScaleX() > 1D) {
            (new LayoutScalingControl()).scale(satellite,
                    (1f / (float) satellite.getRenderContext()
                            .getMultiLayerTransformer().getTransformer(
                                    Layer.LAYOUT).getScaleX()),
                    new Point2D.Double());
        }
        // get view bounds
        Dimension vd = satellite.getPreferredSize();
        if (satellite.isShowing()) {
            vd = satellite.getSize();
        }
        // get visualisation layout size
        Dimension ld = satellite.getGraphLayout().getSize();
        // finally scale it if view bounds are different than visualisation layer bounds
        if (!vd.equals(ld)) {
            float heightRatio = (float) (vd.getWidth() / ld.getWidth()), widthRatio = (float) (vd
                    .getHeight() / ld.getHeight());
            satelliteScaller.scale(satellite,
                    (heightRatio < widthRatio ? heightRatio : widthRatio),
                    new Point2D.Double());
        }

        // center
        Point2D q = new Point2D.Double(satellite.getGraphLayout().getSize().getWidth() / 2,
                satellite.getGraphLayout().getSize().getHeight() / 2);
        Point2D lvc = satellite.getRenderContext().getMultiLayerTransformer()
                .inverseTransform(satellite.getCenter());
        satellite.getRenderContext().getMultiLayerTransformer().getTransformer(
                Layer.LAYOUT).translate(lvc.getX() - q.getX(),
                lvc.getY() - q.getY());
        // Create and add a panel for visualisation visualization.
        panel.add(satellite);

        // force repaint of the view
        panel.repaint();
        panel.getParent().repaint();
        panel.updateUI();
    }

    /**
     * refresh satellite
     */
    private void refreshSatellite() {
        // refresh satellite view
        satellite = new SatelliteVisualizationViewer<>(vv,
                new Dimension(WIDTH, HEIGHT));

        // set vertex shape transformer
        satellite.getRenderContext().setVertexShapeTransformer(
                new SatelliteNodeSizeTransformer());

        // set vertex fill paint
        satellite.getRenderContext().setVertexFillPaintTransformer(
                new VertexFillColor(vv.getPickedVertexState(), graph
                        .getRootNode()));

        // set vertex stroke (its vertex border) transformer
        satellite.getRenderContext().setVertexStrokeTransformer(
                new SatelliteNodeStrokeTransformer());

        // set vertex labeler to generate root node label
        satellite.getRenderContext().setVertexLabelTransformer(new RootNodeLabeller());

        // set edge stroke (set width) transformer
        satellite.getRenderContext().setEdgeStrokeTransformer(
                new SatelliteEdgeTransformer());

        // set edge draw
        satellite.getRenderContext().setEdgeDrawPaintTransformer(
                new PickableEdgePaintTransformer<>(satellite
                        .getPickedEdgeState(), Color.black, Color.cyan));

        // set edge shape to line
        satellite.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<>());

        // set edge arrow predicate
        satellite.getRenderContext().setEdgeArrowPredicate(
                new SatelliteEdgeTransformer());

        satellite.getRenderContext().setEdgeIncludePredicate((Context<Graph<Node, Edge>, Edge> context) -> true);

        // set tool tips
        satellite.setVertexToolTipTransformer(new SatelliteNodeToolTip<>());

        // add mouse listener to focus view at click point
        satellite.addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point2D q = satellite.getRenderContext().getMultiLayerTransformer()
                        .inverseTransform(new Point2D.Double(e.getX(), e.getY()));
                Point2D lvc = vv.getRenderContext().getMultiLayerTransformer()
                        .inverseTransform(vv.getCenter());
                vv.getRenderContext().getMultiLayerTransformer().getTransformer(
                        Layer.LAYOUT).translate(lvc.getX() - q.getX(),
                        lvc.getY() - q.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });
    }
}
