package pl.edu.pwr.wordnetloom.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class NodeStructure {

    private List<NodeExpanded> expanded;
    private List<NodeHidden> hidden;

    public void addExpanded(NodeExpanded n) {
        if(expanded == null) expanded = new ArrayList<>();
        expanded.add(n);
    }

    public void addHidden(NodeHidden n) {
        if(hidden == null) hidden = new ArrayList<>();
        hidden.add(n);
    }

    public List<NodeExpanded> getExpanded() {
        return expanded;
    }

    public List<NodeHidden> getHidden() {
        return hidden;
    }
    public Optional<List<NodeHidden>> getOptionalHidden() {
        return Optional.ofNullable(hidden);
    }

    public void setExpanded(List<NodeExpanded> expanded) {
        this.expanded = expanded;
    }

    public void setHidden(List<NodeHidden> hidden) {
        this.hidden = hidden;
    }

    @Override
    public String toString() {
        return "NodeStructure{" +
                "expanded=" + expanded +
                ", hidden=" + hidden +
                '}';
    }
}