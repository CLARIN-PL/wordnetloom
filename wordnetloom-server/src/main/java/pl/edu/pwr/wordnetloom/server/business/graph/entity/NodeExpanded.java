package pl.edu.pwr.wordnetloom.server.business.graph.entity;

import pl.edu.pwr.wordnetloom.server.business.UuidAdapter;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;
import java.util.List;
import java.util.UUID;

@JsonbPropertyOrder({"id", "lex","synsetMode","label", "pos", "rel", "top", "right", "left", "bottom"})
public class NodeExpanded {

    @JsonbTypeAdapter(UuidAdapter.class)
    private UUID id;

    private List<String> rel;

    private Long pos;

    private Long lex;

    @JsonbProperty("synset_mode")
    private boolean synsetMode;

    private String label;

    private NodeStructure top;

    private NodeStructure bottom;

    private NodeStructure left;

    private NodeStructure right;

    public NodeExpanded() {
    }

    public NodeExpanded(RootNode head, List<String> rel) {
        this.id = head.getId();
        this.lex = head.getLex();
        if (rel != null) {
            this.rel = rel;
        }
        this.pos = head.getPos();
        this.label = head.getLabel();
        this.synsetMode = head.isSynsetMode();
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

    public NodeStructure getBottom() {
        return bottom;
    }

    public NodeStructure getLeft() {
        return left;
    }

    public NodeStructure getRight() {
        return right;
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
