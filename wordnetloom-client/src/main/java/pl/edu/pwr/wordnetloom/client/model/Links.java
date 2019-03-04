package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

public class Links {

    private URI self;

    private URI prev;

    private URI next;

    private URI relations;

    private URI examples;

    private URI graph;

    private URI synset;

    private URI tests;

    @JsonProperty("reverse_relation")
    private URI reverseRelation;

    @JsonProperty("emotional-annotations")
    private URI emotionalAnnotations;

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getPrev() {
        return prev;
    }

    public void setPrev(URI prev) {
        this.prev = prev;
    }

    public URI getNext() {
        return next;
    }

    public void setNext(URI next) {
        this.next = next;
    }

    public URI getRelations() {
        return relations;
    }

    public void setRelations(URI relations) {
        this.relations = relations;
    }

    public URI getExamples() {
        return examples;
    }

    public void setExamples(URI examples) {
        this.examples = examples;
    }

    public URI getGraph() {
        return graph;
    }

    public void setGraph(URI graph) {
        this.graph = graph;
    }

    public URI getSynset() {
        return synset;
    }

    public void setSynset(URI synset) {
        this.synset = synset;
    }

    public URI getEmotionalAnnotations() {
        return emotionalAnnotations;
    }

    public void setEmotionalAnnotations(URI emotionalAnnotations) {
        this.emotionalAnnotations = emotionalAnnotations;
    }

    public URI getTests() {
        return tests;
    }

    public void setTests(URI tests) {
        this.tests = tests;
    }

    public URI getReverseRelation() {
        return reverseRelation;
    }

    public void setReverseRelation(URI reverseRelation) {
        this.reverseRelation = reverseRelation;
    }

    @Override
    public String toString() {
        return "Links{" +
                "self=" + self +
                ", prev=" + prev +
                ", next=" + next +
                ", relations=" + relations +
                ", examples=" + examples +
                ", graph=" + graph +
                ", synset=" + synset +
                ", tests=" + tests +
                ", reverseRelation=" + reverseRelation +
                ", emotionalAnnotations=" + emotionalAnnotations +
                '}';
    }
}