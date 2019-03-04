package pl.edu.pwr.wordnetloom.client.model;

import java.util.UUID;

public class SearchFilter implements Cloneable {

    private PaginationData paginationData;

    private String lemma;

    private Long lexicon;

    private Long partOfSpeechId;

    private Long domainId;

    private Long statusId;

    private Long aspectId;

    private String definition;

    private String comment;

    private String example;

    private UUID relationTypeId;

    private Long registerId;

    private UUID synsetId;

    private Boolean sensesWithoutSynset;

    private Boolean artificial;

    private Boolean synsetMode;

    private Boolean senseMode;

    private Long emotion;

    private Long valuation;

    private Long markedness;

    private Integer start;

    private Integer limit;

    public SearchFilter() {
    }

    public SearchFilter(final PaginationData paginationData) {
        this.paginationData = paginationData;
    }

    public void setPaginationData(final PaginationData paginationData) {
        this.paginationData = paginationData;
    }

    public PaginationData getPaginationData() {
        return paginationData;
    }

    public boolean hasPaginationData() {
        return getPaginationData() != null;
    }

    public Long getLexicon() {
        return lexicon;
    }

    public void setLexicon(Long lexicon) {
        this.lexicon = lexicon;
    }

    public Long getRegisterId() {
        return registerId;
    }

    public void setRegisterId(Long registerId) {
        this.registerId = registerId;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public Long getPartOfSpeechId() {
        return partOfSpeechId;
    }

    public void setPartOfSpeechId(Long partOfSpeechId) {
        this.partOfSpeechId = partOfSpeechId;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public UUID getRelationTypeId() {
        return relationTypeId;
    }

    public void setRelationTypeId(UUID relationTypeId) {
        this.relationTypeId = relationTypeId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getSynsetMode() {
        return synsetMode;
    }

    public void setSynsetMode(Boolean synsetMode) {
        this.synsetMode = synsetMode;
    }

    public Boolean getSenseMode() {
        return senseMode;
    }

    public void setSenseMode(Boolean senseMode) {
        this.senseMode = senseMode;
    }

    public Long getEmotion() {
        return emotion;
    }

    public void setEmotion(Long emotion) {
        this.emotion = emotion;
    }

    public Long getValuation() {
        return valuation;
    }

    public void setValuation(Long valuation) {
        this.valuation = valuation;
    }

    public Long getMarkedness() {
        return markedness;
    }

    public void setMarkedness(Long markedness) {
        this.markedness = markedness;
    }

    public Long getAspectId() {
        return aspectId;
    }

    public void setAspectId(Long aspectId) {
        this.aspectId = aspectId;
    }

    public Boolean getArtificial() {
        return artificial;
    }

    public void setArtificial(Boolean artificial) {
        this.artificial = artificial;
    }

    public UUID getSynsetId() {
        return synsetId;
    }

    public void setSynsetId(UUID synsetId) {
        this.synsetId = synsetId;
    }

    public Integer getStart(){
        return start;
    }

    public void setStart(Integer start){
        this.start = start;
    }

    public Integer getLimit(){
        return limit;
    }

    public void setLimit(Integer limit){
        this.limit = limit;
    }

    public Boolean getSensesWithoutSynset() {
        return sensesWithoutSynset;
    }

    public void setSensesWithoutSynset(Boolean sensesWithoutSynset) {
        this.sensesWithoutSynset = sensesWithoutSynset;
    }

    @Override
    public SearchFilter clone(){
        try{
            return (SearchFilter) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}