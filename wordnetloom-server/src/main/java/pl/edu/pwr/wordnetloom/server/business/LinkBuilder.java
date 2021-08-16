package pl.edu.pwr.wordnetloom.server.business;

import pl.edu.pwr.wordnetloom.server.business.corpusexample.boundary.CorpusExampleResource;
import pl.edu.pwr.wordnetloom.server.business.dictionary.boundary.DictionaryResource;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Dictionary;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Domain;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.PartOfSpeech;
import pl.edu.pwr.wordnetloom.server.business.lexicon.boundary.LexiconResource;
import pl.edu.pwr.wordnetloom.server.business.lexicon.entity.Lexicon;
import pl.edu.pwr.wordnetloom.server.business.relationtype.boundary.RelationTypeResource;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationTest;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;
import pl.edu.pwr.wordnetloom.server.business.security.boundary.SecurityResource;
import pl.edu.pwr.wordnetloom.server.business.sense.boundary.SenseResource;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.Sense;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.SenseExample;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.SenseRelation;
import pl.edu.pwr.wordnetloom.server.business.synset.boundary.SynsetResource;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.Synset;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetRelation;

import javax.inject.Singleton;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.UUID;

@Singleton
public class LinkBuilder {

    public URI forDictionary(Dictionary dic, String methodName, UriInfo uriInfo) {
        return createResourceUri(DictionaryResource.class, methodName, dic.getId(), uriInfo);
    }

    public URI forDictionaries(UriInfo uriInfo) {
        return createResourceUri(DictionaryResource.class, uriInfo);
    }

    public URI forDomain(Domain d, UriInfo uriInfo) {
        return createResourceUri(DictionaryResource.class, "getDomain", d.getId(), uriInfo);
    }

    public URI forStatutes(UriInfo uriInfo) {
        return createResourceUri(DictionaryResource.class, "getAllStatuses", uriInfo);
    }

    public URI forPartOfSpeech(PartOfSpeech p, UriInfo uriInfo) {
        return createResourceUri(DictionaryResource.class, "getPartOfSpeech", p.getId(), uriInfo);
    }

    public URI forPartsOfSpeech(UriInfo uriInfo) {
        return createResourceUri(DictionaryResource.class, "getAllPartsOfSpeech", uriInfo);
    }

    public URI forDomains(UriInfo uriInfo) {
        return createResourceUri(DictionaryResource.class, "getAllDomains", uriInfo);
    }

    public URI forRegisters(UriInfo uriInfo) {
        return createResourceUri(DictionaryResource.class, "getAllRegisters", uriInfo);
    }

    public URI forLexicon(Lexicon l, UriInfo uriInfo) {
        return createResourceUri(LexiconResource.class, "getLexicon", l.getId(), uriInfo);
    }

    public URI forLexicons(UriInfo uriInfo) {
        return createResourceUri(LexiconResource.class, uriInfo);
    }

    public URI forSynsets(UriInfo uriInfo) {
        return createResourceUri(SynsetResource.class, uriInfo);
    }

    public URI forSynset(Synset s, UriInfo uriInfo) {
        return createResourceUri(SynsetResource.class, "synset", s.getId(), uriInfo);
    }

    public URI forSenses(UriInfo uriInfo) {
        return createResourceUri(SenseResource.class, uriInfo);
    }

    public URI forSearchSenses(UriInfo uriInfo, int page, int perPage) {
        return createResourceUri(SenseResource.class, "search", uriInfo, page, perPage);
    }


    public URI forSense(Sense s, UriInfo uriInfo) {
        return createResourceUri(SenseResource.class, "sense", s.getId(), uriInfo);
    }

    public URI forSenseExamples(UUID id, UriInfo uriInfo) {
        return createResourceUri(SenseResource.class, "senseExamples", id, uriInfo);
    }

    public URI forSenseExample(SenseExample se, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(SenseResource.class)
                .path(SenseResource.class, "senseExample").build(se.getSenseAttributes().getId(), se.getId());
    }

    public URI forRelationTypes(UriInfo uriInfo) {
        return createResourceUri(RelationTypeResource.class, uriInfo);
    }

    public URI forRelationType(RelationType rt, UriInfo uriInfo) {
        return createResourceUri(RelationTypeResource.class, "getRelationType", rt.getId(), uriInfo);
    }

    public URI forRelationTests(RelationType rt, UriInfo uriInfo) {
        return createResourceUri(RelationTypeResource.class, "getRelationTests", rt.getId(), uriInfo);
    }

