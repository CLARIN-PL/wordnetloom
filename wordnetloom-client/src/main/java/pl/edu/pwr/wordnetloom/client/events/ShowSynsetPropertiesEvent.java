package pl.edu.pwr.wordnetloom.client.events;

import java.util.UUID;

public class ShowSynsetPropertiesEvent {

    private final UUID id;

    public ShowSynsetPropertiesEvent(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
