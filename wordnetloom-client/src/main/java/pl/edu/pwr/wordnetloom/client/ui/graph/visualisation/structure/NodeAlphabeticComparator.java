package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure;


import java.util.Comparator;

public class NodeAlphabeticComparator implements Comparator<Node> {

    public NodeAlphabeticComparator() {
    }

    @Override
    public int compare(Node node1, Node node2) {
        if (!(node2 instanceof SynsetNode)) {
            return -1;
        }
        if (!(node1 instanceof SynsetNode)) {
            return 1;
        }

        if (!(node1.getNode() instanceof SynsetNode)) {
            return node1.getLabel().compareTo(node2.getLabel());
        }

        SynsetNode s1 = (SynsetNode) node1;
        SynsetNode s2 = (SynsetNode) node2;
        SynsetNode spawner = (SynsetNode) (node1.getNode());

        SynsetEdge ee1 = spawner.getRelations(node1.getNodeDirection())
                .stream()
                .filter(e ->  e.getTarget().equals(s1.getSynsetId())
                        || e.getSource().equals(s1.getSynsetId()))
                .findFirst().orElse(null);

        SynsetEdge ee2 = spawner.getRelations(node2.getNodeDirection())
                .stream()
                .filter(e ->  e.getTarget().equals(s2.getSynsetId())
                        || e.getSource().equals(s2.getSynsetId()))
                .findFirst().orElse(null);

        if (ee1 == null || ee2 == null) {
            return -1;
        }

        return node1.getLabel().compareTo(node2.getLabel());
    }

}
