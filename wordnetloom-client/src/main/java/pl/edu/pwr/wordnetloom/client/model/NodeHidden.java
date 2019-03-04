package pl.edu.pwr.wordnetloom.client.model;

import pl.edu.pwr.wordnetloom.client.service.UuidAdapter;

import javax.json.bind.annotation.JsonbTypeAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NodeHidden {

    private transient String position;

    @JsonbTypeAdapter(UuidAdapter.class)
    private UUID id;

    private Long pos;

    private Long lex;

    private List<String> rel = new ArrayList<>(2);

    private String label;

    public NodeHidden() {
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

    public Optional<List<String>> getOptionalRel() {
        return Optional.ofNullable(rel);
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

    public void setPosition(String position) {
        this.position = position;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setPos(Long pos) {
        this.pos = pos;
    }

    public void setLex(Long lex) {
        this.lex = lex;
    }

    public void setRel(List<String> rel) {
        this.rel = rel;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "NodeHidden{" +
                "position='" + position + '\'' +
                ", id=" + id +
                ", pos=" + pos +
                ", lex=" + lex +
                ", rel=" + rel +
                ", label='" + label + '\'' +
                '}';
    }
}
