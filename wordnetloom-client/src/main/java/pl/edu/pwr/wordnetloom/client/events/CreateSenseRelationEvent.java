package pl.edu.pwr.wordnetloom.client.events;

import java.util.UUID;

public class CreateSenseRelationEvent {

    private final UUID parentId;

    public CreateSenseRelationEvent(UUID parentId) {
        this.parentId = parentId;
    }

    public UUID getParentId() {
        return parentId;
    }
}
