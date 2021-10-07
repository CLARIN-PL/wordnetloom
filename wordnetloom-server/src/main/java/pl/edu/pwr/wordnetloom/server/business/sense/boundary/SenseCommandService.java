package pl.edu.pwr.wordnetloom.server.business.sense.boundary;


import pl.edu.pwr.wordnetloom.server.business.OperationResult;
import pl.edu.pwr.wordnetloom.server.business.dictionary.control.DictionaryQueryService;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.PartOfSpeech;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Register;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Status;
import pl.edu.pwr.wordnetloom.server.business.lexicon.control.LexiconQueryService;
import pl.edu.pwr.wordnetloom.server.business.lexicon.entity.Lexicon;
import pl.edu.pwr.wordnetloom.server.business.relationtype.control.RelationTypeQueryService;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;
import pl.edu.pwr.wordnetloom.server.business.sense.control.SenseQueryService;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.*;
import pl.edu.pwr.wordnetloom.server.business.synset.control.SynsetQueryService;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.Synset;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetAttributes;
import pl.edu.pwr.wordnetloom.server.business.user.control.UserControl;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Transactional
@RequestScoped
public class SenseCommandService {

    @PersistenceContext
    EntityManager em;

    @Inject
    DictionaryQueryService dictionaryQueryService;

    @Inject
    LexiconQueryService lexiconQueryService;

    @Inject
    WordCommandService wordCommandService;

    @Inject
    SenseQueryService senseQueryService;

    @Inject
    SynsetQueryService synsetQueryService;

    @Inject
    RelationTypeQueryService relationTypeQueryService;

    @Inject
    UserControl userControl;

    public OperationResult<Sense> save(JsonObject sense) {

        OperationResult<Sense> result = new OperationResult<>();

        Sense ns = new Sense();
        SenseAttributes senseAttributes = new SenseAttributes();
        Synset synset = new Synset();
        SynsetAttributes synsetAttributes = new SynsetAttributes();

        if (!sense.isNull("lemma") && !sense.getString("lemma").isEmpty()) {
            Word word = wordCommandService.save(sense.getString("lemma"));
            ns.setWord(word);
        } else {
            result.addError("lemma", "Lemma may not be empty");
        }

        if (!sense.isNull("lexicon")) {
            Optional<Lexicon> lex = lexiconQueryService.findById(sense.getInt("lexicon"));
            lex.ifPresent(lexicon -> {
                ns.setLexicon(lexicon);
                synset.setLexicon(lexicon);
            });
        } else {
            result.addError("lexicon", "Lexicon may not be empty");
        }

        if (!sense.isNull("part_of_speech")) {
            dictionaryQueryService.findPartsOfSpeech(sense.getInt("part_of_speech"))
                    .ifPresent(ns::setPartOfSpeech);
        } else {
            result.addError("part_of_speech", "Part of speech may not be empty");
        }

        if (!sense.isNull("domain")) {
            dictionaryQueryService.findDomain(sense.getInt("domain"))
                    .ifPresent(ns::setDomain);
        } else {
            result.addError("domain", "Domain may not be empty");
        }

        if (!sense.isNull("register")) {
            dictionaryQueryService.findDictionaryById(sense.getInt("register"))
                    .filter(r -> r instanceof Register)
                    .map(r -> (Register) r)
                    .ifPresent(senseAttributes::setRegister);
        }

        if (!sense.isNull("definition") && !sense.getString("definition").isEmpty()) {
            senseAttributes.setDefinition(sense.getString("definition"));
        }

        if (!sense.isNull("comment") && !sense.getString("comment").isEmpty()) {
            senseAttributes.setComment(sense.getString("comment"));
        }

        if (!sense.isNull("link") && !sense.getString("link").isEmpty()) {
            senseAttributes.setLink(sense.getString("link"));
        }

        if (!sense.isNull("technical_comment") && !sense.getString("technical_comment").isEmpty()) {
            senseAttributes.setErrorComment(sense.getString("technical_comment"));
        }

        Set<SenseExample> examples = new HashSet<>();
        if (!sense.isNull("examples")) {
            JsonArray jsonArray = sense.getJsonArray("examples");
            for (JsonValue j : jsonArray) {
                JsonObject obj = j.asJsonObject();
                SenseExample example = new SenseExample();
                example.setExample(obj.getString("example"));
                if (!obj.isNull("type")) {
                    example.setType(obj.getString("type"));
                } else {
                    example.setType("P");
                }
                examples.add(example);
            }
        }

        if (ns.getPartOfSpeech() != null && ns.getWord() != null && ns.getLexicon() != null) {
            ns.setVariant(findNextVariant(ns.getWord().getId(), ns.getPartOfSpeech().getId(), ns.getLexicon().getId()));
        }

        if (result.hasErrors()) {
            wordCommandService.remove(ns.getWord().getId());
            return result;
        }

        Status newStatus = dictionaryQueryService.findStatusDefaultValue();
        ns.setStatus(newStatus);

        senseAttributes.setUserName(userControl.getUserName());

        if (sense.getBoolean("create_synset")) {
            synset.setStatus(newStatus);
            synsetAttributes.setUserName(userControl.getUserName());
            em.persist(synset);
            synsetAttributes.setSynset(synset);
            em.persist(synsetAttributes);
            ns.setSynset(synset);
        }

        em.persist(ns);
        senseAttributes.setSense(ns);

        em.persist(senseAttributes);
        examples.forEach(e -> {
            e.setSenseAttributes(senseAttributes);
            em.persist(e);
        });

        result.setEntity(ns);

        return result;
    }

