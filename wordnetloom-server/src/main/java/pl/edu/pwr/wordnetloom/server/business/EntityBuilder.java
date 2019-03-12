package pl.edu.pwr.wordnetloom.server.business;

import org.hibernate.Hibernate;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.*;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Dictionary;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Domain;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.PartOfSpeech;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Status;
import pl.edu.pwr.wordnetloom.server.business.lexicon.entity.Lexicon;
import pl.edu.pwr.wordnetloom.server.business.localistaion.control.LocalisedStringsQueryService;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.GlobalWordnetRelationType;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationTest;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.Sense;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.SenseAttributes;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.SenseExample;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.SenseRelation;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.Synset;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetAttributes;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetExample;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetRelation;
import pl.edu.pwr.wordnetloom.server.business.yiddish.entity.*;

import javax.inject.Inject;
import javax.json.*;
import javax.json.stream.JsonCollectors;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.math.BigInteger;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;

public class EntityBuilder {

    @Inject
    LocalisedStringsQueryService loc;

    @Inject
    LinkBuilder linkBuilder;

    public JsonObjectBuilder dictionaryObjectBuilder(long id, long name, Locale locale) {
        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("id", id);
        builder.add("name", loc.find(name, locale));
        return builder;
    }

    public JsonObjectBuilder selfLinkBuilder(String link) {
        JsonObjectBuilder linkBuilder = createObjectBuilder();
        linkBuilder.add("self", link);
        return linkBuilder;
    }

    public JsonObject buildErrorObject(String msg, Response.Status status) {
        final JsonObjectBuilder b = Json.createObjectBuilder();
        b.add("code", status.getStatusCode());
        b.add("message", msg);
        return b.build();
    }

    public JsonObject buildRootDocument(UriInfo uriInfo) {
        final JsonObjectBuilder entityBuilder = createObjectBuilder()
                .add("_links", Json.createObjectBuilder()
                        .add("security", linkBuilder.forSecurity(uriInfo).toString())
                        .add("corpus_examples", linkBuilder.forCorpusExamples(uriInfo).toString())
                        .add("lexicons", linkBuilder.forLexicons(uriInfo).toString())
                        .add("dictionaries", linkBuilder.forDictionaries(uriInfo).toString())
                        .add("relation_types", linkBuilder.forRelationTypes(uriInfo).toString())
                        .add("senses", linkBuilder.forSenses(uriInfo).toString())
                        .add("synsets", linkBuilder.forSynsets(uriInfo).toString()));
        return entityBuilder.build();
    }

    public JsonObject buildExamplesCorpus(UriInfo uriInfo) {
        final JsonObjectBuilder entityBuilder = createObjectBuilder().add(
                "_actions", createArrayBuilder().add(createObjectBuilder()
                        .add("name", "search")
                        .add("title", "Search corpus example")
                        .add("method", HttpMethod.GET)
                        .add("href", this.linkBuilder.forSearchCorpusExamples(uriInfo).toString())
                        .add("type", MediaType.APPLICATION_JSON)
                        .add("query_params", Json.createArrayBuilder()
                                .add(createObjectBuilder()
                                        .add("name", "lemma")
                                        .add("type", "TEXT")
                                        .add("required", "true")
                                ))));
        return entityBuilder.build();
    }

    public JsonObject buildCorpusExample(URI self, String word, List<String> examples) {
        JsonArrayBuilder array = Json.createArrayBuilder();
        examples.forEach(array::add);
        return Json.createObjectBuilder()
                .add("word", word)
                .add("rows", array)
                .add("_links", createObjectBuilder()
                        .add("self", self.toString())).build();
    }

