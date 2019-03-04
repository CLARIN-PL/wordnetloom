package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class RelationTypes {

    private List<RelationType> rows = new ArrayList<>();

    @JsonProperty("_links")
    private Links links;

    public List<RelationType> getRows() {
        return rows;
    }

    public void setRows(List<RelationType> rows) {
        this.rows = rows;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }
}
