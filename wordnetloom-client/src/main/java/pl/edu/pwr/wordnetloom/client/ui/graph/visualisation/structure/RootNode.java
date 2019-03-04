package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract public class RootNode extends Node {

    private final Map<NodeDirection, NodeSet> sets;

    public NodeSet getSynsetSet(NodeDirection direction) {
        return sets.get(direction);
    }

    public RootNode() {
        sets = new ConcurrentHashMap<>();
        NodeDirection.stream().forEach(d -> sets.put(d, new NodeSet()));
    }
}
