package pl.edu.pwr.wordnetloom.server.business.synset.control;

import org.hibernate.Hibernate;
import pl.edu.pwr.wordnetloom.server.business.SearchFilter;
import pl.edu.pwr.wordnetloom.server.business.lexicon.control.LexiconQueryService;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.Sense;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.Synset;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetAttributes;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetExample;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetRelation;
import pl.edu.pwr.wordnetloom.server.business.user.control.UserControl;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;


@Transactional
@RequestScoped
public class SynsetQueryService {

    @PersistenceContext
    EntityManager em;

    @Inject
    UserControl userControl;

    @Inject
    LexiconQueryService lexiconQueryService;

    public List<Synset> findByFilter(SearchFilter filter) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Synset> qc = cb.createQuery(Synset.class);

        Root<Synset> synset = qc.from(Synset.class);
        synset.fetch("lexicon");
        synset.fetch("status");

        List<Predicate> predicates = new ArrayList<>();

        List<Long> lexicons = userControl.getCurrentUser()
                .map(u -> u.getSettings().getSelectedLexicons())
                .orElse(lexiconQueryService.findLexiconIdsAll());

        predicates.add(SynsetSpecification.byFilter(filter, lexicons).toPredicate(synset, qc, cb));

        List<Order> orders = new ArrayList<>();
        orders.add(cb.asc(synset.get("lexicon")));

        qc.select(synset);
        qc.distinct(true);
        qc.orderBy(orders);
        qc.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Synset> q = em.createQuery(qc);