    public OperationResult<Sense> update(JsonObject sense) {
        OperationResult<Sense> result = new OperationResult<>();

        if (!sense.isNull("id")) {
            UUID id = UUID.fromString(sense.getString("id"));
            Optional<Sense> ous = senseQueryService.findById(id);
            ous.ifPresent(s -> {
                boolean hasWordChanged = false;
                AtomicBoolean hasPartOfSpeechChanged = new AtomicBoolean(false);
                AtomicBoolean hasLexiconChanged = new AtomicBoolean(false);

                if (!sense.isNull("lemma") && !sense.getString("lemma").isEmpty()) {
                    hasWordChanged = !ous.get().getWord().getWord().equals(sense.getString("lemma"));
                    Word word = wordCommandService.save(sense.getString("lemma"));
                    s.setWord(word);
                } else {
                    result.addError("lemma", "Lemma may not be empty");
                }

                // checking the lexicon has changed
                if (!sense.isNull("lexicon")) {
                    Optional<Lexicon> lex = lexiconQueryService.findById(sense.getInt("lexicon"));
                    lex.ifPresent(l -> {
                        hasLexiconChanged.set(ous.get().getLexicon().getId() != l.getId());
                    });
                } else {
                    result.addError("lexicon", "Lexicon may not be empty");
                }

                // checking the part of speech has changed
                if (!sense.isNull("part_of_speech")) {
                    dictionaryQueryService.findPartsOfSpeech(sense.getInt("part_of_speech"))
                            .ifPresent(p -> {
                                hasPartOfSpeechChanged.set(ous.get().getPartOfSpeech().getId() != p.getId());
                            });
                } else {
                    result.addError("part_of_speech", "Part of speech may not be empty");
                }

                int lexicon = sense.getInt("lexicon");
                int part_of_speech = sense.getInt("part_of_speech");

                if (hasLexiconChanged.get() || hasPartOfSpeechChanged.get() || hasWordChanged) {
                    s.setVariant(findNextVariant(s.getWord().getId(), (long)part_of_speech, (long)lexicon));
                }

                // set lexicon
                if (!sense.isNull("lexicon")) {
                    Optional<Lexicon> lex = lexiconQueryService.findById(sense.getInt("lexicon"));
                    lex.ifPresent(s::setLexicon);
                }

                // set part of speech
                if (!sense.isNull("part_of_speech")) {
                    dictionaryQueryService.findPartsOfSpeech(sense.getInt("part_of_speech"))
                            .ifPresent(s::setPartOfSpeech);
                }

                if (!sense.isNull("domain")) {
                    dictionaryQueryService.findDomain(sense.getInt("domain"))
                            .ifPresent(s::setDomain);
                } else {
                    result.addError("domain", "Domain may not be empty");
                }

                if (!sense.isNull("register")) {
                    dictionaryQueryService.findDictionaryById(sense.getInt("register"))
                            .filter(r -> r instanceof Register)
                            .map(r -> (Register) r)
                            .ifPresent(s.getAttributes()::setRegister);
                } else {
                    s.getAttributes().setRegister(null);
                }

                if (!sense.isNull("definition") && !sense.getString("definition").isEmpty()) {
                    s.getAttributes().setDefinition(sense.getString("definition"));
                } else {
                    s.getAttributes().setDefinition(null);
                }

                if (!sense.isNull("comment") && !sense.getString("comment").isEmpty()) {
                    s.getAttributes().setComment(sense.getString("comment"));
                } else {
                    s.getAttributes().setComment(null);
                }

                if (!sense.isNull("link") && !sense.getString("link").isEmpty()) {
                    s.getAttributes().setLink(sense.getString("link"));
                } else {
                    s.getAttributes().setLink(null);
                }

                if (!sense.isNull("technical_comment") && !sense.getString("technical_comment").isEmpty()) {
                    s.getAttributes().setErrorComment(sense.getString("technical_comment"));
                } else {
                    s.getAttributes().setErrorComment(null);
                }

                if (!sense.isNull("status")) {
                    dictionaryQueryService.findDictionaryById(sense.getInt("status"))
                            .filter(st -> st instanceof Status)
                            .map(st -> (Status) st)
                            .ifPresent(s::setStatus);
                }

                if (!result.hasErrors()) {
                    em.merge(s);
                    result.setEntity(s);
                }
            });
        }
        return result;
    }

