package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SenseRelations {

    private String root;

    private List<RelationTree> incoming;
    private List<RelationTree> outgoing;

    @JsonProperty("_links")
    private Links links;

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public List<RelationTree> getIncoming() {
        return incoming;
    }

    public void setIncoming(List<RelationTree> incoming) {
        this.incoming = incoming;
    }

    public List<RelationTree> getOutgoing() {
        return outgoing;
    }

    public void setOutgoing(List<RelationTree> outgoing) {
        this.outgoing = outgoing;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "SenseRelations{" +
                "root='" + root + '\'' +
                ", incoming=" + incoming +
                ", outgoing=" + outgoing +
                '}';
    }
}
