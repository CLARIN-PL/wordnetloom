package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.edu.pwr.wordnetloom.client.service.UuidAdapter;

import javax.json.bind.annotation.JsonbTypeAdapter;
import java.util.*;

public class EmotionalAnnotation {

    @JsonbTypeAdapter(UuidAdapter.class)
    private UUID id;

    @JsonbTypeAdapter(UuidAdapter.class)
    @JsonProperty("sense_id")
    private UUID senseId;

    @JsonProperty("super_annotation")
    private boolean superAnnotation;

    @JsonProperty("emotional_characteristic")
    private boolean emotionalCharacteristic;

    private List<Long> emotions = new LinkedList<>();

    private List<Long> valuations = new LinkedList<>();

    private Long markedness;

    private String example1;

    private String example2;

    @JsonProperty("_links")
    private Links links;

    @JsonProperty("_actions")
    private List<Action> actions;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSenseId() {
        return senseId;
    }

    public void setSenseId(UUID senseId) {
        this.senseId = senseId;
    }

    public boolean isSuperAnnotation() {
        return superAnnotation;
    }

    public void setSuperAnnotation(boolean superAnnotation) {
        this.superAnnotation = superAnnotation;
    }

    public boolean isEmotionalCharacteristic() {
        return emotionalCharacteristic;
    }

    public void setEmotionalCharacteristic(boolean emotionalCharacteristic) {
        this.emotionalCharacteristic = emotionalCharacteristic;
    }

    public List<Long> getEmotions() {
        return emotions;
    }

    public void setEmotions(List<Long> emotions) {
        this.emotions = emotions;
    }

    public void addEmotion(Long emotionId) {
        this.emotions.add(emotionId);
    }

    public List<Long> getValuations() {
        return valuations;
    }

    public void setValuations(List<Long> valuations) {
        this.valuations = valuations;
    }

    public void addValuation(Long valuationId) {
        this.valuations.add(valuationId);
    }

    public Long getMarkedness() {
        return markedness;
    }

    public void setMarkedness(Long markedness) {
        this.markedness = markedness;
    }

    public String getExample1() {
        return example1;
    }

    public void setExample1(String example1) {
        this.example1 = example1;
    }

    public String getExample2() {
        return example2;
    }

    public void setExample2(String example2) {
        this.example2 = example2;
    }

    @Override
    public String toString() {
        return "EmotionalAnnotation{" +
                "id=" + id +
                ", senseId=" + senseId +
                ", superAnnotation=" + superAnnotation +
                ", emotionalCharacteristic=" + emotionalCharacteristic +
                ", emotions=" + emotions +
                ", valuations=" + valuations +
                ", markedness=" + markedness +
                ", example1='" + example1 + '\'' +
                ", example2='" + example2 + '\'' +
                '}';
    }
}