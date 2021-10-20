package pl.edu.pwr.wordnetloom.server.business.tracker;

import pl.edu.pwr.wordnetloom.server.business.dictionary.control.DictionaryQueryService;
import pl.edu.pwr.wordnetloom.server.business.localistaion.control.LocalisedStringCommandService;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;
import pl.edu.pwr.wordnetloom.server.business.revisions.entity.RevisionsInfo;
import pl.edu.pwr.wordnetloom.server.business.sense.control.SenseQueryService;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.*;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.Synset;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetAttributes;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetRelation;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.*;
import pl.edu.pwr.wordnetloom.server.business.tracker.statistic.entity.UserStats;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity.SynsetAttributesHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity.SynsetHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity.SynsetRelationHistory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.json.*;
import javax.json.stream.JsonCollectors;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Math.max;
import static javax.json.Json.createObjectBuilder;

@Singleton
public class TrackerEntityBuilder {

    @Inject
    LocalisedStringCommandService stringCommandService;

    @Inject
    DictionaryQueryService dictionaryQueryService;

    @Inject
    SenseQueryService senseQueryService;

    public JsonObject buildSense(Sense sense, SenseAttributes attributes) {
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
            builder.add("part_of_speech", sense.getPartOfSpeech().getName());
        }

        if (Objects.nonNull(sense.getStatus())) {
            builder.add("status", sense.getStatus().getName());
        }

