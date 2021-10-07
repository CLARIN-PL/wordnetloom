package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Dictionaries {

    private List<Dictionary> rows = new ArrayList<>();

    @JsonProperty("_links")
    private Links links;

    @JsonProperty("_actions")
    private List<Action> actions;

    public List<Dictionary> getRows() {
        return rows;
    }

    public void setRows(List<Dictionary> rows) {
        this.rows = rows;
    }

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
}
