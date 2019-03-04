package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SearchList {

    private Integer start;

    private Integer limit;

    private Integer size;

    @JsonProperty("_links")
    private Links links;

    private List<SearchListItem> rows;

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public List<SearchListItem> getRows() {
        return rows;
    }

    public void setRows(List<SearchListItem> rows) {
        this.rows = rows;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "SearchList{" +
                "start=" + start +
                ", limit=" + limit +
                ", size=" + size +
                ", links=" + links +
                ", rows=" + rows +
                '}';
    }
}

