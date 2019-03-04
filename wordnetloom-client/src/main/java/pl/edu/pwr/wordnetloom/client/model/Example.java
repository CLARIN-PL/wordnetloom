package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public class Example {

    private String example;
    private String type = "P";

    @JsonProperty("_links")
    private Links links = new Links();

    @JsonProperty("_actions")
    private List<Action> actions;

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Example)) return false;

        Example example1 = (Example) o;

        if (example != null ? !example.equals(example1.example) : example1.example != null) return false;
        if (type != null ? !type.equals(example1.type) : example1.type != null) return false;
        return links != null ? links.equals(example1.links) : example1.links == null;
    }

    @Override
    public int hashCode() {
        int result = example != null ? example.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (links != null ? links.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Example{" +
                "example='" + example + '\'' +
                ", type='" + type + '\'' +
                ", links=" + links +
                ", actions=" + actions +
                '}';
    }
}
