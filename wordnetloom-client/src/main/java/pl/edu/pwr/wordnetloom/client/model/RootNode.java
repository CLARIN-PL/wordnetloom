package pl.edu.pwr.wordnetloom.client.model;

public class RootNode {

    private long id;
    private long lex;
    private long pos;
    private String label;

    public RootNode() {
    }

    public RootNode(long id, long lex, long pos, String label) {
        this.id = id;
        this.lex = lex;
        this.pos = pos;
        this.label = label;
    }

    public long getId() {
        return id;
    }

    public long getLex() {
        return lex;
    }

    public long getPos() {
        return pos;
    }

    public String getLabel() {
        return label;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLex(long lex) {
        this.lex = lex;
    }

    public void setPos(long pos) {
        this.pos = pos;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "RootNode{" +
                "id=" + id +
                ", lex=" + lex +
                ", pos=" + pos +
                ", label='" + label + '\'' +
                '}';
    }
}
