package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure;


import pl.edu.pwr.wordnetloom.client.model.DataEntry;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.model.SynsetRelation;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.SynsetDataStore;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.GraphController;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class SynsetNode extends RootNode implements Comparable<SynsetNode> {



    public enum State {
        EXPANDED, SEMI_EXPANDED, NOT_EXPANDED;
    }

    protected static SynsetNodeShape geom = new SynsetNodeShape();

    private UUID synsetId;
    private String self;
    private boolean isSynsetMode;

    private NodeSet inSet;
    private boolean hasFrame = false;
    private boolean dirtyCache = true;
    private boolean isFullyLoaded = false;

    private String label = "";
    private String lexiconMarker = "";
    private Dictionary partOfSpeech;
    private SynsetDataStore store;

    private Map<NodeDirection, List<SynsetEdge>> relations = new HashMap<>();
    private Map<NodeDirection, State> states;

    public SynsetNode(UUID synsetId, SynsetDataStore store) {
        this.synsetId = synsetId;
        this.store = store;

        relations.put(NodeDirection.TOP, new ArrayList<>());
        relations.put(NodeDirection.RIGHT, new ArrayList<>());
        relations.put(NodeDirection.LEFT, new ArrayList<>());
        relations.put(NodeDirection.BOTTOM, new ArrayList<>());

        states = new ConcurrentHashMap<>();
        states.put(NodeDirection.TOP, State.NOT_EXPANDED);
        states.put(NodeDirection.BOTTOM, State.NOT_EXPANDED);
        states.put(NodeDirection.LEFT, State.NOT_EXPANDED);
        states.put(NodeDirection.RIGHT, State.NOT_EXPANDED);

        construct();
    }

    public UUID getSynsetId() {
        return synsetId;
    }

    public boolean isFullyLoaded() {
        return isFullyLoaded;
    }

    public void setFullyLoaded(boolean fullyLoaded) {
        isFullyLoaded = fullyLoaded;
    }

    @Override
    public int compareTo(SynsetNode o) {
        return synsetId.compareTo(o.getSynsetId());
    }

    public void setSet(NodeSet set) {
        inSet = set;
    }

    public NodeSet getSet() {
        return inSet;
    }

    @Override
    public Shape getShape() {
        return geom.shape;
    }

    public Shape getButtonArea(NodeDirection dir) {
        return geom.buttons[dir.ordinal()];
    }

    public void construct() {
        relations.values().forEach(List::clear);

        DataEntry dataEntry = store.getById(synsetId);

        partOfSpeech = Dictionaries.getDictionary(Dictionaries.PART_OF_SPEECH_DICTIONARY)
                .stream()
                .filter(d -> d.getId().equals(dataEntry.getPartOfSpeech()))
                .findFirst().orElse(null);
        
        this.isSynsetMode = dataEntry.isSynset();
        
        this.isFullyLoaded = dataEntry.isFullyLoaded();
        addSynsetEdges(dataEntry);
        dirtyCache = false;
    }

    private void addSynsetEdges(DataEntry dataEntry) {
        NodeDirection.stream()
                .forEach(d -> dataEntry.getRelations(d)
                        .forEach(r -> addRelation(r, d)));
    }

    private void addRelation(SynsetRelation relation, NodeDirection direction) {
        relations.get(direction).add(new SynsetEdge(relation));
    }

    public Dictionary getPartOfSpeech() {
        return partOfSpeech;
    }

    public boolean isDirtyCache() {
        return dirtyCache;
    }

    public void setDirtyCache(boolean dirty) {
        this.dirtyCache = dirty;
    }

    @Override
    public void mouseClick(MouseEvent me, GraphController ui) {
        // expansion of synset relations working only on left mouse button
        if (me.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        Point p = absToVertexRel(me.getPoint(), this,
                ui.getVisualizationViewer());

        NodeDirection.stream().forEach(nd -> {

            Collection<SynsetEdge> edges = getRelations(nd);
            if (edges.size() > 0) {
                Area ar = new Area(getButtonArea(nd));

                if (ar.contains(p)) {
                    switch (getState(nd)) {
                        case EXPANDED:
                            hideRelations(ui, nd);
                            break;
                        case SEMI_EXPANDED:
                        case NOT_EXPANDED:
                            showRelations(ui, nd);
                            break;
                    }
                }
            }
        });

    }

    private void showRelations(GraphController ui, NodeDirection rel) {
        ui.showRelation(this, rel);
    }

    private void hideRelations(GraphController ui, NodeDirection rel) {
        ui.setSelectedNode(this);
        ui.hideRelation(this, rel);
        ui.recreateLayout();
    }

    @Override
    public String getLabel() {
        DataEntry dataEntry = store.getById(getSynsetId());
        if (dataEntry != null && dataEntry.getLabel() != null) {
            this.label = dataEntry.getLabel();
        }
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLexiconMarker() {
        DataEntry dataEntry = store.getById(getSynsetId());
        if (dataEntry != null && dataEntry.getLabel() != null) {
            this.lexiconMarker =  "lexicon."+ dataEntry.getLexicon();
        }
        return lexiconMarker;
    }

    public List<SynsetEdge> getRelations(NodeDirection dir) {
        return relations.get(dir);
    }

    public void setState(NodeDirection dir, State state_new) {
        states.replace(dir, state_new);
    }

    public State getState(NodeDirection dir) {
        return states.get(dir);
    }

    public boolean getFrame() {
        return hasFrame;
    }


    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public boolean isSynsetMode() {
        return isSynsetMode;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SynsetNode)) return false;

        SynsetNode that = (SynsetNode) o;

        return synsetId != null ? synsetId.equals(that.synsetId) : that.synsetId == null;
    }

    @Override
    public int hashCode() {
        return synsetId != null ? synsetId.hashCode() : 0;
    }
}
