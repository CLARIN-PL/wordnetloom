package pl.edu.pwr.wordnetloom.server.business.sense.boundary;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import pl.edu.pwr.wordnetloom.server.business.*;
import pl.edu.pwr.wordnetloom.server.business.graph.control.GraphQueryService;
import pl.edu.pwr.wordnetloom.server.business.graph.entity.NodeExpanded;
import pl.edu.pwr.wordnetloom.server.business.sense.control.SenseQueryService;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Path("senses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
@Tag(name = "Sense Resource", description = "Methods for senses management")
@SecurityRequirement(name = "bearerAuth")
public class SenseResource {

    @Inject
    SenseQueryService queryService;

    @Inject
    SenseCommandService senseCommandService;

    @Inject
    EntityBuilder entityBuilder;

    @Inject
    GraphQueryService graphQuery;

    @Inject
    LinkBuilder linkBuilder;

    @Context
    UriInfo uriInfo;

    @GET
    @Operation(summary = "Get senses links", description = "Get all available senses operations with links")
    public JsonObject senses() {
        return entityBuilder.buildSenses(uriInfo);
    }

    @POST
    @RolesAllowed({"admin"})
    @Operation(summary = "Add new sense", description = "Add new sense to database")
    public Response addSense(JsonObject sense) {
        OperationResult<Sense> s = senseCommandService.save(sense);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.created(linkBuilder.forSense(s.getEntity(), uriInfo))
                .build();
    }

    @GET
    @Path("search")
    @Operation(summary = "Search sense", description = "Search for sense with params")
    public JsonObject search(@HeaderParam("Accept-Language") Locale locale) {
        final SearchFilter searchFilter = new SearchFilterExtractorFromUrl(uriInfo).getFilter();
        long count = queryService.countWithFilter(searchFilter);
        List<Sense> senses = queryService.findByFilter(searchFilter);
        return entityBuilder.buildPaginatedSenseSearch(senses, count, searchFilter, uriInfo, locale);
    }

    @GET
    @Path("{id}")
    @Operation(summary = "Get sense by id", description = "Get sense all infos (by id)")
    public JsonObject sense(@HeaderParam("Accept-Language") Locale locale,
                            @PathParam("id") final UUID id) {
        final SenseAttributes attributes = queryService.findSenseAttributes(id).orElse(null);
        return queryService.findById(id)
                .map(s -> entityBuilder.buildSense(s, attributes, linkBuilder.forSense(s, uriInfo), uriInfo, locale))
                .orElse(Json.createObjectBuilder().build());
    }

    @PUT
    @RolesAllowed({"admin"})
    @Path("{id}")
    @Operation(summary = "Edit sense by id", description = "Edit the existing sense by id")
    public Response updateSense(JsonObject sense,
                                @PathParam("id") UUID id) {
        OperationResult<Sense> s = senseCommandService.update(sense);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.ok()
                .location(linkBuilder.forSense(s.getEntity(), uriInfo))
                .build();
    }

    @DELETE
    @RolesAllowed({"admin"})
    @Path("{id}")
    @Operation(summary = "Delete sense by id", description = "Delete the existing sense by id")
    public Response deleteSense(@PathParam("id") final UUID id) {
        senseCommandService.deleteSense(id);
        return Response.noContent()
                .build();
    }

    @PUT
    @RolesAllowed({"admin"})
    @Path("{id}/move-up")
    @Operation(summary = "Move sense up", description = "Move sense in synset to upper position")
    public Response moveUp(@HeaderParam("Accept-Language") Locale locale,
                           @PathParam("id") final UUID id) {
        senseCommandService.moveSenseUp(id);
        return Response.ok().build();
    }

    @PUT
    @RolesAllowed({"admin"})
    @Path("{id}/move-down")
    @Operation(summary = "Move sense down", description = "Move sense in synset to lower position")
    public Response moveDown(@HeaderParam("Accept-Language") Locale locale,
                             @PathParam("id") final UUID id) {
        senseCommandService.moveSenseDown(id);
        return Response.ok().build();
    }

    @PUT
    @RolesAllowed({"admin"})
    @Path("{id}/detach-synset")
    @Operation(summary = "Detach sense", description = "Detach sense (by id) from synset")
    public Response detachSynset(@HeaderParam("Accept-Language") Locale locale,
                                 @PathParam("id") final UUID id) {
        senseCommandService.detachFromSynset(id);
        return Response.ok().build();
    }

    @PUT
    @RolesAllowed({"admin"})
    @Path("{id}/attach-to-synset/{synsetId}")
    @Operation(summary = "Attach sense", description = "Attach sense (by id) to synset (by id)")
    public Response attachToSynset(@PathParam("id") final UUID senseId,
                                   @PathParam("synsetId") final UUID synsetId) {
        OperationResult<Sense> s = senseCommandService.attachToSynset(senseId, synsetId);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.ok().location(linkBuilder.forSense(s.getEntity(), uriInfo))
                .build();
    }

    @GET
    @Path("{id}/graph")
    @Operation(summary = "Get sense graph", description = "Get graph for sense (by id)")
    public Response senseGraph(@HeaderParam("Accept-Language") Locale locale,
                               @PathParam("id") final UUID id) {
        NodeExpanded node = graphQuery.senseGraph(id, locale);
        return Response.ok().entity(JsonbBuilder.create().toJson(node)).build();
    }

    @GET
    @Path("{id}/examples")
    @Operation(summary = "Get sense examples", description = "Get examples for sense (by id)")
    public JsonObject senseExamples(@HeaderParam("Accept-Language") Locale locale,
                                    @PathParam("id") final UUID id) {
        if (queryService.findSenseAttributes(id).isPresent()) {
            return entityBuilder.buildSenseExamples(id, queryService.findSenseAttributes(id).get().getExamples(), uriInfo);
        }
        return entityBuilder.buildSenseExamples(id, new HashSet<>(), uriInfo);
    }

    @GET
    @Path("{senseId}/examples/{exampleId}")
    @Operation(summary = "Get sense example", description = "Get example (by id) infos for sense (by id)")
    public JsonObject senseExample(@HeaderParam("Accept-Language") Locale locale,
                                   @PathParam("senseId") final UUID senseId,
                                   @PathParam("exampleId") final UUID exampleId) {
        return queryService.findSenseExample(exampleId)
                .map(e -> entityBuilder.buildSenseExample(e, linkBuilder.forSenseExample(e, uriInfo)))
                .orElse(Json.createObjectBuilder().build());
    }

    @POST
    @RolesAllowed({"admin"})
    @Path("{senseId}/examples")
    @Operation(summary = "Add sense example", description = "Add new example for sense (by id)")
    public Response addExample(@PathParam("senseId") final UUID senseId,
                               JsonObject example) {
        OperationResult<SenseExample> s = senseCommandService.addExample(senseId, example);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.created(linkBuilder.forSenseExample(s.getEntity(), uriInfo))
                .build();
    }

    @PUT
    @RolesAllowed({"admin"})
    @Path("{senseId}/examples/{exampleId}")
    @Operation(summary = "Edit sense example", description = "Edit existing example (by id) for sense (by id)")
    public Response updateExample(JsonObject example,
                                  @PathParam("senseId") final UUID senseId,
                                  @PathParam("exampleId") final UUID id) {
        OperationResult<SenseExample> s = senseCommandService.updateExample(example);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.ok().location(linkBuilder.forSenseExample(s.getEntity(), uriInfo))
                .build();
    }

    @DELETE
    @RolesAllowed({"admin"})
    @Path("{senseId}/examples/{exampleId}")
    @Operation(summary = "Delete sense example", description = "Delete existing example (by id) for sense (by id)")
    public Response removeSenseExample(@PathParam("senseId") final UUID senseId,
                                       @PathParam("exampleId") final UUID exampleId) {
        senseCommandService.deleteExample(exampleId);
        return Response.noContent().build();
    }

    @GET
    @Path("{id}/relations")
    @Operation(summary = "Get sense relations", description = "Get all relations for sense (by id)")
    public JsonObject senseRelations(@HeaderParam("Accept-Language") Locale locale,
                                     @PathParam("id") final UUID id) {
        return queryService.findByIdWithRelations(id)
                .map(s -> entityBuilder.buildSenseRelations(s, uriInfo, locale))
                .orElse(Json.createObjectBuilder().build());
    }

    @GET
    @Path("relations")
    @Operation(summary = "Get all relations", description = "Get all relations for sense all senses")
    public JsonObject relations(@HeaderParam("Accept-Language") Locale locale) {
        return entityBuilder.buildSenseRelations(uriInfo);
    }

    @GET
    @Path("relations/search")
    @Operation(summary = "Get relation between two senses", description = "Get details of relation between two senses")
    public JsonObject relations(@HeaderParam("Accept-Language") Locale locale,
                                @QueryParam("target") UUID target,
                                @QueryParam("source") UUID source,
                                @QueryParam("relation_type") UUID relationType) {

        List<SenseRelation> r = queryService.findSenseRelations(source, target, relationType);
        return entityBuilder.buildSenseRelations(r, locale, uriInfo);
    }

    @GET
    @Path("relations/{source}/{relationType}/{target}")
    @Operation(summary = "Get relation between two senses", description = "Get details of relation between two senses")
    public JsonObject relation(@HeaderParam("Accept-Language") Locale locale,
                               @PathParam("source") final UUID source,
                               @PathParam("relationType") final UUID relationType,
                               @PathParam("target") final UUID target) {
        return queryService.findSenseRelation(source, target, relationType)
                .map(r -> entityBuilder.buildSenseRelation(r, locale, uriInfo))
                .orElse(Json.createObjectBuilder().build());
    }

    @POST
    @RolesAllowed({"admin"})
    @Path("relations")
    @Operation(summary = "Add new relation", description = "Add new relation between two senses")
    public Response addSenseRelation(@HeaderParam("Accept-Language") Locale locale,
                                     JsonObject relation) {
        OperationResult<SenseRelation> s = senseCommandService.addSenseRelation(relation);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.created(linkBuilder.forSenseRelation(s.getEntity(), uriInfo))
                .build();
    }

    @DELETE
    @RolesAllowed({"admin"})
    @Path("relations/{source}/{relationType}/{target}")
    @Operation(summary = "Delete relation", description = "Delete relation between two senses")
    public Response deleteSenseRelation(@PathParam("source") final UUID source,
                                        @PathParam("relationType") final UUID relationType,
                                        @PathParam("target") final UUID target) {
        senseCommandService.deleteRelation(source, target, relationType);
        return Response.noContent().build();
    }

    @GET
    @Path("{id}/emotional-annotations")
    @Operation(summary = "Get sense emotions", description = "Get sense emotional annotations")
    public Response findEmotionalAnnotations(@PathParam("id") final UUID senseId) {
        return queryService.findEmotionalAnnotationBySenseId(senseId)
                .map(e -> Response.ok().entity(entityBuilder.buildEmotionalAnnotation(e, uriInfo)).build())
                .orElse(Response.status(Response.Status.BAD_REQUEST).build());
    }

    @POST
    @RolesAllowed({"admin"})
    @Path("{id}/emotional-annotations")
    @Operation(summary = "Add new emotional annotation")
    public Response saveEmotionalAnnotations(@PathParam("id") final UUID senseId, JsonObject ann) {
        OperationResult<EmotionalAnnotation> ea = senseCommandService.saveEmotionalAnnotation(ann);
        if (ea.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ea.getErrors()).build();
        }
        return Response.ok().entity(entityBuilder.buildEmotionalAnnotation(ea.getEntity(), uriInfo)).build();
    }

    @PUT
    @RolesAllowed({"admin"})
    @Path("{id}/emotional-annotations")
    @Operation(summary = "Edit existing emotional annotation")
    public Response updateEmotionalAnnotations(@PathParam("id") final UUID senseId,
                                               JsonObject ann) {
        OperationResult<EmotionalAnnotation> ea = senseCommandService.updateEmotionalAnnotation(ann);
        if (ea.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ea.getErrors()).build();
        }
        return Response.ok().entity(entityBuilder.buildEmotionalAnnotation(ea.getEntity(), uriInfo)).build();
    }

}
