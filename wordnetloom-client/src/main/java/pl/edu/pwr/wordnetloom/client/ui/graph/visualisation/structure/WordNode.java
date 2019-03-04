package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure;

import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.GraphController;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

public class WordNode extends RootNode {

    private final String word;
    private final Dictionary partOfSpeech;
    private final Color color = Color.getHSBColor(245 / 360.0f, 0.38f, 1.0f);

    public WordNode(String word, Dictionary pos) {

        this.word = word;
        partOfSpeech = pos;
    }

    public Dictionary getPartOfSpeech() {
        return partOfSpeech;
    }

    @Override
    public String getLabel() {
        return word;
    }

    @Override
    public Shape getShape() {
       return new Ellipse2D.Float(-40, -20, 80, 40);
    }

    @Override
    public void mouseClick(MouseEvent me, GraphController ui) {
    }

    public Color getColor() {
        return color;
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WordNode) {
            return word.equals(((WordNode) o).word);
        }
        return false;
    }
}
