package pl.edu.pwr.wordnetloom.server.business.tracker;

import javax.ws.rs.core.UriInfo;
import java.util.UUID;
import java.util.logging.Logger;

public class TrackerSearchFilterUrlExtractor {

    TrackerDateService dateService;

    private UriInfo uriInfo;

    private int page;

    private static final Logger log =  Logger.getLogger(TrackerSearchFilterUrlExtractor.class.getName());
    private static final int ELEMENTS_PER_PAGE = 50;

    public TrackerSearchFilterUrlExtractor(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        dateService = new TrackerDateService();
    }

    public TrackerSearchFilter getSearchFilter() {
        final TrackerSearchFilter trackerSearchFilter = new TrackerSearchFilter();

        final String dateFromString = uriInfo.getQueryParameters().getFirst("date_from");
        if (dateFromString != null)
            try {
                final long dateFrom = dateService.getBeginOfDate(
                        dateService.getLocalDateTime(dateService.parseStringDateToDate(dateFromString))
                );
                trackerSearchFilter.setDateFromTimestamp(dateFrom);
            } catch (Exception e) {
                log.warning("Cannot convert date_from: " + dateFromString);
            }

        final String dateToString = uriInfo.getQueryParameters().getFirst("date_to");
        if (dateToString != null)
            try {
                final long dateTo = dateService.getEndOfDate(
                        dateService.getLocalDateTime(dateService.parseStringDateToDate(dateToString))
                );
                trackerSearchFilter.setDateToTimestamp(dateTo);
            } catch (Exception e) {
                log.warning("Cannot convert date_to: " + dateToString);
            }

        final String user = uriInfo.getQueryParameters().getFirst("user");
        if (user != null)
            trackerSearchFilter.setUser(user);

        final String partOfSpeechString = uriInfo.getQueryParameters().getFirst("part_of_speech");
        if (partOfSpeechString != null)
            trackerSearchFilter.setPartOfSpeech(Long.valueOf(partOfSpeechString));

        final String page = uriInfo.getQueryParameters().getFirst("page");
        if (page != null)
            this.page = Integer.parseInt(page);
        else
            this.page = 1;

        final String relationType = uriInfo.getQueryParameters().getFirst("relation_type");
        if (relationType != null)
            trackerSearchFilter.setRelationTypeId(UUID.fromString(relationType));

        final String senseUuid = uriInfo.getQueryParameters().getFirst("sense_id");
        if (senseUuid != null)
            trackerSearchFilter.setSenseUuid(UUID.fromString(senseUuid));

        final String synsetUuid = uriInfo.getQueryParameters().getFirst("synset_id");
        if (synsetUuid != null)
            trackerSearchFilter.setSynsetUuid(UUID.fromString(synsetUuid));

        trackerSearchFilter.setStart(getStart());
        trackerSearchFilter.setEnd(getEnd());

        return trackerSearchFilter;
    }

    private int getEnd() {
        return page * ELEMENTS_PER_PAGE;
    }

    private Integer getStart() {
        return (page - 1) * ELEMENTS_PER_PAGE;
    }
}
