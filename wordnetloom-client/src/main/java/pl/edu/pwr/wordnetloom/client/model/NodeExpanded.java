package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.edu.pwr.wordnetloom.client.service.UuidAdapter;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeAdapter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NodeExpanded {

    @JsonbTypeAdapter(UuidAdapter.class)
    private UUID id;

    private List<String> rel;

    private Long pos;

    private Long lex;

    @JsonProperty("synset_mode")
    private boolean synsetMode;

    private String label;

    private NodeStructure top;

    private NodeStructure bottom;

    private NodeStructure left;

    private NodeStructure right;

    public NodeExpanded() {
    }

    public void addTopExpanded(NodeExpanded n) {
        if (top == null) {
            top = new NodeStructure();
        }
        top.addExpanded(n);
    }

    public void addTopHidden(NodeHidden n) {
        if (top == null) {
            top = new NodeStructure();
        }
        top.addHidden(n);
    }

    public void addBottomExpanded(NodeExpanded n) {
        if (bottom == null) {
            bottom = new NodeStructure();
        }
        bottom.addExpanded(n);
    }

    public void addBottomHidden(NodeHidden n) {
        if (bottom == null) {
            bottom = new NodeStructure();
        }
        bottom.addHidden(n);
    }

    public void addLeftExpanded(NodeExpanded n) {
        if (left == null) {
            left = new NodeStructure();
        }
        left.addExpanded(n);
    }

    public void addLeftHidden(NodeHidden n) {
        if (left == null) {
            left = new NodeStructure();
        }
        left.addHidden(n);
    }

    public void addRightExpanded(NodeExpanded n) {
        if (right == null) {
            right = new NodeStructure();
        }
        right.addExpanded(n);
    }

    public void addRightHidden(NodeHidden n) {
        if (right == null) {
            right = new NodeStructure();
        }
        right.addHidden(n);
    }

    public NodeStructure getTop() {
        return top;
    }

    public Optional<NodeStructure> getOptionalTop() {
        return Optional.ofNullable(top);
    }

    public NodeStructure getBottom() {
        return bottom;
    }

    public Optional<NodeStructure> getOptionalBottom() {
        return Optional.ofNullable(bottom);
    }

    public NodeStructure getLeft() {
        return left;
    }

    public Optional<NodeStructure> getOptionalLeft() {
        return Optional.ofNullable(left);
    }

    public NodeStructure getRight() {
        return right;
    }

    public Optional<NodeStructure> getOptionalRight() {
        return Optional.ofNullable(right);
    }

    public UUID getId() {
        return id;
    }

    public List<String> getRel() {
        return rel;
    }
    public Optional<List<String>> getOptionalRel() {
        return Optional.of(rel);
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

    @Override
    public String toString() {
        return "NodeExpanded{" +
                "id=" + id +
                ", rel=" + rel +
                ", pos=" + pos +
                ", lex=" + lex +
                ", label='" + label + '\'' +
                ", top=" + top +
                ", bottom=" + bottom +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}


