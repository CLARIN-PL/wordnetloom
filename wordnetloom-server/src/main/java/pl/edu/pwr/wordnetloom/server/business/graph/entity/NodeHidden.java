package pl.edu.pwr.wordnetloom.server.business.graph.entity;

import pl.edu.pwr.wordnetloom.server.business.UuidAdapter;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonbPropertyOrder({"id","lex","synsetMode","label","pos", "rel" })
public class NodeHidden {

    private transient String position;

    @JsonbTypeAdapter(UuidAdapter.class)
    private UUID id;

    private Long pos;

    private Long lex;

    @JsonbProperty("synset_mode")
    private boolean synsetMode;

    private List<String> rel = new ArrayList<>(2);

    private String label;

    public NodeHidden(Object position, Object target, Object firstRelation, Object secondRelation,
                      Object label, Object domain, Object pos, Object lexicon, boolean synsetMode) {
        this.position = (String) position;
        this.id = UUID.fromString(target.toString());
        this.label = label != null ? label +" "+ domain : null;
        this.pos = ((BigInteger) pos).longValue();
        this.lex = ((BigInteger) lexicon).longValue();
        rel.add(firstRelation != null ? (String) firstRelation : null);
        rel.add(secondRelation != null ? (String) secondRelation : null);
        this.synsetMode = synsetMode;
    }

    public String getPosition() {
        return position;
    }

    public UUID getId() {
        return id;
    }

    public List<String> getRel() {
        return rel;
    }

    public String getLabel() {
        return label;
    }

    public Long getPos() {
        return pos;
    }

    public Long getLex() {
        return lex;
    }

    public boolean isSynsetMode() {
        return synsetMode;
    }
}
