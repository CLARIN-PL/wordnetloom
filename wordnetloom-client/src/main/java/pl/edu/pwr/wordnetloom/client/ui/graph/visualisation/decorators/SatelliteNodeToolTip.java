package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.decorators;

import org.apache.commons.collections15.Transformer;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.Node;

public class SatelliteNodeToolTip<E> implements Transformer<Node, String> {

    private static String MAIN_NODE = "Root";

    @Override
    public String transform(Node arg0) {
        return (arg0.getNode() == null ? MAIN_NODE + " " : "") + arg0.getLabel();
    }

}
