package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Users {

    private List<UserSimple> rows;

    @JsonProperty("_links")
    private Links links;

    @JsonProperty("_actions")
    private List<Action> action;

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public List<UserSimple> getRows() {
        return rows;
    }

    public void setRows(List<UserSimple> rows) {
        this.rows = rows;
    }

    public List<Action> getAction() {
        return action;
    }

    public void setAction(List<Action> action) {
        this.action = action;
    }
}
