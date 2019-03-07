package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class YiddishProperties {

    private List<YiddishProperty> rows;

    @JsonProperty("_links")
    private Links links;

    public List<YiddishProperty> getRows() {
        return rows;
    }

    public void setRows(List<YiddishProperty> rows) {
        this.rows = rows;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "YiddishProperties{" +
                "rows=" + rows +
                ", links=" + links +
                '}';
    }
}
