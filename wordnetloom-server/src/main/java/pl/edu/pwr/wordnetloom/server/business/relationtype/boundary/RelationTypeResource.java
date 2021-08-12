package pl.edu.pwr.wordnetloom.server.business.relationtype.boundary;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import pl.edu.pwr.wordnetloom.server.business.EntityBuilder;
import pl.edu.pwr.wordnetloom.server.business.LinkBuilder;
import pl.edu.pwr.wordnetloom.server.business.OperationResult;
import pl.edu.pwr.wordnetloom.server.business.relationtype.control.RelationTypeQueryService;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationArgument;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationTest;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Locale;
import java.util.UUID;

@RequestScoped
@Path("/relation-types")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Relation Type Resource", description = "Methods for relation types")
@SecurityRequirement(name = "bearerAuth")
public class RelationTypeResource {

    @Inject
    RelationTypeQueryService query;

    @Inject
    RelationTypeCommandServce command;

    @Inject
    EntityBuilder entityBuilder;

    @Inject
    LinkBuilder linkBuilder;

    @Context
    UriInfo uriInfo;

    @GET
    @Operation(summary = "Get all relation types", description = "Get all available relation types")
    public JsonObject relationTypes(@HeaderParam("Accept-Language") Locale locale,
                                    @QueryParam("argument") RelationArgument argument) {
        if (argument != null) {
            return entityBuilder.buildRelationTypes(
                    query.findAllByRelationArgument(argument), uriInfo, locale);
        }
        return entityBuilder.buildRelationTypes(uriInfo);
    }

    @POST
    @RolesAllowed({"admin"})
    @Operation(summary = "Add new relation type", description = "Add new relation type to database")
    public Response addRelationType(@HeaderParam("Accept-Language") Locale locale, JsonObject json) {
        OperationResult<RelationType> s = command.save(locale, json);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.created(linkBuilder.forRelationType(s.getEntity(), uriInfo))
                .build();
    }

    @PUT
    @RolesAllowed({"admin"})
    @Path("{id}")
    @Operation(summary = "Edit relation type by id", description = "Edit the existing relation type by id")
    public Response updateRelationType(@HeaderParam("Accept-Language") Locale locale,
                                       @PathParam("id") final UUID id,
                                       JsonObject rt) {
        OperationResult<RelationType> s = command.update(id, locale, rt);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.ok()
                .location(linkBuilder.forRelationType(s.getEntity(), uriInfo))
                .build();
    }

    @GET
    @Path("{id}")
    @Operation(summary = "Get relation type by id", description = "Get relation type all infos (by id)")
    public JsonObject getRelationType(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") UUID id) {
        return query.findRelationTypeById(id)
                .map(rt -> entityBuilder.buildRelationType(rt, linkBuilder.forRelationType(rt, uriInfo),
                        linkBuilder.forRelationTests(rt, uriInfo), uriInfo, locale))
                .orElse(Json.createObjectBuilder().build());
    }

    @DELETE
    @RolesAllowed({"admin"})
    @Path("{id}")
    @Operation(summary = "Delete relation type by id", description = "Delete the existing relation type by id")
    public Response deleteRelationType(@PathParam("id") final UUID id) {
        command.deleteRelationType(id);
        return Response.noContent()
                .build();
    }


    @GET
    @Path("{id}/tests")
    @Operation(summary = "Get all relation tests", description = "Get all available relation tests for relation (by id)")
    public JsonObject getRelationTests(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") UUID relId) {
        return entityBuilder.buildRelationTests(query.findAllRelationTests(relId), uriInfo, locale);
    }

    @POST
    @RolesAllowed({"admin"})
    @Path("{id}/tests")
    @Operation(summary = "Add new relation test", description = "Add new relation test for relation (by id)")
    public Response addRelationTest(@HeaderParam("Accept-Language") Locale locale,
                                    @PathParam("id") UUID relId,
                                    JsonObject json) {
        OperationResult<RelationTest> s = command.saveTest(relId, json);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.created(linkBuilder.forRelationTest(s.getEntity(), uriInfo))
                .build();
    }

    @PUT
    @RolesAllowed({"admin"})
    @Path("{id}/tests/{testId}")
    @Operation(summary = "Get all relation test by id", description = "Get relation test (by id) for relation (by id)")
    public Response addRelationTest(@HeaderParam("Accept-Language") Locale locale,
                                    @PathParam("id") UUID relId, @PathParam("testId") long testId,
                                    JsonObject json) {
        OperationResult<RelationTest> s = command.updateTest(relId, testId, json);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.created(linkBuilder.forRelationTest(s.getEntity(), uriInfo))
                .build();
    }

    @DELETE
    @RolesAllowed({"admin"})
    @Path("{id}/tests/{testId}")
    @Operation(summary = "Delete relation test by id", description = "Delete relation test (by id) for relation (by id)")
    public Response deleteRelationTest(@PathParam("id") final UUID id) {
        command.deleteRelationType(id);
        return Response.noContent()
                .build();
    }

    @GET
    @Path("{id}/tests/{testId}")
    @Operation(summary = "Get relation test by id", description = "Get relation test (by id) for relation (by id)")
    public JsonObject getRelationTest(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") UUID relId, @PathParam("testId") long testId) {
        return query.findRelationTest(relId, testId)
                .map(rt -> entityBuilder.buildRelationTest(rt, linkBuilder.forRelationTest(rt, uriInfo), locale))
                .orElse(Json.createObjectBuilder().build());
    }
}
