package pl.edu.pwr.wordnetloom.server.business.user.boundary;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import pl.edu.pwr.wordnetloom.server.business.EntityBuilder;
import pl.edu.pwr.wordnetloom.server.business.LinkBuilder;
import pl.edu.pwr.wordnetloom.server.business.OperationResult;
import pl.edu.pwr.wordnetloom.server.business.user.entity.User;

import javax.annotation.security.RolesAllowed;
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
@Tag(name = "User Resource", description = "Methods for users managements")
@SecurityRequirement(name = "bearerAuth")
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
    @Path("{id:\\d+}")
    @Operation(summary = "Get user by id", description = "Get user all infos (by id)")
    public JsonObject user(@PathParam("id") long id) {
        return service.findById(id)
                .map(l -> entityBuilder.buildUser(l, uriInfo))
                .orElse(Json.createObjectBuilder().build());
    }

    @POST
    @RolesAllowed({"admin"})
    @Operation(summary = "Add new user", description = "Create new user and store it in database")
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
    @RolesAllowed({"admin"})
    @Path("{id:\\d+}")
    @Operation(summary = "Delete user by id", description = "Delete user (by id)")
    public Response deleteUser(@PathParam("id") final Long id) {
        service.deleteUser(id);
        return Response.noContent()
                .build();
    }
}
