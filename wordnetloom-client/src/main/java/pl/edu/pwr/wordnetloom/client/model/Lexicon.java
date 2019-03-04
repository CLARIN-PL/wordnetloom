package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Lexicon {

    private Long id;

    private String name;

    private String identifier;

    @JsonProperty("reference_url")
    private String referenceUrl;

    private String language;

    @JsonProperty("language_shortcut")
    private String languageShortcut;

    private String version;

    private String license;

    private String email;

    @JsonProperty
    private String citation;

    @JsonProperty("confidence_score")
    private String confidenceScore;

    @JsonProperty("_links")
    private Links links;

    @JsonProperty("_actions")
    private List<Action> actions;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getReferenceUrl() {
        return referenceUrl;
    }

    public String getLanguage() {
        return language;
    }

    public String getLanguageShortcut() {
        return languageShortcut;
    }

    public String getVersion() {
        return version;
    }

    public String getLicense() {
        return license;
    }

    public String getEmail() {
        return email;
    }

    public String getCitation() {
        return citation;
    }

    public String getConfidenceScore() {
        return confidenceScore;
    }

    public Links getLinks() {
        return links;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setReferenceUrl(String referenceUrl) {
        this.referenceUrl = referenceUrl;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setLanguageShortcut(String languageShortcut) {
        this.languageShortcut = languageShortcut;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    public void setConfidenceScore(String confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return name;
    }
}
