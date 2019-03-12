package pl.edu.pwr.wordnetloom.client.events;

import java.net.URI;
import java.util.UUID;

public class LoadGraphEvent {

    private boolean inOpenNewTab;
    private UUID synsetId;
    private URI graphLink;
    private boolean senseMode;

    public LoadGraphEvent(UUID synsetId, boolean senseMode) {
        this.synsetId = synsetId;
    }

    public LoadGraphEvent(URI graphLink, boolean senseMode) {
        this.inOpenNewTab = false;
        this.graphLink = graphLink;
        this.senseMode = senseMode;
    }
    public LoadGraphEvent(URI graphLink, boolean inNewTab, boolean senseMode) {
        this.inOpenNewTab = inNewTab;
        this.graphLink = graphLink;
        this.senseMode = senseMode;
    }
    public LoadGraphEvent(UUID synsetId, boolean inNewTab, boolean senseMode) {
        this.inOpenNewTab = inNewTab;
        this.synsetId = synsetId;
        this.senseMode = senseMode;
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

    public boolean isSenseMode() {
        return senseMode;
    }
}