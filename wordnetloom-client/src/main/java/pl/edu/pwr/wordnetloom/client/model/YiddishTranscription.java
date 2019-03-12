package pl.edu.pwr.wordnetloom.client.model;

public class YiddishTranscription {

    private long id;

    private Dictionary transcription;

    private String phonography;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Dictionary getTranscription() {
        return transcription;
    }

    public void setTranscription(Dictionary transcription) {
        this.transcription = transcription;
    }

    public String getPhonography() {
        return phonography;
    }

    public void setPhonography(String phonography) {
        this.phonography = phonography;
    }
}
