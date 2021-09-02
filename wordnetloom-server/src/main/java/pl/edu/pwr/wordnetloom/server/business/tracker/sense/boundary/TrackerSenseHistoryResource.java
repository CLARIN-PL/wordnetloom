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
        return trackerEntityBuilder.buildSenseHistorySearchList(
                senseHistoryQueryService.findSenseHistoryByFilter(trackerSearchFilter),
                senseHistoryQueryService.countSenseHistoryByFilter(trackerSearchFilter)
        );
    }

    @GET
    @Path("attributes/search")
    public JsonObject searchAttributesHistory() {
        final TrackerSearchFilter trackerSearchFilter = new TrackerSearchFilterUrlExtractor(uriInfo).getSearchFilter();
        return trackerEntityBuilder.buildSenseAttributesHistorySearchList(
                senseHistoryQueryService.findSenseAttributesHistoryByFilter(trackerSearchFilter),
                senseHistoryQueryService.countSenseAttributesHistoryByFilter(trackerSearchFilter)
        );
    }

    @GET
    @Path("relations/search")
    public JsonObject searchRelationsHistory() {
        final TrackerSearchFilter trackerSearchFilter = new TrackerSearchFilterUrlExtractor(uriInfo).getSearchFilter();
        return trackerEntityBuilder.buildSenseRelationsHistorySearchList(
                senseHistoryQueryService.findSenseRelationsByFilter(trackerSearchFilter),
                senseHistoryQueryService.countSenseRelationsByFilter(trackerSearchFilter)
        );
    }
}
