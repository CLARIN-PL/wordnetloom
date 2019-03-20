package pl.edu.pwr.wordnetloom.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.wordnetloom.client.model.*;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.*;

import static pl.edu.pwr.wordnetloom.client.service.Dictionaries.*;

@Singleton
public class RemoteService {

    private Client client;

    private static final Logger LOG = LoggerFactory.getLogger(RemoteService.class);
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_LANGUAGE = "Accept-Language";

    private static String SERVER_TARGET_URL = "";

    private static final String PATH_SECURITY_CLAIMS = "/security/claims";
    private static final String PATH_SECURITY_USER = "/security/user";

    private static final String PATH_SECURITY_CHANGE_PASSWORD = "/security/change-password";
    private static final String PATH_DICTIONARIES = "/dictionaries";
    private static final String PATH_LEXICONS = "/lexicons";
    private static final String PATH_SENSES_SEARCH = "/senses/search";
    private static final String PATH_SYNSETS_SEARCH = "/synsets/search";
    private static final String PATH_SYNSETS = "/synsets/{id}";
    private static final String PATH_SENSES = "/senses";
    private static final String PATH_SENSES_ID = "/senses/{id}";
    private static final String PATH_RELATION_TYPES = "/relation-types";
    private static final String PATH_ADD_SENSE_RELATION = "/senses/relations";
    private static final String PATH_ADD_SYNSET_RELATION = "/synsets/relations";
    private static final String PATH_SYNSET_RELATION_SEARCH = "/synsets/relations/search";
    private static final String PATH_ADD_SENSE_TO_NEW_SYNSET = "/synsets/add-sense-to-new-synset/{senseId}";
    private static final String PATH_ATTACH_SENSE_TO_SYNSET = "/senses/{senseId}/attach-to-synset/{synsetId}";
    private static final String PATH_GRAPH_SYNSETS = "/synsets/{id}/graph";
    private static final String PATH_GRAPH_SENSE = "/senses/{id}/graph";
    private static final String PATH_CORPUS_EXAMPLES = "/corpus-examples/search";
    private static final String PATH_PATH_TO_HYPERONYM = "synsets/{id}/path-to-hyperonymy";

    private static User user;

    public static User activeUser() {
        return user;
    }

    @Inject
    Dictionaries dictionaries;

    @Inject
    RelationTypeService relationTypeService;

    @Inject
    Properties properties;

    @PostConstruct
    public void init() {
        client = ClientBuilder.newClient();
        dictionaries.setService(this);
        relationTypeService.setService(this);

        String host = properties.getProperty("server.host");
        SERVER_TARGET_URL = host +"/wordnetloom-server/resources";
    }

    @PreDestroy
    public void preDestroy() {
        client.close();
    }

