package pl.edu.pwr.wordnetloom.client.events;

import pl.edu.pwr.wordnetloom.client.model.Sense;

import java.net.URI;

public class ShowSenseRelationsEvent {

    private final Sense sense;

    public ShowSenseRelationsEvent(final Sense sense) {
        this.sense = sense;
    }

    public URI getRelationsLink() {
        return sense.getLinks().getRelations();
    }

    public Sense getSense() {
        return sense;
    }
}
