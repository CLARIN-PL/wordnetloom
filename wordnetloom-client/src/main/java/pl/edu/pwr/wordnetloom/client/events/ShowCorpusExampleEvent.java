package pl.edu.pwr.wordnetloom.client.events;

public class ShowCorpusExampleEvent {

    private final String word;

    public ShowCorpusExampleEvent(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
