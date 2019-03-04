package pl.edu.pwr.wordnetloom.server.business.corpusexample.boundary;


import pl.edu.pwr.wordnetloom.server.business.EntityBuilder;
import pl.edu.pwr.wordnetloom.server.business.LinkBuilder;
import pl.edu.pwr.wordnetloom.server.business.corpusexample.entity.CorpusExample;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonCollectors;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.HttpURLConnection;
import java.util.Locale;
import java.util.stream.Collectors;

import static javax.json.Json.createObjectBuilder;

@Path("/corpus-examples")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CorpusExampleResource {

        @Inject
        EntityBuilder entityBuilder;

        @Context
        UriInfo uriInfo;

        @Inject
        CorpusExampleQueryService service;

        @GET
        public JsonObject corpusExamples(){
            return entityBuilder.buildExamplesCorpus(uriInfo);
        }

        @GET
        @Path("search")
        public Response searchCorpusExamples(@QueryParam("lemma") String lemma) {
            if(lemma == null){
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity(entityBuilder.buildErrorObject("Lemma query param is mandatory",
                                    Response.Status.BAD_REQUEST))
                            .build();
            }

            return Response.ok().entity(entityBuilder.buildCorpusExample(uriInfo.getRequestUri(), lemma, service
                    .findAllByWord(lemma)
                    .stream()
                    .map(CorpusExample::getText)
                    .collect(Collectors.toList()))).build();
        }
}
