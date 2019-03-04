package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Relations {

    private List<Relation> rows;

    @JsonProperty("_links")
    private Links links;

    public List<Relation> getRows() {
        return rows;
    }

    public void setRows(List<Relation> rows) {
        this.rows = rows;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }
}
