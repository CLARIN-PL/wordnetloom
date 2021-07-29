package pl.edu.pwr.wordnetloom.server.business.relationtype.boundary;

import pl.edu.pwr.wordnetloom.server.business.OperationResult;
import pl.edu.pwr.wordnetloom.server.business.dictionary.control.DictionaryQueryService;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.PartOfSpeech;
import pl.edu.pwr.wordnetloom.server.business.graph.entity.NodeDirection;
import pl.edu.pwr.wordnetloom.server.business.lexicon.control.LexiconQueryService;
import pl.edu.pwr.wordnetloom.server.business.lexicon.entity.Lexicon;
import pl.edu.pwr.wordnetloom.server.business.localistaion.control.LocalisedStringCommandService;
import pl.edu.pwr.wordnetloom.server.business.localistaion.control.LocalisedStringsQueryService;
import pl.edu.pwr.wordnetloom.server.business.localistaion.entity.LocalisedString;
import pl.edu.pwr.wordnetloom.server.business.relationtype.control.RelationTypeQueryService;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.GlobalWordnetRelationType;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationArgument;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationTest;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;
import pl.edu.pwr.wordnetloom.server.business.sense.control.SenseQueryService;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.SenseRelation;
import pl.edu.pwr.wordnetloom.server.business.synset.control.SynsetQueryService;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetRelation;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Transactional
@RequestScoped
public class RelationTypeCommandServce {

    @PersistenceContext
    EntityManager em;

    @Inject
    LocalisedStringsQueryService localisedStringsQueryService;

    @Inject
    LocalisedStringCommandService localisedStringCommandService;

    @Inject
    RelationTypeQueryService query;

    @Inject
    DictionaryQueryService dictionaryQueryService;

    @Inject
    LexiconQueryService lexiconQueryService;

    @Inject
    SenseQueryService senseQueryService;

    @Inject
    SynsetQueryService synsetQueryService;

    public OperationResult<RelationType> save(Locale locale, JsonObject json) {
        OperationResult<RelationType> result = new OperationResult<>();

        String lang = localisedStringsQueryService.getDefaultLanguage();
        if (locale != null) {
            lang = locale.getLanguage().substring(0, 2);
        }

        RelationType rt = new RelationType();

        if (!json.isNull("parent_relation")) {
            JsonObject p = json.get("parent_relation").asJsonObject();
            if (!p.isNull("id")) {
                query.findRelationTypeById(UUID.fromString(p.getString("id")))
                        .ifPresent(rt::setParent);

            }
        }

        if (!json.isNull("allowed_parts_of_speech")) {
            JsonArray pos = json.get("allowed_parts_of_speech").asJsonArray();
            Set<PartOfSpeech> poses = new HashSet<>();
            pos.getValuesAs(JsonObject.class)
                    .forEach(d -> {
                        dictionaryQueryService
                                .findPartsOfSpeech(d.getInt("id"))
                                .ifPresent(poses::add);
                    });
            rt.setPartsOfSpeech(poses);
        }

        if (!json.isNull("allowed_lexicons")) {
            JsonArray pos = json.get("allowed_lexicons").asJsonArray();
            Set<Lexicon> lexicons = new HashSet<>();
            pos.getValuesAs(JsonObject.class)
                    .forEach(d -> {
                        lexiconQueryService
                                .findById(d.getInt("id"))
                                .ifPresent(lexicons::add);
                    });
            rt.setLexicons(lexicons);
        }

        String name = "";
        if (!json.isNull("name")) {
            name = json.getString("name");
        }
        LocalisedString nameLoc = new LocalisedString(null, lang, name);
        localisedStringCommandService.save(nameLoc);
        rt.setName(nameLoc.getKey().getId());

        String dsc = "";
        if (!json.isNull("description")) {
            dsc = json.getString("description");
        }
        LocalisedString dscLoc = new LocalisedString(null, lang, dsc);
        localisedStringCommandService.save(dscLoc);
        rt.setDescription(dscLoc.getKey().getId());

        String shortName = "";
        if (!json.isNull("short_name")) {
            shortName = json.getString("short_name");
        }
        LocalisedString shortLoc = new LocalisedString(null, lang, shortName);
        localisedStringCommandService.save(shortLoc);
        rt.setShortDisplayText(shortLoc.getKey().getId());

        String disp = "";
        if (!json.isNull("display")) {
            disp = json.getString("display");
        }
        LocalisedString dispLoc = new LocalisedString(null, lang, disp);
        localisedStringCommandService.save(dispLoc);
        rt.setDisplayText(dispLoc.getKey().getId());

        if (!json.isNull("color")) {
            rt.setColor(json.getString("color"));
        } else {
            rt.setColor("#FFFFFF");
        }

        if (!json.isNull("direction")) {
            rt.setNodePosition(NodeDirection.valueOf(json.getString("direction")));
        } else {
            rt.setNodePosition(NodeDirection.IGNORE);
        }

        if (!json.isNull("global_wordnet_type")) {
            rt.setGlobalWordnetRelationType(GlobalWordnetRelationType
                    .valueOf(json.getString("global_wordnet_type")));
        } else {
            rt.setGlobalWordnetRelationType(GlobalWordnetRelationType.other);
        }

        if (!json.isNull("argument")) {
            rt.setRelationArgument(RelationArgument.valueOf(json.getString("argument")));
        } else {
            rt.setRelationArgument(RelationArgument.SYNSET_RELATION);
        }

        if (!json.isNull("multilingual")) {
            rt.setMultilingual(json.getBoolean("multilingual"));
        }

        if (!json.isNull("auto_reverse")) {
            rt.setAutoReverse(json.getBoolean("auto_reverse"));
        }

        if (!result.hasErrors()) {
            em.persist(rt);
            result.setEntity(rt);
            localisedStringsQueryService.reload();
        }
        return result;
    }

