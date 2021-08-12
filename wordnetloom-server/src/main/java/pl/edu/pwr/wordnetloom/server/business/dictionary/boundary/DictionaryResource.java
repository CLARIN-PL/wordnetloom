package pl.edu.pwr.wordnetloom.server.business.dictionary.boundary;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import pl.edu.pwr.wordnetloom.server.business.EntityBuilder;
import pl.edu.pwr.wordnetloom.server.business.LinkBuilder;
import pl.edu.pwr.wordnetloom.server.business.dictionary.control.DictionaryQueryService;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Locale;

import static javax.json.Json.createObjectBuilder;

@Path("dictionaries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Dictionaries Resource", description = "Methods for dictionaries")
@SecurityRequirement(name = "bearerAuth")
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
    @Operation(summary = "Dictionaries links", description = "Available operations with links for dictionaries")
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
    @Operation(summary = "Get all statuses", description = "Get all available statuses")
    public JsonObject getAllStatuses(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(Status.class, "getStatus", locale);
    }

    @GET
    @Path("statuses/{id:\\d+}")
    @Operation(summary = "Get status by id", description = "Get status all infos (by id)")
    public JsonObject getStatus(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id) {
        return buildDictionary(id, "getStatus", locale);
    }

    @POST
    @RolesAllowed({"admin"})
    @Path("statuses")
    @Operation(summary = "Add new status", description = "Add new status to database")
    public JsonObject addStatus(@HeaderParam("Accept-Language") Locale locale, JsonObject dic) {
        return command.addStatus(locale, dic)
                .map(d -> entityBuilder.buildDictionary(d, linkBuilder.forDictionary(d, "getStatus", uriInfo), locale))
                .orElse(Json.createObjectBuilder().build());
    }
    @PUT
    @RolesAllowed({"admin"})
    @Path("statuses/{id:\\d+}")
    @Operation(summary = "Edit status by id", description = "Edit the existing status by id")
    public JsonObject updateStatus(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id, JsonObject dic) {
         return command.updateStatus(id, locale, dic)
        .map(d -> entityBuilder.buildDictionary(d, linkBuilder.forDictionary(d, "getStatus", uriInfo), locale))
                .orElse(Json.createObjectBuilder().build());
    }

    @GET
    @Path("registers")
    @Operation(summary = "Get all registers", description = "Get all available registers")
    public JsonObject getAllRegisters(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(Register.class, "getRegister", locale);
    }

    @GET
    @Path("registers/{id:\\d+}")
    @Operation(summary = "Get register by id", description = "Get register all infos (by id)")
    public JsonObject getRegister(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id) {
        return buildDictionary(id, "getRegister", locale);
    }

    @POST
    @RolesAllowed({"admin"})
    @Path("registers")
    @Operation(summary = "Add new register", description = "Add new register to database")
    public JsonObject addRegister(@HeaderParam("Accept-Language") Locale locale, JsonObject dic) {
        return command.addRegister(locale, dic)
                .map(d -> entityBuilder.buildDictionary(d, linkBuilder.forDictionary(d, "getRegister", uriInfo), locale))
                .orElse(Json.createObjectBuilder().build());
    }
    @PUT
    @RolesAllowed({"admin"})
    @Path("registers/{id:\\d+}")
    @Operation(summary = "Edit register by id", description = "Edit the existing register by id")
    public JsonObject updateRegister(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id, JsonObject dic) {
        return command.updateRegister(id, locale, dic)
                .map(d -> entityBuilder.buildDictionary(d, linkBuilder.forDictionary(d, "getRegister", uriInfo), locale))
                .orElse(Json.createObjectBuilder().build());
    }

    @GET
    @Path("domains")
    @Operation(summary = "Get all domains", description = "Get all available domains")
    public JsonObject getAllDomains(@HeaderParam("Accept-Language") Locale locale) {
        return entityBuilder.buildDictionaryDomains(query.findAllDomains(), uriInfo, locale);
    }

    @GET
    @Path("domains/{id:\\d+}")
    @Operation(summary = "Get domain by id", description = "Get domain all infos (by id)")
    public JsonObject getDomain(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id) {
        return query.findDomain(id)
                .map(d -> entityBuilder.buildDomain(d, linkBuilder.forDomain(d, uriInfo), locale))
                .orElse(Json.createObjectBuilder().build());

    }

    @GET
    @Path("parts-of-speech/{id:\\d+}")
    @Operation(summary = "Get part of speech by id", description = "Get part of speech all infos (by id)")
    public JsonObject getPartOfSpeech(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id) {
        return query.findPartsOfSpeech(id)
                .map(p -> entityBuilder.buildPartOfSpeech(p,  linkBuilder.forPartOfSpeech(p, uriInfo), locale))
                .orElse(Json.createObjectBuilder().build());
    }

    @GET
    @Path("parts-of-speech")
    @Operation(summary = "Get all parts of speech", description = "Get all available parts of speech")
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
