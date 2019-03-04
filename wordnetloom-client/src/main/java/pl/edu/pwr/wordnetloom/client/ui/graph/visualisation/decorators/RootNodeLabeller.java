package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.decorators;

import org.apache.commons.collections15.Transformer;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.Node;

public class RootNodeLabeller implements Transformer<Node, String> {

    @Override
    public String transform(Node node) {
        return (node.getNode() == null ? node.getLabel() : null);
    }

}
