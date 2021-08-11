package pl.edu.pwr.wordnetloom.server.business.graph.entity;

import javax.json.bind.annotation.JsonbPropertyOrder;
import java.util.ArrayList;
import java.util.List;

@JsonbPropertyOrder({ "expanded", "hidden" })
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
}
