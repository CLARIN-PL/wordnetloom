package pl.edu.pwr.wordnetloom.server.business.user.boundary;

import pl.edu.pwr.wordnetloom.server.business.EntityBuilder;
import pl.edu.pwr.wordnetloom.server.business.LinkBuilder;
import pl.edu.pwr.wordnetloom.server.business.OperationResult;
import pl.edu.pwr.wordnetloom.server.business.user.entity.User;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService service;

    @Inject
    EntityBuilder entityBuilder;

    @Inject
    LinkBuilder linkBuilder;

    @Context
    UriInfo uriInfo;


    @GET
    public JsonObject users() {
        return entityBuilder.buildUsers(service.findAllUsers(), uriInfo);
    }

    @GET
    @Path("{id:\\d+}")
    public JsonObject user(@PathParam("id") long id) {
        return service.findById(id)
                .map(l -> entityBuilder.buildUser(l, uriInfo))
                .orElse(Json.createObjectBuilder().build());
    }

    @POST
    public Response createUser(JsonObject json) {
        OperationResult<User> user = service.createUser(json);
        if (user.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(user.getErrors()).build();
        }
        return Response.created(linkBuilder.forUser(user.getEntity(), uriInfo))
                .build();
    }

    @DELETE
    @Path("{id:\\d+}")
    public Response deleteUser(@PathParam("id") final Long id) {
        service.deleteUser(id);
        return Response.noContent()
                .build();
    }
}
