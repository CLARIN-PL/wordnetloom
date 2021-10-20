package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Morphology {

    private Long id;

    @JsonProperty("word_form")
    private String wordForm;

    @JsonProperty("morphological_tags")
    private String morphologicalTag;

    @JsonProperty("_actions")
    private List<Action> action;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWordForm() {
        return wordForm;
    }

    public void setWordForm(String wordForm) {
        this.wordForm = wordForm;
    }

    public String getMorphologicalTag() {
        return morphologicalTag;
    }

    public void setMorphologicalTag(String morphologicalTag) {
        this.morphologicalTag = morphologicalTag;
    }

    public List<Action> getAction() {
        return action;
    }

    public void setAction(List<Action> action) {
        this.action = action;
    }

    public String getFullName() {
        return wordForm + " " + morphologicalTag;
    }
}
