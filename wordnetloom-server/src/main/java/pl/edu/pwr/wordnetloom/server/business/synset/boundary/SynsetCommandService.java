package pl.edu.pwr.wordnetloom.server.business.synset.boundary;

import pl.edu.pwr.wordnetloom.server.business.OperationResult;
import pl.edu.pwr.wordnetloom.server.business.dictionary.control.DictionaryQueryService;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Dictionary;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.PartOfSpeech;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Status;
import pl.edu.pwr.wordnetloom.server.business.lexicon.control.LexiconQueryService;
import pl.edu.pwr.wordnetloom.server.business.lexicon.entity.Lexicon;
import pl.edu.pwr.wordnetloom.server.business.relationtype.control.RelationTypeQueryService;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;
import pl.edu.pwr.wordnetloom.server.business.sense.control.SenseQueryService;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.Sense;
import pl.edu.pwr.wordnetloom.server.business.synset.control.SynsetQueryService;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.Synset;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetAttributes;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetExample;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetRelation;
import pl.edu.pwr.wordnetloom.server.business.user.control.UserControl;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Transactional
@RequestScoped
public class SynsetCommandService {

    @PersistenceContext
    EntityManager em;

    @Inject
    SynsetQueryService synsetQueryService;

    @Inject
    RelationTypeQueryService relationTypeQueryService;

    @Inject
    SenseQueryService senseQueryService;

    @Inject
    DictionaryQueryService dictionaryQueryService;

    @Inject
    UserControl userControl;

    @Inject
    LexiconQueryService lexiconQueryService;

    public OperationResult<Synset> update(UUID id, JsonObject json) {
        OperationResult<Synset> result = new OperationResult<>();
        Optional<Synset> synset = synsetQueryService.findById(id);
        Optional<SynsetAttributes> attributes = Optional.empty();

        if (synset.isPresent()) {
            attributes = synsetQueryService.findSynsetAttributes(id);
            if (attributes.isPresent()) {
                if (!json.isNull("status")) {
                    Optional<Dictionary> newStatus = dictionaryQueryService.findDictionaryById(json.getInt("status"));
                    if (newStatus.isPresent()) {
                        synset.get().setAbstract(json.getBoolean("artificial"));
                    } else {
                        result.addError("status", "Unable to find status");
                    }
                }

                if (!json.isNull("artificial")) {
                    synset.get().setAbstract(json.getBoolean("artificial"));
                }

                if (!json.isNull("lexicon")) {
                    Optional<Lexicon> lex = lexiconQueryService.findById(json.getInt("lexicon"));
                    if (lex.isPresent()) {
                        synset.get().setLexicon(lex.get());
                    } else {
                        result.addError("lexicon", "Selected lexicon doesn't exists");
                    }
                } else {
                    result.addError("lexicon", "May not be empty");
                }

                if (!json.isNull("definition") && !json.getString("definition").isEmpty()) {
                    attributes.get().setDefinition(json.getString("definition"));
                }

                if (!json.isNull("comment") && !json.getString("comment").isEmpty()) {
                    attributes.get().setComment(json.getString("comment"));
                }

                if (!json.isNull("technical_comment") && !json.getString("technical_comment").isEmpty()) {
                    attributes.get().setErrorComment(json.getString("technical_comment"));
                }

                if (!json.isNull("princeton_id") && !json.getString("princeton_id").isEmpty()) {
                    attributes.get().setPrincetonId(json.getString("princeton_id"));
                }
                if (!json.isNull("ili_id") && !json.getString("ili_id").isEmpty()) {
                    attributes.get().setIliId(json.getString("ili_id"));
                }

            } else {
                result.addError("attributes", "Attributes not present");
            }
        } else {
            result.addError("synset", "Synset doesn't exists");
        }

        if (!result.hasErrors()) {
            if (synset.isPresent() && attributes.isPresent()) {
                em.merge(synset.get());
                em.merge(attributes.get());
                result.setEntity(synset.get());
            }
        }
        return result;
    }

    public OperationResult<Synset> save(JsonObject json) {
        OperationResult<Synset> result = new OperationResult<>();

        Synset synset = new Synset();

        Status newStatus = dictionaryQueryService.findStatusDefaultValue();
        synset.setStatus(newStatus);
        if (!json.isNull("artificial")) {
            synset.setAbstract(json.getBoolean("artificial"));
        }

        em.persist(synset);

        SynsetAttributes synsetAttributes = new SynsetAttributes();
        if (!json.isNull("lexicon")) {
            Optional<Lexicon> lex = lexiconQueryService.findById(json.getInt("lexicon"));
            if (lex.isPresent()) {
                synset.setLexicon(lex.get());
            } else {
                result.addError("lexicon", "Selected lexicon doesn't exists");
            }
        } else {
            result.addError("lexicon", "May not be empty");
        }

        if (!json.isNull("definition") && !json.getString("definition").isEmpty()) {
            synsetAttributes.setDefinition(json.getString("definition"));
        }

        if (!json.isNull("comment") && !json.getString("comment").isEmpty()) {
            synsetAttributes.setComment(json.getString("comment"));
        }

        if (!json.isNull("technical_comment") && !json.getString("technical_comment").isEmpty()) {
            synsetAttributes.setErrorComment(json.getString("technical_comment"));
        }

        if (!json.isNull("princeton_id") && !json.getString("princeton_id").isEmpty()) {
            synsetAttributes.setPrincetonId(json.getString("princeton_id"));
        }
        if (!json.isNull("ili_id") && !json.getString("ili_id").isEmpty()) {
            synsetAttributes.setIliId(json.getString("ili_id"));
        }

        synsetAttributes.setSynset(synset);

        if (!result.hasErrors()) {
            em.merge(synset);
            em.merge(synsetAttributes);
            result.setEntity(synset);
        }
        return result;
    }

