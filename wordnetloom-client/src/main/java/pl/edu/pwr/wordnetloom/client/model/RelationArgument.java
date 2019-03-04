package pl.edu.pwr.wordnetloom.client.model;

public enum RelationArgument {
    SYNSET_RELATION("Synset relation"),
    SENSE_RELATION("Sense relation");

    private final String str;

    RelationArgument(String name) {
        this.str = name;
    }

    public String getStr() {
        return str;
    }
}