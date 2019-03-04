package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class RelationTests {

    private List<RelationTest> rows = new ArrayList<>();

    @JsonProperty("_links")
    private Links links;

    public List<RelationTest> getRows() {
        return rows;
    }

    public void setRows(List<RelationTest> rows) {
        this.rows = rows;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }
}
