package pl.edu.pwr.wordnetloom.server.business.dictionary.boundary;

import pl.edu.pwr.wordnetloom.server.business.EntityBuilder;
import pl.edu.pwr.wordnetloom.server.business.LinkBuilder;
import pl.edu.pwr.wordnetloom.server.business.dictionary.control.DictionaryQueryService;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.*;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonCollectors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Locale;

import static javax.json.Json.createObjectBuilder;

@Path("dictionaries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DictionaryResource {

    @Inject
    DictionaryQueryService query;

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
                .add("emotions", this.linkBuilder.forEmotions(uriInfo).toString())
                .add("valuations", this.linkBuilder.forValuations(uriInfo).toString())
                .add("markednesses", this.linkBuilder.forMarkednesses(uriInfo).toString())
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
    @Path("emotions")
    public JsonObject getEmotions(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(Emotion.class, "getEmotion", locale);
    }

    @GET
    @Path("emotion/{id:\\d+}")
    public JsonObject getEmotion(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id) {
        return buildDictionary(id, "getEmotion", locale);
    }

    @GET
    @Path("valuations")
    public JsonObject getValuations(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(Valuation.class, "getValuation", locale);
    }

    @GET
    @Path("valuation/{id:\\d+}")
    public JsonObject getValuation(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id) {
        return buildDictionary(id, "getValuation", locale);
    }

    @GET
    @Path("markednesses")
    public JsonObject getMarkednesses(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(Markedness.class, "getMarkedness", locale);
    }

    @GET
    @Path("markedness/{id:\\d+}")
    public JsonObject getMarkedness(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id){
        return buildDictionary(id, "getMarkedness", locale);
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