        List<Synset> result = q.setFirstResult(filter.getPaginationData().getStart())
                .setMaxResults(filter.getPaginationData().getLimit())
                .getResultList();
        fetchLazyObject(result);
        return result;
    }

    public List<RelationType> findAllRelations() {
        return em.createNamedQuery(SynsetRelation.FIND_ALL_RELATIONS, RelationType.class)
                .getResultList();
    }

    private void fetchLazyObject(List<Synset> result) {
        for (Synset synset : result) {
            synset.getSenses().size();
            for (Sense sense : synset.getSenses()) {
                Hibernate.initialize(sense.getDomain());
                Hibernate.initialize(sense.getWord());
            }
        }
    }

    public List<UUID> getRelatedSynsets(UUID synsetId, UUID relationTypeId){
        return em.createNamedQuery(SynsetRelation.FIND_RELATED_SYNSETS_IDS, UUID.class)
                .setParameter("id",synsetId)
                .setParameter("relation", relationTypeId)
                .getResultList();
    }

    public long countWithFilter(SearchFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<Synset> synset = cq.from(Synset.class);

        List<Predicate> predicates = new ArrayList<>();

        List<Long> lexicons = userControl.getCurrentUser()
                .map(u -> u.getSettings().getSelectedLexicons())
                .orElse(lexiconQueryService.findLexiconIdsAll());

        predicates.add(SynsetSpecification.byFilter(filter, lexicons).toPredicate(synset, cq, cb));

        cq.select(cb.count(synset.get("id")));
        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getSingleResult();
    }

    public Optional<Synset> findById(UUID id) {
        try {
            return Optional.of(
                    em.createNamedQuery(Synset.FIND_BY_ID_WITH_LEXICON_AND_SENSES_WITH_DOMAIN, Synset.class)
                            .setParameter("id", id)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<SynsetRelation> findSynsetRelationsByParentLexicon(Long id) {
        return em.createNamedQuery(SynsetRelation.FIND_BY_PARENT_LEXICON_ID, SynsetRelation.class)
                .setParameter("lexId", id)
                .getResultList();
    }

    public List<SynsetRelation> findAllSynsetRelationsById(UUID relationType) {
        return  em.createNamedQuery(SynsetRelation.FIND_SYNSET_RELATIONS_BY_TYPE, SynsetRelation.class)
                .setParameter("relTypeId", relationType)
                .getResultList();
    }

    public Optional<SynsetAttributes> findSynsetAttributes(UUID id) {
        try {
            return Optional.of(
                    em.createNamedQuery(SynsetAttributes.FIND_BY_ID_WITH_EXAMPLES, SynsetAttributes.class)
                            .setParameter("id", id)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Synset> findSynsetWithAttributesAndIncomingRelations(Long id) {
        try {
            return Optional.of(
                    em.createNamedQuery(Synset.FIND_BY_ID_WITH_EXAMPLES_AND_SYNSET_INCOMING_RELATIONS, Synset.class)
                            .setParameter("id", id)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }


    public List<Synset> findSynsetWithAttributesByLexicon(Long lexId) {
        return em.createNamedQuery(Synset.FIND_BY_LEXICON_WITH_EXAMPLES, Synset.class)
                .setParameter("lexId", lexId)
                .getResultList();
    }


    public Optional<SynsetExample> findSynsetExample(UUID id) {
        try {
            return Optional.of(
                    em.createNamedQuery(SynsetExample.FIND_BY_ID, SynsetExample.class)
                            .setParameter("id", id)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Synset> findSynsetHead(UUID id) {
        try {
            return Optional.of(
                    em.createNamedQuery(Synset.FIND_SYNSET_HEAD, Synset.class)
                            .setParameter("synsetPosition", Synset.SYNSET_HEAD_POSITION)
                            .setParameter("synsetId", id)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<SynsetRelation> findParentSynsetByRelationType(List<Long> relType) {
        return em.createNamedQuery(SynsetRelation.FIND_PARENT_SYNSET_BY_RELATION_TYPE, SynsetRelation.class)
                .setParameter("relTypeId", relType)
                .getResultList();
    }

    public Optional<Map<String, List<Object[]>>> findSynsetRelations(UUID id) {
        try {
            String outgoingQuery = "SELECT " +
                    "BIN_TO_UUID(r1.synset_relation_type_id) AS type, " +
                    "rt1.name_id AS nameId, " +
                    "BIN_TO_UUID(r1.child_synset_id) AS synsetId, " +
                    "concat(w.word,' ',s.variant) AS lemma, " +
                    "dom1.name_id AS domainId " +
                    "FROM tbl_synset_relation r1 " +
                    "LEFT JOIN tbl_relation_type rt1 ON r1.synset_relation_type_id = rt1.id " +
                    "LEFT JOIN tbl_sense s ON s.synset_id = r1.child_synset_id " +
                    "LEFT JOIN tbl_word w ON w.id = s.word_id " +
                    "LEFT JOIN tbl_domain dom1 ON  dom1.id = s.domain_id " +
                    "WHERE r1.parent_synset_id = ?1 AND s.synset_position = ?2 " +
                    "ORDER BY type, lemma";

            String incomingQuery = "SELECT BIN_TO_UUID(r1.synset_relation_type_id) AS type, rt1.name_id AS nameId, " +
                    "BIN_TO_UUID(r1.parent_synset_id), concat(w.word,' ',s.variant) AS lemma, dom1.name_id AS domainId " +
                    "FROM tbl_synset_relation r1 " +
                    "LEFT JOIN tbl_relation_type rt1 ON r1.synset_relation_type_id = rt1.id " +
                    "LEFT JOIN tbl_sense s ON s.synset_id = r1.parent_synset_id " +
                    "LEFT JOIN tbl_word w ON w.id = s.word_id " +
                    "LEFT JOIN tbl_domain dom1 ON  dom1.id = s.domain_id " +
                    "WHERE r1.child_synset_id = ?1 AND s.synset_position = ?2 " +
                    "ORDER BY type, lemma";

            List<Object[]> incoming = em.createNativeQuery(incomingQuery)
                    .setParameter(1, id)
                    .setParameter(2, Synset.SYNSET_HEAD_POSITION)
                    .getResultList();

            List<Object[]> outgoing = em.createNativeQuery(outgoingQuery)
                    .setParameter(1, id)
                    .setParameter(2, Synset.SYNSET_HEAD_POSITION)
                    .getResultList();
            Map<String, List<Object[]>> result = new HashMap<>();
            result.put("incoming", incoming);
            result.put("outgoing", outgoing);
            return Optional.of(result);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<SynsetRelation> findSynsetRelationByKey(UUID source, UUID target, UUID relationType) {
        try {
            return Optional.of(
                    em.createNamedQuery(SynsetRelation.FIND_BY_KEY, SynsetRelation.class)
                            .setParameter("source", source)
                            .setParameter("target", target)
                            .setParameter("relationType", relationType)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<SynsetRelation> findSynsetIncomingRelationsById(UUID synsetId) {
        return em.createNamedQuery(SynsetRelation.FIND_SYNSET_INCOMING_RELATIONS, SynsetRelation.class)
                .setParameter("synsetId", synsetId)
                .getResultList();
    }

    public List<SynsetRelation> findSynsetOutgoingRelationsById(UUID synsetId) {
        return em.createNamedQuery(SynsetRelation.FIND_SYNSET_OUTGOING_RELATIONS, SynsetRelation.class)
                .setParameter("synsetId", synsetId)
                .getResultList();
    }

    public List<SynsetRelation> findSynsetRelations(UUID source, UUID target, UUID relationType) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SynsetRelation> qc = cb.createQuery(SynsetRelation.class);

        Root<SynsetRelation> rel = qc.from(SynsetRelation.class);
        rel.fetch("parent");
        rel.fetch("child");
        rel.fetch("relationType");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(SynsetRelationSpecification.byFilter(source, target, relationType)
                .toPredicate(rel, qc, cb));

        qc.select(rel);
        qc.distinct(true);
        qc.where(predicates.toArray(new Predicate[0]));

        TypedQuery<SynsetRelation> q = em.createQuery(qc);

        return q.getResultList();
    }
}
