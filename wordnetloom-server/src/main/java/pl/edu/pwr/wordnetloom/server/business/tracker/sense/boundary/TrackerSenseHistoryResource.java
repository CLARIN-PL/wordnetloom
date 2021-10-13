package pl.edu.pwr.wordnetloom.server.business.tracker.sense.boundary;

import pl.edu.pwr.wordnetloom.server.business.tracker.TrackerSearchFilter;
import pl.edu.pwr.wordnetloom.server.business.tracker.TrackerSearchFilterUrlExtractor;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.control.SenseHistoryQueryService;
import pl.edu.pwr.wordnetloom.server.business.tracker.TrackerEntityBuilder;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.UUID;

@Path("tracker/sense-history")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class TrackerSenseHistoryResource {

    @Inject
    TrackerEntityBuilder trackerEntityBuilder;

    @Inject
    SenseHistoryQueryService senseHistoryQueryService;

    @Context
    UriInfo uriInfo;

    @GET
    @Path("{id}")
    public JsonObject senseHistory(@PathParam("id") final UUID id) {
        return trackerEntityBuilder.buildSenseListHistory(senseHistoryQueryService.findSenseHistory(id));
    }

    @GET
    @Path("incoming-relations/{id}")
    public JsonObject senseIncomingRelationsHistory(@PathParam("id") final UUID id) {
        return trackerEntityBuilder.buildSenseHistoryIncomingRelations(
                senseHistoryQueryService.findSenseIncomingRelationsHistory(id)
        );
    }

    @GET
    @Path("outgoing-relations/{id}")
    public JsonObject senseOutgoingRelationsHistory(@PathParam("id") final UUID id) {
        return trackerEntityBuilder.buildSenseHistoryOutgoingRelations(
                senseHistoryQueryService.findSenseOutgoingRelationsHistory(id)
        );
    }

    @GET
    @Path("search")
    public JsonObject searchHistory() {
        final TrackerSearchFilter trackerSearchFilter = new TrackerSearchFilterUrlExtractor(uriInfo).getSearchFilter();
        long count = senseHistoryQueryService.countSenseHistoryByFilter(trackerSearchFilter);
        return trackerEntityBuilder.buildSenseHistorySearchList(
                senseHistoryQueryService.findSenseHistoryByFilter(trackerSearchFilter),
                (int) Math.ceil((double) count / TrackerSearchFilterUrlExtractor.ELEMENTS_PER_PAGE),
                trackerSearchFilter.getEnd() / TrackerSearchFilterUrlExtractor.ELEMENTS_PER_PAGE,
                trackerSearchFilter.getEnd() <= count,
                trackerSearchFilter.getStart() != 0
        );
    }

    @GET
    @Path("attributes/search")
    public JsonObject searchAttributesHistory() {
        final TrackerSearchFilter trackerSearchFilter = new TrackerSearchFilterUrlExtractor(uriInfo).getSearchFilter();
        long count = senseHistoryQueryService.countSenseAttributesHistoryByFilter(trackerSearchFilter);
        return trackerEntityBuilder.buildSenseAttributesHistorySearchList(
                senseHistoryQueryService.findSenseAttributesHistoryByFilter(trackerSearchFilter),
                (int) Math.ceil((double) count / TrackerSearchFilterUrlExtractor.ELEMENTS_PER_PAGE),
                trackerSearchFilter.getEnd() / TrackerSearchFilterUrlExtractor.ELEMENTS_PER_PAGE,
                trackerSearchFilter.getEnd() <= count,
                trackerSearchFilter.getStart() != 0
        );
    }

    @GET
    @Path("relations/search")
    public JsonObject searchRelationsHistory() {
        final TrackerSearchFilter trackerSearchFilter = new TrackerSearchFilterUrlExtractor(uriInfo).getSearchFilter();
        long count = senseHistoryQueryService.countSenseRelationsByFilter(trackerSearchFilter);
        return trackerEntityBuilder.buildSenseRelationsHistorySearchList(
                senseHistoryQueryService.findSenseRelationsByFilter(trackerSearchFilter),
                (int) Math.ceil((double) count / TrackerSearchFilterUrlExtractor.ELEMENTS_PER_PAGE),
                trackerSearchFilter.getEnd() / TrackerSearchFilterUrlExtractor.ELEMENTS_PER_PAGE,
                trackerSearchFilter.getEnd() <= count,
                trackerSearchFilter.getStart() != 0
        );
    }

    @GET
    @Path("emotional-annotation/{id}")
    public JsonObject emotionalAnnotationHistory(@PathParam("id") final UUID id) {
        return trackerEntityBuilder.buildEmotionalAnnotationHistoryList(
                senseHistoryQueryService.findEmotionalAnnotationBySenseId(id));
    }

    @GET
    @Path("emotional-annotation/search")
    public JsonObject searchEmotionalAnnotationHistory() {
        final TrackerSearchFilter trackerSearchFilter = new TrackerSearchFilterUrlExtractor(uriInfo).getSearchFilter();
        long count = senseHistoryQueryService.countEmotionalAnnotationHistoryByFilter(trackerSearchFilter);
        return trackerEntityBuilder.buildEmotionalAnnotationHistorySearchList(
                senseHistoryQueryService.findEmotionalAnnotationHistoryByFilter(trackerSearchFilter),
                (int) Math.ceil((double) count / TrackerSearchFilterUrlExtractor.ELEMENTS_PER_PAGE),
                trackerSearchFilter.getEnd() / TrackerSearchFilterUrlExtractor.ELEMENTS_PER_PAGE,
                trackerSearchFilter.getEnd() <= count,
                trackerSearchFilter.getStart() != 0
        );
    }
}
