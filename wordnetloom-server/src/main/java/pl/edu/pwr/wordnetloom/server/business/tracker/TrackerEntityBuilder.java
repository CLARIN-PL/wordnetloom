package pl.edu.pwr.wordnetloom.server.business.tracker;

import pl.edu.pwr.wordnetloom.server.business.localistaion.control.LocalisedStringCommandService;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;
import pl.edu.pwr.wordnetloom.server.business.revisions.entity.RevisionsInfo;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.Sense;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.SenseAttributes;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.SenseRelation;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.Synset;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetAttributes;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetRelation;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.SenseAttributesHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.SenseHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.SenseRelationHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.statistic.entity.UserStats;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity.SynsetAttributesHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity.SynsetHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity.SynsetRelationHistory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonCollectors;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static javax.json.Json.createObjectBuilder;

@Singleton
public class TrackerEntityBuilder {

    @Inject
    LocalisedStringCommandService stringCommandService;

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
            builder.add("part_of_speech", sense.getPartOfSpeech().getId());
        }

        if (Objects.nonNull(sense.getStatus())) {
            builder.add("status", sense.getStatus().getName());
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
                .add("part_of_speech", sense.getPartOfSpeech().getId())
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
            builder.add("part_of_speech", senseHistory.getPartOfSpeech().getId());
        }

        if (Objects.nonNull(senseHistory.getStatus())) {
            builder.add("status", senseHistory.getStatus().getName());
        }

        if (Objects.nonNull(senseHistory.getDomain())) {
            builder.add("domain", senseHistory.getDomain().getId());
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
                .stream().map(this::buildSenseHistory)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("pages", pages)
                .add("page", page)
                .add("has_next", hasNext)
                .add("has_prev", hasPrev)
                .add("senses", array)
                .build();
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
                .stream().map(this::buildSynsetHistory)
                .collect(JsonCollectors.toJsonArray());

        return Json.createObjectBuilder()
                .add("pages", pages)
                .add("page", page)
                .add("has_next", hasNext)
                .add("has_prev", hasPrev)
                .add("synsets", array)
                .build();
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
}