    public JsonObject buildSecurity(UriInfo uriInfo) {
        final JsonObjectBuilder entityBuilder = createObjectBuilder()
                .add("_actions", createArrayBuilder()
                        .add(createObjectBuilder()
                                .add("name", "claims")
                                .add("title", "Claim user data from token")
                                .add("method", HttpMethod.GET)
                                .add("href", this.linkBuilder.forSecurityClaims(uriInfo).toString())
                                .add("type", MediaType.APPLICATION_JSON)
                                .add("headers", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "Authorization")
                                                .add("type", "TEXT")
                                                .add("required", "true")
                                        )))
                        .add(createObjectBuilder()
                                .add("name", "authorize")
                                .add("title", "User authorization")
                                .add("method", HttpMethod.POST)
                                .add("href", this.linkBuilder.forSecurityAuthorize(uriInfo).toString())
                                .add("type", MediaType.APPLICATION_FORM_URLENCODED)
                                .add("fields", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "username")
                                                .add("type", "TEXT")
                                                .add("required", "true")
                                        )
                                        .add(createObjectBuilder()
                                                .add("name", "password")
                                                .add("type", "TEXT")
                                                .add("required", "true")
                                        )
                                )

                        )
                        .add(createObjectBuilder()
                                .add("name", "change-password")
                                .add("title", "Change authorized user password")
                                .add("method", HttpMethod.PUT)
                                .add("href", this.linkBuilder.forSecurityChangePassword(uriInfo).toString())
                                .add("type", MediaType.APPLICATION_JSON)
                                .add("headers", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "Authorization")
                                                .add("type", "TEXT")
                                                .add("required", "true")
                                        ))
                                .add("fields", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "password")
                                                .add("type", "TEXT")
                                                .add("required", "true")
                                        )
                                )

                        )
                );
        return entityBuilder.build();
    }

    public JsonObject buildSynsets(UriInfo uriInfo) {
        final JsonObjectBuilder entityBuilder = createObjectBuilder()
                .add("_actions", createArrayBuilder()
                        .add(createObjectBuilder()
                                .add("name", "search")
                                .add("title", "Search synsets")
                                .add("method", HttpMethod.GET)
                                .add("href", this.linkBuilder.forSearchSynsets(uriInfo, 0, 100).toString())
                                .add("type", MediaType.APPLICATION_JSON)
                                .add("headers", createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "Accept-Language")
                                                .add("type", "[pl,en]")
                                                .add("required", "false")
                                        ))
                                .add("query_params", createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "lemma")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "lexicon")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "part_of_speech")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "domain")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "relation_type")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "status")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "artificial")
                                                .add("type", "BOOLEAN")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "definition")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "example")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "comment")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "start")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "limit")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))))
                        .add(createObjectBuilder()
                                .add("name", "add-synset")
                                .add("title", "Add synset")
                                .add("method", HttpMethod.POST)
                                .add("href", this.linkBuilder.forSynsets(uriInfo).toString())
                                .add("type", MediaType.APPLICATION_JSON)
                                .add("fields", createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "lexicon")
                                                .add("type", "NUMBER")
                                                .add("required", "true"))
                                        .add(createObjectBuilder()
                                                .add("name", "artificial")
                                                .add("type", "BOOLEAN")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "definition")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "comment")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "status")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "technical_comment")
                                                .add("type", "TEXT")
                                                .add("required", "false"))))
                );
        return entityBuilder.build();
    }

    public JsonObject buildSenses(UriInfo uriInfo) {

        final JsonObjectBuilder entityBuilder = createObjectBuilder()
                .add("_actions", createArrayBuilder()
                        .add(createObjectBuilder()
                                .add("name", "search")
                                .add("title", "Search senses")
                                .add("method", HttpMethod.GET)
                                .add("href", this.linkBuilder.forSearchSenses(uriInfo, 0, 100).toString())
                                .add("type", MediaType.APPLICATION_JSON)
                                .add("headers", createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "Accept-Language")
                                                .add("type", "[pl,en]")
                                                .add("required", "false")
                                        ))
                                .add("query_params", createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "lemma")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "lexicon")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "part_of_speech")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "domain")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "relation_type")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "status")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "register")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "definition")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "example")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "comment")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "senses_without_synset")
                                                .add("type", "BOOLEAN")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "start")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "limit")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))))
                        .add(createObjectBuilder()
                                .add("name", "add-sense")
                                .add("title", "Creates new sense")
                                .add("method", HttpMethod.POST)
                                .add("href", this.linkBuilder.forSenses(uriInfo).toString())
                                .add("type", MediaType.APPLICATION_JSON)
                                .add("fields", createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "lemma")
                                                .add("type", "TEXT")
                                                .add("required", "true"))
                                        .add(createObjectBuilder()
                                                .add("name", "lexicon")
                                                .add("type", "NUMBER")
                                                .add("required", "true"))
                                        .add(createObjectBuilder()
                                                .add("name", "part_of_speech")
                                                .add("type", "NUMBER")
                                                .add("required", "true"))
                                        .add(createObjectBuilder()
                                                .add("name", "domain")
                                                .add("type", "NUMBER")
                                                .add("required", "true"))
                                        .add(createObjectBuilder()
                                                .add("name", "register")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "definition")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "comment")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "status")
                                                .add("type", "NUMBER")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "technical_comment")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                )));
        return entityBuilder.build();
    }

    public JsonObject buildDictionary(Dictionary dic, URI self, Locale locale) {

        JsonObjectBuilder builder = dictionaryObjectBuilder(dic.getId(), dic.getName(), locale);

        builder.add("is_default", dic.getDefault());

        if (dic.getDescription() != null) {
            builder.add("description", loc.find(dic.getDescription(), locale));
        }

        if (dic instanceof Status) {
            builder.add("color", ((Status) dic).getColor());
        }

        JsonObjectBuilder linkBuilder = createObjectBuilder();
        linkBuilder.add("self", self.toString());

        builder.add("_links", linkBuilder);

        return builder.build();
    }

    public JsonObject buildDictionaries(List<Dictionary> dictionaries, String method, UriInfo uriInfo, Locale locale) {
        JsonArray array = dictionaries.stream()
                .map(d -> buildDictionaryShort(d, method, uriInfo, locale))
                .collect(JsonCollectors.toJsonArray());
        return Json.createObjectBuilder()
                .add("rows", array)
                .add("_links", createObjectBuilder()
                        .add("self", uriInfo.getRequestUri().toString())).build();
    }

    public JsonObject buildDictionaryShort(Dictionary d, String method, UriInfo uriInfo, Locale locale) {
        JsonObjectBuilder builder = dictionaryObjectBuilder(d.getId(), d.getName(), locale);
        builder.add("_links", selfLinkBuilder(this.linkBuilder.forDictionary(d, method, uriInfo).toString()));
        return builder.build();
    }

    public JsonObject buildDomain(Domain d, URI self, Locale locale) {

        JsonObjectBuilder builder = createObjectBuilder();

        builder.add("id", d.getId());
        if (d.getName() != null) {
            builder.add("name", loc.find(d.getName(), locale));
        }
        if (d.getDescription() != null) {
            builder.add("description", loc.find(d.getDescription(), locale));
        }

        JsonObjectBuilder linkBuilder = createObjectBuilder();
        linkBuilder.add("self", self.toString());

        builder.add("_links", linkBuilder);
        return builder.build();
    }

    public JsonObject buildPartOfSpeeches(List<PartOfSpeech> pos, UriInfo uriInfo, Locale locale) {
        JsonArray array = pos.stream()
                .map(p -> buildPartOfSpeech(p, linkBuilder.forPartOfSpeech(p, uriInfo), locale))
                .collect(JsonCollectors.toJsonArray());
        return Json.createObjectBuilder()
                .add("rows", array)
                .add("_links", createObjectBuilder()
                        .add("self", uriInfo.getRequestUri().toString())).build();
    }

    public JsonObject buildPartOfSpeech(PartOfSpeech p, URI self, Locale locale) {

        JsonObjectBuilder builder = createObjectBuilder();

        builder.add("id", p.getId());
        if (p.getName() != null) {
            builder.add("name", loc.find(p.getName(), locale));
        }

        if (p.getColor() != null) {
            builder.add("color", p.getColor());
        }

        JsonObjectBuilder linkBuilder = createObjectBuilder();
        linkBuilder.add("self", self.toString());

        builder.add("_links", linkBuilder);
        return builder.build();
    }

    public JsonObject buildLexicons(List<Lexicon> lexicons, UriInfo uriInfo) {
        JsonArray array = lexicons.stream()
                .map(l -> buildDictionaryLexicon(l, uriInfo))
                .collect(JsonCollectors.toJsonArray());
        return Json.createObjectBuilder()
                .add("rows", array)
                .add("_links", createObjectBuilder()
                        .add("self", uriInfo.getRequestUri().toString()))
                .add("_actions", createArrayBuilder()
                        .add(createObjectBuilder()
                                .add("name", "add-lexicon")
                                .add("title", "Creates new lexicon")
                                .add("method", HttpMethod.POST)
                                .add("href", this.linkBuilder.forLexicons(uriInfo).toString())
                                .add("type", MediaType.APPLICATION_JSON)
                                .add("headers", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "Authorization")
                                                .add("type", "TEXT")
                                                .add("required", "true")
                                        ))
                                .add("fields", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "name")
                                                .add("type", "TEXT")
                                                .add("required", "true"))
                                        .add(createObjectBuilder()
                                                .add("name", "identifier")
                                                .add("type", "TEXT")
                                                .add("required", "true"))
                                        .add(createObjectBuilder()
                                                .add("name", "language")
                                                .add("type", "TEXT")
                                                .add("required", "true"))
                                        .add(createObjectBuilder()
                                                .add("name", "language_shortcut")
                                                .add("type", "TEXT")
                                                .add("required", "true"))
                                        .add(createObjectBuilder()
                                                .add("name", "version")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "license")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "email")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "citation")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "confidence_score")
                                                .add("type", "TEXT")
                                                .add("required", "false")))))
                .build();
    }

    public JsonObject buildLexicon(Lexicon lex, URI self) {
        JsonObjectBuilder builder = createObjectBuilder();

        builder.add("id", lex.getId());

        if (lex.getName() != null) {
            builder.add("name", lex.getName());
        }
        if (lex.getIdentifier() != null) {
            builder.add("identifier", lex.getIdentifier());
        }
        if (lex.getReferenceUrl() != null) {
            builder.add("reference_url", lex.getReferenceUrl());
        }
        if (lex.getLanguageName() != null) {
            builder.add("language", lex.getLanguageName());
        }
        if (lex.getLanguageShortcut() != null) {
            builder.add("language_shortcut", lex.getLanguageShortcut());
        }
        if (lex.getLexiconVersion() != null) {
            builder.add("version", lex.getLexiconVersion());
        }
        if (lex.getLicense() != null) {
            builder.add("license", lex.getLicense());
        }
        if (lex.getEmail() != null) {
            builder.add("email", lex.getEmail());
        }
        if (lex.getCitation() != null) {
            builder.add("citation", lex.getCitation());
        }
        if (lex.getConfidenceScore() != null) {
            builder.add("confidence_score", lex.getConfidenceScore());
        }

        JsonObjectBuilder linkBuilder = createObjectBuilder();
        linkBuilder.add("self", self.toString());

        builder.add("_links", linkBuilder);
        return builder.build();
    }

    public JsonObject buildDictionaryLexicon(Lexicon lex, UriInfo uriInfo) {
        JsonObjectBuilder builder = createObjectBuilder();

        builder.add("id", lex.getId());

        if (lex.getName() != null) {
            builder.add("name", lex.getName());
        }
        builder.add("_links", selfLinkBuilder(this.linkBuilder.forLexicon(lex, uriInfo).toString()));
        return builder.build();
    }

    public JsonObject buildDictionaryPartOfSpeech(PartOfSpeech pos, UriInfo uriInfo, Locale locale) {
        JsonObjectBuilder builder = dictionaryObjectBuilder(pos.getId(), pos.getName(), locale);
        builder.add("_links", selfLinkBuilder(this.linkBuilder.forPartOfSpeech(pos, uriInfo).toString()));
        return builder.build();
    }

    public JsonObject buildDictionaryDomains(List<Domain> domains, UriInfo uriInfo, Locale locale) {
        JsonArray array = domains.stream()
                .map(d -> buildDictionaryDomain(d, uriInfo, locale))
                .collect(JsonCollectors.toJsonArray());
        return Json.createObjectBuilder()
                .add("rows", array)
                .add("_links", createObjectBuilder()
                        .add("self", uriInfo.getRequestUri().toString())).build();
    }

    public JsonObject buildDictionaryDomain(Domain d, UriInfo uriInfo, Locale locale) {
        JsonObjectBuilder builder = dictionaryObjectBuilder(d.getId(), d.getName(), locale);
        builder.add("_links", selfLinkBuilder(this.linkBuilder.forDomain(d, uriInfo).toString()));
        return builder.build();
    }

    public JsonObject buildRelationTypes(UriInfo uriInfo) {
        JsonObjectBuilder builder = createObjectBuilder();
        JsonObjectBuilder linkBuilder = createObjectBuilder();
        linkBuilder.add("self", uriInfo.getRequestUri().toString());
        linkBuilder.add("synset_relation_types", this.linkBuilder.forRelationTypes(uriInfo).toString() + "?argument=SYNSET_RELATION");
        linkBuilder.add("sense_relation_types", this.linkBuilder.forRelationTypes(uriInfo).toString() + "?argument=SENSE_RELATION");

        builder.add("_links", linkBuilder);
        builder.add("_actions", createArrayBuilder()
                .add(createObjectBuilder()
                        .add("name", "create")
                        .add("title", "Create relation")
                        .add("method", HttpMethod.POST)
                        .add("href", uriInfo.getRequestUri().toString())
                        .add("type", MediaType.APPLICATION_JSON)
                        .add("headers", Json.createArrayBuilder()
                                .add(createObjectBuilder()
                                        .add("name", "Authorization")
                                        .add("type", "TEXT")
                                        .add("required", "true")
                                ))
                        .add("fields", Json.createArrayBuilder()
                                .add(createObjectBuilder()
                                        .add("name", "argument")
                                        .add("type", "ENUM[SYNSET_RELATION, SENSE_RELATION]")
                                        .add("required", "true"))
                                .add(createObjectBuilder()
                                        .add("name", "name")
                                        .add("type", "TEXT")
                                        .add("required", "true"))
                                .add(createObjectBuilder()
                                        .add("name", "short_name")
                                        .add("type", "TEXT")
                                        .add("required", "true"))
                                .add(createObjectBuilder()
                                        .add("name", "description")
                                        .add("type", "TEXT")
                                        .add("required", "false"))
                                .add(createObjectBuilder()
                                        .add("name", "display")
                                        .add("type", "TEXT")
                                        .add("required", "false"))
                                .add(createObjectBuilder()
                                        .add("name", "auto_reverse")
                                        .add("type", "BOOLEAN")
                                        .add("required", "false"))
                                .add(createObjectBuilder()
                                        .add("name", "multilingual")
                                        .add("type", "BOOLEAN")
                                        .add("required", "false"))
                                .add(createObjectBuilder()
                                        .add("name", "parent_relation")
                                        .add("type", "UUID")
                                        .add("required", "false"))
                                .add(createObjectBuilder()
                                        .add("name", "reverse_relation")
                                        .add("type", "UUID")
                                        .add("required", "false"))
                                .add(createObjectBuilder()
                                        .add("name", "color")
                                        .add("type", "HEX")
                                        .add("required", "false"))
                                .add(createObjectBuilder()
                                        .add("name", "direction")
                                        .add("type", "ENUM[TOP, BOTTOM, LEFT, RIGHT, IGNORE]")
                                        .add("required", "true"))
                        )));
        return builder.build();
    }

    public JsonObject buildRelationTypes(Map<RelationType, Set<RelationType>> allByRelationArgument, UriInfo uriInfo, Locale locale) {
        JsonObjectBuilder builder = createObjectBuilder();
        JsonArrayBuilder relations = createArrayBuilder();
        allByRelationArgument.forEach((key, value) -> relations.add(buildDictionaryRelationType(
                key, value, linkBuilder.forRelationType(key, uriInfo), linkBuilder.forRelationTests(key, uriInfo), uriInfo, locale)));
        builder.add("rows", relations);
        builder.add("_links", selfLinkBuilder(uriInfo.getRequestUri().toString()));
        return builder.build();
    }

    public JsonObject buildDictionaryRelationType(RelationType rt, Set<RelationType> child,
                                                  URI self, URI tests,
                                                  UriInfo uriInfo, Locale locale) {
        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("id", rt.getId().toString());
        String parent = "";
        if (rt.getParent() != null) {
            parent = loc.find(rt.getParent().getName(), locale) + ": ";
        }
        builder.add("name", parent + loc.find(rt.getName(), locale));

        if (rt.getShortDisplayText() != null) {
            builder.add("short_name", loc.find(rt.getShortDisplayText(), locale));
        }
        if (rt.getReverse() != null) {
            builder.add("reverse", rt.getReverse().getId().toString());
        }

        if (rt.getDescription() != null) {
            builder.add("description", loc.find(rt.getDescription(), locale));
        }

        builder.add("color", rt.getColor() != null ? rt.getColor() : "#000000");
        if (rt.getNodePosition() != null) {
            builder.add("direction", rt.getNodePosition().getAsString());
        } else {
            builder.add("direction", "IGNORE");
        }
        if (child != null && !child.isEmpty()) {
            JsonArrayBuilder sub = createArrayBuilder();
            child.forEach(srt -> sub.add(buildDictionaryRelationType(srt, null,
                    linkBuilder.forRelationType(srt, uriInfo), linkBuilder.forRelationTests(srt, uriInfo),
                    uriInfo, locale)));
            builder.add("subrelations", sub);
        }

        JsonObjectBuilder linkBuilder = selfLinkBuilder(self.toString());
        linkBuilder.add("tests", tests.toString());
        builder.add("_links", linkBuilder);

        return builder.build();
    }

    public JsonObject buildRelationType(RelationType rt, URI self, URI tests, UriInfo uriInfo, Locale locale) {
        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("id", rt.getId().toString());
        if (rt.getLexicons().size() > 0) {
            JsonArray le = rt.getLexicons()
                    .stream()
                    .map(l -> buildDictionaryLexicon(l, uriInfo))
                    .collect(JsonCollectors.toJsonArray());
            builder.add("allowed_lexicons", le);
        }
        if (rt.getPartsOfSpeech().size() > 0) {
            JsonArray pos = rt.getPartsOfSpeech()
                    .stream()
                    .map(p -> buildDictionaryPartOfSpeech(p, uriInfo, locale))
                    .collect(JsonCollectors.toJsonArray());
            builder.add("allowed_parts_of_speech", pos);
        }
        if (rt.getName() != null) {
            builder.add("name", loc.find(rt.getName(), locale));
        }

        if (rt.getDescription() != null) {
            builder.add("description", loc.find(rt.getDescription(), locale));
        }

        if (rt.getDisplayText() != null) {
            builder.add("display", loc.find(rt.getDisplayText(), locale));
        }

        if (rt.getShortDisplayText() != null) {
            builder.add("short_name", loc.find(rt.getShortDisplayText(), locale));
        }

        if (rt.getAutoReverse() != null) {
            builder.add("auto_reverse", rt.getAutoReverse());
        }

        builder.add("argument", rt.getRelationArgument().name());

        builder.add("color", rt.getColor() != null ? rt.getColor() : "#000000");

        if (rt.getNodePosition() != null) {
            builder.add("direction", rt.getNodePosition().getAsString());
        } else {
            builder.add("direction", "IGNORE");
        }

        if (rt.getGlobalWordnetRelationType() != null) {
            builder.add("global_wordnet_type", rt.getGlobalWordnetRelationType().name());
        } else {
            builder.add("global_wordnet_type", GlobalWordnetRelationType.other.name());
        }

        if (rt.getMultilingual() != null) {
            builder.add("multilingual", rt.getMultilingual());
        }

        if (rt.getParent() != null) {
            builder.add("parent_relation", buildDictionaryRelationType(rt.getParent(), null,
                    linkBuilder.forRelationType(rt.getParent(), uriInfo), linkBuilder.forRelationTests(rt.getParent(), uriInfo), uriInfo, locale));
        }

        if (rt.getReverse() != null) {
            builder.add("reverse_relation", buildDictionaryRelationType(rt.getReverse(), null,
                    linkBuilder.forRelationType(rt.getReverse(), uriInfo), linkBuilder.forRelationTests(rt.getReverse(), uriInfo),
                    uriInfo, locale));
        }

        JsonObjectBuilder linkBuilder = selfLinkBuilder(self.toString());
        linkBuilder.add("tests", tests.toString());
        builder.add("_links", linkBuilder);

        return builder.build();
    }

    public JsonObject buildSynset(Synset synset, SynsetAttributes attributes, URI self, UriInfo uriInfo, Locale locale) {

        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("id", synset.getId().toString());

        if (Objects.nonNull(synset.getLexicon())) {
            builder.add("lexicon", synset.getLexicon().getId());
        }

        if (Objects.nonNull(attributes.getDefinition())) {
            builder.add("definition", attributes.getDefinition());
        }

        if (Objects.nonNull(attributes.getComment())) {
            builder.add("comment", attributes.getComment());
        }

        builder.add("artificial", synset.getAbstract());

        if (Objects.nonNull(attributes.getErrorComment())) {
            builder.add("ili_id", attributes.getDefinition());
        }

        if (Objects.nonNull(attributes.getPrincetonId())) {
            builder.add("princeton_id", attributes.getPrincetonId());
        }

        if (Objects.nonNull(synset.getStatus())) {
            builder.add("status", synset.getStatus().getId());
        }

        if (Objects.nonNull(attributes.getErrorComment())) {
            builder.add("technical_comment", attributes.getErrorComment());
        }

        if (Objects.nonNull(attributes.getOwner())) {
            builder.add("owner", attributes.getOwner().getFullname());
        }

        builder.add("senses", synset.getSenses().stream()
                .map(s -> buildSenseShort(s, uriInfo, locale, false))
                .collect(JsonCollectors.toJsonArray()));

        JsonObjectBuilder linkBuilder = createObjectBuilder();
        linkBuilder.add("self", self.toString());
        linkBuilder.add("examples", this.linkBuilder.forSynsetExamples(synset.getId(), uriInfo).toString());
        linkBuilder.add("relations", this.linkBuilder.forSynsetRelations(synset, uriInfo).toString());
        linkBuilder.add("synset-graph", this.linkBuilder.forSynsetsGraph(synset.getId(), uriInfo).toString());

        builder.add("_links", linkBuilder);
        builder.add("_actions", createArrayBuilder()
                .add(createObjectBuilder()
                        .add("name", "update")
                        .add("title", "Update synset")
                        .add("method", HttpMethod.PUT)
                        .add("href", self.toString())
                        .add("type", MediaType.APPLICATION_JSON)
                        .add("headers", Json.createArrayBuilder()
                                .add(createObjectBuilder()
                                        .add("name", "Authorization")
                                        .add("type", "TEXT")
                                        .add("required", "true")
                                ))
                        .add("fields", Json.createArrayBuilder()
                                .add(createObjectBuilder()
                                        .add("name", "lexicon")
                                        .add("type", "NUMBER")
                                        .add("required", "true"))
                                .add(createObjectBuilder()
                                        .add("name", "definition")
                                        .add("type", "TEXT")
                                        .add("required", "false"))
                                .add(createObjectBuilder()
                                        .add("name", "comment")
                                        .add("type", "TEXT")
                                        .add("required", "false"))
                                .add(createObjectBuilder()
                                        .add("name", "artificial")
                                        .add("type", "BOOLEAN")
                                        .add("required", "false"))
                                .add(createObjectBuilder()
                                        .add("name", "status")
                                        .add("type", "NUMBER")
                                        .add("required", "false"))
                                .add(createObjectBuilder()
                                        .add("name", "technical_comment")
                                        .add("type", "TEXT")
                                        .add("required", "false")))
                )
                .add(createObjectBuilder()
                        .add("name", "delete")
                        .add("title", "Delete synset")
                        .add("method", HttpMethod.DELETE)
                        .add("href", self.toString())
                        .add("type", MediaType.APPLICATION_JSON)
                        .add("headers", Json.createArrayBuilder()
                                .add(createObjectBuilder()
                                        .add("name", "Authorization")
                                        .add("type", "TEXT")
                                        .add("required", "true")
                                ))
                ));

        return builder.build();
    }

    public JsonObject buildRelationTests(List<RelationTest> tests, UriInfo info, Locale locale) {
        JsonObjectBuilder builder = createObjectBuilder();

        builder.add("rows", tests.
                stream()
                .map(t -> buildRelationTest(t, linkBuilder.forRelationTest(t, info), locale))
                .collect(JsonCollectors.toJsonArray())
        );

        JsonObjectBuilder lBuilder = createObjectBuilder();
        lBuilder.add("self", info.getRequestUri().toString());

        builder.add("_links", lBuilder);
        return builder.build();
    }

    public JsonObject buildRelationTest(RelationTest rt, URI self, Locale locale) {
        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("id", rt.getId());

        if (rt.getTest() != null) {
            builder.add("test", rt.getTest());
        }

        if (rt.getSenseApartOfSpeech() != null) {
            builder.add("sense_a_pos", rt.getSenseApartOfSpeech().getId());
        }
        if (rt.getSenseBpartOfSpeech() != null) {
            builder.add("sense_b_pos", rt.getSenseBpartOfSpeech().getId());
        }
        if (rt.getSenseBpartOfSpeech() != null) {
            builder.add("position", rt.getPosition());
        }

        JsonObjectBuilder linkBuilder = createObjectBuilder();
        linkBuilder.add("self", self.toString());

        builder.add("_links", linkBuilder);
        return builder.build();
    }

    private Integer findNextPage(long count, Integer start, int limit) {
        if (count < limit) return start;
        int next = start + limit;
        if (next <= count) {
            return next;
        }
        return start;
    }

    private Integer findPrevPage(long count, Integer start, int limit) {
        if (count < limit) return start;
        if (start - limit >= 0) {
            return start - limit;
        }
        return start;
    }

    public JsonObject buildPaginatedSenseSearch(List<Sense> senses, long count, SearchFilter filter, UriInfo uriInfo, Locale locale) {
        JsonObjectBuilder linkBuilder = createObjectBuilder();

        String uri = uriInfo.getRequestUri().toString().replaceAll("&limit=[^&]+", "");
        uri = uri.replaceAll("&start=[^&]+", "");

        String next = uri + "&start=" + findNextPage(count, filter.getPaginationData().getStart(), filter.getPaginationData().getLimit());
        String prev = uri + "&start=" + findPrevPage(count, filter.getPaginationData().getStart(), filter.getPaginationData().getLimit());
        String limit = "&limit=" + filter.getPaginationData().getLimit();

        linkBuilder.add("self", uriInfo.getRequestUri().toString());
        linkBuilder.add("prev", prev + limit);
        linkBuilder.add("next", next + limit);

        return Json.createObjectBuilder()
                .add("start", filter.getPaginationData().getStart())
                .add("limit", filter.getPaginationData().getLimit())
                .add("size", count)
                .add("_links", linkBuilder)
                .add("rows", senses
                        .stream().map(s -> buildSenseShort(s, uriInfo, locale, true))
                        .collect(JsonCollectors.toJsonArray())).build();
    }

    public JsonObject buildPaginatedSynsetSearch(List<Synset> synsets, long count, SearchFilter filter, UriInfo uriInfo, Locale locale) {
        JsonObjectBuilder linkBuilder = createObjectBuilder();

        String uri = uriInfo.getRequestUri().toString().replaceAll("&limit=[^&]+", "");
        uri = uri.replaceAll("&start=[^&]+", "");

        String next = uri + "&start=" + findNextPage(count, filter.getPaginationData().getStart(), filter.getPaginationData().getLimit());
        String prev = uri + "&start=" + findPrevPage(count, filter.getPaginationData().getStart(), filter.getPaginationData().getLimit());
        String limit = "&limit=" + filter.getPaginationData().getLimit();

        linkBuilder.add("self", uriInfo.getRequestUri().toString());
        linkBuilder.add("prev", prev + limit);
        linkBuilder.add("next", next + limit);

        return Json.createObjectBuilder()
                .add("start", filter.getPaginationData().getStart())
                .add("limit", filter.getPaginationData().getLimit())
                .add("size", count)
                .add("_links", linkBuilder)
                .add("rows", synsets
                        .stream().map(s -> buildSynsetShort(s, uriInfo, locale, false))
                        .collect(JsonCollectors.toJsonArray())).build();
    }

    public JsonObject buildSenseRelations(UriInfo uriInfo) {
        return Json.createObjectBuilder()
                .add("_links", createObjectBuilder()
                        .add("self", uriInfo.getRequestUri().toString()))
                .add("_actions", createArrayBuilder()
                        .add(createObjectBuilder()
                                .add("name", "add-sense-relation")
                                .add("title", "Creates relation of requested type between source sense and target sense")
                                .add("method", HttpMethod.POST)
                                .add("href", uriInfo.getRequestUri().toString())
                                .add("type", MediaType.APPLICATION_JSON)
                                .add("headers", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "Authorization")
                                                .add("type", "TEXT")
                                                .add("required", "true")
                                        ))
                                .add("fields", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "source")
                                                .add("type", "UUID")
                                                .add("required", "true"))
                                        .add(createObjectBuilder()
                                                .add("name", "target")
                                                .add("type", "UUID")
                                                .add("required", "true"))
                                        .add(createObjectBuilder()
                                                .add("name", "relation_type")
                                                .add("type", "UUID")
                                                .add("required", "true"))
                                )))
                .build();
    }

    public JsonObject buildSenseRelations(Sense sense, UriInfo uriInfo, Locale locale) {
        return Json.createObjectBuilder()
                .add("root", buildLabel(sense, false, locale))
                .add("outgoing", buildSenseRelations(sense.getIncomingRelations(), false, locale, uriInfo))
                .add("incoming", buildSenseRelations(sense.getOutgoingRelations(), true, locale, uriInfo))
                .add("_links", createObjectBuilder()
                        .add("self", linkBuilder.forSenseRelations(sense, uriInfo).toString()))
                .build();
    }

    public JsonObject buildSynsetRelations(UriInfo uriInfo) {
        return createObjectBuilder()
                .add("_links", createObjectBuilder()
                        .add("self", uriInfo.getRequestUri().toString()))
                .add("_actions", createArrayBuilder()
                        .add(createObjectBuilder()
                                .add("name", "add-synset-relation")
                                .add("title", "Creates relation of requested type between source synset and target synset")
                                .add("method", HttpMethod.POST)
                                .add("href", uriInfo.getRequestUri().toString())
                                .add("type", MediaType.APPLICATION_JSON)
                                .add("headers", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "Authorization")
                                                .add("type", "TEXT")
                                                .add("required", "true")
                                        ))
                                .add("fields", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "source")
                                                .add("type", "NUMBER")
                                                .add("required", "true"))
                                        .add(createObjectBuilder()
                                                .add("name", "target")
                                                .add("type", "NUMBER")
                                                .add("required", "true"))
                                        .add(createObjectBuilder()
                                                .add("name", "relation_type")
                                                .add("type", "NUMBER")
                                                .add("required", "true"))
                                ))
                        .add(createObjectBuilder()
                                .add("name", "search-synset-relations")
                                .add("title", "Search synset relations ")
                                .add("method", HttpMethod.GET)
                                .add("href", linkBuilder.forSearchSynsetRelations(uriInfo).toString())
                                .add("type", MediaType.APPLICATION_JSON)
                                .add("headers", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "Authorization")
                                                .add("type", "TEXT")
                                                .add("required", "true")
                                        ))
                                .add("query_params", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "source")
                                                .add("type", "UUID")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "target")
                                                .add("type", "UUID")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "relation_type")
                                                .add("type", "UUID")
                                                .add("required", "false"))
                                ))
                )
                .build();
    }

    public JsonObject buildSynsetRelations(Synset synset, Map<String, List<Object[]>> relations, UriInfo uriInfo, Locale locale) {
        return Json.createObjectBuilder()
                .add("root", buildLabel(synset, locale))
                .add("incoming", buildSynsetRelations(synset.getId(), relations.get("incoming"), locale, uriInfo, true))
                .add("outgoing", buildSynsetRelations(synset.getId(), relations.get("outgoing"), locale, uriInfo, false))
                .add("_links", createObjectBuilder()
                        .add("self", linkBuilder.forSynsetRelations(synset, uriInfo).toString()))
                .build();
    }

    public JsonObject buildSenseRelation(Sense se, SenseRelation sr, Locale locale, UriInfo uriInfo) {
        JsonObjectBuilder entityBuilder = Json.createObjectBuilder()
                .add("label", buildLabel(se, false, locale))
                .add("_links", createObjectBuilder()
                        .add("self", linkBuilder.forSenseRelation(sr, uriInfo).toString()));
        return entityBuilder.build();
    }

    public JsonObject buildSenseRelations(List<SenseRelation> sr, Locale locale, UriInfo uriInfo) {
        return createObjectBuilder()
                .add("rows", sr.stream()
                        .map(r -> buildSenseRelation(r, locale, uriInfo))
                        .collect(JsonCollectors.toJsonArray()))
                .add("_links", createObjectBuilder()
                        .add("self", uriInfo.getRequestUri().toString())).build();
    }

    public JsonObject buildSenseRelation(SenseRelation sr, Locale locale, UriInfo uriInfo) {
        return createObjectBuilder()
                .add("relation_type", createObjectBuilder()
                        .add("id", sr.getRelationType().getId().toString())
                        .add("label", loc.find(sr.getRelationType().getName(), locale))
                        .add("_links", createObjectBuilder()
                                .add("self", linkBuilder.forRelationType(sr.getRelationType(), uriInfo).toString()))
                )
                .add("source", createObjectBuilder()
                        .add("id", sr.getParent().getId().toString())
                        .add("label", buildLabel(sr.getParent(), false, locale))
                        .add("_links", createObjectBuilder()
                                .add("self", linkBuilder.forSense(sr.getParent(), uriInfo).toString()))
                )
                .add("target", createObjectBuilder()
                        .add("id", sr.getChild().getId().toString())
                        .add("label", buildLabel(sr.getChild(), false, locale))
                        .add("_links", createObjectBuilder()
                                .add("self", linkBuilder.forSense(sr.getChild(), uriInfo).toString()))
                )
                .add("_links", createObjectBuilder()
                        .add("self", linkBuilder.forSenseRelation(sr, uriInfo).toString())
                        .add("reverse_relation", ""))
                .add("_actions", createArrayBuilder()
                        .add(createObjectBuilder()
                                .add("name", "delete")
                                .add("title", "Delete relation")
                                .add("method", HttpMethod.DELETE)
                                .add("href", linkBuilder.forSenseRelation(sr, uriInfo).toString())
                                .add("type", MediaType.APPLICATION_JSON)
                                .add("headers", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "Authorization")
                                                .add("type", "TEXT")
                                                .add("required", "true")
                                        ))
                                .add("query_param", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "with_reverse")
                                                .add("type", "BOOLEAN")
                                                .add("required", "false")
                                        ))

                        )).build();
    }


    public JsonObject buildSynsetRelation(SynsetRelation sr,
                                          Optional<Synset> parentSynsetHead,
                                          Optional<Synset> childSynsetHead,
                                          Locale locale, UriInfo uriInfo) {
        return createObjectBuilder()
                .add("relation_type", createObjectBuilder()
                        .add("id", sr.getRelationType().getId().toString())
                        .add("label", loc.find(sr.getRelationType().getName(), locale))
                        .add("_links", createObjectBuilder()
                                .add("self", linkBuilder.forRelationType(sr.getRelationType(), uriInfo).toString()))
                )
                .add("source", createObjectBuilder()
                        .add("id", sr.getParent().getId().toString())
                        .add("label", buildLabel(parentSynsetHead.get(), locale))
                        .add("_links", createObjectBuilder()
                                .add("self", linkBuilder.forSynset(sr.getParent(), uriInfo).toString()))
                )
                .add("target", createObjectBuilder()
                        .add("id", sr.getChild().getId().toString())
                        .add("label", buildLabel(childSynsetHead.get(), locale))
                        .add("_links", createObjectBuilder()
                                .add("self", linkBuilder.forSynset(sr.getChild(), uriInfo).toString()))
                )
                .add("_links", createObjectBuilder()
                        .add("self", linkBuilder.forSynsetRelation(sr, uriInfo).toString()))
                .add("_actions", createArrayBuilder()
                        .add(createObjectBuilder()
                                .add("name", "delete")
                                .add("title", "Delete relation")
                                .add("method", HttpMethod.DELETE)
                                .add("href", linkBuilder.forSynsetRelation(sr, uriInfo).toString())
                                .add("type", MediaType.APPLICATION_JSON)
                                .add("headers", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "Authorization")
                                                .add("type", "TEXT")
                                                .add("required", "true")
                                        ))
                        )).build();
    }

    public JsonObject buildSynsetRelation(UUID id, Object[] row, Locale locale, UriInfo uriInfo, boolean incomming) {
        URI self;
        if (incomming) {
            self = linkBuilder.forSynsetRelation(UUID.fromString((String) row[2]), id, UUID.fromString((String) row[0]), uriInfo);
        } else {
            self = linkBuilder.forSynsetRelation(id, UUID.fromString((String) row[2]), UUID.fromString((String) row[0]), uriInfo);
        }
        return createObjectBuilder()
                .add("label", buildLabel((String) row[3], ((BigInteger) row[4]).longValue(), locale))
                .add("_links", createObjectBuilder()
                        .add("self", self.toString()))
                .build();
    }


    public JsonObject buildSense(Sense sense, SenseAttributes attributes, URI self, UriInfo uriInfo, Locale locale) {
        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("id", sense.getId().toString());

        if (sense.getLexicon() != null) {
            builder.add("lexicon", sense.getLexicon().getId());
        }

        if (sense.getSynset() != null) {
            builder.add("synset", String.valueOf(sense.getSynset().getId()));
        }

        if (sense.getWord() != null) {
            builder.add("lemma", sense.getWord().getWord());
        }

        builder.add("variant", sense.getVariant());

        if (sense.getPartOfSpeech() != null) {
            builder.add("part_of_speech", sense.getPartOfSpeech().getId());
        }

        if (Objects.nonNull(sense.getDomain())) {
            builder.add("domain", sense.getDomain().getId());
        }

        if (attributes != null) {

            if (Objects.nonNull(attributes.getRegister())) {
                builder.add("register", attributes.getRegister().getId());
            }

            if (Objects.nonNull(attributes.getDefinition())) {
                builder.add("definition", attributes.getDefinition());
            }

            if (Objects.nonNull(attributes.getComment())) {
                builder.add("comment", attributes.getComment());
            }

            if (Objects.nonNull(sense.getStatus())) {
                builder.add("status", sense.getStatus().getId());
            }

            if (Objects.nonNull(attributes.getErrorComment())) {
                builder.add("technical_comment", attributes.getErrorComment());
            }

            if (Objects.nonNull(attributes.getOwner())) {
                builder.add("owner", attributes.getOwner().getFullname());
            }

            if (Objects.nonNull(attributes.getLink())) {
                builder.add("link", attributes.getLink());
            }
        }

        JsonObjectBuilder linkBuilder = createObjectBuilder()
                .add("self", self.toString());
        if (sense.getSynset() != null) {
            linkBuilder.add("synset", this.linkBuilder.forSynset(sense.getSynset(), uriInfo).toString());
        }
        linkBuilder.add("sense-graph", this.linkBuilder.forSenseGraph(sense.getId(), uriInfo).toString())
                .add("examples", this.linkBuilder.forSenseExamples(sense.getId(), uriInfo).toString())
                .add("relations", this.linkBuilder.forSenseRelations(sense, uriInfo).toString());

        if(sense.getLexicon().getId() == 4){
            linkBuilder.add("yiddish", this.linkBuilder.forYiddishExtension(sense, uriInfo).toString());
        }

        builder.add("_links", linkBuilder);

        builder.add("_actions", createArrayBuilder()
                .add(createObjectBuilder()
                        .add("name", "update")
                        .add("title", "Update sense")
                        .add("method", HttpMethod.PUT)
                        .add("href", this.linkBuilder.forSense(sense, uriInfo).toString())
                        .add("type", MediaType.APPLICATION_JSON)
                        .add("headers", Json.createArrayBuilder()
                                .add(createObjectBuilder()
                                        .add("name", "Authorization")
                                        .add("type", "TEXT")
                                        .add("required", "true")
                                ))
                        .add("fields", Json.createArrayBuilder()
                                .add(createObjectBuilder()
                                        .add("name", "lemma")
                                        .add("type", "TEXT")
                                        .add("required", "true"))
                                .add(createObjectBuilder()
                                        .add("name", "lexicon")
                                        .add("type", "NUMBER")
                                        .add("required", "true"))
                                .add(createObjectBuilder()
                                        .add("name", "part_of_speech")
                                        .add("type", "NUMBER")
                                        .add("required", "true"))
                                .add(createObjectBuilder()
                                        .add("name", "domain")
                                        .add("type", "NUMBER")
                                        .add("required", "true"))
                                .add(createObjectBuilder()
                                        .add("name", "register")
                                        .add("type", "NUMBER")
                                        .add("required", "false"))
                                .add(createObjectBuilder()
                                        .add("name", "definition")
                                        .add("type", "TEXT")
                                        .add("required", "false"))
                                .add(createObjectBuilder()
                                        .add("name", "comment")
                                        .add("type", "TEXT")
                                        .add("required", "false"))
                                .add(createObjectBuilder()
                                        .add("name", "status")
                                        .add("type", "NUMBER")
                                        .add("required", "false"))
                                .add(createObjectBuilder()
                                        .add("name", "technical_comment")
                                        .add("type", "TEXT")
                                        .add("required", "false")))
                )
                .add(createObjectBuilder()
                        .add("name", "move-up")
                        .add("title", "Move sense position up in synset")
                        .add("method", HttpMethod.PUT)
                        .add("href", this.linkBuilder.forSenseMoveUp(sense.getId(), uriInfo).toString())
                        .add("type", MediaType.APPLICATION_JSON)
                        .add("headers", Json.createArrayBuilder()
                                .add(createObjectBuilder()
                                        .add("name", "Authorization")
                                        .add("type", "TEXT")
                                        .add("required", "true")
                                )))
                .add(createObjectBuilder()
                        .add("name", "move-down")
                        .add("title", "Move sense position down in synset")
                        .add("method", HttpMethod.PUT)
                        .add("href", this.linkBuilder.forSenseMoveDown(sense.getId(), uriInfo).toString())
                        .add("type", MediaType.APPLICATION_JSON)
                        .add("headers", Json.createArrayBuilder()
                                .add(createObjectBuilder()
                                        .add("name", "Authorization")
                                        .add("type", "TEXT")
                                        .add("required", "true")
                                )))
                .add(createObjectBuilder()
                        .add("name", "detach-from-synset")
                        .add("title", "Detach sense from synset")
                        .add("method", HttpMethod.PUT)
                        .add("href", this.linkBuilder.forSenseDetachSynset(sense.getId(), uriInfo).toString())
                        .add("type", MediaType.APPLICATION_JSON)
                        .add("headers", Json.createArrayBuilder()
                                .add(createObjectBuilder()
                                        .add("name", "Authorization")
                                        .add("type", "TEXT")
                                        .add("required", "true")
                                )))
                .add(createObjectBuilder()
                        .add("name", "delete")
                        .add("title", "Delete sense")
                        .add("method", HttpMethod.DELETE)
                        .add("href", self.toString())
                        .add("type", MediaType.APPLICATION_JSON)
                        .add("headers", Json.createArrayBuilder()
                                .add(createObjectBuilder()
                                        .add("name", "Authorization")
                                        .add("type", "TEXT")
                                        .add("required", "true")
                                )))
        );

        return builder.build();
    }


    public JsonObject buildSenseShort(Sense sense, UriInfo uriInfo, Locale locale, boolean searchMode) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        JsonObjectBuilder linkBuilder = createObjectBuilder();
        linkBuilder.add("self", this.linkBuilder.forSense(sense, uriInfo).toString());

        if (searchMode && sense.getSynset() != null) {
            linkBuilder.add("synset-graph", this.linkBuilder.forSynsetsGraph(sense.getSynset().getId(), uriInfo).toString());
            linkBuilder.add("sense-graph", this.linkBuilder.forSenseGraph(sense.getId(), uriInfo).toString());
        }

        builder.add("id", sense.getId().toString());

        if (searchMode && sense.getPartOfSpeech() != null) {
            builder.add("part_of_speech", sense.getPartOfSpeech().getId());
        }

        if (searchMode) {
            if (sense.getWord() != null) {
                builder.add("label", buildLabel(sense, true, locale));
                if(Hibernate.isInitialized(sense.getYiddish())){
                    builder.add("variant", sense.getVariant());
                    JsonArray variants = sense.getYiddish()
                            .stream()
                            .map(this::buildYiddishShortExtension)
                            .collect(JsonCollectors.toJsonArray());
                    builder.add("variants", variants);
                }
            }
        } else {
            if (sense.getWord() != null) {
                builder.add("label", buildLabel(sense, false, locale));
            }
        }

        builder.add("lexicon", sense.getLexicon().getId());

        if (sense.getWord() != null) {
            builder.add("label", buildLabel(sense, false, locale));
        }

        builder.add("_links", linkBuilder);

        return builder.build();
    }

    public JsonObject buildYiddishShortExtension(YiddishSenseExtension yse){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("id", yse.getId());
        builder.add("variant_type", yse.getVariant().name());
        builder.add("latin", yse.getLatinSpelling()!=null ? yse.getLatinSpelling():"");
        builder.add("yiddish", yse.getYiddishSpelling()!=null ? yse.getYiddishSpelling():"");
        builder.add("yivo", yse.getYivoSpelling()!=null ? yse.getYivoSpelling():"");
        return builder.build();
    }

    public JsonObject buildSynsetShort(Synset synset, UriInfo uriInfo, Locale locale, boolean withRelations) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        if (synset.getSenses().size() > 0) {
            builder.add("label", buildSenseString(synset, locale));
        } else {
            builder.add("label", "");
        }
        JsonObjectBuilder linkBuilder = createObjectBuilder();
        linkBuilder.add("self", this.linkBuilder.forSynset(synset, uriInfo).toString());
        linkBuilder.add("synset-graph", this.linkBuilder.forSynsetsGraph(synset.getId(), uriInfo).toString());
        builder.add("_links", linkBuilder);

        return builder.build();
    }


    private String buildSenseString(Synset synset, Locale locale) {
        StringBuilder sb = new StringBuilder();
        synset.getSenses().stream().findFirst().ifPresent(s -> {
            sb.append(s.getWord().getWord());
            sb.append(" ").append(s.getVariant());
            sb.append(" (").append(loc.find(s.getDomain().getName(), locale)).append(")");
        });
        if (synset.getSenses().size() > 1) {
            sb.append("|");
            synset.getSenses().stream().skip(1).forEach(s -> {
                sb.append(" ").append(s.getWord().getWord());
                sb.append(" ").append(s.getVariant());
                sb.append(" (").append(loc.find(s.getDomain().getName(), locale)).append("),");
            });
        }
        if (sb.toString().endsWith(",")) {
            return sb.substring(0, sb.length() - 1);
        }
        return sb.toString();
    }


    private JsonArray buildSenseRelations(Set<SenseRelation> list, boolean parent, Locale locale, UriInfo uriInfo) {

        JsonArrayBuilder builder = createArrayBuilder();

        Map<RelationType, List<SenseRelation>> map = list.stream()
                .collect(Collectors.groupingBy(SenseRelation::getRelationType));

        map.forEach((k, v) -> {

            JsonArray a = v.stream()
                    .map(r -> {
                        Sense s = parent ? r.getParent() : r.getChild();
                        return buildSenseRelation(s, r, locale, uriInfo);
                    })
                    .collect(JsonCollectors.toJsonArray());

            JsonObject rel = Json.createObjectBuilder()
                    .add("relation_type_name", loc.find(k.getName(), locale))
                    .add("rows", a)
                    .build();

            builder.add(rel);
        });
        return builder.build();
    }

    private JsonArray buildSynsetRelations(UUID id, List<Object[]> list, Locale locale, UriInfo uriInfo, boolean incomming) {

        JsonArrayBuilder builder = createArrayBuilder();

        Map<String, List<Object[]>> map = list.stream()
                .collect(Collectors.groupingBy(o -> loc.find(((BigInteger) o[1]).longValue(), locale)));

        map.forEach((k, v) -> {

            JsonArray a = v.stream()
                    .map(r -> buildSynsetRelation(id, r, locale, uriInfo, incomming))
                    .collect(JsonCollectors.toJsonArray());

            JsonObject rel = Json.createObjectBuilder()
                    .add("relation_type_name", k)
                    .add("rows", a)
                    .build();

            builder.add(rel);
        });
        return builder.build();
    }


    public String buildLabel(Sense sense, Boolean withLexicon, Locale locale) {
        StringBuilder sb = new StringBuilder();
        sb.append(sense.getWord())
                .append(" ")
                .append(sense.getVariant());
        if (sense.getDomain() != null) {
            sb.append(" (").append(loc.find(sense.getDomain().getName(), locale)).append(")");
        }
        if (withLexicon && sense.getLexicon() != null) {
            sb.append(" ").append(sense.getLexicon().getIdentifier());
        }
        return sb.toString();
    }

    public String buildLabel(Synset synset, Locale locale) {
        return synset.getSenses().stream().findFirst()
                .map(s -> (synset.getAbstract() ? "S " : "") + s.getWord() + " " + s.getVariant() + " (" + loc.find(s.getDomain().getName(), locale) + ")")
                .orElse("");

    }

    public String buildLabel(String lemma, Long domainNameId, Locale locale) {
        return lemma + " (" + loc.find(domainNameId, locale) + ")";

    }

    public JsonObject buildSenseExamples(UUID id, Set<SenseExample> examples, UriInfo uriInfo) {
        JsonArrayBuilder exampleArray = createArrayBuilder();
        examples.forEach(e -> exampleArray.add(exampleBuilder(uriInfo.getRequestUri().toString() + "/" + e.getId(), e.getExample(), e.getType())));
        return createObjectBuilder()
                .add("rows", exampleArray)
                .add("_links", createObjectBuilder()
                        .add("self", linkBuilder.forSenseExamples(id, uriInfo).toString()))
                .add("_actions", createArrayBuilder()
                        .add(createObjectBuilder()
                                .add("name", "add-example")
                                .add("title", "Creates new example")
                                .add("method", HttpMethod.POST)
                                .add("href", this.linkBuilder.forSenseExamples(id, uriInfo).toString())
                                .add("type", MediaType.APPLICATION_JSON)
                                .add("headers", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "Authorization")
                                                .add("type", "TEXT")
                                                .add("required", "true")
                                        ))
                                .add("fields", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "type")
                                                .add("type", "TEXT")
                                                .add("required", "true"))
                                        .add(createObjectBuilder()
                                                .add("name", "example")
                                                .add("type", "TEXT")
                                                .add("required", "true"))
                                ))
                ).build();

    }

    public JsonObject buildSenseExample(SenseExample e, URI self) {
        JsonObjectBuilder builder = exampleBuilder(self.toString() + "/" + e.getId(), e.getExample(), e.getType());
        builder.add("_links", selfLinkBuilder(self.toString()));
        builder.add("_actions", createArrayBuilder()
                .add(createObjectBuilder()
                        .add("name", "update")
                        .add("title", "Update example")
                        .add("method", HttpMethod.PUT)
                        .add("href", self.toString())
                        .add("type", MediaType.APPLICATION_JSON)
                        .add("headers", Json.createArrayBuilder()
                                .add(createObjectBuilder()
                                        .add("name", "Authorization")
                                        .add("type", "TEXT")
                                        .add("required", "true")
                                ))
                        .add("fields", Json.createArrayBuilder()
                                .add(createObjectBuilder()
                                        .add("name", "type")
                                        .add("type", "TEXT")
                                        .add("required", "true"))
                                .add(createObjectBuilder()
                                        .add("name", "example")
                                        .add("type", "TEXT")
                                        .add("required", "true"))
                        ))
                .add(createObjectBuilder()
                        .add("name", "delete")
                        .add("title", "Delete example")
                        .add("method", HttpMethod.DELETE)
                        .add("href", self.toString())
                        .add("type", MediaType.APPLICATION_JSON)
                        .add("headers", Json.createArrayBuilder()
                                .add(createObjectBuilder()
                                        .add("name", "Authorization")
                                        .add("type", "TEXT")
                                        .add("required", "true")
                                ))));
        return builder.build();
    }

    public JsonObjectBuilder exampleBuilder(String uri, String text, String type) {
        JsonObjectBuilder builder = createObjectBuilder();
        if (text != null)
            builder.add("example", text);
        if (type != null)
            builder.add("type", type);
        builder.add("_links", createObjectBuilder()
                .add("self", uri));
        return builder;
    }

    public JsonObject buildSynsetExamples(UUID id, Set<SynsetExample> ex, UriInfo uriInfo) {
        JsonArray array = ex.stream()
                .map(se -> exampleBuilder(linkBuilder.forSynsetExample(id, se.getId(), uriInfo).toString(), se.getExample(), se.getType()).build())
                .collect(JsonCollectors.toJsonArray());
        return createObjectBuilder()
                .add("rows", array)
                .add("_links", createObjectBuilder()
                        .add("self", uriInfo.getRequestUri().toString()))
                .add("_actions", createArrayBuilder()
                        .add(createObjectBuilder()
                                .add("name", "add-example")
                                .add("title", "Creates new example")
                                .add("method", HttpMethod.POST)
                                .add("href", this.linkBuilder.forSynsetExamples(id, uriInfo).toString())
                                .add("type", MediaType.APPLICATION_JSON)
                                .add("headers", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "Authorization")
                                                .add("type", "TEXT")
                                                .add("required", "true")
                                        ))
                                .add("fields", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "example")
                                                .add("type", "TEXT")
                                                .add("required", "true"))
                                        .add(createObjectBuilder()
                                                .add("name", "type")
                                                .add("type", "TEXT")
                                                .add("required", "true"))
                                )))
                .build();
    }

    public JsonObject buildSynsetExample(UUID id, SynsetExample se, UriInfo uriInfo) {
        JsonObjectBuilder builder = exampleBuilder(uriInfo.getRequestUri().toString(), se.getExample(), se.getType());
        return builder.add("_links", selfLinkBuilder(uriInfo.getRequestUri().toString()))
                .add("_actions", createArrayBuilder()
                        .add(createObjectBuilder()
                                .add("name", "update")
                                .add("title", "Update example")
                                .add("method", HttpMethod.PUT)
                                .add("href", this.linkBuilder.forSynsetExample(id, se.getId(), uriInfo).toString())
                                .add("type", MediaType.APPLICATION_JSON)
                                .add("headers", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "Authorization")
                                                .add("type", "TEXT")
                                                .add("required", "true")
                                        ))
                                .add("fields", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "example")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                        .add(createObjectBuilder()
                                                .add("name", "type")
                                                .add("type", "TEXT")
                                                .add("required", "false"))
                                ))
                        .add(createObjectBuilder()
                                .add("name", "delete")
                                .add("title", "Delete example")
                                .add("method", HttpMethod.DELETE)
                                .add("href", this.linkBuilder.forSynsetExample(id, se.getId(), uriInfo).toString())
                                .add("type", MediaType.APPLICATION_JSON)
                                .add("headers", Json.createArrayBuilder()
                                        .add(createObjectBuilder()
                                                .add("name", "Authorization")
                                                .add("type", "TEXT")
                                                .add("required", "true")
                                        ))))
                .build();
    }

    public JsonObject buildYiddishVariants(Set<YiddishSenseExtension> senseYiddish, UriInfo uriInfo, Locale locale) {
        JsonArray array = senseYiddish.stream()
                .map(e -> buildYiddishVariant(e, uriInfo, locale))
                .collect(JsonCollectors.toJsonArray());
        return createObjectBuilder()
                .add("rows", array)
                .add("_links", selfLinkBuilder(uriInfo.getRequestUri().toString()))
                .build();
    }

    public JsonObject buildYiddishVariant(YiddishSenseExtension e, UriInfo uriInfo, Locale locale) {

        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("id", e.getId());
        if (e.getVariant() != null)
            builder.add("variant_type", e.getVariant().name());

        if (e.getLatinSpelling() != null)
            builder.add("latin_spelling", e.getLatinSpelling());

        if (e.getYiddishSpelling() != null)
            builder.add("yiddish_spelling", e.getYiddishSpelling());

        if (e.getYivoSpelling() != null)
            builder.add("yivo_spelling", e.getYivoSpelling());

        if (e.getMeaning() != null)
            builder.add("meaning", e.getMeaning());

        if (e.getEtymologicalRoot() != null)
            builder.add("etymological_root", e.getEtymologicalRoot());

        if (e.getComment() != null)
            builder.add("comment", e.getComment());

        if (e.getContext() != null)
            builder.add("context", e.getContext());

        if (e.getEtymology() != null)
            builder.add("etymology", e.getEtymology());

        if (e.getAge() != null) {
            builder.add("age", Json.createObjectBuilder()
                    .add("id", e.getAge().getId())
                    .add("name", loc.find(e.getAge().getName(), locale)));
        }

        if (e.getGrammaticalGender() != null){
            builder.add("grammatical_gender", Json.createObjectBuilder()
                    .add("id", e.getGrammaticalGender().getId())
                    .add("name", loc.find(e.getGrammaticalGender().getName(), locale)));
        }

        if(e.getLexicalCharacteristic() != null) {
            builder.add("lexical_characteristic",
                    Json.createObjectBuilder()
                            .add("id", e.getLexicalCharacteristic().getId())
                            .add("name", loc.find(e.getLexicalCharacteristic().getName(), locale)));
        }

        if(e.getStatus() != null) {
            builder.add("status",
                    Json.createObjectBuilder()
                            .add("id", e.getStatus().getId())
                            .add("name", loc.find(e.getStatus().getName(), locale)));
        }

        if(e.getStyle() != null) {
            builder.add("style",
                    Json.createObjectBuilder()
                            .add("id", e.getStyle().getId())
                            .add("name", loc.find(e.getStyle().getName(), locale)));
        }

        JsonArray inflection = e.getInflection()
                .stream()
                .map(i -> buildInflection(i, locale))
                .collect(JsonCollectors.toJsonArray());

        JsonArray particles = e.getParticles()
                .stream()
                .map(i -> buildParticle(i, locale))
                .collect(JsonCollectors.toJsonArray());

        JsonArray domains = e.getYiddishDomains()
                .stream()
                .map(i -> buildYiddishDomain(i, locale))
                .collect(JsonCollectors.toJsonArray());

        JsonArray trans = e.getTranscriptions()
                .stream()
                .map(i -> buildTranscription(i, locale))
                .collect(JsonCollectors.toJsonArray());

        JsonArray src = e.getSource()
                .stream()
                .map(i -> buildSource(i, locale))
                .collect(JsonCollectors.toJsonArray());

        builder.add("inflections", inflection);
        builder.add("particles", particles);
        builder.add("semantic_fields", domains);
        builder.add("transcriptions", trans);
        builder.add("sources", src);

        JsonObjectBuilder lBuilder = createObjectBuilder();
        lBuilder.add("self", linkBuilder.forYiddishVariant(e, uriInfo).toString());
        builder.add("_links", lBuilder);

        return builder.build();
    }

    private JsonObject buildYiddishDomain(YiddishDomain i, Locale locale) {
        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("id", i.getId());
        builder.add("domain", Json.createObjectBuilder()
                .add("id", i.getDomain().getId())
                .add("name",loc.find(i.getDomain().getName(),locale)));
        if(i.getModifier() !=null)
            builder.add("modifier", Json.createObjectBuilder()
                    .add("id", i.getModifier().getId())
                    .add("name", loc.find(i.getModifier().getName(),locale))
            );

        return  builder.build();
    }

    private JsonObject buildTranscription(Transcription i, Locale locale) {
        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("id", i.getId());
        builder.add("transcription", Json.createObjectBuilder()
                .add("id", i.getTranscriptionDictionary().getId())
                .add("name", loc.find(i.getTranscriptionDictionary().getName(),locale)));
        if(i.getPhonography() !=null)
            builder.add("phonography", i.getPhonography());
        return  builder.build();
    }

    private JsonObject buildSource(SourceDictionary i, Locale locale) {
        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("id", i.getId());
        builder.add("name", loc.find(i.getName(),locale));
        return  builder.build();
    }

    private JsonObject buildParticle(Particle p, Locale locale) {
        JsonObjectBuilder builder = createObjectBuilder();

        builder.add("id", p.getId());
        JsonObjectBuilder particle = Json.createObjectBuilder();

        if(p instanceof RootParticle) {
            particle.add("type", "root");
            particle.add("value", ((RootParticle) p).getRoot());
        }
        if(p instanceof PrefixParticle) {
            particle.add("id", ((PrefixParticle) p).getPrefix().getId());
            particle.add("type", "prefix");
            particle.add("value", loc.find(((PrefixParticle) p).getPrefix().getName(), locale));
        }
        if(p instanceof InterfixParticle) {
            particle.add("id", ((InterfixParticle) p).getInterfix().getId());
            particle.add("type", "interfix");
            particle.add("value", loc.find(((InterfixParticle) p).getInterfix().getName(), locale));
        }
        if(p instanceof SuffixParticle) {
            particle.add("id",  ((SuffixParticle) p).getSuffix().getId());
            particle.add("type", "suffix");
            particle.add("value", loc.find(((SuffixParticle) p).getSuffix().getName(), locale));
        }
        if(p instanceof ConstituentParticle){
            particle.add("type", "constituent");
            particle.add("value", ((ConstituentParticle) p).getConstituent());
        }
        builder.add("particle", particle);

        return  builder.build();
    }

    private JsonObject buildInflection(Inflection i, Locale locale) {
        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("id", i.getId());
        builder.add("inflection", Json.createObjectBuilder()
                .add("id", i.getInflectionDictionary().getId())
                .add("name", loc.find(i.getInflectionDictionary().getName(),locale)));

        if(i.getText() !=null)
            builder.add("text", i.getText());
        return  builder.build();
    }
}