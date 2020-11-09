package pl.edu.pwr.wordnetloom.server.business.synset.control;

import pl.edu.pwr.wordnetloom.server.business.SearchFilter;
import pl.edu.pwr.wordnetloom.server.business.sense.control.Specification;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.Sense;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.Synset;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetAttributes;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetExample;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetRelation;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static pl.edu.pwr.wordnetloom.server.business.sense.control.SenseSpecification.*;

public class SynsetSpecification {

    public static Specification<Synset> byFilter(SearchFilter filter, List<Long> userLexicons) {

        return (root, query, cb) -> {

            List<Predicate> criteria = new ArrayList<>();

            if(filter.getSynsetId() != null){
                criteria.add(bySynsetId(filter.getSynsetId()).toPredicate(root,query,cb));
            }
            if (filter.getLexicon() != null) {
                criteria.add(byLexiconId(filter.getLexicon()).toPredicate(root, query, cb));
            }

            if (filter.getStatusId() != null) {
                criteria.add(byStatus(filter.getStatusId()).toPredicate(root, query, cb));
            }

            if (filter.getArtificial() != null) {
                criteria.add(byArtificial(filter.getArtificial()).toPredicate(root, query, cb));
            }

            if (filter.getRelationTypeId() != null) {
                if(filter.getNegateRelationType() != null && filter.getNegateRelationType()) {
                    criteria.addAll(byNotSynsetRelationType(filter.getRelationTypeId(), root, cb));
                }else{
                    criteria.addAll(bySynsetRelationType(filter.getRelationTypeId(), root, cb));
                }
            }

            if (filter.getLemma() != null || filter.getPartOfSpeechId() != null
                    || filter.getDomainId() != null) {
                Predicate sense = filterBySense(filter, root, cb);
                if (sense != null) criteria.add(sense);
            }

            if(filter.getDefinition() != null || filter.getComment() != null
                    || filter.getExample() != null) {
                Predicate attributes = filterSynsetAttributes(filter, root, cb);
                if (attributes != null) criteria.add(attributes);
            }
            if(!userLexicons.isEmpty()) {
                Expression<Long> expression = root.get("lexicon").get("id");
                Predicate userLexiconsPredicate = expression.in(userLexicons);
                criteria.add(userLexiconsPredicate);
            }


            return cb.and(criteria.toArray(new Predicate[0]));
        };
    }

    public static Predicate filterBySense(SearchFilter filter, Root<Synset> root, CriteriaBuilder cb) {


        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Sense> senseRoot = subquery.from(Sense.class);
        subquery.select(senseRoot.get("synset").get("id"));

        List<Predicate> predicates = new ArrayList<>();
        if (filter.getLemma() != null) {
            predicates.add(byLemmaLike(filter.getLemma()).toPredicate(senseRoot, query, cb));
        }

        if (filter.getPartOfSpeechId() != null) {
            predicates.add(byPartOfSpeech(filter.getPartOfSpeechId()).toPredicate(senseRoot, query, cb));
        }

        if (filter.getDomainId() != null) {
            predicates.add(byDomain(filter.getDomainId()).toPredicate(senseRoot, query, cb));
        }

        subquery.where(cb.and(predicates.toArray(new Predicate[0])));
        return cb.in(root.get("id")).value(subquery);
    }

    public static Predicate filterSynsetAttributes(SearchFilter filter, Root<Synset> root, CriteriaBuilder cb) {

        if (filter.getComment() != null || filter.getDefinition() != null
                || filter.getExample() != null) {

            CriteriaQuery<Long> query = cb.createQuery(Long.class);
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<SynsetAttributes> synsetAttributesRoot = subquery.from(SynsetAttributes.class);
            subquery.select(synsetAttributesRoot.get("synset").get("id"));

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getDefinition() != null && !filter.getDefinition().isEmpty()) {
                Predicate definitionPredicate = cb.like(synsetAttributesRoot.get("definition"), "%" + filter.getDefinition() + "%");
                predicates.add(definitionPredicate);
            }

            if (filter.getComment() != null && !filter.getComment().isEmpty()) {
                Predicate commentPredicate = cb.like(synsetAttributesRoot.get("comment"), "%" + filter.getComment() + "%");
                predicates.add(commentPredicate);
            }

            if (filter.getExample() != null) {
                Join<SynsetAttributes, SynsetExample> synsetExampleJoin = synsetAttributesRoot.join("examples");
                Predicate examplePredicate = cb.like(synsetExampleJoin.get("examples"), filter.getExample());
                predicates.add(examplePredicate);
            }

            subquery.where(cb.and(predicates.toArray(new Predicate[0])));
            return cb.in(root.get("id")).value(subquery);
        }
        return null;
    }

    public static List<Predicate> bySynsetRelationType(UUID rel, Root<Synset> root, CriteriaBuilder cb) {
        Join<Synset, SynsetRelation> outgoing = root.join("outgoingRelations", JoinType.LEFT);
        Predicate outgoingRelationsPredicate = cb.equal(outgoing.get("relationType").get("id"), rel);
        return Collections.singletonList(outgoingRelationsPredicate);
    }

    public static List<Predicate> byNotSynsetRelationType(UUID rel, Root<Synset> root, CriteriaBuilder cb) {
        Join<Synset, SynsetRelation> outgoing = root.join("outgoingRelations", JoinType.LEFT);
        Predicate outgoingRelationsPredicate = cb.notEqual(outgoing.get("relationType").get("id"), rel);
        return Collections.singletonList(outgoingRelationsPredicate);
    }

    public static Specification<Synset> byLexiconId(Long id) {
        return (root, query, cb) -> cb.equal(root.get("lexicon").get("id"), id);
    }

    public static Specification<Synset> byArtificial(Boolean art) {
        return (root, query, cb) -> cb.equal(root.get("isAbstract"), art);
    }

    public static Specification<Synset> byStatus(Long statusId) {
        return (root, query, cb) -> cb.equal(root.get("status").get("id"), statusId);
    }

    public static Specification<Synset> bySynsetId(UUID id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }
}