    public OperationResult<RelationType> update(UUID id, Locale locale, JsonObject json) {
        OperationResult<RelationType> result = new OperationResult<>();
        String lang = localisedStringsQueryService.getDefaultLanguage();
        if (locale != null) {
            lang = locale.getLanguage().substring(0, 2);
        }
        Optional<RelationType> rt = query.findRelationTypeById(id);

        if (rt.isPresent()) {
            if (!json.isNull("parent_relation")) {
                JsonObject p = json.get("parent_relation").asJsonObject();
                if (!p.isNull("id")) {
                    query.findRelationTypeById(UUID.fromString(p.getString("id")))
                            .ifPresent(par -> rt.get().setParent(par));

                }
            } else {
                rt.get().setParent(null);
            }

            if (!json.isNull("reverse_relation")) {
                JsonObject r = json.get("reverse_relation").asJsonObject();
                if (!r.isNull("id")) {
                    query.findRelationTypeById(UUID.fromString(r.getString("id")))
                            .ifPresent(par -> rt.get().setReverse(par));
                }
            } else {
                rt.get().setReverse(null);
            }

            if (!json.isNull("allowed_parts_of_speech")) {
                JsonArray pos = json.get("allowed_parts_of_speech").asJsonArray();
                Set<PartOfSpeech> poses = new HashSet<>();
                pos.getValuesAs(JsonObject.class)
                        .forEach(d -> {
                            dictionaryQueryService
                                    .findPartsOfSpeech(d.getInt("id"))
                                    .ifPresent(poses::add);
                        });
                rt.get().setPartsOfSpeech(poses);
            }

            if (!json.isNull("allowed_lexicons")) {
                JsonArray pos = json.get("allowed_lexicons").asJsonArray();
                Set<Lexicon> lexicons = new HashSet<>();
                pos.getValuesAs(JsonObject.class)
                        .forEach(d -> {
                            lexiconQueryService
                                    .findById(d.getInt("id"))
                                    .ifPresent(lexicons::add);
                        });
                rt.get().setLexicons(lexicons);
            }

            String name = "";
            if (!json.isNull("name")) {
                name = json.getString("name");
            }
            Optional<LocalisedString> locNam = localisedStringsQueryService.findById(rt.get().getName(), lang);
            if (locNam.isPresent()) {
                locNam.get().setValue(name);
                localisedStringCommandService.save(locNam.get());
            }

            String dsc = "";
            if (!json.isNull("description")) {
                dsc = json.getString("description");
            }
            Optional<LocalisedString> dscNam = localisedStringsQueryService.findById(rt.get().getDescription(), lang);
            if (dscNam.isPresent()) {
                dscNam.get().setValue(dsc);
                localisedStringCommandService.save(dscNam.get());
            }

            String shortName = "";
            if (!json.isNull("short_name")) {
                shortName = json.getString("short_name");
            }
            Optional<LocalisedString> shortNam = localisedStringsQueryService.findById(rt.get().getShortDisplayText(), lang);
            if (shortNam.isPresent()) {
                shortNam.get().setValue(shortName);
                localisedStringCommandService.save(shortNam.get());
            }

            String disp = "";
            if (!json.isNull("display")) {
                disp = json.getString("display");
            }
            Optional<LocalisedString> dispNam = localisedStringsQueryService.findById(rt.get().getDisplayText(), lang);
            if (dispNam.isPresent()) {
                dispNam.get().setValue(disp);
                localisedStringCommandService.save(dispNam.get());
            }

            if (!json.isNull("color")) {
                rt.get().setColor(json.getString("color"));
            } else {
                rt.get().setColor("#FFFFFF");
            }

            if (!json.isNull("direction")) {
                rt.get().setNodePosition(NodeDirection.valueOf(json.getString("direction")));
            } else {
                rt.get().setNodePosition(NodeDirection.IGNORE);
            }

            if (!json.isNull("global_wordnet_type")) {
                rt.get().setGlobalWordnetRelationType(GlobalWordnetRelationType
                        .valueOf(json.getString("global_wordnet_type")));
            }

            if (!json.isNull("argument")) {
                rt.get().setRelationArgument(RelationArgument.valueOf(json.getString("argument")));
            }

            if (!json.isNull("multilingual")) {
                rt.get().setMultilingual(json.getBoolean("multilingual"));
            }

            if (!json.isNull("auto_reverse")) {
                rt.get().setAutoReverse(json.getBoolean("auto_reverse"));
            }
            if (!result.hasErrors()) {
                em.merge(rt.get());
                result.setEntity(rt.get());
                localisedStringsQueryService.reload();
            }
        }
        return result;
    }

