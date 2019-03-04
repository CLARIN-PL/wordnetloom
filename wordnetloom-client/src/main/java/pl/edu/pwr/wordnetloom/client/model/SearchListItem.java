package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.edu.pwr.wordnetloom.client.service.UuidAdapter;

import javax.json.bind.annotation.JsonbTypeAdapter;
import java.util.List;
import java.util.UUID;

public class SearchListItem {

    @JsonbTypeAdapter(UuidAdapter.class)
    private UUID id;

    private UUID synset;

    private Long lexicon;

    @JsonProperty("part_of_speech")
    private String partOfSpeech;

    private int variant;

    private List<YiddishVariant> variants;

    private String label;

    @JsonProperty("_links")
    private Links links;

    public SearchListItem() {
    }

    public SearchListItem(String label) {
        this.label = label;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getLexicon() {
        return lexicon;
    }

    public void setLexicon(Long lexicon) {
        this.lexicon = lexicon;
    }

    public UUID getSynset() {
        return synset;
    }

    public void setSynset(UUID synset) {
        this.synset = synset;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
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

    public boolean hasSynset(){
        return getLinks() != null && getLinks().getGraph() != null;
    }

    public int getVariant() {
        return variant;
    }

    public void setVariant(int variant) {
        this.variant = variant;
    }

    public List<YiddishVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<YiddishVariant> variants) {
        this.variants = variants;
    }

    @Override
    public String toString() {
        return "SearchListItem{" +
                "id=" + id +
                ", synset=" + synset +
                ", partOfSpeech='" + partOfSpeech + '\'' +
                ", label='" + label + '\'' +
                ", links=" + links +
                '}';
    }
}