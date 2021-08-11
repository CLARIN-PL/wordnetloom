package pl.edu.pwr.wordnetloom.server.business.security.boundary;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import pl.edu.pwr.wordnetloom.server.business.EntityBuilder;
import pl.edu.pwr.wordnetloom.server.business.OperationResult;
import pl.edu.pwr.wordnetloom.server.business.security.control.JwtManager;
import pl.edu.pwr.wordnetloom.server.business.user.control.UserFinder;
import pl.edu.pwr.wordnetloom.server.business.user.entity.User;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;
import java.util.logging.Logger;

@Path("/security")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Security Resource", description = "Methods for security management")
public class SecurityResource {

    private static final Logger log = Logger.getLogger(SecurityResource.class.getName());

    @Inject
    JwtManager jwtManager;

    @Inject
    SecurityService service;

    @Inject
    EntityBuilder entityBuilder;

    @Context
    UriInfo uriInfo;

    @Inject
    UserFinder userFinder;

    @GET
    @Operation(summary = "Get security links", description = "Get all available security operations with links")
    public JsonObject getSecurity() {
        return entityBuilder.buildSecurity(uriInfo);
    }

    @POST
    @Path("/authorize")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Operation(summary = "Authorize user", description = "Authorize user with username(email) and password")
    public Response authorize(@FormParam("username") String username, @FormParam("password") String password) {
        log.info("Authenticating " + username);
        if (username == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(entityBuilder.buildErrorObject("Username and password is mandatory", Response.Status.BAD_REQUEST))
                    .build();
        }
        try {
            return Response.ok(service.authenticate(username, password)).build();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(entityBuilder.buildErrorObject("Username and/or password is incorrect", Response.Status.BAD_REQUEST))
                .build();
    }

    @GET
    @Path("/claims")
    @Operation(summary = "Get user settings", description = "Get all user settings (only for authorized users)")
    @SecurityRequirement(name = "bearerAuth")
    public Response claims() {
        Optional<User> optUser = userFinder.getCurrentUser();
        if (optUser.isEmpty())
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(entityBuilder.buildErrorObject("Username is incorrect", Response.Status.BAD_REQUEST))
                    .build();

        return Response.ok(jwtManager.createUserString(optUser.get())).build();
    }

    @PUT
    @Path("/user")
    @Operation(summary = "Update user", description = "Update user settings and data")
    @SecurityRequirement(name = "bearerAuth")
    public Response updateUser(JsonObject json) {
        String email = userFinder.getCurrentUser().get().getEmail();
        OperationResult<User> s = service.updateUser(email, json);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.ok().build();
    }
}
