package pl.edu.pwr.wordnetloom.client.model;

public class YiddishInflection {

    private long id;

    private Dictionary inflection;

    private String text;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Dictionary getInflection() {
        return inflection;
    }

    public void setInflection(Dictionary inflection) {
        this.inflection = inflection;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
