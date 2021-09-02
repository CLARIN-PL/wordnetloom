package pl.edu.pwr.wordnetloom.server.business.sense.control;

import pl.edu.pwr.wordnetloom.server.business.SearchFilter;
import pl.edu.pwr.wordnetloom.server.business.lexicon.control.LexiconQueryService;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.*;
import pl.edu.pwr.wordnetloom.server.business.user.control.UserControl;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@RequestScoped
public class SenseQueryService {

    @PersistenceContext
    EntityManager em;

    @Inject
    UserControl userControl;

    @Inject
    LexiconQueryService lexiconQueryService;

    public List<Sense> findByFilter(SearchFilter filter) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Sense> qc = cb.createQuery(Sense.class);

        Root<Sense> sense = qc.from(Sense.class);
        sense.fetch("domain");
        sense.fetch("partOfSpeech");
        sense.fetch("lexicon");
        sense.fetch("synset", JoinType.LEFT);
        sense.fetch("word");

        List<Predicate> predicates = new ArrayList<>();

        List<Long> lexicons  = userControl.getCurrentUser()
                .map( u -> u.getSettings().getSelectedLexicons())
                .orElse(lexiconQueryService.findLexiconIdsAll());

        predicates.add(SenseSpecification.byFilter(filter, lexicons).toPredicate(sense, qc, cb));

        List<Order> orders = new ArrayList<>();
        orders.add(cb.asc(sense.get("word").get("word")));
        orders.add(cb.asc(sense.get("partOfSpeech").get("id")));
        orders.add(cb.asc(sense.get("variant")));
        orders.add(cb.asc(sense.get("lexicon").get("id")));

        qc.select(sense);
        qc.distinct(true);
        qc.orderBy(orders);
        qc.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Sense> q = em.createQuery(qc);

        return q.setFirstResult(filter.getPaginationData().getStart())
                .setMaxResults(filter.getPaginationData().getLimit())
                .getResultList();
    }

    public long countWithFilter(SearchFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<Sense> sense = cq.from(Sense.class);

        List<Predicate> predicates = new ArrayList<>();
        List<Long> lexicons  = userControl.getCurrentUser()
                .map( u -> u.getSettings().getSelectedLexicons())
                .orElse(lexiconQueryService.findLexiconIdsAll());

        predicates.add(SenseSpecification.byFilter(filter, lexicons).toPredicate(sense, cq, cb));

        cq.select(cb.count(sense.get("id")));
        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getSingleResult();
    }

    public Optional<SenseRelation> findSenseRelation(UUID source, UUID target, UUID relationType) {
        try {
            return Optional.of(
                    em.createNamedQuery(SenseRelation.FIND_BY_KEY, SenseRelation.class)
                            .setParameter("source", source)
                            .setParameter("target", target)
                            .setParameter("relationType", relationType)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Sense> findById(UUID id) {
        try {
            return Optional.of(
                    em.createNamedQuery(Sense.FIND_BY_ID_WITH_ATTRIBUTES, Sense.class)
                            .setParameter("id", id)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Sense> findHeadSense(UUID id) {
        try {
            return Optional.of(
                    em.createNamedQuery(Sense.FIND_BY_UUID_WITH_WORD_AND_DOMAIN, Sense.class)
                            .setParameter("id", id)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Sense> findByIdWithRelations(UUID id) {
        try {
            return Optional.of(
                    em.createNamedQuery(Sense.FIND_BY_ID_WITH_RELATIONS_AND_DOMAINS, Sense.class)
                            .setParameter("id", id)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<SenseAttributes> findSenseAttributes(UUID id) {
        try {

            SenseAttributes sa = em.createNamedQuery(SenseAttributes.FIND_BY_ID, SenseAttributes.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(sa);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<SenseExample> findSenseExample(UUID id) {
        try {
            return Optional.of(
                    em.createNamedQuery(SenseExample.FIND_BY_ID, SenseExample.class)
                            .setParameter("id", id)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

     public Long countByWordId(UUID id) {
        return em.createNamedQuery(Sense.COUNT_SENSE_BY_WORD_ID, Long.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public Long countSenseBySynsetId(UUID id) {
        return em.createNamedQuery(Sense.COUNT_SENSES_BY_SYNSET_ID, Long.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public Optional<Sense> findSenseBySynsetIdAndPosition(UUID id, Integer position) {
        try {
            return Optional.of(
                    em.createNamedQuery(Sense.FIND_SENSE_BY_SYNSET_ID_AND_POSITION, Sense.class)
                            .setParameter("id", id)
                            .setParameter("position", position)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Sense> findSensesBySynsetId(UUID synsetId) {
        return em.createNamedQuery(Sense.FIND_BY_SYNSET_ID, Sense.class)
                .setParameter("id", synsetId)
                .getResultList();
    }

    public List<SenseRelation> findAllSenseRelationsById(UUID relationType) {
        return  em.createNamedQuery(SenseRelation.FIND_SENSE_RELATIONS_BY_TYPE, SenseRelation.class)
                .setParameter("relTypeId", relationType)
                .getResultList();
    }

    public List<SenseRelation> findAllIncomingRelationsById(UUID senseId) {
        return em.createNamedQuery(SenseRelation.FIND_SENSE_INCOMING_RELATIONS, SenseRelation.class)
                .setParameter("senseId", senseId)
                .getResultList();
    }

    public List<SenseRelation> findAllOutgoingRelationsById(UUID senseId) {
        return em.createNamedQuery(SenseRelation.FIND_SENSE_OUTGOING_RELATIONS, SenseRelation.class)
                .setParameter("senseId", senseId)
                .getResultList();
    }

    public List<SenseRelation> findSenseRelations(UUID source, UUID target, UUID relationType) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SenseRelation> qc = cb.createQuery(SenseRelation.class);

        Root<SenseRelation> rel = qc.from(SenseRelation.class);
        rel.fetch("parent");
        rel.fetch("child");
        rel.fetch("relationType");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(SenseRelationSpecification.byFilter(source,target,relationType)
                .toPredicate(rel, qc, cb));

        qc.select(rel);
        qc.distinct(true);
        qc.where(predicates.toArray(new Predicate[0]));

        TypedQuery<SenseRelation> q = em.createQuery(qc);

        List<SenseRelation> results = q.getResultList();
        results.forEach( sr -> {
            sr.getParent().getWord().getId();
            sr.getParent().getDomain().getId();
            sr.getChild().getWord().getId();
            sr.getChild().getDomain().getId();
        });
        return results;
    }

    public List<RelationType> findAllRelations() {
        return em.createNamedQuery(SenseRelation.FIND_ALL_RELATIONS, RelationType.class)
                .getResultList();
    }
}
