package pl.edu.pwr.wordnetloom.server.business.dictionary.boundary;

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
                .add("ages", this.linkBuilder.forAges(uriInfo).toString())
                .add("sources", this.linkBuilder.forSources(uriInfo).toString())
                .add("transcriptions", this.linkBuilder.forTranscription(uriInfo).toString())
                .add("styles", this.linkBuilder.forStyles(uriInfo).toString())
                .add("suffixes", this.linkBuilder.forSuffixes(uriInfo).toString())
                .add("prefixes", this.linkBuilder.forPrefixes(uriInfo).toString())
                .add("interfixes", this.linkBuilder.forInterfixes(uriInfo).toString())
                .add("inflections", this.linkBuilder.forInflections(uriInfo).toString())
                .add("dialectals", this.linkBuilder.forDialectals(uriInfo).toString())
                .add("yiddish_statuses", this.linkBuilder.forYiddishStatuses(uriInfo).toString())
                .add("yiddish_domains", this.linkBuilder.forYiddishDomains(uriInfo).toString())
                .add("yiddish_domain_modifiers", this.linkBuilder.forYiddishDomainModifiers(uriInfo).toString())
                .add("lexical_characteristics", this.linkBuilder.forLexicalCharacteristics(uriInfo).toString())
                .add("grammatical_genders", this.linkBuilder.forGrammaticalGenders(uriInfo).toString())
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

    @PUT
    @Path("statuses/{id:\\d+}")
    public JsonObject getUpdateStatus(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id, JsonObject dic) {
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

    @GET
    @Path("grammatical-genders")
    public JsonObject getAllGrammaticalGenders(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(GrammaticalGenderDictionary.class, "getGrammaticalGender", locale);
    }

    @GET
    @Path("grammatical-genders/{id}")
    public JsonObject getGrammaticalGender(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id){
        return buildDictionary(id, "getGrammaticalGender", locale);
    }

    @GET
    @Path("lexical-characteristics")
    public JsonObject getAllLexicalCharacteristics(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(LexicalCharacteristicDictionary.class, "getLexicalCharacteristic", locale);
    }

    @GET
    @Path("lexical-characteristic/{id}")
    public JsonObject getLexicalCharacteristic(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id){
        return buildDictionary(id, "getLexicalCharacteristic", locale);
    }

    @GET
    @Path("yiddish-domain-modifiers")
    public JsonObject getAllYiddishDomainModifiers(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(DomainModifierDictionary.class, "getYiddishDomainModifier", locale);
    }

    @GET
    @Path("yiddish-domain-modifiers/{id}")
    public JsonObject getYiddishDomainModifier(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id){
        return buildDictionary(id, "getYiddishDomainModifier", locale);
    }

    @GET
    @Path("yiddish-domains")
    public JsonObject getAllYiddishDomains(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(DomainDictionary.class, "getYiddishDomain", locale);
    }

    @GET
    @Path("yiddish-domains/{id}")
    public JsonObject getYiddishDomain(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id){
        return buildDictionary(id, "getYiddishDomain", locale);
    }

    @GET
    @Path("yiddish-statuses")
    public JsonObject getAllYiddishStatuses(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(StatusDictionary.class, "getYiddishStatus", locale);
    }

    @GET
    @Path("yiddish-statuses/{id}")
    public JsonObject getYiddishStatus(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id){
        return buildDictionary(id, "getYiddishStatus", locale);
    }

    @GET
    @Path("dialectals")
    public JsonObject getAllDialectals(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(DialectalDictionary.class, "getDialectal", locale);
    }

    @GET
    @Path("dialectals/{id}")
    public JsonObject getDialectal(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id){
        return buildDictionary(id, "getDialectal", locale);
    }

    @GET
    @Path("inflections")
    public JsonObject getAllInflections(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(InflectionDictionary.class, "getInflection", locale);
    }

    @GET
    @Path("inflections/{id}")
    public JsonObject getInflection(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id){
        return buildDictionary(id, "getInflection", locale);
    }

    @GET
    @Path("interfixes")
    public JsonObject getAllInterfixes(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(InterfixDictionary.class, "getInterfix", locale);
    }

    @GET
    @Path("interfixes/{id}")
    public JsonObject getInterfix(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id){
        return buildDictionary(id, "getInterfix", locale);
    }

    @GET
    @Path("suffixes")
    public JsonObject getAllSuffixes(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(SuffixDictionary.class, "getSuffix", locale);
    }

    @GET
    @Path("suffixes/{id}")
    public JsonObject getSuffix(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id){
        return buildDictionary(id, "getSuffix", locale);
    }

    @GET
    @Path("prefixes")
    public JsonObject getAllPrefixes(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(PrefixDictionary.class, "getPrefix", locale);
    }

    @GET
    @Path("prefixes/{id}")
    public JsonObject getPrefix(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id){
        return buildDictionary(id, "getPrefix", locale);
    }

    @GET
    @Path("styles")
    public JsonObject getAllStyles(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(StyleDictionary.class, "getStyle", locale);
    }

    @GET
    @Path("styles/{id}")
    public JsonObject getStyle(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id){
        return buildDictionary(id, "getStyle", locale);
    }

    @GET
    @Path("transcriptions")
    public JsonObject getAllTranscriptions(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(TranscriptionDictionary.class, "getTranscription", locale);
    }

    @GET
    @Path("transcriptions/{id}")
    public JsonObject getTranscription(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id){
        return buildDictionary(id, "getTranscription", locale);
    }

    @GET
    @Path("sources")
    public JsonObject getAllSources(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(SourceDictionary.class, "getSource", locale);
    }

    @GET
    @Path("sources/{id:\\d+}")
    public JsonObject getSource(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id){
        return buildDictionary(id, "getSource", locale);
    }

    @GET
    @Path("ages")
    public JsonObject getAllAges(@HeaderParam("Accept-Language") Locale locale) {
        return buildDictionaryArray(AgeDictionary.class, "getAge", locale);
    }

    @GET
    @Path("ages/{id:\\d+}")
    public JsonObject getAge(@HeaderParam("Accept-Language") Locale locale, @PathParam("id") long id){
        return buildDictionary(id, "getAge", locale);
    }

}
