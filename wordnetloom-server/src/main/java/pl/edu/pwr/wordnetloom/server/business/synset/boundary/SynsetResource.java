package pl.edu.pwr.wordnetloom.server.business.synset.boundary;

import pl.edu.pwr.wordnetloom.server.business.*;
import pl.edu.pwr.wordnetloom.server.business.graph.control.GraphQueryService;
import pl.edu.pwr.wordnetloom.server.business.graph.entity.NodeExpanded;
import pl.edu.pwr.wordnetloom.server.business.synset.control.SynsetQueryService;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.Synset;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetAttributes;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetExample;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetRelation;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import javax.json.stream.JsonCollectors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static javax.json.Json.createObjectBuilder;

@Path("/synsets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SynsetResource {

    @Context
    UriInfo uriInfo;

    @Inject
    SynsetCommandService synsetCommandService;

    @Inject
    SynsetQueryService synsetQueryService;

    @Inject
    GraphQueryService graphQuery;

    @Inject
    EntityBuilder entityBuilder;

    @Inject
    LinkBuilder linkBuilder;

    @GET
    public JsonObject synsets() {
        return entityBuilder.buildSynsets(uriInfo);
    }

    @GET
    @Path("search")
    public JsonObject search(@HeaderParam("Accept-Language") Locale locale) {
        final SearchFilter filter = new SearchFilterExtractorFromUrl(uriInfo).getFilter();
        final long count = synsetQueryService.countWithFilter(filter);
        return entityBuilder.buildPaginatedSynsetSearch(synsetQueryService.findByFilter(filter), count,
                filter, uriInfo, locale);
    }

    @GET
    @Path("{id}")
    public JsonObject synset(@HeaderParam("Accept-Language") Locale locale,
                             @PathParam("id") final UUID id) {
        return synsetQueryService.findById(id)
                .map(s -> entityBuilder.buildSynset(s, synsetQueryService.findSynsetAttributes(id).get(),
                        linkBuilder.forSynset(s, uriInfo), uriInfo, locale))
                .orElse(Json.createObjectBuilder().build());
    }

    @PUT
    @Path("{id}")
    public Response updateSynset(JsonObject synset,
                                 @PathParam("id") final UUID id){
        System.out.println("Updating synset ");
        OperationResult<Synset> updatingSynsetResult = synsetCommandService.update(id, synset);
        if(updatingSynsetResult.hasErrors()){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(updatingSynsetResult.getErrors()).build();
        }

        return Response.ok()
                .location(linkBuilder.forSynset(updatingSynsetResult.getEntity(), uriInfo))
                .build();
    }

    @GET
    @Path("{id}/path-to-hyperonymy")
    public Response pathToHyperonymy(@HeaderParam("Accept-Language") Locale locale,
                                       @PathParam("id") final UUID id){
        // TODO: zrobić ścieżkę do hiperonimu
        List<NodeExpanded> expanded = graphQuery.pathToHyperonym(id, locale);

        return Response.ok().entity(JsonbBuilder.create().toJson(expanded)).build();
    }

    @POST
    public Response addSynset(JsonObject synset) {
        OperationResult<Synset> s = synsetCommandService.save(synset);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.created(linkBuilder.forSynset(s.getEntity(), uriInfo))
                .build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteSynset(@PathParam("id") final UUID id) {
        synsetCommandService.delete(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/add-sense-to-new-synset/{senseId}")
    public Response addSenseToNewSynset(@HeaderParam("Accept-Language") Locale locale,
                             @PathParam("senseId") final UUID senseId) {
        OperationResult<Synset> s = synsetCommandService.addSenseToNewSynset(senseId);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.created(linkBuilder.forSynset(s.getEntity(), uriInfo))
                .build();
    }

    @PUT
    @Path("{id}")
    public Response updateSynset(@PathParam("id") final UUID id, JsonObject synset) {
        OperationResult<Synset> s = synsetCommandService.update(id, synset);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.ok()
                .location(linkBuilder.forSynset(s.getEntity(), uriInfo))
                .build();
    }

    @GET
    @Path("{id}/graph")
    public Response synsetGraph(@HeaderParam("Accept-Language") Locale locale,
                                @PathParam("id") final UUID id) {
        NodeExpanded node = graphQuery.synsetGraph(id, locale);
        return Response.ok().entity(JsonbBuilder.create().toJson(node)).build();
    }

    @GET
    @Path("{id}/relations")
    public JsonObject synsetRelations(@HeaderParam("Accept-Language") Locale locale,
                                      @PathParam("id") final UUID id) {
        return synsetQueryService.findSynsetRelations(id)
                .map(s -> entityBuilder.buildSynsetRelations(synsetQueryService.findSynsetHead(id).get(), s, uriInfo, locale))
                .orElse(Json.createObjectBuilder().build());
    }


    @GET
    @Path("relations/{source}/{relationType}/{target}")
    public JsonObject relation(@HeaderParam("Accept-Language") Locale locale,
                               @PathParam("source") final UUID source,
                               @PathParam("relationType") final UUID relationType,
                               @PathParam("target") final UUID target) {
        return synsetQueryService.findSynsetRelationByKey(source, target, relationType)
                .map(r -> entityBuilder.buildSynsetRelation(r,
                        synsetQueryService.findSynsetHead(r.getParent().getId()),
                        synsetQueryService.findSynsetHead(r.getChild().getId()),
                        locale, uriInfo))
                .orElse(Json.createObjectBuilder().build());
    }

    @GET
    @Path("relations")
    public JsonObject relations() {
        return entityBuilder.buildSynsetRelations(uriInfo);
    }


    @POST
    @Path("relations")
    public Response addSynsetRelation(JsonObject relation) {
        OperationResult<SynsetRelation> s = synsetCommandService.addSynsetRelation(relation);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.created(linkBuilder.forSynsetRelation(s.getEntity(), uriInfo))
                .build();
    }

    @GET
    @Path("relations/search")
    public JsonObject searchRelations(@HeaderParam("Accept-Language") Locale locale,
                                @QueryParam("target") UUID target,
                                @QueryParam("source") UUID source,
                                @QueryParam("relation_type") UUID relationType) {

        List<SynsetRelation> sr = synsetQueryService.findSynsetRelations(source, target, relationType);
        return createObjectBuilder()
                .add("rows", sr.stream()
                        .map(r -> entityBuilder.buildSynsetRelation(r,
                                synsetQueryService.findSynsetHead(r.getParent().getId()),
                                synsetQueryService.findSynsetHead(r.getChild().getId()),
                                locale, uriInfo))
                        .collect(JsonCollectors.toJsonArray()))
                .add("_links", createObjectBuilder()
                        .add("self", uriInfo.getRequestUri().toString())).build();
    }


    @DELETE
    @Path("relations/{source}/{relationType}/{target}")
    public Response deleteRelation( @PathParam("source") final UUID source,
                                      @PathParam("relationType") final UUID relationType,
                                      @PathParam("target") final UUID target) {
        synsetCommandService.deleteRelation(source, relationType, target);
        return Response.noContent().build();
    }

    @GET
    @Path("{synsetId}/examples")
    public Response synsetExamples(@HeaderParam("Accept-Language") Locale locale,
                                   @PathParam("synsetId") final UUID synsetId) {
        Response.ResponseBuilder response = Response.ok();
        synsetQueryService.findSynsetAttributes(synsetId)
                .map(SynsetAttributes::getExamples)
                .ifPresent(examples -> response.entity(entityBuilder.buildSynsetExamples(synsetId, examples, uriInfo)));
        return response.build();
    }

    @POST
    @Path("{synsetId}/examples")
    public Response addExample(@PathParam("synsetId") final UUID synsetId, JsonObject example) {
        OperationResult<SynsetExample> s = synsetCommandService.addExample(synsetId, example);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.created(linkBuilder.forSynsetExample(synsetId, s.getEntity().getId(), uriInfo))
                .build();
    }

    @GET
    @Path("{synsetId}/examples/{exampleId}")
    public JsonObject synsetExample(@HeaderParam("Accept-Language") Locale locale,
                                    @PathParam("synsetId") final UUID synsetId,
                                    @PathParam("exampleId") final UUID exampleId) {
        return synsetQueryService.findSynsetExample(exampleId)
                .map(e -> entityBuilder.buildSynsetExample(synsetId, e, uriInfo))
                .orElse(Json.createObjectBuilder().build());
    }

    @PUT
    @Path("{synsetId}/examples/{exampleId}")
    public Response updateSynsetExample(@PathParam("synsetId") final UUID synsetId,
                                        @PathParam("exampleId") final UUID exampleId,
                                        JsonObject example) {
        OperationResult<SynsetExample> s = synsetCommandService.updateExample(exampleId,example);
        if (s.hasErrors()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(s.getErrors()).build();
        }
        return Response.ok().location(linkBuilder.forSynsetExample(synsetId, exampleId, uriInfo))
                .build();
    }

    @DELETE
    @Path("{synsetId}/examples/{exampleId}")
    public Response deleteSynsetExample(@PathParam("synsetId") final UUID senseId,
                                        @PathParam("exampleId") final UUID exampleId) {
        synsetCommandService.deleteExample(exampleId);
        return Response.noContent().build();
    }

}