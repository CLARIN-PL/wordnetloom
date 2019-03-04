package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.edu.pwr.wordnetloom.client.service.UuidAdapter;

import javax.json.bind.annotation.JsonbTypeAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Sense {

    @JsonbTypeAdapter(UuidAdapter.class)
    private UUID id;

    @JsonbTypeAdapter(UuidAdapter.class)
    private UUID synset;

    @JsonProperty("part_of_speech")
    private Long partOfSpeech;

    private Long domain;

    private Long lexicon;

    private String lemma;

    private Integer variant;

    private Long aspect;

    private Long register;

    private Long status;

    private String definition;

    private String comment;

    private String owner;

    private String link;

    @JsonProperty("technical_comment")
    private String technicalComment;

    private String label;

    private List<Example> examples = new ArrayList<>();

    @JsonProperty("create_synset")
    private boolean createSynset = false;

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

    public UUID getSynset() {
        return synset;
    }

    public void setSynset(UUID synset) {
        this.synset = synset;
    }

    public Long getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(Long partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Long getDomain() {
        return domain;
    }

    public void setDomain(Long domain) {
        this.domain = domain;
    }

    public Long getLexicon() {
        return lexicon;
    }

    public void setLexicon(Long lexicon) {
        this.lexicon = lexicon;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public Integer getVariant() {
        return variant;
    }

    public void setVariant(Integer variant) {
        this.variant = variant;
    }

    public Long getAspect() {
        return aspect;
    }

    public void setAspect(Long aspect) {
        this.aspect = aspect;
    }

    public Long getRegister() {
        return register;
    }

    public void setRegister(Long register) {
        this.register = register;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTechnicalComment() {
        return technicalComment;
    }

    public void setTechnicalComment(String technicalComment) {
        this.technicalComment = technicalComment;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<Example> getExamples() {
        return examples;
    }

    public void setExamples(List<Example> examples) {
        this.examples = examples;
    }

    public boolean isCreateSynset() {
        return createSynset;
    }

    public void setCreateSynset(boolean createSynset) {
        this.createSynset = createSynset;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public String toString() {
        return "Sense{" +
                "id=" + id +
                ", synset=" + synset +
                ", partOfSpeech=" + partOfSpeech +
                ", domain=" + domain +
                ", lexicon=" + lexicon +
                ", lemma='" + lemma + '\'' +
                ", variant='" + variant + '\'' +
                ", aspect=" + aspect +
                ", register=" + register +
                ", status=" + status +
                ", definition='" + definition + '\'' +
                ", comment='" + comment + '\'' +
                ", owner='" + owner + '\'' +
                ", link='" + link + '\'' +
                ", technicalComment='" + technicalComment + '\'' +
                ", label='" + label + '\'' +
                ", examples=" + examples +
                ", links=" + links +
                '}';
    }
}