    public OperationResult<Sense> attachToSynset(UUID senseId, UUID synsetId) {
        OperationResult<Sense> result = new OperationResult<>();
        Optional<Sense> sense = senseQueryService.findById(senseId);
        if (sense.isPresent()) {
            Optional<Synset> synset = synsetQueryService.findById(synsetId);
            if (synset.isPresent()) {
                sense.get().setSynset(synset.get());
                sense.get().setSynsetPosition(senseQueryService.countSenseBySynsetId(synset.get().getId()).intValue());
            } else {
                result.addError("Synset", "Synset id:" + senseId + " doesn't exists");
            }
        } else {
            result.addError("Sense", "Sense id:" + senseId + " doesn't exists");
        }
        if (!result.hasErrors() && sense.isPresent()) {
            Sense se = em.merge(sense.get());
            result.setEntity(se);
        }

        return result;
    }

    public OperationResult<SenseExample> addExample(UUID senseId, JsonObject example) {
        OperationResult<SenseExample> result = new OperationResult<>();

        SenseExample se = new SenseExample();

        if (!example.isNull("example") && !example.getString("example").isEmpty()) {
            se.setExample(example.getString("example"));
        } else {
            result.addError("example", "Example may not be empty");
        }

        if (!example.isNull("type")) {
            se.setType(example.getString("type"));
        } else {
            result.addError("type", "Type may not be empty");
        }

        Optional<SenseAttributes> sa = senseQueryService.findSenseAttributes(senseId);
        if (sa.isPresent()) {
            se.setSenseAttributes(sa.get());
        } else {
            result.addError("sense", "Sense id:" + senseId + " doesn't exists");
        }

        if (!result.hasErrors()) {
            em.persist(se);
            result.setEntity(se);
        }
        return result;
    }

