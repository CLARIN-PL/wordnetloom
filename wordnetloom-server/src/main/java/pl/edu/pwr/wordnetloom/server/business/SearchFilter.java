package pl.edu.pwr.wordnetloom.server.business;

import java.util.UUID;

public class SearchFilter {


    private PaginationData paginationData;

    private String lemma;

    private Long lexicon;

    private Long partOfSpeechId;

    private Long domainId;

    private Long statusId;

    private String definition;

    private String comment;

    private String example;

    private UUID relationTypeId;

    private Long registerId;

    private Long aspectId;

    private UUID synsetId;

    private Boolean artificial;

    private Long emotion;

    private Long valuation;

    private Long markedness;

    private Boolean senseWithoutSynset;

    private Boolean negateRelationType;

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

    public Boolean getArtificial() {
        return artificial;
    }

    public void setArtificial(Boolean artificial) {
        this.artificial = artificial;
    }

    public Long getAspectId() {
        return aspectId;
    }

    public void setAspectId(Long aspectId) {
        this.aspectId = aspectId;
    }

    public UUID getRelationTypeId() {
        return relationTypeId;
    }

    public void setRelationTypeId(UUID relationTypeId) {
        this.relationTypeId = relationTypeId;
    }

    public UUID getSynsetId() {
        return synsetId;
    }

    public void setSynsetId(UUID synsetId) {
        this.synsetId = synsetId;
    }

    public Boolean getSenseWithoutSynset() {
        return senseWithoutSynset;
    }

    public void setSenseWithoutSynset(Boolean senseWithoutSynset) {
        this.senseWithoutSynset = senseWithoutSynset;
    }

    public Boolean getNegateRelationType() {
        return negateRelationType;
    }

    public void setNegateRelationType(Boolean negateRelationType) {
        this.negateRelationType = negateRelationType;
    }

    @Override
    public String toString() {
        return "SearchFilter{" +
                "paginationData=" + paginationData +
                ", lemma='" + lemma + '\'' +
                ", lexicon=" + lexicon +
                ", partOfSpeechId=" + partOfSpeechId +
                ", domainId=" + domainId +
                ", statusId=" + statusId +
                ", definition='" + definition + '\'' +
                ", comment='" + comment + '\'' +
                ", example='" + example + '\'' +
                ", relationTypeId=" + relationTypeId +
                ", registerId=" + registerId +
                ", aspectId=" + aspectId +
                ", synsetId=" + synsetId +
                ", artificial=" + artificial +
                ", emotion=" + emotion +
                ", valuation=" + valuation +
                ", markedness=" + markedness +
                ", senseWithoutSynset=" + senseWithoutSynset +
                ", negateRelationType=" + negateRelationType +
                '}';
    }
}