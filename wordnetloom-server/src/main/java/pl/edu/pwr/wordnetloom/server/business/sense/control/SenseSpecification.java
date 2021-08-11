package pl.edu.pwr.wordnetloom.server.business.sense.control;

import pl.edu.pwr.wordnetloom.server.business.SearchFilter;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.*;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SenseSpecification {

    public static Specification<Sense> byFilter(SearchFilter filter, List<Long> userLexicons) {
        return (root, query, cb) -> {
            List<Predicate> criteria = new ArrayList<>();

            if (filter.getLemma() != null && !filter.getLemma().isEmpty()) {
                criteria.add(byLemmaLike(filter.getLemma()).toPredicate(root, query, cb));
            }

            if (filter.getPartOfSpeechId() != null) {
                criteria.add(byPartOfSpeech(filter.getPartOfSpeechId()).toPredicate(root, query, cb));
            }

            if (filter.getDomainId() != null) {
                criteria.add(byDomain(filter.getDomainId()).toPredicate(root, query, cb));
            }

            if (filter.getLexicon() != null) {
                criteria.add(byLexiconId(filter.getLexicon()).toPredicate(root, query, cb));
            }

            if(filter.getStatusId() != null){
                criteria.add(byStatus(filter.getStatusId()).toPredicate(root, query, cb));
            }

            if(filter.getSenseWithoutSynset() != null && filter.getSenseWithoutSynset()){
                criteria.add(synsetIsNull().toPredicate(root, query, cb));
            }

            if (filter.getRelationTypeId() != null) {
                    criteria.addAll(bySenseRelationType(filter.getRelationTypeId(), root, cb));
            }

            Expression<Long> expression = root.get("lexicon").get("id");
            Predicate userLexiconsPredicate = expression.in(userLexicons);
            criteria.add(userLexiconsPredicate);

            Predicate attributes = filterSenseAttributes(filter, root, cb);
            if (attributes != null) criteria.add(attributes);

            return cb.and(criteria.toArray(new Predicate[0]));
        };
    }

    public static Predicate filterSenseAttributes(SearchFilter filter, Root<Sense> root, CriteriaBuilder cb) {
        if (filter.getRegisterId() != null || filter.getComment() != null || filter.getDefinition() != null
                || filter.getExample() != null || filter.getAspectId() != null) {

            CriteriaQuery<UUID> query = cb.createQuery(UUID.class);
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<SenseAttributes> senseAttributesRoot = subquery.from(SenseAttributes.class);
            subquery.select(senseAttributesRoot.get("sense").get("id"));

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getRegisterId() != null) {
                Predicate registerPredicate = cb.equal(senseAttributesRoot.get("register").get("id"), filter.getRegisterId());
                predicates.add(registerPredicate);
            }

            if (filter.getAspectId() != null) {
                Predicate registerPredicate = cb.equal(senseAttributesRoot.get("aspect").get("id"), filter.getAspectId());
                predicates.add(registerPredicate);
            }

            if (filter.getDefinition() != null && !filter.getDefinition().isEmpty()) {
                Predicate definitionPredicate = cb.like(senseAttributesRoot.get("definition"), "%" + filter.getDefinition() + "%");
                predicates.add(definitionPredicate);
            }

            if (filter.getComment() != null && !filter.getComment().isEmpty()) {
                Predicate commentPredicate = cb.like(senseAttributesRoot.get("comment"), "%" + filter.getComment() + "%");
                predicates.add(commentPredicate);
            }

            if (filter.getExample() != null) {
                Join<SenseAttributes, SenseExample> senseExampleJoin = senseAttributesRoot.join("examples");
                Predicate examplePredicate = cb.like(senseExampleJoin.get("examples"), filter.getExample());
                predicates.add(examplePredicate);
            }

            subquery.where(cb.and(predicates.toArray(new Predicate[0])));
            return cb.in(root.get("id")).value(subquery);
        }
        return null;
    }

    public static List<Predicate> bySenseRelationType(UUID rel, Root<Sense> root, CriteriaBuilder cb) {
        Join<Sense, SenseRelation> outgoing = root.join("outgoingRelations", JoinType.LEFT);
        Predicate outgoingRelationsPredicate = cb.equal(outgoing.get("relationType").get("id"), rel);
        return Collections.singletonList(outgoingRelationsPredicate);
    }

    public static Specification<Sense> byPartOfSpeech(Long posId) {
        return (root, query, cb) -> cb.equal(root.get("partOfSpeech").get("id"), posId);
    }

    public static Specification<Sense> byStatus(Long statusId) {
        return (root, query, cb) -> cb.equal(root.get("status").get("id"), statusId);
    }

    public static Specification<Sense> synsetIsNull() {
        return (root, query, cb) -> cb.isNull(root.get("synset").get("id"));
    }

    public static Specification<Sense> byDomain(Long domainId) {
        return (root, query, cb) -> cb.equal(root.get("domain").get("id"), domainId);
    }

    public static Specification<Sense> byWord(Word word) {
        return (root, query, cb) -> cb.equal(root.get("word"), word);
    }

    public static Specification<Sense> byLemma(String lemma) {
        return (root, query, cb) -> cb.equal(root.get("word").get("word"), lemma);
    }

    public static Specification<Sense> byLemmaLike(String lemma) {
        return (root, query, cb) -> cb.like(root.get("word").get("word"), lemma + "%");
    }

    public static Specification<Sense> byLemmaLikeContains(String lemma) {
        if (lemma == null || lemma.isEmpty()) {
            return byLemmaLike("%");
        } else {
            return byLemmaLike("%" + lemma + "%");
        }
    }

    public static Specification<Sense> byVarinat(Integer variant) {
        return (root, query, cb) -> cb.equal(root.get("variant"), variant);
    }

    public static Specification<Sense> byLexiconId(Long id) {
        return (root, query, cb) -> cb.equal(root.get("lexicon").get("id"), id);
    }
}
