package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.renderes;

import edu.uci.ics.jung.visualization.picking.PickedInfo;
import org.apache.commons.collections15.Transformer;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.*;

import java.awt.*;

/**
 * Class responsible for setting color of a vertex.
 */
public final class VertexFillColor implements Transformer<Node, Paint> {

    protected PickedInfo<Node> pi;
    protected Node root;

    public final static Color vertexBackgroundColorSelected = Color.yellow;
    public final static Color vertexBackgroundColorRoot = new Color(255, 178, 178);
    public final static Color vertexBackgroundColorMarked = new Color(255, 195, 195);

    public VertexFillColor(PickedInfo<Node> pickedInfo, Node rootNode) {
        this.pi = pickedInfo;
        root = rootNode;
    }

    @Override
    public Paint transform(Node v) {
        if (pi.isPicked(v)) {
            return vertexBackgroundColorSelected;
        } else if (v == root) {
            return vertexBackgroundColorRoot;
        } else if (v.isMarked()) {
            return vertexBackgroundColorMarked;
        } else if (v instanceof WordNode) {
            return ((WordNode) v).getColor();
        } else if (v instanceof SynsetNode) {
            SynsetNode synset = (SynsetNode) v;
            Dictionary partOfSpeech = synset.getPartOfSpeech();
            if (partOfSpeech == null || "".equals(synset.getLabel())) {
                return Color.RED;
            }
            return Color.decode(partOfSpeech.getColor());
        } else if (v instanceof NodeSet) {
            return NodeSet.vertexBackgroundColor;
        }

        return new Color(255, 0, 255);
    }
}