    public URI forRelationTest(RelationTest rt, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(RelationTypeResource.class)
                .path(RelationTypeResource.class, "getRelationTest").build(rt.getRelationType().getId(), rt.getId());
    }

    private URI createResourceUri(Class<?> resourceClass, String method, long id, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(resourceClass).path(resourceClass, method).build(id);
    }

    private URI createResourceUri(Class<?> resourceClass, String method, UUID id, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(resourceClass).path(resourceClass, method).build(id);
    }

    private URI createResourceUri(Class<?> resourceClass, String method, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(resourceClass).path(resourceClass, method).build();
    }

    private URI createResourceUri(Class<?> resourceClass, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(resourceClass)
                .build();
    }

    private URI createResourceUri(Class<?> resourceClass, String method, UriInfo uriInfo, int page, int perPage) {
        return uriInfo.getBaseUriBuilder().path(resourceClass).path(resourceClass, method)
                .queryParam("start", page)
                .queryParam("limit", perPage)
                .build();
    }

    public URI forSenseRelations(Sense s, UriInfo uriInfo) {
        return createResourceUri(SenseResource.class, "senseRelations", s.getId(), uriInfo);
    }

    public URI forSenseRelation(SenseRelation r, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(SenseResource.class)
                .path(SenseResource.class, "relation")
                .build(r.getParent().getId(), r.getRelationType().getId(), r.getChild().getId());
    }

    public URI forSenseGraph(UUID senseId, UriInfo uriInfo) {
        return createResourceUri(SenseResource.class, "senseGraph", senseId, uriInfo);
    }

    public URI forSenseMoveUp(UUID senseId, UriInfo uriInfo) {
        return createResourceUri(SenseResource.class, "moveUp", senseId, uriInfo);
    }

    public URI forSenseMoveDown(UUID senseId, UriInfo uriInfo) {
        return createResourceUri(SenseResource.class, "moveDown", senseId, uriInfo);
    }

    public URI forSenseDetachSynset(UUID senseId, UriInfo uriInfo) {
        return createResourceUri(SenseResource.class, "detachSynset", senseId, uriInfo);
    }

    public URI forSynsetExamples(UUID id, UriInfo uriInfo) {
        return createResourceUri(SynsetResource.class, "synsetExamples", id, uriInfo);
    }

    public URI forSynsetExample(UUID synsetId, UUID example, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(SynsetResource.class)
                .path(SynsetResource.class, "synsetExample").build(synsetId, example);
    }

    public URI forSynsetRelations(Synset s, UriInfo uriInfo) {
        return createResourceUri(SynsetResource.class, "synsetRelations", s.getId(), uriInfo);
    }

    public URI forSynsetsGraph(UUID id, UriInfo uriInfo) {
        return createResourceUri(SynsetResource.class, "synsetGraph", id, uriInfo);
    }

    public URI forSecurity(UriInfo uriInfo) {
        return createResourceUri(SecurityResource.class, uriInfo);
    }

    public URI forSecurityAuthorize(UriInfo uriInfo) {
        return createResourceUri(SecurityResource.class, "authorize", uriInfo);
    }

    public URI forSecurityClaims(UriInfo uriInfo) {
        return createResourceUri(SecurityResource.class, "claims", uriInfo);
    }

    public URI forSearchCorpusExamples(UriInfo uriInfo) {
        return createResourceUri(CorpusExampleResource.class, "searchCorpusExamples", uriInfo);
    }

    public URI forCorpusExamples(UriInfo uriInfo) {
        return createResourceUri(CorpusExampleResource.class, uriInfo);
    }

    public URI forSearchSynsets(UriInfo uriInfo, int start, int limit) {
        return createResourceUri(SynsetResource.class, "search", uriInfo, start, limit);
    }

    public URI forSynsetRelation(UUID source, UUID target, UUID relationType, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(SynsetResource.class)
                .path(SynsetResource.class, "relation")
                .build(source, relationType, target);
    }

    public URI forSynsetRelation(SynsetRelation sr, UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(SynsetResource.class)
                .path(SynsetResource.class, "relation")
                .build(sr.getParent().getId(), sr.getRelationType().getId(), sr.getChild().getId());
    }

    public URI forSearchSynsetRelations(UriInfo uriInfo) {
        return createResourceUri(SynsetResource.class, "searchRelations", uriInfo);
    }
}
