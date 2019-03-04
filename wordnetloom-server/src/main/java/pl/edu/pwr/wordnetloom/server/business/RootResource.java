package pl.edu.pwr.wordnetloom.server.business;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import static javax.json.Json.createObjectBuilder;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RootResource {

    @Inject
    EntityBuilder entityBuilder;

    @Context
    UriInfo uriInfo;

    @GET
    public JsonObject getRoot() {
        return entityBuilder.buildRootDocument(uriInfo);
    }
}
