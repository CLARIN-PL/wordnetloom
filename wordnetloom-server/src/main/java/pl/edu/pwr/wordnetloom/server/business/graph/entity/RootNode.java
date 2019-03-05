package pl.edu.pwr.wordnetloom.server.business.graph.entity;

import pl.edu.pwr.wordnetloom.server.business.UuidAdapter;

import javax.json.bind.annotation.JsonbTypeAdapter;
import java.util.UUID;

public class RootNode {

    @JsonbTypeAdapter(UuidAdapter.class)
    private UUID id;

    private long lex;

    private long pos;

    private String label;

    private boolean synsetMode;

    public RootNode() {
    }

    public RootNode( boolean synsetMode) {
        this.synsetMode = synsetMode;
    }

    public RootNode(UUID id, long lex, long pos, String label, boolean synsetMode) {
        this.id = id;
        this.lex = lex;
        this.pos = pos;
        this.label = label;
        this.synsetMode = synsetMode;
    }

    public UUID getId(){
        return id;
    }

    public long getLex() {
        return lex;
    }

    public long getPos() {
        return pos;
    }

    public String getLabel() {
        return label;
    }

    public boolean isSynsetMode() {
        return synsetMode;
    }
}