    public OperationResult<SenseRelation> addSenseRelation(JsonObject relation) {
        OperationResult<SenseRelation> result = new OperationResult<>();

        SenseRelation re = new SenseRelation();
        Optional<Sense> parent = Optional.empty();
        Optional<Sense> child = Optional.empty();

        if (!relation.isNull("source") && !relation.getString("source").isEmpty()) {
            parent = senseQueryService.findById(UUID.fromString(relation.getString("source")));
            if (parent.isPresent()) {
                re.setParent(parent.get());
            } else {
                result.addError("source", "Sense not found");
            }
        } else {
            result.addError("source", "Source sense may not be empty");
        }

        if (!relation.isNull("target") && !relation.getString("target").isEmpty()) {
            child = senseQueryService.findById(UUID.fromString(relation.getString("target")));
            if (child.isPresent()) {
                re.setChild(child.get());
            } else {
                result.addError("target", "Sense not found");
            }
        } else {
            result.addError("target", "Target sense may not be empty");
        }

        SenseRelation reverse = new SenseRelation();
        if (!relation.isNull("relation_type") && !relation.getString("relation_type").isEmpty()) {
            Optional<RelationType> type = relationTypeQueryService.findRelationTypeById(UUID.fromString(relation.getString("relation_type")));
            if (type.isPresent()) {
                re.setRelationType(type.get());

                if (type.get().getAutoReverse() && type.get().getReverse() != null) {
                    reverse.setRelationType(type.get().getReverse());
                    child.ifPresent(reverse::setParent);
                    parent.ifPresent(reverse::setChild);
                } else {
                    reverse = null;
                }
                Set<Lexicon> allowedLexicons = type.get().getLexicons();
                if (!allowedLexicons.isEmpty()) {
                    boolean allowedParentLexicon = allowedLexicons.contains(parent.get().getLexicon());
                    boolean allowedChildLexicon = allowedLexicons.contains(child.get().getLexicon());
                    if (!allowedParentLexicon || !allowedChildLexicon) {
                        result.addError("allowed_lexicon", "Sense lexicon not allowed for this relation type");
                    } else {
                        if (!type.get().getMultilingual()) {
                            if (!parent.get().getLexicon().equals(child.get().getLexicon())) {
                                result.addError("allowed_lexicon", "Relation is not multilingual both sense must be in same lexicon");
                            }
                        } else {
                            if (!parent.get().getLexicon().equals(child.get().getLexicon())) {
                                result.addError("allowed_lexicon", "Relation is multilingual both senses must be part of different lexicons");
                            }
                        }
                    }
                }
                Set<PartOfSpeech> allowedPos = type.get().getPartsOfSpeech();
                if (!allowedPos.isEmpty()) {
                    boolean allowedParentPos = allowedPos.contains(parent.get().getPartOfSpeech());

                    boolean allowedChildPos = allowedPos.contains(child.get().getPartOfSpeech());

                    if (!allowedParentPos || !allowedChildPos) {
                        result.addError("allowed_part_of_speech", "Sense part of speech is not allowed for this relation type");
                    }
                }

            } else {
                result.addError("relation_type", "Relation type not found");
            }
        } else {
            result.addError("relation_type", "May not be empty");
        }


        if (!result.hasErrors()) {
            em.persist(re);
            if (reverse != null) {
                em.persist(reverse);
            }
            result.setEntity(re);
        }
        return result;
    }

    public OperationResult<SenseExample> updateExample(JsonObject example) {
        OperationResult<SenseExample> result = new OperationResult<>();
        if (!example.isNull("id")) {
            UUID id = UUID.fromString(example.getString("id"));
            Optional<SenseExample> oe = senseQueryService.findSenseExample(id);

            if (oe.isPresent()) {
                if (!example.isNull("example") && !example.getString("example").isEmpty()) {
                    oe.get().setExample(example.getString("example"));
                } else {
                    result.addError("example", "May not be empty");
                }

                if (!example.isNull("type")) {
                    oe.get().setType(example.getString("type"));
                } else {
                    result.addError("type", "May not be empty");
                }
                if (!result.hasErrors()) {
                    em.merge(oe.get());
                    result.setEntity(oe.get());
                }
            }
        }
        return result;
    }

    private int findNextVariant(UUID wordId, Long posId, Long lex) {
        try {
            Optional<Integer> variant = Optional.ofNullable(em.createNamedQuery(Sense.FIND_NEXT_VARIANT, Integer.class)
                    .setParameter("wordId", wordId)
                    .setParameter("lex", lex)
                    .setParameter("posId", posId).getSingleResult());
            return Math.max(0, variant.orElse(0)) + 1;
        } catch (NoResultException | NoSuchElementException ex) {
            return 1;
        }
    }

    public void deleteExample(UUID exampleId) {
        senseQueryService.findSenseExample(exampleId)
                .ifPresent(e -> em.remove(e));
    }

    public void moveSenseUp(UUID id) {
        Optional<Sense> s = senseQueryService.findById(id);
        s.ifPresent(sense -> {
            int current = sense.getSynsetPosition();
            if (current > 0) {
                Optional<Sense> upper = senseQueryService.findSenseBySynsetIdAndPosition(sense.getSynset().getId(), current - 1);
                upper.ifPresent(up -> {
                    sense.setSynsetPosition(current - 1);
                    up.setSynsetPosition(current);
                    em.merge(sense);
                    em.merge(up);
                });
            }
        });
    }

    public void moveSenseDown(UUID id) {
        Optional<Sense> s = senseQueryService.findById(id);
        s.ifPresent(sense -> {
            int current = sense.getSynsetPosition();
            if (current >= 0) {
                Optional<Sense> lower = senseQueryService.findSenseBySynsetIdAndPosition(sense.getSynset().getId(), current + 1);
                if (lower.isPresent()) {
                    sense.setSynsetPosition(current + 1);
                    lower.get().setSynsetPosition(current);
                    em.merge(sense);
                    em.merge(lower.get());
                }
            }
        });
    }

