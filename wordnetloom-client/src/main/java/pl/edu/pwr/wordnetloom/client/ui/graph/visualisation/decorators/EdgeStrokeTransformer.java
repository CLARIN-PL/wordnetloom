package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.decorators;

import edu.uci.ics.jung.visualization.RenderContext;
import org.apache.commons.collections15.Transformer;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.Edge;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.SynsetEdge;

import java.awt.*;

public class EdgeStrokeTransformer
        implements Transformer<Edge, Stroke> {

    protected final Stroke solid = new BasicStroke(1);

    @Override
    public Stroke transform(Edge e) {
        if (e instanceof SynsetEdge) {
            SynsetEdge es = (SynsetEdge) e;
            // TODO
            if (es.getRelationId() !=null /*&& es.getRelationId() != 10 && es.getRelationId() != 11*/) {
                return RenderContext.DASHED;
            } else {
                return solid;
            }
        }
        return solid;
    }
}