    public void delete(UUID id) {
        synsetQueryService.findById(id)
                .ifPresent(s -> em.remove(s));
    }

    public OperationResult<Synset> addSenseToNewSynset(UUID senseId) {
        OperationResult<Synset> result = new OperationResult<>();
        Optional<Sense> sense = senseQueryService.findById(senseId);
        Synset synset = new Synset();
        SynsetAttributes attributes = new SynsetAttributes();

        if (sense.isPresent()) {

            Status defaultStatus = dictionaryQueryService.findStatusDefaultValue();
            synset.setStatus(defaultStatus);
            synset.setLexicon(sense.get().getLexicon());

            attributes.setUserName(userControl.getCurrentUser().get().getFullname());

            sense.get().setSynset(synset);
            sense.get().setSynsetPosition(Synset.SYNSET_HEAD_POSITION);

        } else {
            result.addError("sense", "Sense id:" + senseId.toString() + " doesn't exists");
        }
        if (!result.hasErrors() && sense.isPresent()) {
            em.persist(synset);
            attributes.setSynset(synset);
            em.persist(attributes);
            em.merge(sense.get());
            result.setEntity(synset);
        }
        return result;
    }


    public void deleteExample(UUID exampleId) {
        synsetQueryService.findSynsetExample(exampleId)
                .ifPresent(e -> em.remove(e));
    }

    public OperationResult<SynsetExample> addExample(UUID synsetId, JsonObject example) {
        OperationResult<SynsetExample> result = new OperationResult<>();
        SynsetExample se = new SynsetExample();

        if (!example.isNull("example") && !example.getString("example").isEmpty()) {
            se.setExample(example.getString("example"));
        } else {
            result.addError("example", "May not be empty");
        }

        if (!example.isNull("type")) {
            se.setType(example.getString("type"));
        } else {
            result.addError("type", "May not be empty");
        }

        Optional<SynsetAttributes> sa = synsetQueryService.findSynsetAttributes(synsetId);
        if (sa.isPresent()) {
            se.setSynsetAttributes(sa.get());
        } else {
            result.addError("synset", "Synset id:" + synsetId + " doesn't exists");
        }

        if (!result.hasErrors()) {
            em.persist(se);
            result.setEntity(se);
        }
        return result;
    }

    public OperationResult<SynsetExample> updateExample(UUID id, JsonObject example) {
        OperationResult<SynsetExample> result = new OperationResult<>();
        Optional<SynsetExample> oe = synsetQueryService.findSynsetExample(id);
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
        return result;
    }

    public OperationResult<SynsetRelation> addSynsetRelation(JsonObject relation) {

        OperationResult<SynsetRelation> result = new OperationResult<>();

        SynsetRelation re = new SynsetRelation();
        Optional<Synset> parent = Optional.empty();
        Optional<Synset> child = Optional.empty();

        if (!relation.isNull("source") && !relation.getString("source").isEmpty()) {
            parent = synsetQueryService.findById(UUID.fromString(relation.getString("source")));
            if (parent.isPresent()) {
                re.setParent(parent.get());
            } else {
                result.addError("source", "Synset not found");
            }
        } else {
            result.addError("source", "May not be empty");
        }

        if (!relation.isNull("target") && !relation.getString("target").isEmpty()) {
            child = synsetQueryService.findById(UUID.fromString(relation.getString("target")));
            if (child.isPresent()) {
                re.setChild(child.get());
            } else {
                result.addError("target", "Synset not found");
            }
        } else {
            result.addError("target", "May not be empty");
        }


        SynsetRelation reverse = new SynsetRelation();

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
                if(!allowedLexicons.isEmpty()) {
                    boolean allowedParentLexicon = allowedLexicons.contains(parent.get().getLexicon());
                    boolean allowedChildLexicon = allowedLexicons.contains(child.get().getLexicon());
                    if (!allowedParentLexicon || !allowedChildLexicon) {
                        result.addError("allowed_lexicon", "Synset lexicon not allowed for this relation type");
                    } else {
                        if (!type.get().getMultilingual()) {
                            if (!parent.get().getLexicon().equals(child.get().getLexicon())) {
                                result.addError("allowed_lexicon", "Relation is not multilingual both synsets must be in same lexicon");
                            }
                        } else {
                            if (!parent.get().getLexicon().equals(child.get().getLexicon())) {
                                result.addError("allowed_lexicon", "Relation is multilingual both synsets must be part of different lexicons");
                            }
                        }
                    }
                }
                Set<PartOfSpeech> allowedPos = type.get().getPartsOfSpeech();
                if(!allowedPos.isEmpty()) {
                    boolean allowedParentPos = allowedPos.contains(parent.get().getSenses()
                            .stream()
                            .findFirst().get().getPartOfSpeech());

                    boolean allowedChildPos = allowedPos.contains(child.get().getSenses().stream()
                            .findFirst().get().getPartOfSpeech());

                    if (!allowedParentPos || !allowedChildPos) {
                        result.addError("allowed_part_of_speech", "Synset part of speech is not allowed for this relation type");
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

    public void deleteRelation(UUID source, UUID relationType, UUID target) {
        synsetQueryService.findSynsetRelationByKey(source, target, relationType)
                .ifPresent(r -> em.remove(r));
    }
}
