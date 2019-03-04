package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure;

import pl.edu.pwr.wordnetloom.client.model.SynsetRelation;
import pl.edu.pwr.wordnetloom.client.model.RelationType;
import pl.edu.pwr.wordnetloom.client.service.RelationTypeService;

import java.awt.*;
import java.util.UUID;

/**
 * Edge between synsets.
 */
public class SynsetEdge extends Edge {

    private SynsetRelation synsetRelation;
    private SynsetNode s1;
    private SynsetNode s2;

    public SynsetEdge(SynsetRelation synsetRelation) {
        this.synsetRelation = synsetRelation;
    }

    public UUID getRelationId() {
        return synsetRelation.getRelationType();
    }

    public void setParentSynset(SynsetNode s1) {
        this.s1 = s1;
    }

    public void setChildSynset(SynsetNode s2) {
        this.s2 = s2;
    }

    public SynsetNode getParentSynset() {
        return s1;
    }

    public SynsetNode getChildSynset() {
        return s2;
    }

    /**
     * @return child node id
     */
    public UUID getTarget() {
        return synsetRelation.getTarget();
    }

    /**
     * @return parent node id
     */
    public UUID getSource() {
        return synsetRelation.getSource();
    }

    public RelationType getRelationType() {
        return RelationTypeService.getSynsetRelationTypeById(synsetRelation.getRelationType())
                .orElse(null);
    }

    @Override
    public String toString() {
        if( getRelationType() == null) return "";
        return getRelationType().getShortName();
    }

    @Override
    public Color getColor() {
        Color col = synsetRelation.getColor();
        if (col == null) return Color.black;
        return col;
    }

    public SynsetRelation getSynsetRelation() {
        return synsetRelation;
    }

    public void setSynsetRelation(SynsetRelation relation) {
        this.synsetRelation = relation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SynsetEdge)) return false;

        SynsetEdge that = (SynsetEdge) o;

        return synsetRelation != null ? synsetRelation.equals(that.synsetRelation) : that.synsetRelation == null;
    }

    @Override
    public int hashCode() {
        return synsetRelation != null ? synsetRelation.hashCode() : 0;
    }

}
