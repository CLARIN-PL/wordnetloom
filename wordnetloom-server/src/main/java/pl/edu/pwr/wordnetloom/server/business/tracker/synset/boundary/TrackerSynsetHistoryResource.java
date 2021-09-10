package pl.edu.pwr.wordnetloom.server.business.tracker.synset.boundary;

import pl.edu.pwr.wordnetloom.server.business.tracker.TrackerEntityBuilder;
import pl.edu.pwr.wordnetloom.server.business.tracker.TrackerSearchFilter;
import pl.edu.pwr.wordnetloom.server.business.tracker.TrackerSearchFilterUrlExtractor;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.control.SenseHistoryQueryService;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.control.SynsetHistoryQueryService;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.UUID;

@Path("tracker/synset-history")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class TrackerSynsetHistoryResource {

    @Inject
    TrackerEntityBuilder trackerEntityBuilder;

    @Inject
    SynsetHistoryQueryService synsetHistoryQueryService;

    @Inject
    SenseHistoryQueryService senseHistoryQueryService;

    @Context
    UriInfo uriInfo;

    @GET
    @Path("{id}")
    public JsonObject synsetHistory(@PathParam("id") final UUID id) {
        return trackerEntityBuilder.buildSynsetListHistory(synsetHistoryQueryService.findSynsetHistory(id));
    }

    @GET
    @Path("incoming-relations/{id}")
    public JsonObject synsetIncomingRelationsHistory(@PathParam("id") final UUID id) {
        return trackerEntityBuilder.buildSynsetHistoryIncomingRelations(
                synsetHistoryQueryService.findSynsetIncomingRelationsHistory(id)
        );
    }

    @GET
    @Path("outgoing-relations/{id}")
    public JsonObject synsetOutgoingRelationsHistory(@PathParam("id") final UUID id) {
        return trackerEntityBuilder.buildSynsetHistoryOutgoingRelations(
                synsetHistoryQueryService.findSynsetOutgoingRelationsHistory(id)
        );
    }

    @GET
    @Path("search")
    public JsonObject searchHistory() {
        final TrackerSearchFilter trackerSearchFilter = new TrackerSearchFilterUrlExtractor(uriInfo).getSearchFilter();
        long count = synsetHistoryQueryService.countSynsetHistoryByFilter(trackerSearchFilter);
        return trackerEntityBuilder.buildSynsetHistorySearchList(
                synsetHistoryQueryService.findSynsetHistoryByFilter(trackerSearchFilter),
                count,
                trackerSearchFilter.getEnd() / TrackerSearchFilterUrlExtractor.ELEMENTS_PER_PAGE,
                trackerSearchFilter.getEnd() <= count,
                trackerSearchFilter.getStart() != 0
        );
    }

    @GET
    @Path("attributes/search")
    public JsonObject searchAttributesHistory() {
        final TrackerSearchFilter trackerSearchFilter = new TrackerSearchFilterUrlExtractor(uriInfo).getSearchFilter();
        long count = synsetHistoryQueryService.countSynsetAttributesHistoryByFilter(trackerSearchFilter);
        return trackerEntityBuilder.buildSynsetAttributesHistorySearchList(
                synsetHistoryQueryService.findSynsetAttributesHistoryByFilter(trackerSearchFilter),
                count,
                trackerSearchFilter.getEnd() / TrackerSearchFilterUrlExtractor.ELEMENTS_PER_PAGE,
                trackerSearchFilter.getEnd() <= count,
                trackerSearchFilter.getStart() != 0

        );
    }

    @GET
    @Path("relations/search")
    public JsonObject searchRelationsHistory() {
        final TrackerSearchFilter trackerSearchFilter = new TrackerSearchFilterUrlExtractor(uriInfo).getSearchFilter();
        long count = synsetHistoryQueryService.countSynsetRelationsHistoryByFilter(trackerSearchFilter);
        return trackerEntityBuilder.buildSynsetRelationsHistorySearchList(
                synsetHistoryQueryService.findSynsetRelationsHistoryByFilter(trackerSearchFilter),
                count,
                trackerSearchFilter.getEnd() / TrackerSearchFilterUrlExtractor.ELEMENTS_PER_PAGE,
                trackerSearchFilter.getEnd() <= count,
                trackerSearchFilter.getStart() != 0
        );
    }

    @GET
    @Path("senses/{id}")
    public JsonObject synsetSensesHistory(@PathParam("id") final UUID id) {
        return trackerEntityBuilder.buildSenseListHistory(senseHistoryQueryService.findBySynsetId(id));
    }
}
