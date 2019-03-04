package pl.edu.pwr.wordnetloom.client.events;

import java.util.UUID;

public class RemoveRelationEvent {

    private final UUID first;
    private final UUID second;

    public RemoveRelationEvent(UUID first, UUID second) {
        this.first = first;
        this.second = second;
    }

    public UUID getFirst() {
        return first;
    }

    public UUID getSecond() {
        return second;
    }
}
