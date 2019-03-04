package pl.edu.pwr.wordnetloom.client.events;

import java.net.URI;
import java.util.UUID;

public class PathToHyperonymEvent {

    private UUID synsetId;
    private URI pathLink;

    public PathToHyperonymEvent(UUID synsetId) {
        this.synsetId = synsetId;
    }

    public UUID getSynsetId(){
        return synsetId;
    }

    @Override
    public String toString(){
        return "PathToHyperonymEvent{" +
                "synsetId=" + synsetId +
                "}";
    }
}
