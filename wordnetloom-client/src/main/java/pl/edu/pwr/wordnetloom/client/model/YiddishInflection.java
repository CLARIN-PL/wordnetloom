package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class YiddishInflection {

    @JsonProperty("inflection_id")
    private long inflectionId;

    private long id;

    private String name;

    private String text;

    public long getInflectionId() {
        return inflectionId;
    }

    public void setInflectionId(long inflectionId) {
        this.inflectionId = inflectionId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "YiddishInflection{" +
                "inflectionId=" + inflectionId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
