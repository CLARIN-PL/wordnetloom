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

        final String aspectId = getUriInfo().getQueryParameters().getFirst("aspect");
        if (aspectId != null) {
            searchFilter.setAspectId(Long.valueOf(aspectId));
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

        final String emotionId = getUriInfo().getQueryParameters().getFirst("emotion");
        if (emotionId != null) {
            searchFilter.setEmotion(Long.valueOf(emotionId));
        }

        final String valuationId = getUriInfo().getQueryParameters().getFirst("valuation");
        if (valuationId != null) {
            searchFilter.setValuation(Long.valueOf(valuationId));
        }

        final String markednessId = getUriInfo().getQueryParameters().getFirst("markedness");
        if (markednessId != null) {
            searchFilter.setMarkedness(Long.valueOf(markednessId));
        }

        final String senseWithoutSynsets = getUriInfo().getQueryParameters().getFirst("senses_without_synset");
        if (senseWithoutSynsets != null) {
            searchFilter.setSenseWithoutSynset(Boolean.parseBoolean(senseWithoutSynsets));
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