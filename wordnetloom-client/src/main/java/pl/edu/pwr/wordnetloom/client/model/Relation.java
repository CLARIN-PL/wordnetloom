package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Relation {

    private RelationItem source;

    private RelationItem target;

    @JsonProperty("relation_type")
    private RelationItem relationType;

    @JsonProperty("_links")
    private Links links;

    @JsonProperty("_actions")
    private List<Action> actions;

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public RelationItem getSource() {
        return source;
    }

    public void setSource(RelationItem source) {
        this.source = source;
    }

    public RelationItem getTarget() {
        return target;
    }

    public void setTarget(RelationItem target) {
        this.target = target;
    }

    public RelationItem getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationItem relationType) {
        this.relationType = relationType;
    }

    @Override
    public String toString() {
        return source + " - "+relationType+ " - "+target;
    }
}