    public void detachFromSynset(UUID id) {
        Optional<Sense> sense = senseQueryService.findById(id);
        if (sense.isPresent() && sense.get().getSynset() != null) {
            Optional<Synset> synset = synsetQueryService.findById(sense.get().getSynset().getId());
            if (synset.isPresent()) {
                synset.get().getSenses().remove(sense.get());
                if (synset.get().getSenses().size() == 0) {
                    em.remove(synset.get());
                } else {
                    AtomicInteger counter = new AtomicInteger(0);
                    synset.get().getSenses()
                            .forEach(z -> {
                                z.setSynsetPosition(counter.getAndIncrement());
                                em.merge(z);
                            });
                }
                sense.get().setSynset(null);
                sense.get().setSynsetPosition(0);
                em.merge(sense.get());
            }
        }

    }

    public void deleteSense(UUID id) {
        senseQueryService.findById(id)
                .ifPresent(s -> {
                    if (s.getSynset() == null) {
                        em.remove(s);
                        return;
                    }
                    Optional<Synset> synset = synsetQueryService.findById(s.getSynset().getId());
                    if (synset.isPresent()) {
                        synset.get().getSenses().remove(s);
                        if (synset.get().getSenses().size() == 0) {
                            em.remove(s);
                            em.remove(synset.get());
                        } else {
                            AtomicInteger counter = new AtomicInteger(0);
                            synset.get().getSenses()
                                    .forEach(z -> {
                                        z.setSynsetPosition(counter.getAndIncrement());
                                        em.merge(z);
                                    });
                            em.remove(s);
                        }
                    }

                });
    }

    public void deleteRelation(UUID source, UUID target, UUID relationType) {
        senseQueryService.findSenseRelation(source, target, relationType)
                .ifPresent(e -> em.remove(e));
    }

    private List<SenseValuation> getSenseValuations(UUID annotationId) {
        return em.createNamedQuery(SenseValuation.FIND_BY_EMOTIONAL_ID, SenseValuation.class)
                .setParameter("id", annotationId)
                .getResultList();
    }

    private List<SenseEmotion> getSenseEmotions(UUID annotationId) {
        return em.createNamedQuery(SenseEmotion.FIND_BY_EMOTIONAL_ID, SenseEmotion.class)
                .setParameter("id", annotationId)
                .getResultList();
    }

    private void checkAndAddEmotionalAttributes(JsonObject annotation, EmotionalAnnotation emotionalAnnotation) {
        if (!annotation.isNull("super_annotation"))
            emotionalAnnotation.setSuperAnnotation(annotation.getBoolean("super_annotation"));

        if (!annotation.isNull("emotional_characteristic"))
            emotionalAnnotation.setEmotionalCharacteristic(annotation.getBoolean("emotional_characteristic"));

        if (!annotation.isNull("example1"))
            emotionalAnnotation.setExample1(annotation.getString("example1"));

        if (!annotation.isNull("example2"))
            emotionalAnnotation.setExample2(annotation.getString("example2"));

        if (!annotation.isNull("markedness")) {
            long markednessId = annotation.getInt("markedness");
            dictionaryQueryService.findMarkedness(markednessId).ifPresent(emotionalAnnotation::setMarkedness);
        }
    }

    public OperationResult<EmotionalAnnotation> saveEmotionalAnnotation(JsonObject annotation) {
        OperationResult<EmotionalAnnotation> result = new OperationResult<>();

        if (!annotation.isNull("sense_id")) {
            UUID senseId = UUID.fromString(annotation.getString("sense_id"));
            Optional<Sense> opt = senseQueryService.findHeadSense(senseId);

            if (opt.isPresent()) {
                EmotionalAnnotation emotionalAnnotation = new EmotionalAnnotation();
                emotionalAnnotation.setSense(opt.get());
                emotionalAnnotation.setOwner(userControl.getUserName());
                checkAndAddEmotionalAttributes(annotation, emotionalAnnotation);

                if (!result.hasErrors()) {
                    em.persist(emotionalAnnotation);
                    EmotionalAnnotation dbEmotionalAnnotation = senseQueryService.findEmotionalAnnotationBySenseId(senseId).get();

                    if (!annotation.isNull("emotions"))
                        addNewEmotions(annotation, dbEmotionalAnnotation);

                    if (!annotation.isNull("valuations"))
                        addNewValuations(annotation, dbEmotionalAnnotation);
                }

                result.setEntity(emotionalAnnotation);
            } else {
                result.addError("sense", "Cannot find sense");
            }
        } else {
            result.addError("sense_id", "Sense id cannot be null");
        }

        return result;
    }

