package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class YiddishTranscription {

    @JsonProperty("transcription_id")
    private long transcriptionId;

    private long id;

    private String name;

    private String phonography;

    public long getTranscriptionId() {
        return transcriptionId;
    }

    public void setTranscriptionId(long transcriptionId) {
        this.transcriptionId = transcriptionId;
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

    public String getPhonography() {
        return phonography;
    }

    public void setPhonography(String phonography) {
        this.phonography = phonography;
    }

    @Override
    public String toString() {
        return "YiddishTranscription{" +
                "transcriptionId=" + transcriptionId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", phonography='" + phonography + '\'' +
                '}';
    }
}