    public void authorize(User user) throws Exception {
        this.user = user;

        Form form = new Form();
        form.param("username", user.getUsername());
        form.param("password", user.getPassword());

        WebTarget webTarget = client.target(SERVER_TARGET_URL).path("/security/authorize");

        Jwt jwt = webTarget.request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED), Jwt.class);

        if (jwt == null) {
            LOG.debug("Failed to obtain JWT.");
            throw new Exception("Bad credentials incorrect username or password");
        }

        user.setToken("Bearer " + jwt.getToken());

        claimUserJwt();

        dictionaries.initializeDictionaries();
        relationTypeService.initializeRelationTypes();
    }

    private void claimUserJwt() {
        LOG.debug("Accessing " + PATH_SECURITY_CLAIMS + "...");

        WebTarget webTarget = client.target(SERVER_TARGET_URL)
                .path(PATH_SECURITY_CLAIMS);

        Response response = webTarget.request()
                .header(HEADER_AUTHORIZATION, user.getToken()).get();

        if (response.getStatus() == 200) {
            String json = response.readEntity(String.class);
            JsonReader reader = Json.createReader(new StringReader(json));
            JsonObject jsonObject = reader.readObject();
            activeUser().setFirstName(jsonObject.getString("first_name"));
            activeUser().setLastName(jsonObject.getString("last_name"));
            activeUser().setEmail(jsonObject.getString("email"));
            activeUser().setLexicons(jsonObject.getString("lexicons"));
            activeUser().setShowMarkers(jsonObject.getBoolean("show_marker"));
            activeUser().setShowTooltips(jsonObject.getBoolean("show_tooltips"));
            activeUser().setRole(jsonObject.getString("role"));
        }
        response.close();
    }

    public Map<String, String> mapDictionaries() throws Exception {
        LOG.debug("Accessing " + PATH_DICTIONARIES + "...");

        WebTarget webTarget = client.target(SERVER_TARGET_URL)
                .path(PATH_DICTIONARIES);

        Response response = webTarget.request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        Map<String, String> dic = new HashMap<>();

        if (response.getStatus() == 200) {
            String json = response.readEntity(String.class);
            JsonReader reader = Json.createReader(new StringReader(json));
            JsonObject jsonObject = reader.readObject();

            JsonObject _links = jsonObject.getJsonObject("_links");
            dic.put(PART_OF_SPEECH_DICTIONARY, _links.getString("parts_of_speech"));
            dic.put(DOMAIN_DICTIONARY, _links.getString("domains"));
            dic.put(STATUS_DICTIONARY, _links.getString("statuses"));
            dic.put(REGISTER_DICTIONARY, _links.getString("registers"));
            dic.put(EMOTION_DICTIONARY, _links.getString("emotions"));
            dic.put(LEXICON_DICTIONARY, SERVER_TARGET_URL + PATH_LEXICONS);
        }
        response.close();

        return dic;
    }

    public ObservableList<Dictionary> fetchDictionaries(String target) throws IOException {
        LOG.debug("Accessing " + target + "...");
        ObservableList<Dictionary> dictionary = FXCollections.observableArrayList();
        WebTarget webTarget = client.target(target);

        Response response = webTarget.request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            pl.edu.pwr.wordnetloom.client.model.Dictionaries jsonArray = response.readEntity(pl.edu.pwr.wordnetloom.client.model.Dictionaries.class);
            dictionary.addAll(jsonArray.getRows());
        }
        response.close();
        return dictionary;
    }

    public  Dictionary findDictionary(URI target) throws IOException {
        LOG.debug("Searching dictionary " + target);

        WebTarget webTarget = client.target(target);

        Response response = webTarget.request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(Dictionary.class);
        }
        response.close();
        return null;
    }

    public SearchList search(SearchFilter filter) {
        final String PATH = filter.getSynsetMode() ? PATH_SYNSETS_SEARCH : PATH_SENSES_SEARCH;
        WebTarget target = client.target(SERVER_TARGET_URL).path(PATH);

        if (filter.getLemma() != null) {
            target = target.queryParam("lemma", filter.getLemma());
        }

        if (filter.getLexicon() != null) {
            target = target.queryParam("lexicon", filter.getLexicon());
        }
        if (filter.getPartOfSpeechId() != null) {
            target = target.queryParam("part_of_speech", filter.getPartOfSpeechId());
        }
        if (filter.getDomainId() != null) {
            target = target.queryParam("domain", filter.getDomainId());
        }
        if (filter.getAspectId() != null) {
            target = target.queryParam("aspect", filter.getAspectId());
        }
        if (filter.getDefinition() != null) {
            target = target.queryParam("definition", filter.getDefinition());
        }
        if (filter.getComment() != null) {
            target = target.queryParam("comment", filter.getComment());
        }
        if (filter.getExample() != null) {
            target = target.queryParam("example", filter.getExample());
        }
        if (filter.getRegisterId() != null) {
            target = target.queryParam("register", filter.getRegisterId());
        }
        if (filter.getStatusId() != null) {
            target = target.queryParam("status", filter.getStatusId());
        }
        if (filter.getSensesWithoutSynset() != null && filter.getSensesWithoutSynset()) {
            target = target.queryParam("senses_without_synset", filter.getSensesWithoutSynset());
        }
        if (filter.getSynsetId() != null) {
            target = target.queryParam("synset", filter.getSynsetId());
        }
        if (filter.getRelationTypeId() != null) {
            target = target.queryParam("relation_type", filter.getRelationTypeId());
        }
        if (filter.getStart() != null) {
            target = target.queryParam("start", filter.getStart());
        }
        if (filter.getLimit() != null) {
            target = target.queryParam("limit", filter.getLimit());
        }
        LOG.debug("Searching: " + target.getUri());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(SearchList.class);
        }
        return new SearchList();
    }


    public NodeExpanded findSynsetGraph(UUID id) {

        WebTarget target = client.target(SERVER_TARGET_URL)
                .path(PATH_GRAPH_SYNSETS).resolveTemplate("id", id);

        return loadNode(target);
    }

    public NodeExpanded findSenseGraph(UUID id) {

        WebTarget target = client.target(SERVER_TARGET_URL)
                .path(PATH_GRAPH_SENSE).resolveTemplate("id", id);

        return loadNode(target);
    }

    public List<NodeExpanded> findPathToHyperonym(UUID id) {
        WebTarget target = client.target(SERVER_TARGET_URL)
                .path(PATH_PATH_TO_HYPERONYM)
                .resolveTemplate("id", id);

        System.out.println("Loading path top hyperonym " + target.getUri());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if(response.getStatus() == 200) {
            return response.readEntity(new GenericType<List<NodeExpanded>>(){});
        }
        // TODO: obsłużyć błędy
        return null;
    }

    public NodeExpanded findSynsetGraph(URI link) {
        WebTarget target = client.target(link);
        return loadNode(target);
    }

    private NodeExpanded loadNode(WebTarget target) {
        LOG.debug("Loading graph: " + target.getUri());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(NodeExpanded.class);
        }
        return new NodeExpanded();
    }

    public Synset findSynset(UUID id) {

        WebTarget target = client.target(SERVER_TARGET_URL)
                .path(PATH_SYNSETS).resolveTemplate("id", id);

        LOG.debug("Loading synset: " + target.getUri());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(Synset.class);
        }
        return new Synset();
    }

    public Synset findSynset(URI link){
        WebTarget target =  client.target(link);

        LOG.debug("Loading synset " + target.getUri());
        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if(response.getStatus() == 200){
            return response.readEntity(Synset.class);
        }

        return new Synset();
    }

    public Sense findSense(UUID id) {

        WebTarget target = client.target(SERVER_TARGET_URL)
                .path(PATH_SENSES_ID).resolveTemplate("id", id);

        LOG.debug("Loading sense: " + target.getUri());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(Sense.class);
        }
        return new Sense();
    }

    public Sense findSense(URI link) {

        WebTarget target = client.target(link);

        LOG.debug("Loading sense: " + target.getUri());
        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(Sense.class);
        }
        return new Sense();
    }

    public RelationTypes findRelationTypes(RelationArgument argument){
        WebTarget target = client.target(SERVER_TARGET_URL)
                .path(PATH_RELATION_TYPES)
                .queryParam("argument", argument);

        LOG.debug("Loading relation types (" + argument + ") : " + target.getUri());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(RelationTypes.class);
        }
        return new RelationTypes();
    }

    public Relations findSynsetRelationBetween(UUID source, UUID trg){
        WebTarget target = client.target(SERVER_TARGET_URL)
                .path(PATH_SYNSET_RELATION_SEARCH)
                .queryParam("source", source)
                .queryParam("target", trg);

        LOG.debug("Searching relations: " + target.getUri());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(Relations.class);
        }
        return new Relations();
    }

    public List<RelationTest> getRelationTests(URI link) {
        WebTarget target = client.target(link);

        LOG.debug("Loading relation tests: " + target.getUri());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            RelationTests tests = response.readEntity(RelationTests.class);
            if(tests.getRows().isEmpty()) {
                return new ArrayList<>();
            }
            return tests.getRows();
        }
        return new ArrayList<>();
    }

    public List<Example> getExamples(URI link){
        WebTarget target = client.target(link);

        LOG.debug("Loading examples: " + target.getUri());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            Examples examples = response.readEntity(Examples.class);
            if(examples.getRows().isEmpty()) {
                return new ArrayList<>();
            }
            return examples.getRows();
        }
        return new ArrayList<>();
    }

    public SenseRelations getSenseRelations(URI link) {
        WebTarget target = client.target(link);

        LOG.debug("Loading sense relations: " + target.getUri());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(SenseRelations.class);
        }
        return new SenseRelations();
    }

    public Relation getRelation(URI link) {
        WebTarget target = client.target(link);

        LOG.debug("Loading synset relation: " + target.getUri());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(Relation.class);
        }
        return new Relation();
    }


    public RelationType getRelationType(URI link) {
        WebTarget target = client.target(link);

        LOG.debug("Loading relation type: " + target.getUri());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(RelationType.class);
        }
        return new RelationType();
    }

    public Relation getSenseRelation(URI link) {
        WebTarget target = client.target(link);

        LOG.debug("Loading sense relation: " + target.getUri());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(Relation.class);
        }
        return new Relation();
    }

    public Relation addSenseRelation(SenseRelation relation) throws IOException, ValidationException {

        WebTarget target = client.target(SERVER_TARGET_URL)
                .path(PATH_ADD_SENSE_RELATION);

        LOG.debug("Saving sense relation: " + relation);

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .post(Entity.entity(relation, MediaType.APPLICATION_JSON));

        if (isCreatedStatus(response)) {
            LOG.debug("Sense relation created: " + response.getLocation());
            return  getSenseRelation(response.getLocation());
        }

        if (isBadRequestStatus(response)) {
            LOG.debug("Sense relation validation failed: " + relation);
            String map = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errors = mapper.readValue(map, new TypeReference<Map<String, String>>() {
            });
            throw new ValidationException(errors);
        }
        return null;
    }

    public Relation addSynsetRelation(SynsetRelation relation) throws IOException, ValidationException {

        WebTarget target = client.target(SERVER_TARGET_URL)
                .path(PATH_ADD_SYNSET_RELATION);

        LOG.debug("Saving synset relation: " + relation);

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .post(Entity.entity(relation, MediaType.APPLICATION_JSON));

        if (isCreatedStatus(response)) {
            LOG.debug("Synset relation created: " + response.getLocation());
            Relation r = getRelation(response.getLocation());
            System.out.println(r);
            return r;
        }

        if (isBadRequestStatus(response)) {
            LOG.debug("Synset relation validation failed: " + relation);
            String map = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errors = mapper.readValue(map, new TypeReference<Map<String, String>>() {
            });
            throw new ValidationException(errors);
        }
        return null;
    }


    public Synset addSenseToNewSynset(UUID senseId) throws IOException {

        WebTarget target = client.target(SERVER_TARGET_URL)
                .path(PATH_ADD_SENSE_TO_NEW_SYNSET)
                .resolveTemplate("senseId", senseId);

        LOG.debug("Attaching to new synset - sense id: " + senseId.toString());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .method("PUT");

        if(isCreatedStatus(response)){
            LOG.debug("Sense moved to new synset: " + response.getEntity());
            return findSynset(response.getLocation());
        }
        badRequestHandler(response);
        throw null;
    }

    private void badRequestHandler(Response response) throws IOException {
        if(isBadRequestStatus(response)){
            String map = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errors = mapper.readValue(map, new TypeReference<Map<String, String>>() {});
            throw new ValidationException(errors);
        }
    }

    public Sense attachSenseToSynset(UUID senseId, UUID synsetId) throws ValidationException, IOException{

        WebTarget target = client.target(SERVER_TARGET_URL)
                .path(PATH_ATTACH_SENSE_TO_SYNSET)
                .resolveTemplate("senseId", senseId)
                .resolveTemplate("synsetId", synsetId);

        LOG.debug("Attaching sense: "+senseId+" to synset: " + synsetId.toString());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .method("PUT");
        System.out.println(response.getStatus());
        if(isOkStatus(response)){
            LOG.debug("Sense moved to new synset: " + response.getEntity());
            return findSense(response.getLocation());
        }

        badRequestHandler(response);

        throw null;
    }


    public CorpusExamples getCorpusExamples(String word) throws IOException {
        WebTarget target = client.target(SERVER_TARGET_URL)
                .path(PATH_CORPUS_EXAMPLES)
                .queryParam("lemma", word);

        LOG.debug("Loading corpus examples: " + target.getUri());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            return response.readEntity(CorpusExamples.class);
        }
        return new CorpusExamples();
    }

    public Sense saveSense(Sense sense) throws IOException, ValidationException {

        WebTarget target = client.target(SERVER_TARGET_URL)
                .path(PATH_SENSES);

        LOG.debug("Saving sense: " + sense);

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .post(Entity.entity(sense, MediaType.APPLICATION_JSON));

        if (isCreatedStatus(response)) {
            LOG.debug("Sense created: " + sense);
            return findSense(response.getLocation());
        }

        if (isBadRequestStatus(response)) {
            LOG.debug("Sense validation failed: " + sense);
            String map = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errors = mapper.readValue(map, new TypeReference<Map<String, String>>() {
            });
            throw new ValidationException(errors);
        }
        return null;
    }

    public Sense updateSense(Sense sense) throws IOException, ValidationException {

        WebTarget target = client.target(sense.getLinks().getSelf());

        LOG.debug("Updating sense: " + sense);

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .put(Entity.entity(sense, MediaType.APPLICATION_JSON));

        if (isOkStatus(response)) {
            LOG.debug("Sense updated: " + sense);
            return findSense(response.getLocation());
        }

        if (isBadRequestStatus(response)) {
            LOG.debug("Sense validation failed: " + sense);
            String map = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errors = mapper.readValue(map, new TypeReference<Map<String, String>>() {
            });
            throw new ValidationException(errors);
        }
        return null;
    }

    public Synset updateSynset(Synset synset) throws IOException {
        WebTarget target = client.target(synset.getLinks().getSelf());

        LOG.debug("Updating synset " + synset);

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .put(Entity.entity(synset, MediaType.APPLICATION_JSON));

        if(isOkStatus(response)){
            LOG.debug("Synset updated " + synset);
            return findSynset(response.getLocation());
        }

        if(isBadRequestStatus(response)){
            LOG.debug("Synset validation failed: " + synset);
            String map = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errors = mapper.readValue(map, new TypeReference<Map<String, String>>() {
            });
            throw new ValidationException(errors);
        }
        return null;
    }

    public boolean updateUser() throws IOException, ValidationException {

        WebTarget target = client.target(SERVER_TARGET_URL)
                .path(PATH_SECURITY_USER);

        LOG.debug("Updating user: " + activeUser());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .put(Entity.entity(activeUser(), MediaType.APPLICATION_JSON));

        if (isOkStatus(response)) {
            LOG.debug("User updated: " + activeUser());
            return true;
        }

        if (isBadRequestStatus(response)) {
            LOG.debug("User validation failed: " + activeUser());
            String map = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errors = mapper.readValue(map, new TypeReference<Map<String, String>>() {
            });
            throw new ValidationException(errors);
        }
        return false;
    }

    public Example updateExample(Example example) throws IOException, ValidationException {

        WebTarget target = client.target(example.getLinks().getSelf());

        LOG.debug("Updating example: " + example);

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .put(Entity.entity(example, MediaType.APPLICATION_JSON));

        if (isOkStatus(response)) {
            Example ee = findExample(response.getLocation());
            LOG.debug("Example updated: " + ee);
            return ee;
        }

        if (isBadRequestStatus(response)) {
            LOG.debug("Example validation failed: " + example);
            String map = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errors = mapper.readValue(map, new TypeReference<Map<String, String>>() {
            });
            throw new ValidationException(errors);
        }
        return null;
    }

    public Example addExample(URI link, Example example) throws IOException, ValidationException {

        WebTarget target = client.target(link);

        LOG.debug("Saving new example: " + example);

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .post(Entity.entity(example, MediaType.APPLICATION_JSON));

        if (isCreatedStatus(response)) {
            Example se = findExample(response.getLocation());
            LOG.debug("Example created: " + se);
            return se;
        }

        if (isBadRequestStatus(response)) {
            LOG.debug("Example validation failed: " + example);
            String map = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errors = mapper.readValue(map, new TypeReference<Map<String, String>>() {
            });
            throw new ValidationException(errors);
        }
        return null;
    }

    private boolean isBadRequestStatus(Response response) {
        return response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode();
    }

    private boolean isCreatedStatus(Response response) {
        return response.getStatus() == Response.Status.CREATED.getStatusCode();
    }

    public Example findExample(URI link) {

        WebTarget target = client.target(link);

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            Example ex = response.readEntity(Example.class);
            LOG.debug("Example found: " + ex);
            return ex;
        }
        return new Example();
    }

    public Lexicon findLexicon(URI link) {

        WebTarget target = client.target(link);

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            Lexicon lx = response.readEntity(Lexicon.class);
            LOG.debug("Lexicon found: " + link.toString());
            return lx;
        }
        return new Lexicon();
    }

    public RelationType findRelationType(URI link) {

        WebTarget target = client.target(link);

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            RelationType lx = response.readEntity(RelationType.class);
            LOG.debug("Relation type found: " + link.toString());
            return lx;
        }
        return new RelationType();
    }


    public Lexicon addLexicon(Lexicon lexicon) throws IOException, ValidationException {

        WebTarget target = client.target(lexicon.getLinks().getSelf());

        LOG.debug("Adding lexicon: " + lexicon);

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .post(Entity.entity(lexicon, MediaType.APPLICATION_JSON));

        if (isOkStatus(response)) {
            Lexicon lx = findLexicon(response.getLocation());
            LOG.debug("Adding lexicon: " + lx);
            return lx;
        }

        if (isBadRequestStatus(response)) {
            LOG.debug("Lexicon validation failed: " + lexicon);
            String map = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errors = mapper.readValue(map, new TypeReference<Map<String, String>>() {
            });
            throw new ValidationException(errors);
        }
        return null;
    }

    public RelationType addRelationType(RelationType rt) throws IOException, ValidationException {

        WebTarget target = client.target(SERVER_TARGET_URL)
                .path(PATH_RELATION_TYPES);

        LOG.debug("Adding relation type: " + rt);

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .post(Entity.entity(rt, MediaType.APPLICATION_JSON));

        if (isOkStatus(response)) {
            RelationType res = findRelationType(response.getLocation());
            LOG.debug("Adding relation type: " + rt);
            return res;
        }

        if (isBadRequestStatus(response)) {
            LOG.debug("Relation type validation: " + rt);
            String map = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errors = mapper.readValue(map, new TypeReference<Map<String, String>>() {
            });
            throw new ValidationException(errors);
        }
        return null;
    }

    public RelationTest findRelationTest(URI link) {

        WebTarget target = client.target(link);

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .get();

        if (response.getStatus() == 200) {
            RelationTest lx = response.readEntity(RelationTest.class);
            LOG.debug("Relation test found: " + link.toString());
            return lx;
        }
        return new RelationTest();
    }

    public RelationTest addRelationTest(RelationTest rt) throws IOException, ValidationException {

        WebTarget target = client.target(rt.getLinks().getTests());

        LOG.debug("Adding relation type: " + rt);

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .post(Entity.entity(rt, MediaType.APPLICATION_JSON));

        if (isOkStatus(response)) {
            RelationTest res = findRelationTest(response.getLocation());
            LOG.debug("Adding relation test: " + rt);
            return res;
        }

        if (isBadRequestStatus(response)) {
            LOG.debug("Relation test validation: " + rt);
            String map = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errors = mapper.readValue(map, new TypeReference<Map<String, String>>() {
            });
            throw new ValidationException(errors);
        }
        return null;
    }

    public RelationTest updateRelationTest(RelationTest rt) throws IOException, ValidationException {

        WebTarget target = client.target(rt.getLinks().getSelf());

        LOG.debug("Updating relation test: " + rt);

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .put(Entity.entity(rt, MediaType.APPLICATION_JSON));

        if (isOkStatus(response)) {
            RelationTest res = findRelationTest(response.getLocation());
            LOG.debug("Updating relation test: " + rt);
            return res;
        }

        if (isBadRequestStatus(response)) {
            LOG.debug("Relation test validation: " + rt);
            String map = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errors = mapper.readValue(map, new TypeReference<Map<String, String>>() {
            });
            throw new ValidationException(errors);
        }
        return null;
    }

    public RelationType updateRelationType(RelationType rt) throws IOException, ValidationException {

        WebTarget target = client.target(rt.getLinks().getSelf());

        LOG.debug("Updating relation type: " + rt.getLinks().getSelf().toString());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .put(Entity.entity(rt, MediaType.APPLICATION_JSON));

        if (isOkStatus(response)) {
            RelationType res = findRelationType(response.getLocation());
            LOG.debug("Relation updated type");
            return res;
        }

        if (isBadRequestStatus(response)) {
            LOG.debug("Relation type validation: " + rt);
            String map = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errors = mapper.readValue(map, new TypeReference<Map<String, String>>() {
            });
            throw new ValidationException(errors);
        }
        return null;
    }

    public Lexicon updateLexicon(Lexicon lexicon) throws IOException, ValidationException {

        WebTarget target = client.target(lexicon.getLinks().getSelf());

        LOG.debug("Updating lexicon: " + lexicon);

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .put(Entity.entity(lexicon, MediaType.APPLICATION_JSON));

        if (isOkStatus(response)) {
            Lexicon lx = findLexicon(response.getLocation());
            LOG.debug("Lexicon updated: " + lx);
            return lx;
        }

        if (isBadRequestStatus(response)) {
            LOG.debug("Lexicon validation failed: " + lexicon);
            String map = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errors = mapper.readValue(map, new TypeReference<Map<String, String>>() {
            });
            throw new ValidationException(errors);
        }
        return null;
    }

    public boolean executeAction(Action a, Response.Status expectedStatus) {

        WebTarget target = client.target(a.getHref());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .method(a.getMethod());

        if (response.getStatus() == expectedStatus.getStatusCode()) {
            LOG.debug("Executing action: " + a);
            return true;
        }
        return false;
    }

    public void delete(URI link) {

        WebTarget target = client.target(link);

        LOG.debug("Deleting " + target.getUri());

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .delete();

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
            LOG.debug("Entity removed: " + link);
        }
    }

    public boolean changePassword(String ps) throws IOException, ValidationException {
        WebTarget target = client.target(SERVER_TARGET_URL)
                .path(PATH_SECURITY_CHANGE_PASSWORD);

        LOG.debug("Changing user password ....");

        JsonObject json = Json.createObjectBuilder()
                .add("password", ps).build();

        Response response = target
                .request()
                .header(HEADER_AUTHORIZATION, user.getToken())
                .header(HEADER_LANGUAGE, user.getLanguage().getAbbreviation())
                .put(Entity.entity(json, MediaType.APPLICATION_JSON));

        if (isOkStatus(response)) {
            LOG.debug("Password changed ");
            return true;
        }

        if (isBadRequestStatus(response)) {
            LOG.debug("Password validation failed: " + activeUser());
            String map = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errors = mapper.readValue(map, new TypeReference<Map<String, String>>() {
            });
            throw new ValidationException(errors);
        }
        return false;
    }

    private boolean isOkStatus(Response response) {
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }
}