package pl.edu.pwr.wordnetloom.server.business.dictionary.boundary;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import pl.edu.pwr.wordnetloom.server.business.EntityBuilder;
import pl.edu.pwr.wordnetloom.server.business.LinkBuilder;
import pl.edu.pwr.wordnetloom.server.business.OperationResult;
import pl.edu.pwr.wordnetloom.server.business.dictionary.control.DictionaryQueryService;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.*;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonCollectors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Locale;

import static javax.json.Json.createObjectBuilder;

@Path("dictionaries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Dictionaries Resource", description = "Methods for dictionaries")
public class DictionaryResource {

    @Inject
    DictionaryQueryService query;

    @Inject
    DictionaryCommandService command;

    @Inject
    EntityBuilder entityBuilder;

    @Inject
    LinkBuilder linkBuilder;

    @Context
    UriInfo uriInfo;

    @GET
    public JsonObject getDictionaries(){

        final JsonObjectBuilder linkBuilder = createObjectBuilder();
        linkBuilder.add("_links", Json.createObjectBuilder()
                .add("domains", this.linkBuilder.forDomains(uriInfo).toString())
                .add("parts_of_speech", this.linkBuilder.forPartsOfSpeech(uriInfo).toString())
                .add("statuses", this.linkBuilder.forStatutes(uriInfo).toString())
                .add("registers", this.linkBuilder.forRegisters(uriInfo).toString())
                .build());

        return linkBuilder.build();
    }

    @GET
    @Path("statuses")
    public JsonObject getAllStatuses(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(Status.class, "getStatus", locale);
    }

    @GET
    @Path("statuses/{id:\\d+}")
    public JsonObject getStatus(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id) {
        return buildDictionary(id, "getStatus", locale);
    }

    @POST
    @Path("statuses")
    public JsonObject addStatus(@HeaderParam("Accept-Language") Locale locale, JsonObject dic) {
        return command.addStatus(locale, dic)
                .map(d -> entityBuilder.buildDictionary(d, linkBuilder.forDictionary(d, "getStatus", uriInfo), locale))
                .orElse(Json.createObjectBuilder().build());
    }
    @PUT
    @Path("statuses/{id:\\d+}")
    public JsonObject updateStatus(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id, JsonObject dic) {
         return command.updateStatus(id, locale, dic)
        .map(d -> entityBuilder.buildDictionary(d, linkBuilder.forDictionary(d, "getStatus", uriInfo), locale))
                .orElse(Json.createObjectBuilder().build());
    }

    @GET
    @Path("registers")
    public JsonObject getAllRegisters(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(Register.class, "getRegister", locale);
    }

    @GET
    @Path("registers/{id:\\d+}")
    public JsonObject getRegister(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id) {
        return buildDictionary(id, "getRegister", locale);
    }

    @POST
    @Path("registers")
    public JsonObject addRegister(@HeaderParam("Accept-Language") Locale locale, JsonObject dic) {
        return command.addRegister(locale, dic)
                .map(d -> entityBuilder.buildDictionary(d, linkBuilder.forDictionary(d, "getRegister", uriInfo), locale))
                .orElse(Json.createObjectBuilder().build());
    }
    @PUT
    @Path("registers/{id:\\d+}")
    public JsonObject updateRegister(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id, JsonObject dic) {
        return command.updateRegister(id, locale, dic)
                .map(d -> entityBuilder.buildDictionary(d, linkBuilder.forDictionary(d, "getRegister", uriInfo), locale))
                .orElse(Json.createObjectBuilder().build());
    }

    @GET
    @Path("domains")
    public JsonObject getAllDomains(@HeaderParam("Accept-Language") Locale locale) {
        return entityBuilder.buildDictionaryDomains(query.findAllDomains(),uriInfo,locale);
    }

    @GET
    @Path("domains/{id:\\d+}")
    public JsonObject getDomain(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id) {
        return query.findDomain(id)
                .map(d -> entityBuilder.buildDomain(d, linkBuilder.forDomain(d, uriInfo), locale))
                .orElse(Json.createObjectBuilder().build());

    }

    @GET
    @Path("parts-of-speech/{id:\\d+}")
    public JsonObject getPartOfSpeech(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id) {
        return query.findPartsOfSpeech(id)
                .map(p -> entityBuilder.buildPartOfSpeech(p,  linkBuilder.forPartOfSpeech(p, uriInfo), locale))
                .orElse(Json.createObjectBuilder().build());
    }

    @GET
    @Path("parts-of-speech")
    public JsonObject getAllPartsOfSpeech(@HeaderParam("Accept-Language") Locale locale) {
        return entityBuilder.buildPartOfSpeeches(query.findAllPartsOfSpeech(), uriInfo, locale);
    }

    private <T> JsonObject buildDictionaryArray(final Class<T> clazz, String methodName, final Locale locale) {
        return entityBuilder.buildDictionaries(query.findDictionaryByClass(clazz), methodName,uriInfo, locale);
    }

    private JsonObject buildDictionary(long id, String methodName, final Locale locale) {
         return query.findDictionaryById(id)
                .map(d -> entityBuilder.buildDictionary(d, linkBuilder.forDictionary(d, methodName, uriInfo), locale))
                .orElse(Json.createObjectBuilder().build());

    }
}
