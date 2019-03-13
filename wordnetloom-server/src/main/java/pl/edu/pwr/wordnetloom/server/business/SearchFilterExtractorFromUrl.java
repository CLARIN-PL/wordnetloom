package pl.edu.pwr.wordnetloom.server.business;

import javax.ws.rs.core.UriInfo;
import java.util.UUID;

public class SearchFilterExtractorFromUrl {

    private UriInfo uriInfo;
    private static final int DEFAULT_LIMIT = 100;
    private static final int DEFAULT_START = 0;

    public SearchFilterExtractorFromUrl(final UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public SearchFilter getFilter() {

        final SearchFilter searchFilter = new SearchFilter();

        searchFilter.setPaginationData(extractPaginationData());
        searchFilter.setLemma(getUriInfo().getQueryParameters().getFirst("lemma"));

        final String lexiconIdStr = getUriInfo().getQueryParameters().getFirst("lexicon");
        if (lexiconIdStr != null) {
            searchFilter.setLexicon(Long.valueOf(lexiconIdStr));
        }

        final String posIdStr = getUriInfo().getQueryParameters().getFirst("part_of_speech");
        if (posIdStr != null) {
            searchFilter.setPartOfSpeechId(Long.valueOf(posIdStr));
        }

        final String domainIdStr = getUriInfo().getQueryParameters().getFirst("domain");
        if (domainIdStr != null) {
            searchFilter.setDomainId(Long.valueOf(domainIdStr));
        }

        final String relationTypeId = getUriInfo().getQueryParameters().getFirst("relation_type");
        if (relationTypeId != null) {
            searchFilter.setRelationTypeId(UUID.fromString(relationTypeId));
        }

        final String statusId = getUriInfo().getQueryParameters().getFirst("status");
        if (statusId != null) {
            searchFilter.setStatusId(Long.valueOf(statusId));
        }

        final String registerId = getUriInfo().getQueryParameters().getFirst("register");
        if (registerId != null) {
            searchFilter.setRegisterId(Long.valueOf(registerId));
        }

        final String synsetIdStr = getUriInfo().getQueryParameters().getFirst("synset");
        if (synsetIdStr != null) {
            searchFilter.setSynsetId(UUID.fromString(synsetIdStr));
        }

        final String isArtStr = getUriInfo().getQueryParameters().getFirst("artificial");
        if(isArtStr != null){
            searchFilter.setArtificial(Boolean.valueOf(isArtStr));
        }

        searchFilter.setDefinition(getUriInfo().getQueryParameters().getFirst("definition"));

        searchFilter.setComment(getUriInfo().getQueryParameters().getFirst("comment"));

        searchFilter.setExample(getUriInfo().getQueryParameters().getFirst("example"));

        final String senseWithoutSynsets = getUriInfo().getQueryParameters().getFirst("senses_without_synset");
        if (senseWithoutSynsets != null) {
            searchFilter.setSenseWithoutSynset(Boolean.parseBoolean(senseWithoutSynsets));
        }

        final String yiddishDomainId = getUriInfo().getQueryParameters().getFirst("yiddish_domain");
        if (yiddishDomainId != null) {
            searchFilter.setYiddishDomainId(Long.valueOf(yiddishDomainId));
        }

        final String yiddishDomainModificationId = getUriInfo().getQueryParameters().getFirst("yiddish_domain_modification");
        if (yiddishDomainModificationId != null) {
            searchFilter.setYiddishDomainModificationId(Long.valueOf(yiddishDomainModificationId));
        }

        final String grammaticalGenderId  = getUriInfo().getQueryParameters().getFirst("grammatical_gender");
        if (grammaticalGenderId != null) {
            searchFilter.setGrammaticalGenderId(Long.valueOf(grammaticalGenderId));
        }

        final String styleId = getUriInfo().getQueryParameters().getFirst("style");
        if (styleId != null) {
            searchFilter.setStyleId(Long.valueOf(styleId));
        }

        final String yiddishStatusId = getUriInfo().getQueryParameters().getFirst("yiddish_status");
        if (yiddishStatusId != null) {
            searchFilter.setYiddishStatusId(Long.valueOf(yiddishStatusId));
        }

        final String lexicalCharacteristicId = getUriInfo().getQueryParameters().getFirst("lexical_characteristic");
        if (lexicalCharacteristicId != null) {
            searchFilter.setLexicalCharacteristicId(Long.valueOf(lexicalCharacteristicId));
        }

        final String sourceId = getUriInfo().getQueryParameters().getFirst("source");
        if (sourceId != null) {
            searchFilter.setSourceId(Long.valueOf(sourceId));
        }

        final String ageId = getUriInfo().getQueryParameters().getFirst("age");
        if (ageId != null) {
            searchFilter.setAgeId(Long.valueOf(ageId));
        }

        final String inflectionId = getUriInfo().getQueryParameters().getFirst("inflection");
        if (inflectionId != null) {
            searchFilter.setInflectionId(Long.valueOf(inflectionId));
        }

        searchFilter.setEtymologicalRoot(getUriInfo().getQueryParameters().getFirst("etymological_root"));

        final String particlePrefix = getUriInfo().getQueryParameters().getFirst("particle_prefix");
        if (particlePrefix != null) {
            searchFilter.setParticlePrefix(Long.valueOf(particlePrefix));
        }

        final String particleSuffix = getUriInfo().getQueryParameters().getFirst("particle_suffix");
        if (particleSuffix != null) {
            searchFilter.setParticleSuffix(Long.valueOf(particleSuffix));
        }

        final String particleInterfix = getUriInfo().getQueryParameters().getFirst("particle_interfix");
        if (particleInterfix != null) {
            searchFilter.setParticleInterfx(Long.valueOf(particleInterfix));
        }

        searchFilter.setParticleRoot(getUriInfo().getQueryParameters().getFirst("particle_root"));
        searchFilter.setParticleConstituent(getUriInfo().getQueryParameters().getFirst("particle_constituent"));
        searchFilter.setEtymology(getUriInfo().getQueryParameters().getFirst("etymology"));
        searchFilter.setSortBy(getUriInfo().getQueryParameters().getFirst("sort_by"));
        if(searchFilter.getSortBy() == null){
            searchFilter.setSortBy("latin");
        }

        return searchFilter;
    }

    protected UriInfo getUriInfo() {
        return uriInfo;
    }

    protected PaginationData extractPaginationData() {
        int limit = getLimit();
        int start = getStart();
        return new PaginationData(start, limit);
    }

    private Integer getLimit() {
        final String limit = uriInfo.getQueryParameters().getFirst("limit");
        if (limit == null) {
            return DEFAULT_LIMIT;
        }
        return Integer.parseInt(limit);
    }

    private Integer getStart() {
        final String start = uriInfo.getQueryParameters().getFirst("start");
        if (start == null) {
            return DEFAULT_START;
        }
        return Integer.parseInt(start);
    }

}