    private void addNewValuations(JsonObject annotation, EmotionalAnnotation dbEmotionalAnnotation) {
        for (int i = 0; i < annotation.getJsonArray("valuations").size(); i++) {
            long valuationId = annotation.getJsonArray("valuations").getInt(i);
            dictionaryQueryService.findValuation(valuationId).ifPresent(
                    v -> em.persist(new SenseValuation(dbEmotionalAnnotation, v)));
        }
    }

    private void addNewEmotions(JsonObject annotation, EmotionalAnnotation dbEmotionalAnnotation) {
        for (int i = 0; i < annotation.getJsonArray("emotions").size(); i++) {
            long emotionId = annotation.getJsonArray("emotions").getInt(i);
            dictionaryQueryService.findEmotion(emotionId).ifPresent(
                    v -> em.persist(new SenseEmotion(dbEmotionalAnnotation, v)));
        }
    }

    public OperationResult<EmotionalAnnotation> updateEmotionalAnnotation(JsonObject annotation) {
        OperationResult<EmotionalAnnotation> result = new OperationResult<>();

        if (!annotation.isNull("sense_id")) {
            UUID senseId = UUID.fromString(annotation.getString("sense_id"));
            Optional<Sense> opt = senseQueryService.findHeadSense(senseId);

            if (opt.isPresent()) {
                EmotionalAnnotation emotionalAnnotation = senseQueryService.findEmotionalAnnotationBySenseId(senseId).get();
                emotionalAnnotation.setSense(opt.get());
                checkAndAddEmotionalAttributes(annotation, emotionalAnnotation);

                if (!annotation.isNull("emotions"))
                    checkAndAddOrDeleteEmotions(annotation, emotionalAnnotation);

                if (!annotation.isNull("valuations"))
                    checkAndAddOrDeleteValuations(annotation, emotionalAnnotation);

                if (!result.hasErrors())
                    em.merge(emotionalAnnotation);

                result.setEntity(emotionalAnnotation);
            } else {
                result.addError("sense", "Cannot find sense");
            }
        } else {
            result.addError("sense_id", "Sense id cannot be null");
        }

        return result;
    }

    private void checkAndAddOrDeleteValuations(JsonObject annotation, EmotionalAnnotation emotionalAnnotation) {
        List<Long> valuationIdList = new LinkedList<>();
        for (int i = 0; i < annotation.getJsonArray("valuations").size(); i++)
            valuationIdList.add((long) annotation.getJsonArray("valuations").getInt(i));

        List<SenseValuation> dbValuations = getSenseValuations(emotionalAnnotation.getId());
        for (SenseValuation senseValuation : dbValuations) {
            if (!valuationIdList.contains(senseValuation.getValuation().getId()))
                em.remove(senseValuation);
            else
                valuationIdList.remove(senseValuation.getValuation().getId());
        }

        for (long valuationId : valuationIdList)
            dictionaryQueryService.findValuation(valuationId).ifPresent(
                    v -> em.persist(new SenseValuation(emotionalAnnotation, v)));
    }

    private void checkAndAddOrDeleteEmotions(JsonObject annotation, EmotionalAnnotation emotionalAnnotation) {
        List<Long> emotionIdList = new LinkedList<>();
        for (int i = 0; i < annotation.getJsonArray("emotions").size(); i++)
            emotionIdList.add((long) annotation.getJsonArray("emotions").getInt(i));

        List<SenseEmotion> dbEmotions = getSenseEmotions(emotionalAnnotation.getId());
        for (SenseEmotion senseEmotion : dbEmotions) {
            if (!emotionIdList.contains(senseEmotion.getEmotion().getId()))
                em.remove(senseEmotion);
            else
                emotionIdList.remove(senseEmotion.getEmotion().getId());
        }

        for (long emotionId : emotionIdList)
            dictionaryQueryService.findEmotion(emotionId).ifPresent(
                    v -> em.persist(new SenseEmotion(emotionalAnnotation, v)));
    }
}
