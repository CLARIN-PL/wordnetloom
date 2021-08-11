package pl.edu.pwr.wordnetloom.server.business.lexicon.boundary;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import pl.edu.pwr.wordnetloom.server.business.EntityBuilder;
import pl.edu.pwr.wordnetloom.server.business.LinkBuilder;
import pl.edu.pwr.wordnetloom.server.business.OperationResult;
import pl.edu.pwr.wordnetloom.server.business.lexicon.control.LexiconQueryService;
import pl.edu.pwr.wordnetloom.server.business.lexicon.entity.Lexicon;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Locale;

@Path("/lexicons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Lexicons Resource", description = "Methods for lexicons")
@SecurityRequirement(name = "bearerAuth")
public class LexiconResource {

    @Inject
    LexiconQueryService query;

    @Inject
    LexiconCommandService command;

    @Inject
    EntityBuilder entityBuilder;

    @Inject
    LinkBuilder linkBuilder;

    @Context
    UriInfo uriInfo;

    @GET
    @Operation(summary = "Get all lexicons", description = "Get all available lexicons")
    public JsonObject getAllLexicons(@HeaderParam("Accept-Language") Locale locale) {
        return entityBuilder.buildLexicons(query.findAll(), uriInfo);
    }

    @GET
    @Path("{id:\\d+}")
    @Operation(summary = "Get lexicon by id", description = "Get lexicon all infos (by id)")
    public JsonObject getLexicon(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id) {
        return query.findById(id)
                .map(l -> entityBuilder.buildLexicon(l, linkBuilder.forLexicon(l, uriInfo)))
                .orElse(Json.createObjectBuilder().build());
    }

    @POST
    @Operation(summary = "Add new lexicon", description = "Add new lexicon to database")
    public Response addLexicon(@HeaderParam("Accept-Language") Locale locale, JsonObject json) {
        OperationResult<Lexicon> s = command.save(json);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.created(linkBuilder.forLexicon(s.getEntity(), uriInfo))
                .build();
    }

    @PUT
    @Path("{id}")
    @Operation(summary = "Edit lexicon by id", description = "Edit the existing lexicon by id")
    public Response updateLexicon(@HeaderParam("Accept-Language") Locale locale, JsonObject json) {
        OperationResult<Lexicon> s = command.update(json);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.created(linkBuilder.forLexicon(s.getEntity(), uriInfo))
                .build();
    }

    @DELETE
    @Path("{id}")
    @Operation(summary = "Delete lexicon by id", description = "Delete the existing lexicon by id")
    public Response deleteLexicon(@PathParam("id") final Long id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED)
                .build();
    }
}
