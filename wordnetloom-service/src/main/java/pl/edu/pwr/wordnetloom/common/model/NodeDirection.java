package pl.edu.pwr.wordnetloom.common.model;

public enum NodeDirection {

    LEFT("LEFT"),
    RIGHT("RIGHT"),
    BOTTOM("BOTTOM"),
    TOP("TOP");

    private final String str;

    NodeDirection(String name) {
        str = name;
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
            default:
                return null;
        }
    }
}