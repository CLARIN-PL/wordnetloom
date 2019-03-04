package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.decorators;

import org.apache.commons.collections15.Transformer;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.*;

/**
 * <p>
 * <b>VertexToolTipTransformer</b></p>
 * <p>
 * this class provides tooltips for vertices at visualization viewer<br>
 * tooltips are different for different types of nodes</p>
 */
public class VertexToolTipTransformer implements Transformer<Node, String> {

    @Override
    public String transform(Node vn) {
        String ret = "";
        //if (!ToolTipGenerator.getGenerator().hasEnabledTooltips()) {
        //    return ret;
        // }
        if (vn instanceof SynsetNode) {
            SynsetNode vns = ((SynsetNode) vn);
            ret = vns.getLabel();
        } else if (vn instanceof NodeSet) {
            int syns = 0;

            for (Node v : ((NodeSet) vn).getSynsets()) {
                if (v instanceof SynsetNode) {
                    syns++;
                }
            }

           return  "<html>" +
                    "<b>Liczba synsetów:</b> " + syns +
                    "</html>";
        }
        return ret;
    }
}
