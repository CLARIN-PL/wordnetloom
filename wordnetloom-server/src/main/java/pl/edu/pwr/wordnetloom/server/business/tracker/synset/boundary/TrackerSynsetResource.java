package pl.edu.pwr.wordnetloom.server.business.tracker.synset.boundary;

import pl.edu.pwr.wordnetloom.server.business.synset.control.SynsetQueryService;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetAttributes;
import pl.edu.pwr.wordnetloom.server.business.tracker.TrackerEntityBuilder;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("tracker/synsets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class TrackerSynsetResource {

    @Inject
    SynsetQueryService synsetQueryService;

    @Inject
    TrackerEntityBuilder trackerEntityBuilder;

    @GET
    @Path("{id}")
    public JsonObject synset(@PathParam("id") final UUID id) {
        SynsetAttributes synsetAttributes = synsetQueryService.findSynsetAttributes(id).orElse(null);
        return synsetQueryService.findById(id)
                .map(s -> trackerEntityBuilder.buildSynset(s, synsetAttributes))
                .orElse(Json.createObjectBuilder().build());
    }

    @GET
    @Path("/incoming-relations/{id}")
    public JsonObject synsetIncomingRelations(@PathParam("id") final UUID id) {
        return trackerEntityBuilder.buildSynsetIncomingRelations(synsetQueryService.findSynsetIncomingRelationsById(id));
    }

    @GET
    @Path("/outgoing-relations/{id}")
    public JsonObject synsetOutgoingRelations(@PathParam("id") final UUID id) {
        return trackerEntityBuilder.buildSynsetOutgoingRelations(synsetQueryService.findSynsetOutgoingRelationsById(id));
    }

    @GET
    @Path("/senses/{id}")
    public JsonObject synsetSenses(@PathParam("id") final UUID id) {
        return synsetQueryService.findById(id)
                .map(s -> trackerEntityBuilder.buildSynsetSenses(s))
                .orElse(Json.createObjectBuilder().build());
    }

    @GET
    @Path("/relations")
    public JsonObject allSenseRelations() {
        return trackerEntityBuilder.buildListOfRelations(
                synsetQueryService.findAllRelations()
        );
    }
}
