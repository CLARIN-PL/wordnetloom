package pl.edu.pwr.wordnetloom.server.business.sense.boundary;


import pl.edu.pwr.wordnetloom.server.business.OperationResult;
import pl.edu.pwr.wordnetloom.server.business.dictionary.control.DictionaryQueryService;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.*;
import pl.edu.pwr.wordnetloom.server.business.lexicon.control.LexiconQueryService;
import pl.edu.pwr.wordnetloom.server.business.lexicon.entity.Lexicon;
import pl.edu.pwr.wordnetloom.server.business.relationtype.control.RelationTypeQueryService;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;
import pl.edu.pwr.wordnetloom.server.business.sense.control.SenseQueryService;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.*;
import pl.edu.pwr.wordnetloom.server.business.synset.control.SynsetQueryService;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.Synset;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetAttributes;
import pl.edu.pwr.wordnetloom.server.business.user.control.UserFinder;
import pl.edu.pwr.wordnetloom.server.business.user.entity.User;
import pl.edu.pwr.wordnetloom.server.business.yiddish.entity.*;

import javax.ejb.Stateless;
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

@Stateless
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
    UserFinder userFinder;

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

        User user = userFinder.getCurrentUser().orElse(null);
        senseAttributes.setOwner(user);

        Status newStatus = dictionaryQueryService.findStatusDefaultValue();
        ns.setStatus(newStatus);

        if (sense.getBoolean("create_synset")) {
            synset.setStatus(newStatus);
            synsetAttributes.setOwner(user);
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
                    s.setVariant(findNextVariant(s.getWord().getId(), (long) part_of_speech, (long) lexicon));
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

    public OperationResult<YiddishSenseExtension> addYiddishVariant(UUID id, JsonObject json) {
        OperationResult<YiddishSenseExtension> result = new OperationResult<>();

        Optional<Sense> s = senseQueryService.findById(id);

        if (s.isPresent()) {
            YiddishSenseExtension yse = new YiddishSenseExtension();
            yse.setSense(s.get());
            if (!json.isNull("variant_type")) {
                yse.setVariant(VariantType.valueOf(json.getString("variant_type")));
            } else {
                result.addError("variant_type", "Variant type may not be empty");
            }

            if (!json.isNull("latin_spelling")) {
                yse.setLatinSpelling(json.getString("latin_spelling"));
            } else {
                yse.setLatinSpelling(null);
            }

            if (!json.isNull("yiddish_spelling")) {
                yse.setYiddishSpelling(json.getString("yiddish_spelling"));
            } else {
                yse.setYiddishSpelling(null);
            }

            if (!json.isNull("yivo_spelling")) {
                yse.setYivoSpelling(json.getString("yivo_spelling"));
            } else {
                yse.setYivoSpelling(null);
            }

            if (!json.isNull("meaning")) {
                yse.setMeaning(json.getString("meaning"));
            } else {
                yse.setMeaning(null);
            }

            if (!json.isNull("etymological_root")) {
                yse.setEtymologicalRoot(json.getString("etymological_root"));
            } else {
                yse.setEtymologicalRoot(null);
            }

            if (!json.isNull("comment")) {
                yse.setComment(json.getString("comment"));
            } else {
                yse.setComment(null);
            }

            if (!json.isNull("context")) {
                yse.setContext(json.getString("context"));
            } else {
                yse.setContext(null);
            }

            if (!json.isNull("etymology")) {
                yse.setEtymology(json.getString("etymology"));
            } else {
                yse.setEtymology(null);
            }

            if (!json.isNull("age")) {
                dictionaryQueryService.findDictionaryById(json.getJsonObject("age").getInt("id"))
                        .filter(d -> d instanceof AgeDictionary)
                        .map(d -> (AgeDictionary) d)
                        .ifPresent(yse::setAge);
            } else {
                yse.setAge(null);
            }

            if (!json.isNull("grammatical_gender")) {
                dictionaryQueryService.findDictionaryById(json.getJsonObject("grammatical_gender").getInt("id"))
                        .filter(d -> d instanceof GrammaticalGenderDictionary)
                        .map(d -> (GrammaticalGenderDictionary) d)
                        .ifPresent(yse::setGrammaticalGender);
            } else {
                yse.setGrammaticalGender(null);
            }

            if (!json.isNull("lexical_characteristic")) {
                dictionaryQueryService.findDictionaryById(json.getJsonObject("lexical_characteristic").getInt("id"))
                        .filter(d -> d instanceof LexicalCharacteristicDictionary)
                        .map(d -> (LexicalCharacteristicDictionary) d)
                        .ifPresent(yse::setLexicalCharacteristic);
            } else {
                yse.setLexicalCharacteristic(null);
            }

            if (!json.isNull("status")) {
                dictionaryQueryService.findDictionaryById(json.getJsonObject("status").getInt("id"))
                        .filter(d -> d instanceof StatusDictionary)
                        .map(d -> (StatusDictionary) d)
                        .ifPresent(yse::setStatus);
            } else {
                yse.setStatus(null);
            }

            if (!json.isNull("style")) {
                dictionaryQueryService.findDictionaryById(json.getJsonObject("style").getInt("id"))
                        .filter(d -> d instanceof StyleDictionary)
                        .map(d -> (StyleDictionary) d)
                        .ifPresent(yse::setStyle);
            } else {
                yse.setStyle(null);
            }

            if (!json.isNull("sources")) {
                json.getJsonArray("sources")
                        .forEach(src -> {
                            JsonObject j = src.asJsonObject();

                            dictionaryQueryService.findDictionaryById(j.getInt("id"))
                                    .filter(d -> d instanceof SourceDictionary)
                                    .map(d -> (SourceDictionary) d)
                                    .ifPresent(d -> yse.getSource().add(d));
                        });
            }

            if (!json.isNull("semantic_fields")) {
                json.getJsonArray("semantic_fields")
                        .forEach(src -> {
                            YiddishDomain domain = new YiddishDomain();
                            domain.setSenseExtension(yse);
                            JsonObject j = src.asJsonObject();
                            if (!j.isNull("domain")) {
                                dictionaryQueryService.findDictionaryById(j.getJsonObject("domain").getInt("id"))
                                        .filter(d -> d instanceof DomainDictionary)
                                        .map(d -> (DomainDictionary) d)
                                        .ifPresent(domain::setDomain);
                            }
                            if (!j.isNull("modifier")) {
                                dictionaryQueryService.findDictionaryById(j.getJsonObject("modifier").getInt("id"))
                                        .filter(d -> d instanceof DomainModifierDictionary)
                                        .map(d -> (DomainModifierDictionary) d)
                                        .ifPresent(domain::setModifier);
                            }
                            yse.getYiddishDomains().add(domain);
                        });
            }

            if (!json.isNull("transcriptions")) {
                json.getJsonArray("transcriptions")
                        .forEach(trs -> {
                            Transcription t = new Transcription();
                            JsonObject j = trs.asJsonObject();
                            if (!j.isNull("transcription")) {
                                dictionaryQueryService.findDictionaryById(j.getJsonObject("transcription").getInt("id"))
                                        .filter(d -> d instanceof TranscriptionDictionary)
                                        .map(d -> (TranscriptionDictionary) d)
                                        .ifPresent(t::setTranscriptionDictionary);
                            }
                            if (!j.isNull("phonography")) {
                                t.setPhonography(j.getString("phonography"));
                            }
                            yse.getTranscriptions().add(t);
                        });
            }

            if (!json.isNull("inflections")) {
                json.getJsonArray("inflections")
                        .forEach(trs -> {
                            Inflection i = new Inflection();
                            JsonObject j = trs.asJsonObject();
                            if (!j.isNull("inflection")) {
                                dictionaryQueryService.findDictionaryById(j.getJsonObject("inflection").getInt("id"))
                                        .filter(d -> d instanceof InflectionDictionary)
                                        .map(d -> (InflectionDictionary) d)
                                        .ifPresent(i::setInflectionDictionary);
                            }
                            if (!j.isNull("text")) {
                                i.setText(j.getString("text"));
                            }
                            yse.getInflection().add(i);
                        });
            }

            if (!json.isNull("particles")) {
                AtomicInteger pos = new AtomicInteger(0);
                json.getJsonArray("particles")
                        .forEach(part -> {
                            JsonObject j = part.asJsonObject();
                            if (!j.isNull("particle")) {
                                String type = j.getJsonObject("particle").getString("type");
                                String value = j.getJsonObject("particle").getString("value");
                                Integer dicId = null;
                                if (!j.getJsonObject("particle").isNull("id")) {
                                    dicId = j.getJsonObject("particle").getInt("id");
                                }
                                if (type.equals("root")) {
                                    RootParticle rp = new RootParticle();
                                    rp.setExtension(yse);
                                    rp.setPosition(pos.getAndIncrement());
                                    rp.setRoot(value);
                                    yse.getParticles().add(rp);
                                }
                                if (type.equals("constituent")) {
                                    ConstituentParticle cp = new ConstituentParticle();
                                    cp.setConstituent(value);
                                    cp.setExtension(yse);
                                    cp.setPosition(pos.getAndIncrement());
                                    yse.getParticles().add(cp);
                                }
                                if (type.equals("prefix")) {
                                    PrefixParticle pp = new PrefixParticle();
                                    pp.setExtension(yse);
                                    pp.setPosition(pos.getAndIncrement());
                                    dictionaryQueryService.findDictionaryById(dicId)
                                            .filter(d -> d instanceof PrefixDictionary)
                                            .map(d -> (PrefixDictionary) d)
                                            .ifPresent(pp::setPrefix);
                                    yse.getParticles().add(pp);
                                }
                                if (type.equals("suffix")) {
                                    SuffixParticle sp = new SuffixParticle();
                                    sp.setExtension(yse);
                                    sp.setPosition(pos.getAndIncrement());
                                    dictionaryQueryService.findDictionaryById(dicId)
                                            .filter(d -> d instanceof SuffixDictionary)
                                            .map(d -> (SuffixDictionary) d)
                                            .ifPresent(sp::setSuffix);
                                    yse.getParticles().add(sp);
                                }
                                if (type.equals("interfix")) {
                                    InterfixParticle ip = new InterfixParticle();
                                    ip.setExtension(yse);
                                    ip.setPosition(pos.getAndIncrement());
                                    dictionaryQueryService.findDictionaryById(dicId)
                                            .filter(d -> d instanceof InterfixDictionary)
                                            .map(d -> (InterfixDictionary) d)
                                            .ifPresent(ip::setInterfix);
                                    yse.getParticles().add(ip);
                                }
                            }
                        });
            }

            if (!result.hasErrors()) {
                em.persist(yse);
                result.setEntity(yse);
            }
        }

        return result;
    }

    public OperationResult<YiddishSenseExtension> updateYiddishVariant(UUID id, Long variantId, JsonObject json) {
        OperationResult<YiddishSenseExtension> result = new OperationResult<>();
        Optional<YiddishSenseExtension> yse = senseQueryService.findYiddishVariant(id, variantId);
        if (yse.isPresent()) {
            if (!json.isNull("variant_type")) {
                yse.get().setVariant(VariantType.valueOf(json.getString("variant_type")));
            } else {
                result.addError("variant_type", "Variant type may not be empty");
            }

            if (!json.isNull("latin_spelling")) {
                yse.get().setLatinSpelling(json.getString("latin_spelling"));
            } else {
                yse.get().setLatinSpelling(null);
            }

            if (!json.isNull("yiddish_spelling")) {
                yse.get().setYiddishSpelling(json.getString("yiddish_spelling"));
            } else {
                yse.get().setYiddishSpelling(null);
            }

            if (!json.isNull("yivo_spelling")) {
                yse.get().setYivoSpelling(json.getString("yivo_spelling"));
            } else {
                yse.get().setYivoSpelling(null);
            }

            if (!json.isNull("meaning")) {
                yse.get().setMeaning(json.getString("meaning"));
            } else {
                yse.get().setMeaning(null);
            }

            if (!json.isNull("etymological_root")) {
                yse.get().setEtymologicalRoot(json.getString("etymological_root"));
            } else {
                yse.get().setEtymologicalRoot(null);
            }

            if (!json.isNull("comment")) {
                yse.get().setComment(json.getString("comment"));
            } else {
                yse.get().setComment(null);
            }

            if (!json.isNull("context")) {
                yse.get().setContext(json.getString("context"));
            } else {
                yse.get().setContext(null);
            }

            if (!json.isNull("etymology")) {
                yse.get().setEtymology(json.getString("etymology"));
            } else {
                yse.get().setEtymology(null);
            }

            if (!json.isNull("age")) {
                dictionaryQueryService.findDictionaryById(json.getJsonObject("age").getInt("id"))
                        .filter(d -> d instanceof AgeDictionary)
                        .map(d -> (AgeDictionary) d)
                        .ifPresent(d -> yse.get().setAge(d));
            } else {
                yse.get().setAge(null);
            }

            if (!json.isNull("grammatical_gender")) {
                dictionaryQueryService.findDictionaryById(json.getJsonObject("grammatical_gender").getInt("id"))
                        .filter(d -> d instanceof GrammaticalGenderDictionary)
                        .map(d -> (GrammaticalGenderDictionary) d)
                        .ifPresent(d -> yse.get().setGrammaticalGender(d));
            } else {
                yse.get().setGrammaticalGender(null);
            }

            if (!json.isNull("lexical_characteristic")) {
                dictionaryQueryService.findDictionaryById(json.getJsonObject("lexical_characteristic").getInt("id"))
                        .filter(d -> d instanceof LexicalCharacteristicDictionary)
                        .map(d -> (LexicalCharacteristicDictionary) d)
                        .ifPresent(d -> yse.get().setLexicalCharacteristic(d));
            } else {
                yse.get().setLexicalCharacteristic(null);
            }

            if (!json.isNull("status")) {
                dictionaryQueryService.findDictionaryById(json.getJsonObject("status").getInt("id"))
                        .filter(d -> d instanceof StatusDictionary)
                        .map(d -> (StatusDictionary) d)
                        .ifPresent(d -> yse.get().setStatus(d));
            } else {
                yse.get().setStatus(null);
            }

            if (!json.isNull("style")) {
                dictionaryQueryService.findDictionaryById(json.getJsonObject("style").getInt("id"))
                        .filter(d -> d instanceof StyleDictionary)
                        .map(d -> (StyleDictionary) d)
                        .ifPresent(d -> yse.get().setStyle(d));
            } else {
                yse.get().setStyle(null);
            }

            if (!json.isNull("sources")) {
                Set<SourceDictionary> sd = new HashSet<>();
                json.getJsonArray("sources")
                        .forEach(src -> {
                            JsonObject j = src.asJsonObject();

                            dictionaryQueryService.findDictionaryById(j.getInt("id"))
                                    .filter(d -> d instanceof SourceDictionary)
                                    .map(d -> (SourceDictionary) d)
                                    .ifPresent(sd::add);
                        });
                yse.get().setSource(sd);
            }

            if (!json.isNull("semantic_fields")) {
                Set<YiddishDomain> dom = new HashSet<>();
                json.getJsonArray("semantic_fields")
                        .forEach(src -> {
                            YiddishDomain domain = new YiddishDomain();
                            domain.setSenseExtension(yse.get());
                            JsonObject j = src.asJsonObject();
                            if (!j.isNull("domain")) {
                                dictionaryQueryService.findDictionaryById(j.getJsonObject("domain").getInt("id"))
                                        .filter(d -> d instanceof DomainDictionary)
                                        .map(d -> (DomainDictionary) d)
                                        .ifPresent(domain::setDomain);
                            }
                            if (!j.isNull("modifier")) {
                                dictionaryQueryService.findDictionaryById(j.getJsonObject("modifier").getInt("id"))
                                        .filter(d -> d instanceof DomainModifierDictionary)
                                        .map(d -> (DomainModifierDictionary) d)
                                        .ifPresent(domain::setModifier);
                            }
                            dom.add(domain);
                        });
                yse.get().setYiddishDomains(dom);
            }

            if (!json.isNull("transcriptions")) {
                Set<Transcription> tr = new HashSet<>();
                json.getJsonArray("transcriptions")
                        .forEach(trs -> {
                            Transcription t = new Transcription();
                            JsonObject j = trs.asJsonObject();
                            if (!j.isNull("transcription")) {
                                dictionaryQueryService.findDictionaryById(j.getJsonObject("transcription").getInt("id"))
                                        .filter(d -> d instanceof TranscriptionDictionary)
                                        .map(d -> (TranscriptionDictionary) d)
                                        .ifPresent(t::setTranscriptionDictionary);
                            }
                            if (!j.isNull("phonography")) {
                                t.setPhonography(j.getString("phonography"));
                            }
                            tr.add(t);
                        });
                yse.get().setTranscriptions(tr);
            }

            if (!json.isNull("inflections")) {
                Set<Inflection> inf = new HashSet<>();
                json.getJsonArray("inflections")
                        .forEach(trs -> {
                            Inflection i = new Inflection();
                            JsonObject j = trs.asJsonObject();
                            if (!j.isNull("inflection")) {
                                dictionaryQueryService.findDictionaryById(j.getJsonObject("inflection").getInt("id"))
                                        .filter(d -> d instanceof InflectionDictionary)
                                        .map(d -> (InflectionDictionary) d)
                                        .ifPresent(i::setInflectionDictionary);
                            }
                            if (!j.isNull("text")) {
                                i.setText(j.getString("text"));
                            }
                           inf.add(i);
                        });
                yse.get().setInflection(inf);
            }

            if (!json.isNull("particles")) {
                AtomicInteger pos = new AtomicInteger(0);
                Set<Particle> pa = new HashSet<>();
                json.getJsonArray("particles")
                        .forEach(part -> {
                            JsonObject j = part.asJsonObject();
                            if (!j.isNull("particle")) {
                                String type = j.getJsonObject("particle").getString("type");
                                String value = j.getJsonObject("particle").getString("value");
                                Integer dicId = null;
                                if (!j.getJsonObject("particle").isNull("id")) {
                                    dicId = j.getJsonObject("particle").getInt("id");
                                }
                                if (type.equals("root")) {
                                    RootParticle rp = new RootParticle();
                                    rp.setExtension(yse.get());
                                    rp.setPosition(pos.getAndIncrement());
                                    rp.setRoot(value);
                                    pa.add(rp);
                                }
                                if (type.equals("constituent")) {
                                    ConstituentParticle cp = new ConstituentParticle();
                                    cp.setConstituent(value);
                                    cp.setExtension(yse.get());
                                    cp.setPosition(pos.getAndIncrement());
                                    pa.add(cp);
                                }
                                if(dicId != null) {
                                    if (type.equals("prefix")) {
                                        PrefixParticle pp = new PrefixParticle();
                                        pp.setExtension(yse.get());
                                        pp.setPosition(pos.getAndIncrement());
                                        dictionaryQueryService.findDictionaryById(dicId)
                                                .filter(d -> d instanceof PrefixDictionary)
                                                .map(d -> (PrefixDictionary) d)
                                                .ifPresent(pp::setPrefix);
                                        pa.add(pp);
                                    }
                                    if (type.equals("suffix")) {
                                        SuffixParticle sp = new SuffixParticle();
                                        sp.setExtension(yse.get());
                                        sp.setPosition(pos.getAndIncrement());
                                        dictionaryQueryService.findDictionaryById(dicId)
                                                .filter(d -> d instanceof SuffixDictionary)
                                                .map(d -> (SuffixDictionary) d)
                                                .ifPresent(sp::setSuffix);
                                        pa.add(sp);
                                    }
                                    if (type.equals("interfix")) {
                                        InterfixParticle ip = new InterfixParticle();
                                        ip.setExtension(yse.get());
                                        ip.setPosition(pos.getAndIncrement());
                                        dictionaryQueryService.findDictionaryById(dicId)
                                                .filter(d -> d instanceof InterfixDictionary)
                                                .map(d -> (InterfixDictionary) d)
                                                .ifPresent(ip::setInterfix);
                                        pa.add(ip);
                                    }
                                }
                            }
                        });
                yse.get().getParticles().forEach(em::remove);
                yse.get().setParticles(pa);
            }else {
                result.addError("Yiddish property","Entity not found");
            }

            if (!result.hasErrors()) {
                em.merge(yse.get());
                result.setEntity(yse.get());
            }

        }
        return result;
    }

    public void deleteYiddishVariant(UUID id, Long variantId) {

        YiddishSenseExtension ye = senseQueryService.findYiddishVariant(id, variantId).get();
        ye.setSource(null);
        em.remove(ye);
    }
}
