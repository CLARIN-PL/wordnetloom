package pl.edu.pwr.wordnetloom.server.business.tracker.sense.boundary;

import pl.edu.pwr.wordnetloom.server.business.sense.control.SenseQueryService;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.SenseAttributes;
import pl.edu.pwr.wordnetloom.server.business.tracker.TrackerEntityBuilder;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("tracker/senses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class TrackerSenseResource {

    @Inject
    SenseQueryService senseQueryService;

    @Inject
    TrackerEntityBuilder trackerEntityBuilder;

    @GET
    @Path("{id}")
    public JsonObject sense(@PathParam("id") final UUID id) {
        final SenseAttributes attributes = senseQueryService.findSenseAttributes(id).orElse(null);
        return senseQueryService.findById(id)
                .map(s -> trackerEntityBuilder.buildSense(s, attributes))
                .orElse(Json.createObjectBuilder().build());
    }

    @GET
    @Path("/incoming-relations/{id}")
    public JsonObject senseIncomingRelations(@PathParam("id") final UUID id) {
        return trackerEntityBuilder.buildSenseIncomingRelations(senseQueryService.findAllIncomingRelationsById(id));
    }

    @GET
    @Path("/outgoing-relations/{id}")
    public JsonObject senseOutgoingRelations(@PathParam("id") final UUID id) {
        return trackerEntityBuilder.buildSenseOutgoingRelations(senseQueryService.findAllOutgoingRelationsById(id));
    }

    @GET
    @Path("/relations")
    public JsonObject allSenseRelations() {
        return trackerEntityBuilder.buildListOfSenseRelations(
                senseQueryService.findAllRelations()
        );
    }
}
