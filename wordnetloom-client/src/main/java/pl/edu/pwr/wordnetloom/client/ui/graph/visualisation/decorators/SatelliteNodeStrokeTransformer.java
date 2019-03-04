package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.decorators;

import org.apache.commons.collections15.Transformer;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.Node;

import java.awt.*;

public class SatelliteNodeStrokeTransformer
        implements Transformer<Node, Stroke> {

    @Override
    public Stroke transform(Node arg0) {
        return (new BasicStroke((arg0.getNode() == null ? 0.1f : 0.0f)));
    }

}
