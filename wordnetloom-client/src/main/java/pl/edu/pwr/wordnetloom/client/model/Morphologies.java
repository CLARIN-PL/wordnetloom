package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Morphologies {

    @JsonProperty("rows")
    private List<Morphology> morphologies;

    @JsonProperty("_links")
    private Links links;

    @JsonProperty("_actions")
    private List<Action> action;

    public List<Morphology> getMorphologies() {
        return morphologies;
    }

    public void setMorphologies(List<Morphology> morphologies) {
        this.morphologies = morphologies;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public List<Action> getAction() {
        return action;
    }

    public void setAction(List<Action> action) {
        this.action = action;
    }
}
