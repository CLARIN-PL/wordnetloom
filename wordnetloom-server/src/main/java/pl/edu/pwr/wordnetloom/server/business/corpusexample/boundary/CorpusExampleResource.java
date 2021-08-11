package pl.edu.pwr.wordnetloom.server.business.corpusexample.boundary;


import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import pl.edu.pwr.wordnetloom.server.business.EntityBuilder;
import pl.edu.pwr.wordnetloom.server.business.corpusexample.entity.CorpusExample;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.stream.Collectors;


@Path("/corpus-examples")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Corpus Example Resource", description = "Methods for corpus examples")
@SecurityRequirement(name = "bearerAuth")
public class CorpusExampleResource {

        @Inject
        EntityBuilder entityBuilder;

        @Context
        UriInfo uriInfo;

        @Inject
        CorpusExampleQueryService service;

        @GET
        @Operation(summary = "Corpus examples links", description = "Available operations with links for corpus examples")
        public JsonObject corpusExamples(){
            return entityBuilder.buildExamplesCorpus(uriInfo);
        }

        @GET
        @Path("search")
        @Operation(summary = "Search corpus example", description = "Searching the corpus example by lemma")
        public Response searchCorpusExamples(@QueryParam("lemma") String lemma) {
            if(lemma == null)
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(entityBuilder.buildErrorObject("Lemma query param is mandatory",
                                Response.Status.BAD_REQUEST))
                        .build();

            return Response.ok().entity(entityBuilder.buildCorpusExample(uriInfo.getRequestUri(), lemma, service
                    .findAllByWord(lemma)
                    .stream()
                    .map(CorpusExample::getText)
                    .collect(Collectors.toList()))).build();
        }
}
