package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure;

import java.util.stream.Stream;

public enum NodeDirection {
    LEFT("LEFT"),
    RIGHT("RIGHT"),
    BOTTOM("BOTTOM"),
    TOP("TOP"),
    IGNORE("IGNORE");

    private final String str;

    NodeDirection(String name) {
        this.str = name;
    }

    public String getAsString() {
        return str;
    }

    public NodeDirection getOpposite() {
        switch (this) {
            case BOTTOM:
                return TOP;
            case TOP:
                return BOTTOM;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            case IGNORE:
                return IGNORE;
            default:
                return null;
        }
    }

    public static Stream<NodeDirection> stream(){
        return Stream.of(values()).filter(nd -> !nd.equals(IGNORE));
    }
    public static Stream<NodeDirection> streamAll(){
        return Stream.of(values());
    }
}