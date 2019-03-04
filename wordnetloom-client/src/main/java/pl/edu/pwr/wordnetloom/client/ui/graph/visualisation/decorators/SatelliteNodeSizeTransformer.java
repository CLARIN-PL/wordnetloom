package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.decorators;

import edu.uci.ics.jung.visualization.decorators.SettableVertexShapeTransformer;
import edu.uci.ics.jung.visualization.util.VertexShapeFactory;
import org.apache.commons.collections15.Transformer;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.Node;

import java.awt.*;

public class SatelliteNodeSizeTransformer implements
        SettableVertexShapeTransformer<Node>, Transformer<Node, Shape> {

    protected Transformer<Node, Integer> vsf;
    protected Transformer<Node, Float> varf;
    protected VertexShapeFactory<Node> factory;

    public final static int DEFAULT_ROOT_SIZE = 8;
    public final static int DEFAULT_MARKED_SIZE = 9;
    public final static int DEFAULT_SIZE = 4;
    public final static float DEFAULT_ASPECT_RATIO = 1.0f;

    public SatelliteNodeSizeTransformer(Transformer<Node, Integer> vsf,
                                        Transformer<Node, Float> varf) {
        this.vsf = vsf;
        this.varf = varf;
        factory = new VertexShapeFactory<>(vsf, varf);
    }

    public SatelliteNodeSizeTransformer() {
        this.vsf = (Node arg0) -> (arg0.getNode() == null
                ? DEFAULT_ROOT_SIZE
                : arg0.isMarked()
                        ? DEFAULT_MARKED_SIZE
                        : DEFAULT_SIZE);
        this.varf = (Node arg0) -> DEFAULT_ASPECT_RATIO;
        this.factory = new VertexShapeFactory<>(vsf, varf);
    }

    @Override
    public void setSizeTransformer(Transformer<Node, Integer> vsf) {
        this.vsf = vsf;
        factory = new VertexShapeFactory<>(vsf, varf);
    }

    @Override
    public void setAspectRatioTransformer(Transformer<Node, Float> varf) {
        this.varf = varf;
        factory = new VertexShapeFactory<>(vsf, varf);
    }

    @Override
    public Shape transform(Node arg0) {
        return (arg0.getNode() == null
                ? factory.getEllipse(arg0)
                : (arg0.isMarked()
                        ? factory.getRegularStar(arg0, 5)
                        : factory.getRectangle(arg0)));
    }
}
