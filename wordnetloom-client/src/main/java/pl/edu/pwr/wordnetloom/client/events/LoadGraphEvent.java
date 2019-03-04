package pl.edu.pwr.wordnetloom.client.events;

import java.net.URI;
import java.util.UUID;

public class LoadGraphEvent {

    private boolean inOpenNewTab;
    private UUID synsetId;
    private URI graphLink;

    public LoadGraphEvent(UUID synsetId) {
        this.synsetId = synsetId;
    }

    public LoadGraphEvent(URI graphLink) {
        this.inOpenNewTab = false;
        this.graphLink = graphLink;
    }
    public LoadGraphEvent(URI graphLink, boolean inNewTab) {
        this.inOpenNewTab = inNewTab;
        this.graphLink = graphLink;
    }
    public LoadGraphEvent(UUID synsetId, boolean inNewTab) {
        this.inOpenNewTab = inNewTab;
        this.synsetId = synsetId;
    }

    public boolean isOpenInNewTab() {
        return inOpenNewTab;
    }

    public UUID getSynsetId() {
        return synsetId;
    }

    public URI getGraphLink() {
        return graphLink;
    }

    @Override
    public String toString() {
        return "LoadGraphEvent{" +
                "inOpenNewTab=" + inOpenNewTab +
                ", synsetId=" + synsetId +
                ", graphLink=" + graphLink +
                '}';
    }
}