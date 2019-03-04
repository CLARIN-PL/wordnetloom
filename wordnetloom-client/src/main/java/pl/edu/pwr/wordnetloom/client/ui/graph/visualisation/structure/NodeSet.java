package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure;

import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.GraphController;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NodeSet extends Node {

    public final static Color vertexBackgroundColor = Color.getHSBColor(0.25f, 0.5f, 1);
    private final static Shape shape = new Ellipse2D.Float(-20, -20, 40, 40);

    private final Set<SynsetNode> synsets = new HashSet<>();

    public void add(SynsetNode synset) {
        synsets.add(synset);
    }

    public boolean remove(SynsetNode synset) {
        return synsets.remove(synset);
    }

    public void removeAll() {
        synsets.clear();
    }

    public Set<SynsetNode> getSynsets() {
        return Collections.unmodifiableSet(synsets);
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public void mouseClick(MouseEvent me, GraphController ui) {
    }

    @Override
    public String getLabel() {
        return "Zbiór synsetów";
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "zbiór: [[ " + synsets.toString() + "]]";
    }

    public boolean contains(SynsetNode node) {
        return synsets.contains(node);
    }
}
