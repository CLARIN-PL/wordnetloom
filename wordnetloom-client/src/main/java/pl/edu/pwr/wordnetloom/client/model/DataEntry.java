package pl.edu.pwr.wordnetloom.client.model;

import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.NodeDirection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataEntry {

    private UUID id;

    private Map<NodeDirection, List<SynsetRelation>> relations;

    private String label;

    private Long partOfSpeech;

    private Long lexicon;

    private boolean isSynset;

    private boolean fullyLoaded = false;

    public DataEntry() {
        relations = new ConcurrentHashMap<>();
        relations.put(NodeDirection.TOP, new ArrayList<>());
        relations.put(NodeDirection.RIGHT, new ArrayList<>());
        relations.put(NodeDirection.LEFT, new ArrayList<>());
        relations.put(NodeDirection.BOTTOM, new ArrayList<>());
    }

    public DataEntry isFullyLoaded(boolean loaded) {
        this.fullyLoaded = loaded;
        return this;
    }

    public DataEntry withSynsetId(UUID id) {
        this.id = id;
        return this;
    }

    public DataEntry withLabel(String label) {
        this.label = label;
        return this;
    }

    public DataEntry withLexicon(Long id) {
        this.lexicon = id;
        return this;
    }

    public DataEntry withPartOfSpeech(Long pos) {
        this.partOfSpeech = pos;
        return this;
    }

    public boolean isFullyLoaded() {
        return fullyLoaded;
    }

    public void setFullyLoaded(boolean fullyLoaded) {
        this.fullyLoaded = fullyLoaded;
    }

    public List<SynsetRelation> getRelations(NodeDirection direction) {
        if (direction != NodeDirection.IGNORE) {
            return relations.get(direction);
        }
        return new ArrayList<>();
    }

    public void setRelations(List<SynsetRelation> relations, NodeDirection direction) {
        if (direction != NodeDirection.IGNORE) {
            this.relations.get(direction).clear();
            this.relations.get(direction).addAll(relations);
        }
    }

    public void addRelation(SynsetRelation relation, NodeDirection direction) {
        if (direction != NodeDirection.IGNORE) {
            if(!relations.get(direction).contains(relation)){
                relations.get(direction).add(relation);
            }
        }
    }

    public UUID getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Long getPartOfSpeech() {
        return partOfSpeech;
    }

    public Long getLexicon() {
        return lexicon;
    }

    public void setLexicon(Long lexicon) {
        this.lexicon = lexicon;
    }

    public boolean isSynset() {
        return isSynset;
    }

    public void setSynset(boolean synset) {
        isSynset = synset;
    }

    @Override
    public String toString() {
        return "DataEntry{" +
                "id=" + id +
                ", relations=" + relations +
                ", label='" + label + '\'' +
                ", partOfSpeech=" + partOfSpeech +
                ", lexicon=" + lexicon +
                '}';
    }
}
