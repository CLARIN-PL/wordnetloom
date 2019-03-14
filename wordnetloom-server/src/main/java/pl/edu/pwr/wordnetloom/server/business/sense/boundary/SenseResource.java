package pl.edu.pwr.wordnetloom.server.business.sense.boundary;

import pl.edu.pwr.wordnetloom.server.business.*;
import pl.edu.pwr.wordnetloom.server.business.graph.control.GraphQueryService;
import pl.edu.pwr.wordnetloom.server.business.graph.entity.NodeExpanded;
import pl.edu.pwr.wordnetloom.server.business.sense.control.SenseQueryService;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.*;
import pl.edu.pwr.wordnetloom.server.business.yiddish.entity.YiddishSenseExtension;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
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
    public JsonObject senses() {
        return entityBuilder.buildSenses(uriInfo);
    }

    @POST
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
    public JsonObject search(@HeaderParam("Accept-Language") Locale locale) {
        final SearchFilter searchFilter = new SearchFilterExtractorFromUrl(uriInfo).getFilter();
        long count = queryService.countWithFilter(searchFilter);
        List<Sense> senses = queryService.findByFilter(searchFilter);
        return entityBuilder.buildPaginatedSenseSearch(senses, count, searchFilter, uriInfo, locale);
    }

    @GET
    @Path("{id}")
    public JsonObject sense(@HeaderParam("Accept-Language") Locale locale,
                            @PathParam("id") final UUID id) {
        final SenseAttributes attributes = queryService.findSenseAttributes(id).orElse(null);
        return queryService.findById(id)
                .map(s -> entityBuilder.buildSense(s, attributes, linkBuilder.forSense(s, uriInfo), uriInfo, locale))
                .orElse(Json.createObjectBuilder().build());
    }

    @PUT
    @Path("{id}")
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
    @Path("{id}")
    public Response deleteSense(@PathParam("id") final UUID id) {
        senseCommandService.deleteSense(id);
        return Response.noContent()
                .build();
    }


    @PUT
    @Path("{id}/move-up")
    public Response moveUp(@HeaderParam("Accept-Language") Locale locale,
                           @PathParam("id") final UUID id) {
        senseCommandService.moveSenseUp(id);
        return Response.ok().build();
    }

    @PUT
    @Path("{id}/move-down")
    public Response moveDown(@HeaderParam("Accept-Language") Locale locale,
                             @PathParam("id") final UUID id) {
        senseCommandService.moveSenseDown(id);
        return Response.ok().build();
    }

    @PUT
    @Path("{id}/detach-synset")
    public Response detachSynset(@HeaderParam("Accept-Language") Locale locale,
                                 @PathParam("id") final UUID id) {
        senseCommandService.detachFromSynset(id);
        return Response.ok().build();
    }

    @PUT
    @Path("{id}/attach-to-synset/{synsetId}")
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
    public Response senseGraph(@HeaderParam("Accept-Language") Locale locale,
                               @PathParam("id") final UUID id) {
        NodeExpanded node = graphQuery.senseGraph(id, locale);
        return Response.ok().entity(JsonbBuilder.create().toJson(node)).build();
    }

    @GET
    @Path("{id}/examples")
    public JsonObject senseExamples(@HeaderParam("Accept-Language") Locale locale,
                                    @PathParam("id") final UUID id) {
        if (queryService.findSenseAttributes(id).isPresent()) {
            return entityBuilder.buildSenseExamples(id, queryService.findSenseAttributes(id).get().getExamples(), uriInfo);
        }
        return entityBuilder.buildSenseExamples(id, new HashSet<>(), uriInfo);
    }

    @GET
    @Path("{senseId}/examples/{exampleId}")
    public JsonObject senseExample(@HeaderParam("Accept-Language") Locale locale,
                                   @PathParam("senseId") final UUID senseId,
                                   @PathParam("exampleId") final UUID exampleId) {
        return queryService.findSenseExample(exampleId)
                .map(e -> entityBuilder.buildSenseExample(e, linkBuilder.forSenseExample(e, uriInfo)))
                .orElse(Json.createObjectBuilder().build());
    }

    @POST
    @Path("{senseId}/examples")
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
    @Path("{senseId}/examples/{exampleId}")
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
    @Path("{senseId}/examples/{exampleId}")
    public Response removeSenseExample(@PathParam("senseId") final UUID senseId,
                                       @PathParam("exampleId") final UUID exampleId) {
        senseCommandService.deleteExample(exampleId);
        return Response.noContent().build();
    }

    @GET
    @Path("{id}/relations")
    public JsonObject senseRelations(@HeaderParam("Accept-Language") Locale locale,
                                     @PathParam("id") final UUID id) {
        return queryService.findByIdWithRelations(id)
                .map(s -> entityBuilder.buildSenseRelations(s, uriInfo, locale))
                .orElse(Json.createObjectBuilder().build());
    }

    @GET
    @Path("relations")
    public JsonObject relations(@HeaderParam("Accept-Language") Locale locale) {
        return entityBuilder.buildSenseRelations(uriInfo);
    }

    @GET
    @Path("relations/search")
    public JsonObject relations(@HeaderParam("Accept-Language") Locale locale,
                                @QueryParam("target") UUID target,
                                @QueryParam("source") UUID source,
                                @QueryParam("relation_type") UUID relationType) {

        List<SenseRelation> r = queryService.findSenseRelations(source, target, relationType);
        return entityBuilder.buildSenseRelations(r, locale, uriInfo);
    }

    @GET
    @Path("relations/{source}/{relationType}/{target}")
    public JsonObject relation(@HeaderParam("Accept-Language") Locale locale,
                               @PathParam("source") final UUID source,
                               @PathParam("relationType") final UUID relationType,
                               @PathParam("target") final UUID target) {
        return queryService.findSenseRelation(source, target, relationType)
                .map(r -> entityBuilder.buildSenseRelation(r, locale, uriInfo))
                .orElse(Json.createObjectBuilder().build());
    }

    @POST
    @Path("relations")
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
    @Path("relations/{source}/{relationType}/{target}")
    public Response deleteSenseRelation(@PathParam("source") final UUID source,
                                        @PathParam("relationType") final UUID relationType,
                                        @PathParam("target") final UUID target) {
        senseCommandService.deleteRelation(source, target, relationType);
        return Response.noContent().build();
    }


    @GET
    @Path("{id}/yiddish")
    public JsonObject yiddishVariants(@HeaderParam("Accept-Language") Locale locale,
                                      @PathParam("id") final UUID id) {
        return entityBuilder.buildYiddishVariants(queryService.findSenseYiddish(id), uriInfo, locale);
    }

    @GET
    @Path("{id}/yiddish/{variantId}")
    public JsonObject yiddishVariant(@HeaderParam("Accept-Language") Locale locale,
                                      @PathParam("id") final UUID id, @PathParam("variantId") Long variantId) {
        return  queryService.findYiddishVariant(id, variantId)
                .map(y -> entityBuilder.buildYiddishVariant(y, uriInfo, locale))
                .orElse(Json.createObjectBuilder().build());
    }

    @POST
    @Path("{id}/yiddish")
    public Response addVariant(@HeaderParam("Accept-Language") Locale locale,
                                      @PathParam("id") final UUID id, JsonObject json) {

        OperationResult<YiddishSenseExtension> e = senseCommandService.addYiddishVariant(id, json);
        if (e.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getErrors()).build();
        }
        return Response.created(linkBuilder.forYiddishVariant(e.getEntity(), uriInfo))
                .build();
    }

    @PUT
    @Path("{id}/yiddish/{variantId}")
    public Response updateYiddishVariant(JsonObject json,
                                  @PathParam("id") final UUID id, @PathParam("variantId") Long variantId) {
        OperationResult<YiddishSenseExtension> s = senseCommandService.updateYiddishVariant(id,variantId, json);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.ok().location(linkBuilder.forYiddishVariant(s.getEntity(), uriInfo))
                .build();
    }

    @DELETE
    @Path("{id}/yiddish/{variantId}")
    public Response removeYiddishVariant(@PathParam("id") final UUID id, @PathParam("variantId") Long variantId) {
        senseCommandService.deleteYiddishVariant(id, variantId);
        return Response.noContent().build();
    }
}