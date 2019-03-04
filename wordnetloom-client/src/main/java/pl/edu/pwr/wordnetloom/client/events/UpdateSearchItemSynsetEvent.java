package pl.edu.pwr.wordnetloom.client.events;

import java.util.UUID;

public class UpdateSearchItemSynsetEvent {

    private final UUID sense;

    private final UUID synset;

    public UpdateSearchItemSynsetEvent(UUID synset, UUID sense) {
        this.synset = synset;
        this.sense  = sense;
    }

    public UUID getSynset() {
        return synset;
    }

    public UUID getSense() {
        return sense;
    }
}
