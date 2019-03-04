package pl.edu.pwr.wordnetloom.client.events;

import pl.edu.pwr.wordnetloom.client.model.Relation;

public class ShowSynsetRelationsEvent {
    private final Relation relation;

    public ShowSynsetRelationsEvent(Relation relation) {
        this.relation = relation;
    }

    public Relation getRelation() {
        return relation;
    }

}