        if (Objects.nonNull(sense.getDomain())) {
            builder.add("domain", sense.getDomain().getName());
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

            if (Objects.nonNull(attributes.getErrorComment())) {
                builder.add("status_error_comment", attributes.getErrorComment());
            }

            if (Objects.nonNull(attributes.getUserName())) {
                builder.add("owner", attributes.getUserName());
            }
        }
        return builder.build();
    }

    public JsonObject buildSenseIncomingRelations(List<SenseRelation> senseRelationList) {
        JsonArray array = senseRelationList.stream()
                .map(this::buildSenseIncomingRelation)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("incoming_relations", array)
                .build();
    }

    private JsonObject buildSenseIncomingRelation(SenseRelation senseRelation) {
        return Json.createObjectBuilder()
                .add("relation_name", stringCommandService.getById(senseRelation.getRelationType().getName()))
                .add("sense_id", senseRelation.getParent().getId().toString())
                .add("sense_name", senseRelation.getParent().getWord().getWord())
                .build();
    }

    public JsonObject buildSenseOutgoingRelations(List<SenseRelation> senseRelationList) {
        JsonArray array = senseRelationList.stream()
                .map(this::buildSenseOutgoingRelation)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("outgoing_relations", array)
                .build();
    }

    private JsonObject buildSenseOutgoingRelation(SenseRelation senseRelation) {
        return Json.createObjectBuilder()
                .add("relation_name", stringCommandService.getById(senseRelation.getRelationType().getName()))
                .add("sense_id", senseRelation.getChild().getId().toString())
                .add("sense_name", senseRelation.getChild().getWord().getWord())
                .build();
    }

    public JsonObject buildSynset(Synset synset, SynsetAttributes synsetAttributes) {
        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("id", synset.getId().toString());

        builder.add("abstract", synset.getAbstract());

        if (Objects.nonNull(synset.getStatus())) {
            builder.add("status", synset.getStatus().getName());
        }

        if (synsetAttributes != null) {
            if (Objects.nonNull(synsetAttributes.getDefinition())) {
                builder.add("definition", synsetAttributes.getDefinition());
            }

            if (Objects.nonNull(synsetAttributes.getComment())) {
                builder.add("comment", synsetAttributes.getComment());
            }

            if (Objects.nonNull(synsetAttributes.getUserName())) {
                builder.add("owner", synsetAttributes.getUserName());
            }

            if (Objects.nonNull(synsetAttributes.getErrorComment())) {
                builder.add("status_error_comment", synsetAttributes.getErrorComment());
            }
        }

        return builder.build();
    }

    public JsonObject buildSynsetSenses(Synset synset) {
        JsonArray array = synset.getSenses()
                .stream().map(this::buildSimpleSense)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("senses", array)
                .build();
    }

    private JsonObject buildSimpleSense(Sense sense) {
        return Json.createObjectBuilder()
                .add("sense_id", sense.getId().toString())
                .add("lemma", sense.getWord().getWord())
                .add("part_of_speech", sense.getPartOfSpeech().getName())
                .build();
    }

    public JsonObject buildSynsetIncomingRelations(List<SynsetRelation> synsetRelationList) {
        JsonArray array = synsetRelationList.stream()
                .map(this::buildSynsetIncomingRelation)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("incoming_relations", array)
                .build();
    }

    private JsonObject buildSynsetIncomingRelation(SynsetRelation synsetRelation) {
        return Json.createObjectBuilder()
                .add("relation_name", stringCommandService.getById(synsetRelation.getRelationType().getName()))
                .add("synset_id", synsetRelation.getParent().getId().toString())
                .add("synset_name", synsetRelation.getParent().toString())
                .build();
    }

    public JsonObject buildSynsetOutgoingRelations(List<SynsetRelation> synsetRelationList) {
        JsonArray array = synsetRelationList.stream()
                .map(this::buildSynsetOutgoingRelation)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("outgoing_relations", array)
                .build();
    }

    private JsonObject buildSynsetOutgoingRelation(SynsetRelation synsetRelation) {
        return Json.createObjectBuilder()
                .add("relation_name", stringCommandService.getById(synsetRelation.getRelationType().getName()))
                .add("synset_id", synsetRelation.getChild().getId().toString())
                .add("synset_name", synsetRelation.getChild().toString())
                .build();
    }

    public JsonObject buildSenseListHistory(List<SenseHistory> senseHistoryList) {
        JsonArray array = senseHistoryList
                .stream().map(this::buildSenseHistory)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("sense_history", array)
                .build();
    }

    private void addRevisionInfoToJson(JsonObjectBuilder builder, RevisionsInfo revisionsInfo, int revType) {
        builder.add("timestamp", revisionsInfo.getTimestamp());
        builder.add("operation", revType);
        builder.add("editor", revisionsInfo.getUserEmail());
        builder.add("datetime", new Date(revisionsInfo.getTimestamp()).toString());
        builder.add("rev_id", revisionsInfo.getId());
    }

    private JsonObject buildSenseHistory(SenseHistory senseHistory) {
        SenseAttributesHistory attributes = senseHistory.getSenseAttributesHistory();
        JsonObjectBuilder builder = createObjectBuilder();
        addRevisionInfoToJson(builder, senseHistory.getRevisionsInfo(), senseHistory.getRevType());

        builder.add("id", senseHistory.getId().toString());

        if (senseHistory.getWord() != null) {
            builder.add("lemma", senseHistory.getWord().getWord());
        }

        builder.add("variant", senseHistory.getVariant());

        if (senseHistory.getPartOfSpeech() != null) {
            builder.add("part_of_speech", senseHistory.getPartOfSpeech().getName());
        }

        if (Objects.nonNull(senseHistory.getStatus())) {
            builder.add("status", senseHistory.getStatus().getName());
        }

        if (Objects.nonNull(senseHistory.getDomain())) {
            builder.add("domain", senseHistory.getDomain().getName());
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

            if (Objects.nonNull(attributes.getErrorComment())) {
                builder.add("status_error_comment", attributes.getErrorComment());
            }

            if (Objects.nonNull(attributes.getUserName())) {
                builder.add("owner", attributes.getUserName());
            }
        }
        return builder.build();
    }

    public JsonObject buildSenseHistoryIncomingRelations(List<SenseRelationHistory> senseRelationList) {
        JsonArray array = senseRelationList.stream()
                .map(this::buildSenseHistoryIncomingRelation)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("incoming_relations_history", array)
                .build();
    }

    private JsonObject buildSenseHistoryIncomingRelation(SenseRelationHistory senseRelation) {
        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("relation_name", stringCommandService.getById(senseRelation.getRelationType().getName()))
                .add("sense_id", senseRelation.getParent().getId().toString())
                .add("sense_name", senseRelation.getParent().getWord().getWord());
        addRevisionInfoToJson(builder, senseRelation.getRevisionsInfo(), senseRelation.getRevType());

        return builder.build();
    }

    public JsonObject buildSenseHistoryOutgoingRelations(List<SenseRelationHistory> senseRelationList) {
        JsonArray array = senseRelationList.stream()
                .map(this::buildSenseHistoryOutgoingRelation)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("outgoing_relations_history", array)
                .build();
    }

    private JsonObject buildSenseHistoryOutgoingRelation(SenseRelationHistory senseRelation) {
        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("relation_name", stringCommandService.getById(senseRelation.getRelationType().getName()))
                .add("sense_id", senseRelation.getChild().getId().toString())
                .add("sense_name", senseRelation.getChild().getWord().getWord());
        addRevisionInfoToJson(builder, senseRelation.getRevisionsInfo(), senseRelation.getRevType());

        return builder.build();
    }

    public JsonObject buildSynsetListHistory(List<SynsetHistory> synsetHistoryList) {
        JsonArray array = synsetHistoryList.stream()
                .map(this::buildSynsetHistory)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("synset_history", array)
                .build();
    }

    private JsonObject buildSynsetHistory(SynsetHistory synsetHistory) {
        SynsetAttributesHistory attributes = synsetHistory.getAttributes();
        JsonObjectBuilder builder = createObjectBuilder();
        addRevisionInfoToJson(builder, synsetHistory.getRevisionsInfo(), synsetHistory.getRevType());

        builder.add("id", synsetHistory.getId().toString());

        builder.add("unit_strings", synsetHistory.toString());

        if (Objects.nonNull(synsetHistory.getAbstract()))
            builder.add("abstract", synsetHistory.getAbstract());

        if (Objects.nonNull(attributes)) {
            if (Objects.nonNull(attributes.getDefinition())) {
                builder.add("definition", attributes.getDefinition());
            }

            if (Objects.nonNull(attributes.getComment())) {
                builder.add("comment", attributes.getComment());
            }

            if (Objects.nonNull(attributes.getUserName())) {
                builder.add("owner", attributes.getUserName());
            }
        }

        return builder.build();
    }

    public JsonObject buildSynsetHistoryIncomingRelations(List<SynsetRelationHistory> synsetRelationList) {
        JsonArray array = synsetRelationList.stream()
                .map(this::buildSynsetHistoryIncomingRelation)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("incoming_relations_history", array)
                .build();
    }

    private JsonObject buildSynsetHistoryIncomingRelation(SynsetRelationHistory synsetRelation) {
        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("relation_name", stringCommandService.getById(synsetRelation.getRelationType().getName()))
                .add("synset_id", synsetRelation.getParent().getId().toString())
                .add("synset_units", synsetRelation.getParent().toString());
        addRevisionInfoToJson(builder, synsetRelation.getRevisionsInfo(), synsetRelation.getRevType());

        return builder.build();
    }

    public JsonObject buildSynsetHistoryOutgoingRelations(List<SynsetRelationHistory> synsetRelationList) {
        JsonArray array = synsetRelationList.stream()
                .map(this::buildSynsetHistoryOutgoingRelation)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("outgoing_relations_history", array)
                .build();
    }

    private JsonObject buildSynsetHistoryOutgoingRelation(SynsetRelationHistory synsetRelation) {
        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("relation_name", stringCommandService.getById(synsetRelation.getRelationType().getName()))
                .add("synset_id", synsetRelation.getChild().getId().toString())
                .add("synset_units", synsetRelation.getChild().toString());
        addRevisionInfoToJson(builder, synsetRelation.getRevisionsInfo(), synsetRelation.getRevType());

        return builder.build();
    }

    public JsonObject buildUsersMonthlyStats(List<UserStats> userStatsList) {
        JsonArray array = userStatsList
                .stream().map(this::buildUsersMonthlyStatsElem)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("users_stats", array)
                .build();
    }

    private JsonObject buildUsersMonthlyStatsElem(UserStats userStats) {
        JsonObjectBuilder builder = createObjectBuilder();
        Calendar cal = Calendar.getInstance();
        cal.setTime(userStats.getDate());

        builder.add("user", userStats.getUser());
        builder.add("day", cal.get(Calendar.DAY_OF_MONTH));
        builder.add("month", cal.get(Calendar.MONTH));
        builder.add("year", cal.get(Calendar.YEAR));
        addUserStats(builder, userStats);

        return builder.build();
    }

    public JsonObject buildUserMonthlyStats(List<UserStats> userStatsList) {
        JsonObjectBuilder builder = createObjectBuilder();
        if (!userStatsList.isEmpty()) {
            UserStats userStats = userStatsList.get(0);
            Calendar cal = Calendar.getInstance();
            cal.setTime(userStats.getDate());

            builder.add("user", userStats.getUser());
            builder.add("month", cal.get(Calendar.MONTH));
            builder.add("year", cal.get(Calendar.YEAR));

            JsonArray array = userStatsList.stream()
                    .map(this::buildUserMonthlyStatsElem)
                    .collect(JsonCollectors.toJsonArray());
            builder.add("stats", array);
        }

        return builder.build();
    }

    private JsonObject buildUserMonthlyStatsElem(UserStats userStats) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(userStats.getDate());

        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("day", cal.get(Calendar.DAY_OF_MONTH));
        addUserStats(builder, userStats);

        return builder.build();
    }

    private void addUserStats(JsonObjectBuilder builder, UserStats userStats) {
        builder
            .add("senses_created", userStats.getSenseCreated())
            .add("senses_modified", userStats.getSenseModified())
            .add("senses_removed", userStats.getSenseRemoved())
            .add("sense_relations_created", userStats.getSenseRelationCreated())
            .add("sense_relations_modified", userStats.getSenseRelationModified())
            .add("sense_relations_removed", userStats.getSenseRelationRemoved())
            .add("synsets_created", userStats.getSynsetCreated())
            .add("synsets_modified", userStats.getSynsetModified())
            .add("synsets_removed", userStats.getSynsetRemoved())
            .add("synset_relations_created", userStats.getSynsetRelationCreated())
            .add("synset_relations_modified", userStats.getSynsetRelationModified())
            .add("synset_relations_removed", userStats.getSynsetRelationRemoved());
    }

    public JsonObject buildUsersDailyStats(List<UserStats> userStatsList) {
        JsonArray array = userStatsList
                .stream().map(this::buildUsersDailyStatsElem)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("users_stats", array)
                .build();
    }

    private JsonObject buildUsersDailyStatsElem(UserStats userStats) {
        JsonObjectBuilder builder = createObjectBuilder();
        Calendar cal = Calendar.getInstance();
        cal.setTime(userStats.getDate());

        builder.add("user", userStats.getUser());
        builder.add("hour", cal.get(Calendar.HOUR_OF_DAY));
        builder.add("day", cal.get(Calendar.DAY_OF_MONTH));
        builder.add("month", cal.get(Calendar.MONTH));
        builder.add("year", cal.get(Calendar.YEAR));

        addUserStats(builder, userStats);
        return builder.build();
    }

    public JsonObject buildUserDailyStats(List<UserStats> userStatsList) {
        JsonObjectBuilder builder = createObjectBuilder();
        if (!userStatsList.isEmpty()) {
            UserStats userStats = userStatsList.get(0);
            Calendar cal = Calendar.getInstance();
            cal.setTime(userStats.getDate());

            builder.add("user", userStats.getUser());
            builder.add("day", cal.get(Calendar.DAY_OF_MONTH));
            builder.add("month", cal.get(Calendar.MONTH));
            builder.add("year", cal.get(Calendar.YEAR));

            JsonArray array = userStatsList.stream()
                    .map(this::buildUserDailyStatsElem)
                    .collect(JsonCollectors.toJsonArray());
            builder.add("stats", array);
        }

        return builder.build();
    }

    private JsonObject buildUserDailyStatsElem(UserStats userStats) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(userStats.getDate());

        JsonObjectBuilder builder = createObjectBuilder();
        builder.add("hour", cal.get(Calendar.HOUR_OF_DAY));
        addUserStats(builder, userStats);

        return builder.build();
    }

    public JsonObject buildSenseHistorySearchList(List<SenseHistory> senseHistoryList,
                                                  int pages,
                                                  int page,
                                                  boolean hasNext,
                                                  boolean hasPrev) {
        JsonArray array = senseHistoryList
                .stream().map(this::buildSenseHistorySearchListElem)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("pages", pages)
                .add("page", page)
                .add("has_next", hasNext)
                .add("has_prev", hasPrev)
                .add("senses", array)
                .build();
    }

    public JsonObject buildSenseHistorySearchListElem(SenseHistory senseHistory) {
        JsonObjectBuilder builder = createObjectBuilder();
        addRevisionInfoToJson(builder, senseHistory.getRevisionsInfo(), senseHistory.getRevType());

        if (senseHistory.getBeforeHistory() != null)
            addBefore(builder, senseHistory.getBeforeHistory());

        builder.add("id", senseHistory.getId().toString());

        if (senseHistory.getWord() != null) {
            builder.add("lemma", senseHistory.getWord().getWord());
        }

        builder.add("variant", senseHistory.getVariant());

        if (senseHistory.getPartOfSpeech() != null) {
            builder.add("part_of_speech", senseHistory.getPartOfSpeech().getName());
        }

        if (Objects.nonNull(senseHistory.getStatus())) {
            builder.add("status", senseHistory.getStatus().getName());
        }

        if (Objects.nonNull(senseHistory.getDomain())) {
            builder.add("domain", senseHistory.getDomain().getName());
        }

        return builder.build();
    }

    public JsonObject buildSenseAttributesHistorySearchList(List<SenseAttributesHistory> senseAttributesHistoryList,
                                                            int pages,
                                                            int page,
                                                            boolean hasNext,
                                                            boolean hasPrev) {
        JsonArray array = senseAttributesHistoryList
                .stream().map(this::buildSenseAttributesHistorySearchListElem)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("pages", pages)
                .add("page", page)
                .add("has_next", hasNext)
                .add("has_prev", hasPrev)
                .add("attributes", array)
                .build();
    }

    public JsonObject buildSenseAttributesHistorySearchListElem(SenseAttributesHistory attributesHistory) {
        JsonObjectBuilder builder = createObjectBuilder();
        addRevisionInfoToJson(builder, attributesHistory.getRevisionsInfo(), attributesHistory.getRevType());

        if (attributesHistory.getBeforeHistory() != null)
            addBefore(builder, attributesHistory.getBeforeHistory());

        builder.add("sense_id", attributesHistory.getId().toString());

        builder.add("lemma", attributesHistory.getLemma());

        if (Objects.nonNull(attributesHistory.getRegister())) {
            builder.add("register", attributesHistory.getRegister().getId());
        }

        if (Objects.nonNull(attributesHistory.getDefinition())) {
            builder.add("definition", attributesHistory.getDefinition());
        }

        if (Objects.nonNull(attributesHistory.getComment())) {
            builder.add("comment", attributesHistory.getComment());
        }

        if (Objects.nonNull(attributesHistory.getErrorComment())) {
            builder.add("status_error_comment", attributesHistory.getErrorComment());
        }

        if (Objects.nonNull(attributesHistory.getUserName())) {
            builder.add("owner", attributesHistory.getUserName());
        }

        return builder.build();
    }

    public JsonObject buildSenseRelationsHistorySearchList(List<SenseRelationHistory> senseRelationHistoryList,
                                                           int pages,
                                                           int page,
                                                           boolean hasNext,
                                                           boolean hasPrev) {
        JsonArray array = senseRelationHistoryList
                .stream().map(this::buildSenseRelationsHistorySearchListElem)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("pages", pages)
                .add("page", page)
                .add("has_next", hasNext)
                .add("has_prev", hasPrev)
                .add("sense_relations", array)
                .build();
    }

    private JsonObject buildSenseRelationsHistorySearchListElem(SenseRelationHistory senseRelationHistory) {
        JsonObjectBuilder builder = createObjectBuilder();
        addRevisionInfoToJson(builder, senseRelationHistory.getRevisionsInfo(), senseRelationHistory.getRevType());

        if (senseRelationHistory.getParent() != null) {
            builder.add("source_sense_id", senseRelationHistory.getParent().getId().toString());
            builder.add("source_sense_lemma", senseRelationHistory.getParent().getWord().getWord());
        }

        if (senseRelationHistory.getChild() != null) {
            builder.add("target_sense_id", senseRelationHistory.getChild().getId().toString());
            builder.add("target_sense_lemma", senseRelationHistory.getChild().getWord().getWord());
        }

        if (senseRelationHistory.getRelationType() != null) {
            builder.add("relation_id", senseRelationHistory.getRelationType().getId().toString());
            builder.add("relation_name", stringCommandService.getById(senseRelationHistory.getRelationType().getName()));
        }

        return builder.build();
    }

    public JsonObject buildListOfRelations(List<RelationType> senseRelationsList) {
        JsonArray array = senseRelationsList
                .stream().map(this::buildListOfSenseRelationsElem)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("relations", array)
                .build();
    }

    private JsonObject buildListOfSenseRelationsElem(RelationType relationType) {
        return Json.createObjectBuilder()
                .add("relation_id", relationType.getId().toString())
                .add("relation_name", stringCommandService.getById(relationType.getName()))
                .build();
    }



    public JsonObject buildSynsetHistorySearchList(List<SynsetHistory> synsetHistoryList,
                                                   int pages,
                                                   int page,
                                                   boolean hasNext,
                                                   boolean hasPrev) {
        JsonArray array = synsetHistoryList
                .stream().map(this::buildSynsetHistorySearchListElem)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("pages", pages)
                .add("page", page)
                .add("has_next", hasNext)
                .add("has_prev", hasPrev)
                .add("synsets", array)
                .build();
    }

    public JsonObject buildSynsetHistorySearchListElem(SynsetHistory synsetHistory) {
        JsonObjectBuilder builder = createObjectBuilder();
        addRevisionInfoToJson(builder, synsetHistory.getRevisionsInfo(), synsetHistory.getRevType());

        if (synsetHistory.getBeforeHistory() != null)
            addBefore(builder, synsetHistory.getBeforeHistory());

        builder.add("id", synsetHistory.getId().toString());

        builder.add("unit_strings", synsetHistory.toString());

        if (synsetHistory.getAbstract() != null)
            builder.add("abstract", synsetHistory.getAbstract());

        if (synsetHistory.getLexicon() != null)
            builder.add("lexicon", synsetHistory.getLexicon().getId());

        if (synsetHistory.getStatus() != null)
            builder.add("status", synsetHistory.getStatus().getName());

        return builder.build();
    }



    public JsonObject buildSynsetAttributesHistorySearchList(List<SynsetAttributesHistory> synsetAttributesHistoryList,
                                                             int pages,
                                                             int page,
                                                             boolean hasNext,
                                                             boolean hasPrev) {
        JsonArray array = synsetAttributesHistoryList
                .stream().map(this::buildSynsetAttributesHistorySearchListElem)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("pages", pages)
                .add("page", page)
                .add("has_next", hasNext)
                .add("has_prev", hasPrev)
                .add("synset_atttributes", array)
                .build();
    }

    private JsonObject buildSynsetAttributesHistorySearchListElem(SynsetAttributesHistory attributesHistory) {
        JsonObjectBuilder builder = createObjectBuilder();
        addRevisionInfoToJson(builder, attributesHistory.getRevisionsInfo(), attributesHistory.getRevType());

        if (attributesHistory.getBeforeHistory() != null)
            addBefore(builder, attributesHistory.getBeforeHistory());

        if (attributesHistory.getId() != null) {
            builder.add("synset_id", attributesHistory.getId().toString());
        }

        if (attributesHistory.getComment() != null) {
            builder.add("comment", attributesHistory.getComment());
        }

        if (attributesHistory.getDefinition() != null) {
            builder.add("definition", attributesHistory.getDefinition());
        }

        if (attributesHistory.getErrorComment() != null) {
            builder.add("error_comment", attributesHistory.getErrorComment());
        }

        if (attributesHistory.getIliId() != null) {
            builder.add("ili_id", attributesHistory.getIliId());
        }

        if (attributesHistory.getPrincetonId() != null) {
            builder.add("princeton_id", attributesHistory.getPrincetonId());
        }

        builder.add("unit_strings", attributesHistory.toString());

        return builder.build();
    }

    public JsonObject buildSynsetRelationsHistorySearchList(List<SynsetRelationHistory> synsetRelationHistoryList,
                                                            int pages,
                                                            int page,
                                                            boolean hasNext,
                                                            boolean hasPrev) {
        JsonArray array = synsetRelationHistoryList
                .stream().map(this::buildSynsetRelationsHistorySearchListElem)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("pages", pages)
                .add("page", page)
                .add("has_next", hasNext)
                .add("has_prev", hasPrev)
                .add("synset_relations", array)
                .build();
    }

    private JsonObject buildSynsetRelationsHistorySearchListElem(SynsetRelationHistory synsetRelationHistory) {
        JsonObjectBuilder builder = createObjectBuilder();
        addRevisionInfoToJson(builder, synsetRelationHistory.getRevisionsInfo(), synsetRelationHistory.getRevType());

        if (synsetRelationHistory.getParent() != null) {
            builder.add("source_synset_id", synsetRelationHistory.getParent().getId().toString());
            builder.add("source_synset_lemma", synsetRelationHistory.getParent().toString());
        }

        if (synsetRelationHistory.getChild() != null) {
            builder.add("target_synset_id", synsetRelationHistory.getChild().getId().toString());
            builder.add("target_synset_lemma", synsetRelationHistory.getChild().toString());
        }

        if (synsetRelationHistory.getRelationType() != null) {
            builder.add("relation_id", synsetRelationHistory.getRelationType().getId().toString());
            builder.add("relation_name", stringCommandService.getById(synsetRelationHistory.getRelationType().getName()));
        }

        return builder.build();
    }

    private void addBefore(JsonObjectBuilder builder, BeforeHistory beforeHistory) {
        if (beforeHistory.getComment() != null)
            builder.add("before_comment", beforeHistory.getComment());

        if (beforeHistory.getDefinition() != null)
            builder.add("before_definition", beforeHistory.getDefinition());

        if (beforeHistory.getErrorComment() != null)
            builder.add("before_error_comment", beforeHistory.getErrorComment());

        if (beforeHistory.getRegister() != null)
            builder.add("before_register", beforeHistory.getRegister().getId());

        if (beforeHistory.getLexicon() != null)
            builder.add("before_lexicon", beforeHistory.getLexicon().getId());

        if (beforeHistory.getWord() != null)
            builder.add("before_lemma", beforeHistory.getWord().getWord());

        if (beforeHistory.getVariant() != null)
            builder.add("before_variant", beforeHistory.getVariant());

        if (beforeHistory.getPartOfSpeech() != null)
            builder.add("before_part_of_speech", beforeHistory.getPartOfSpeech().getName());

        if (beforeHistory.getDomain() != null)
            builder.add("before_domain", beforeHistory.getDomain().getName());

        if (beforeHistory.getStatus() != null)
            builder.add("before_status", beforeHistory.getStatus().getName());

        if (beforeHistory.getAbstract() != null)
            builder.add("before_is_abstract", beforeHistory.getAbstract());

        if (beforeHistory.getPrincetonId() != null)
            builder.add("before_princeton_id", beforeHistory.getPrincetonId());

        if (beforeHistory.getIliId() != null)
            builder.add("before_ili_id", beforeHistory.getIliId());
    }

    public JsonObject buildEmotionalAnnotation(EmotionalAnnotation emotionalAnnotation) {
        JsonObjectBuilder builder = createObjectBuilder();

        builder.add("id", emotionalAnnotation.getId().toString());
        builder.add("super_annotation", emotionalAnnotation.isSuperAnnotation());
        builder.add("emotional_characteristic", emotionalAnnotation.isEmotionalCharacteristic());
        builder.add("emotions", buildSenseEmotionString(emotionalAnnotation.getEmotions()));
        builder.add("valuations", buildSenseValuationString(emotionalAnnotation.getValuations()));

        if (emotionalAnnotation.getMarkedness() != null)
            builder.add("markedness", stringCommandService.getById(emotionalAnnotation.getMarkedness().getName()));

        if (emotionalAnnotation.getExample1() != null)
            builder.add("example1", emotionalAnnotation.getExample1());

        if (emotionalAnnotation.getExample2() != null)
            builder.add("example2", emotionalAnnotation.getExample2());

        return builder.build();
    }

    private String buildSenseEmotionString(Set<SenseEmotion> emotionSet) {
        StringBuilder stringBuilder = new StringBuilder();
        emotionSet.forEach(d -> stringBuilder.append(stringCommandService.getById(d.getEmotion().getName())).append(", "));
        return stringBuilder.substring(0, max(stringBuilder.length() - 2, 0));
    }

    private String buildSenseValuationString(Set<SenseValuation> valuationSet) {
        StringBuilder stringBuilder = new StringBuilder();
        valuationSet.forEach(d -> stringBuilder.append(stringCommandService.getById(d.getValuation().getName())).append(", "));
        return stringBuilder.substring(0, max(stringBuilder.length() - 2, 0));
    }

    public JsonObject buildEmotionalAnnotationHistoryList(List<EmotionalAnnotationHistory> emotionalAnnotationHistories) {
        JsonArray array = emotionalAnnotationHistories
                .stream().map(this::buildEmotionalAnnotationHistory)
                .collect(JsonCollectors.toJsonArray());
        return createObjectBuilder()
                .add("emotional_annotation_history_list", array)
                .build();
    }

    private JsonObject buildEmotionalAnnotationHistory(EmotionalAnnotationHistory emotionalAnnotationHistory) {
        JsonObjectBuilder builder = createObjectBuilder();
        addRevisionInfoToJson(builder, emotionalAnnotationHistory.getRevisionsInfo(), emotionalAnnotationHistory.getRevType());

        builder.add("id", emotionalAnnotationHistory.getId().toString());
        builder.add("super_annotation", emotionalAnnotationHistory.isSuperAnnotation());
        builder.add("emotional_characteristic", emotionalAnnotationHistory.isEmotionalCharacteristic());
        builder.add("emotions", buildSenseEmotionHistoryString(emotionalAnnotationHistory.getEmotions()));
        builder.add("valuations", buildSenseValuationHistoryString(emotionalAnnotationHistory.getValuations()));

        if (emotionalAnnotationHistory.getMarkednessId() != null)
            builder.add("markedness", getMarkednessNameById(emotionalAnnotationHistory.getMarkednessId()));

        if (emotionalAnnotationHistory.getExample1() != null)
            builder.add("example1", emotionalAnnotationHistory.getExample1());

        if (emotionalAnnotationHistory.getExample2() != null)
            builder.add("example2", emotionalAnnotationHistory.getExample2());

        return builder.build();
    }

    private String buildSenseEmotionHistoryString(List<SenseEmotionHistory> emotionSet) {
        StringBuilder stringBuilder = new StringBuilder();
        emotionSet.forEach(d -> stringBuilder.append(stringCommandService.getById(d.getEmotion().getName())).append(", "));
        return stringBuilder.substring(0, max(stringBuilder.length() - 2, 0));
    }

    private String buildSenseValuationHistoryString(List<SenseValuationHistory> valuationSet) {
        StringBuilder stringBuilder = new StringBuilder();
        valuationSet.forEach(d -> stringBuilder.append(stringCommandService.getById(d.getValuation().getName())).append(", "));
        return stringBuilder.substring(0, max(stringBuilder.length() - 2, 0));
    }

    private String getMarkednessNameById(Long id) {
        AtomicReference<String> result = new AtomicReference<>(" ");
        dictionaryQueryService.findMarkedness(id).ifPresent(
                m -> result.set(stringCommandService.getById(m.getName())));
        return result.get();
    }

    public JsonObject buildEmotionalAnnotationHistorySearchList(List<EmotionalAnnotationHistory> emotionalAnnotationHistoryList,
                                                             int pages,
                                                             int page,
                                                             boolean hasNext,
                                                             boolean hasPrev) {
        JsonArray array = emotionalAnnotationHistoryList
                .stream().map(this::buildEmotionalAnnotationHistorySearchListElem)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("pages", pages)
                .add("page", page)
                .add("has_next", hasNext)
                .add("has_prev", hasPrev)
                .add("emotional_annotation", array)
                .build();
    }

    private JsonObject buildEmotionalAnnotationHistorySearchListElem(EmotionalAnnotationHistory emotionalAnnotationHistory) {
        JsonObjectBuilder builder = createObjectBuilder();
        addRevisionInfoToJson(builder, emotionalAnnotationHistory.getRevisionsInfo(), emotionalAnnotationHistory.getRevType());

        if (emotionalAnnotationHistory.getBeforeHistory() != null) {
            EmotionalAnnotationHistory history = emotionalAnnotationHistory.getBeforeHistory();
            builder.add("before_emotions", buildSenseEmotionHistoryString(history.getEmotions()));
            builder.add("before_valuations", buildSenseValuationHistoryString(history.getValuations()));
            builder.add("before_editor", history.getRevisionsInfo().getUserEmail());

            if(history.getMarkednessId() != null)
                builder.add("before_markedness", getMarkednessNameById(history.getMarkednessId()));
        }

        builder.add("id", emotionalAnnotationHistory.getId().toString());
        builder.add("emotions", buildSenseEmotionHistoryString(emotionalAnnotationHistory.getEmotions()));
        builder.add("valuations", buildSenseValuationHistoryString(emotionalAnnotationHistory.getValuations()));

        if (emotionalAnnotationHistory.getMarkednessId() != null)
            builder.add("markedness", getMarkednessNameById(emotionalAnnotationHistory.getMarkednessId()));

        if (emotionalAnnotationHistory.getSenseId() != null)
            senseQueryService.findById(emotionalAnnotationHistory.getSenseId()).ifPresent(
                    s -> {
                        builder.add("sense_id", s.getId().toString());
                        builder.add("sense_lemma", s.getWord().getWord());
                    });

        return builder.build();
    }

    public JsonObject buildSenseMorphology(List<Morphology> morphologies) {
        JsonArray array = morphologies
                .stream()
                .map(this::buildSenseMorphologyElem)
                .collect(JsonCollectors.toJsonArray());

        return createObjectBuilder()
                .add("morphologies", array)
                .build();
    }

    private JsonObject buildSenseMorphologyElem(Morphology morphology) {
        return createObjectBuilder()
                .add("word_form", morphology.getWordForm())
                .add("morphological_tag", morphology.getMorphologicalTag())
                .build();
    }
}
