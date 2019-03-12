package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class SenseRelation {

    private UUID target;

    private UUID source;

    @JsonProperty("relation_type")
    private UUID relationType;

    private String label;

    @JsonProperty("_links")
    private Links links;

    public SenseRelation() {
    }

    public SenseRelation(UUID source, UUID target, UUID relationType) {
        this.target = target;
        this.source = source;
        this.relationType = relationType;
    }

    public UUID getTarget() {
        return target;
    }

    public void setTarget(UUID target) {
        this.target = target;
    }

    public UUID getSource() {
        return source;
    }

    public void setSource(UUID source) {
        this.source = source;
    }

    public UUID getRelationType() {
        return relationType;
    }

    public void setRelationType(UUID relationType) {
        this.relationType = relationType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

}