    public void deleteRelationType(UUID id) {

        RelationType rt = em.find(RelationType.class, id);
        if (rt != null) {
            Long name = rt.getName();
            Long desc = rt.getDescription();
            Long display = rt.getDisplayText();
            Long shortName = rt.getShortDisplayText();
            rt.setLexicons(new HashSet<>());
            rt.setPartsOfSpeech(new HashSet<>());

            List<RelationType> parent = query.findAllByParent(rt.getId());
            List<RelationType> reverse = query.findAllByReverse(rt.getId());
            parent.forEach(p -> {
                p.setParent(null);
                em.merge(p);
            });
            reverse.forEach(r -> {
                r.setReverse(null);
                em.merge(r);
            });

            if (rt.getRelationArgument().equals(RelationArgument.SENSE_RELATION)) {
                List<SenseRelation> relations = senseQueryService.findAllSenseRelationsById(rt.getId());
                relations.forEach(r -> em.remove(r));
            } else if (rt.getRelationArgument().equals(RelationArgument.SYNSET_RELATION)) {
                List<SynsetRelation> relations = synsetQueryService.findAllSynsetRelationsById(rt.getId());
                relations.forEach(r -> em.remove(r));
            }

            em.remove(rt);

            localisedStringCommandService.remove(name);
            localisedStringCommandService.remove(desc);
            localisedStringCommandService.remove(display);
            localisedStringCommandService.remove(shortName);
        }
    }

    public OperationResult<RelationTest> saveTest(UUID relId, JsonObject json) {
        OperationResult<RelationTest> result = new OperationResult<>();

        Optional<RelationType> relationType = query.findRelationTypeById(relId);
        RelationTest t = new RelationTest();
        if (relationType.isPresent()) {
            Integer position = query.findRelationTestNextPositionById(relationType.get().getId());
            t.setPosition(position);
            t.setRelationType(relationType.get());
            if (!json.isNull("test")) {
                t.setTest(json.getString("test"));
            }

            if (!json.isNull("sense_a_pos")) {
                dictionaryQueryService.findPartsOfSpeech(json.getInt("sense_a_pos"))
                        .ifPresent(t::setSenseApartOfSpeech);

            }
            if (!json.isNull("sense_b_pos")) {
                dictionaryQueryService.findPartsOfSpeech(json.getInt("sense_b_pos"))
                        .ifPresent(t::setSenseBpartOfSpeech);
            }
            if (!result.hasErrors()) {
                em.persist(t);
                result.setEntity(t);
            }
        } else {
            result.addError("relation_type", "Relation type doesn't exists");
        }
        return result;
    }

    public OperationResult<RelationTest> updateTest(UUID id, Long testId, JsonObject json) {
        OperationResult<RelationTest> result = new OperationResult<>();

        Optional<RelationTest> test = query.findRelationTest(id, testId);
        if (test.isPresent()) {
            if (!json.isNull("test")) {
                test.get().setTest(json.getString("test"));
            }

            if (!json.isNull("sense_a_pos")) {
                dictionaryQueryService.findPartsOfSpeech(json.getInt("sense_a_pos"))
                        .ifPresent(test.get()::setSenseApartOfSpeech);

            }
            if (!json.isNull("sense_b_pos")) {
                dictionaryQueryService.findPartsOfSpeech(json.getInt("sense_b_pos"))
                        .ifPresent(test.get()::setSenseBpartOfSpeech);
            }
            if (!result.hasErrors()) {
                em.merge(test.get());
                result.setEntity(test.get());
            }
        } else {
            result.addError("relation_type", "Relation type doesn't exists");
        }
        return result;
    }
}
