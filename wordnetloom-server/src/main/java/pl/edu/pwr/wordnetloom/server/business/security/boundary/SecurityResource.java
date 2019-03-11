package pl.edu.pwr.wordnetloom.server.business.security.boundary;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import pl.edu.pwr.wordnetloom.server.business.EntityBuilder;
import pl.edu.pwr.wordnetloom.server.business.OperationResult;
import pl.edu.pwr.wordnetloom.server.business.security.control.JwtManager;
import pl.edu.pwr.wordnetloom.server.business.security.entity.Jwt;
import pl.edu.pwr.wordnetloom.server.business.user.entity.User;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.text.ParseException;
import java.util.logging.Logger;

@Path("/security")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
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

    @GET
    public JsonObject getSecurity() {
        return entityBuilder.buildSecurity(uriInfo);
    }

    @POST
    @Path("/authorize")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authorize(@FormParam("username") String username, @FormParam("password") String password) {
        log.info("Authenticating " + username);
        if (username == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(entityBuilder.buildErrorObject("Username and password is mandatory", Response.Status.BAD_REQUEST))
                    .build();
        }
        try {
            User user = service.authenticate(username, password);
            if (user != null) {
                if (user.getEmail() != null) {
                    log.info("Generating JWT for org.jboss.user " + user.getEmail());
                }
                String token = jwtManager.createJwt(user);
                return Response.ok(new Jwt(token)).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Login error message: " + e.getMessage());
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(entityBuilder.buildErrorObject("Username and/or password is incorrect", Response.Status.BAD_REQUEST))
                .build();
    }


    @GET
    @Path("/claims")
    public Response claims(@HeaderParam("Authorization") String auth) {
        if (auth != null && auth.startsWith("Bearer ")) {
            try {
                JWT j = JWTParser.parse(auth.substring(7));
                return Response.ok(j.getJWTClaimsSet().getClaims()).build(); //Note: nimbusds converts token expiration time to milliseconds
            } catch (ParseException e) {
                log.warning(e.toString());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(entityBuilder.buildErrorObject("Invalid token", Response.Status.BAD_REQUEST))
                        .build();
            }
        }
        return Response.status(Response.Status.NO_CONTENT)
                .build(); //no jwt means no claims to extract
    }

    @PUT
    @Path("/user")
    public Response updateUser(@HeaderParam("Authorization") String auth, JsonObject json) {
        if (auth != null && auth.startsWith("Bearer ")) {
            try {
                JWT j = JWTParser.parse(auth.substring(7));
                String email = (String) j.getJWTClaimsSet().getClaims().get("sub");
                OperationResult<User> s = service.updateUser(email, json);
                if (s.hasErrors()) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(s.getErrors()).build();
                }
                return Response.ok().build();
            } catch (ParseException e) {
                log.warning(e.toString());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(entityBuilder.buildErrorObject("Invalid token", Response.Status.BAD_REQUEST))
                        .build();
            }
        }
        return Response.status(Response.Status.NO_CONTENT)
                .build();
    }

    @PUT
    @Path("/change-password")
    public Response changePassword(@HeaderParam("Authorization") String auth, JsonObject json) {
        if (auth != null && auth.startsWith("Bearer ")) {
            try {
                JWT j = JWTParser.parse(auth.substring(7));
                if (json.containsKey("password") && json.getString("password") != null && !json.getString("password").isEmpty()) {
                    String email = (String) j.getJWTClaimsSet().getClaims().get("sub");
                    service.changePassword(email, json.getString("password"));
                    return Response.ok().build();
                } else {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(entityBuilder.buildErrorObject("Invalid password", Response.Status.BAD_REQUEST))
                            .build();
                }
            } catch (ParseException e) {
                log.warning(e.toString());
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(entityBuilder.buildErrorObject("Invalid token", Response.Status.BAD_REQUEST))
                        .build();
            }
        }
        return Response.status(Response.Status.NO_CONTENT)
                .build();
    }

}
