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

    private Long yiddishDomainId;

    private Long yiddishDomainModificationId;

    private Long grammaticalGenderId;

    private Long styleId;

    private Long yiddishStatusId;

    private Long lexicalCharacteristicId;

    private Long sourceId;

    private Long ageId;

    private Long inflectionId;

    private String etymologicalRoot;

    private Long particlePrefix;

    private Long particleSuffix;

    private String particleRoot;

    private String particleConstituent;

    private String etymology;

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

    public Long getYiddishDomainId() {
        return yiddishDomainId;
    }

    public void setYiddishDomainId(Long yiddishDomainId) {
        this.yiddishDomainId = yiddishDomainId;
    }

    public Long getYiddishDomainModificationId() {
        return yiddishDomainModificationId;
    }

    public void setYiddishDomainModificationId(Long yiddishDomainModificationId) {
        this.yiddishDomainModificationId = yiddishDomainModificationId;
    }

    public Long getGrammaticalGenderId() {
        return grammaticalGenderId;
    }

    public void setGrammaticalGenderId(Long grammaticalGenderId) {
        this.grammaticalGenderId = grammaticalGenderId;
    }

    public Long getStyleId() {
        return styleId;
    }

    public void setStyleId(Long styleId) {
        this.styleId = styleId;
    }

    public Long getYiddishStatusId() {
        return yiddishStatusId;
    }

    public void setYiddishStatusId(Long yiddishStatusId) {
        this.yiddishStatusId = yiddishStatusId;
    }

    public Long getLexicalCharacteristicId() {
        return lexicalCharacteristicId;
    }

    public void setLexicalCharacteristicId(Long lexicalCharacteristicId) {
        this.lexicalCharacteristicId = lexicalCharacteristicId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getAgeId() {
        return ageId;
    }

    public void setAgeId(Long ageId) {
        this.ageId = ageId;
    }

    public Long getInflectionId() {
        return inflectionId;
    }

    public void setInflectionId(Long inflectionId) {
        this.inflectionId = inflectionId;
    }

    public String getEtymologicalRoot() {
        return etymologicalRoot;
    }

    public void setEtymologicalRoot(String etymologicalRoot) {
        this.etymologicalRoot = etymologicalRoot;
    }

    public Long getParticlePrefix() {
        return particlePrefix;
    }

    public void setParticlePrefix(Long particlePrefix) {
        this.particlePrefix = particlePrefix;
    }

    public Long getParticleSuffix() {
        return particleSuffix;
    }

    public void setParticleSuffix(Long particleSuffix) {
        this.particleSuffix = particleSuffix;
    }

    public String getParticleRoot() {
        return particleRoot;
    }

    public void setParticleRoot(String particleRoot) {
        this.particleRoot = particleRoot;
    }

    public String getParticleConstituent() {
        return particleConstituent;
    }

    public void setParticleConstituent(String particleConstituent) {
        this.particleConstituent = particleConstituent;
    }

    public String getEtymology() {
        return etymology;
    }

    public void setEtymology(String etymology) {
        this.etymology = etymology;
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