package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.edu.pwr.wordnetloom.client.service.UuidAdapter;

import javax.json.bind.annotation.JsonbTypeAdapter;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Synset {

    @JsonbTypeAdapter(UuidAdapter.class)
    private UUID id;

    private List<Sense> senses;

    private Long lexicon;

    private String definition;

    private String comment;

    private String link;

    @JsonProperty("artificial")
    private Boolean isAbstract;

    @JsonProperty("ili_id")
    private String iliId;

    @JsonProperty("princeton_id")
    private String princetonId;

    private Long status;

    @JsonProperty("technical_comment")
    private String technicalComment;

    private String owner;

    private List<Example> examples;

    @JsonProperty("_links")
    private Links links;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Sense> getSenses() {
        return senses;
    }

    public void setSenses(List<Sense> senses) {
        this.senses = senses;
    }

    public Long getLexicon() {
        return lexicon;
    }

    public void setLexicon(Long lexicon) {
        this.lexicon = lexicon;
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

    public Boolean getAbstract() {
        return isAbstract;
    }

    public void setAbstract(Boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public String getIliId() {
        return iliId;
    }

    public void setIliId(String iliId) {
        this.iliId = iliId;
    }

    public String getPrincetonId() {
        return princetonId;
    }

    public void setPrincetonId(String princetonId) {
        this.princetonId = princetonId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getTechnicalComment() {
        return technicalComment;
    }

    public void setTechnicalComment(String technicalComment) {
        this.technicalComment = technicalComment;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
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

    @Override
    public String toString() {
        return "Synset{" +
                "id=" + id +
                ", senses=" + senses +
                ", lexicon=" + lexicon +
                ", definition='" + definition + '\'' +
                ", comment='" + comment + '\'' +
                ", link='" + link + '\'' +
                ", isAbstract=" + isAbstract +
                ", iliId='" + iliId + '\'' +
                ", princetonId='" + princetonId + '\'' +
                ", status=" + status +
                ", technicalComment='" + technicalComment + '\'' +
                ", owner='" + owner + '\'' +
                ", examples=" + examples +
                ", links=" + links +
                '}';
    }
}
