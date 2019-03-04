package pl.edu.pwr.wordnetloom.server.business.relationtype.boundary;

import pl.edu.pwr.wordnetloom.server.business.EntityBuilder;
import pl.edu.pwr.wordnetloom.server.business.LinkBuilder;
import pl.edu.pwr.wordnetloom.server.business.OperationResult;
import pl.edu.pwr.wordnetloom.server.business.relationtype.control.RelationTypeQueryService;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationArgument;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationTest;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Locale;
import java.util.UUID;

@Path("/relation-types")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RelationTypeResource {

    @Inject
    RelationTypeQueryService query;

    @Inject
    RelationTypeCommandServce  command;

    @Inject
    EntityBuilder entityBuilder;

    @Inject
    LinkBuilder linkBuilder;

    @Context
    UriInfo uriInfo;

    @GET
    public JsonObject relationTypes(@HeaderParam("Accept-Language") Locale locale,
                                    @QueryParam("argument") RelationArgument argument) {
        if(argument != null){
            return entityBuilder.buildRelationTypes(
                    query.findAllByRelationArgument(argument), uriInfo, locale);
        }
        return entityBuilder.buildRelationTypes(uriInfo);
    }

    @POST
    public Response addRelationType(@HeaderParam("Accept-Language") Locale locale, JsonObject json) {
        OperationResult<RelationType> s = command.save(locale,json);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.created(linkBuilder.forRelationType(s.getEntity(), uriInfo))
                .build();
    }

    @PUT
    @Path("{id}")
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
    public JsonObject getRelationType(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") UUID id) {
        return query.findRelationTypeById(id)
                .map(rt -> entityBuilder.buildRelationType(rt, linkBuilder.forRelationType(rt, uriInfo),
                        linkBuilder.forRelationTests(rt, uriInfo), uriInfo, locale))
                .orElse(Json.createObjectBuilder().build());
    }

    @DELETE
    @Path("{id}")
    public Response deleteRelationType(@PathParam("id") final UUID id) {
        command.deleteRelationType(id);
        return Response.noContent()
                .build();
    }


    @GET
    @Path("{id}/tests")
    public JsonObject getRelationTests(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") UUID relId) {
        return entityBuilder.buildRelationTests(query.findAllRelationTests(relId), uriInfo, locale);
    }

    @POST
    @Path("{id}/tests")
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
    @Path("{id}/tests/{testId}")
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
    @Path("{id}/tests/{testId}")
    public Response deleteRelationTest(@PathParam("id") final UUID id) {
        command.deleteRelationType(id);
        return Response.noContent()
                .build();
    }

    @GET
    @Path("{id}/tests/{testId}")
    public JsonObject getRelationTest(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") UUID relId, @PathParam("testId") long testId) {
        return query.findRelationTest(relId, testId)
                .map(rt -> entityBuilder.buildRelationTest(rt, linkBuilder.forRelationTest(rt, uriInfo), locale))
                .orElse(Json.createObjectBuilder().build());
    }
}
