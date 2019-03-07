package pl.edu.pwr.wordnetloom.client.model;

public class YiddishSemanticField {

    private long id;

    private Dictionary domain;

    private Dictionary modifier;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Dictionary getDomain() {
        return domain;
    }

    public void setDomain(Dictionary domain) {
        this.domain = domain;
    }

    public Dictionary getModifier() {
        return modifier;
    }

    public void setModifier(Dictionary modifier) {
        this.modifier = modifier;
    }

    @Override
    public String toString() {
        return "YiddishSemanticField{" +
                "id=" + id +
                ", domain=" + domain +
                ", modifier=" + modifier +
                '}';
    }
}
