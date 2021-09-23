package pl.edu.pwr.wordnetloom.server.business.tracker.synset.control;

import pl.edu.pwr.wordnetloom.server.business.sense.control.SenseQueryService;
import pl.edu.pwr.wordnetloom.server.business.tracker.BeforeHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.TrackerSearchFilter;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.SenseAttributesHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.SenseHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity.SynsetAttributesHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity.SynsetHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity.SynsetRelationHistory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@RequestScoped
public class SynsetHistoryQueryService {

    @PersistenceContext
    EntityManager em;

    @Inject
    SenseQueryService senseQueryService;

    public List<SynsetHistory> findSynsetHistoryByFilter(TrackerSearchFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SynsetHistory> qc = cb.createQuery(SynsetHistory.class);
        Root<SynsetHistory> synsetHistoryRoot = qc.from(SynsetHistory.class);
        synsetHistoryRoot.fetch("revisionsInfo");

        qc.select(synsetHistoryRoot);
        qc.distinct(true);
        qc.orderBy(synsetHistoryByFilterOrders(synsetHistoryRoot, cb));
        qc.where(synsetHistoryByFilterPredicates(filter, synsetHistoryRoot, cb, qc));

        TypedQuery<SynsetHistory> query = em.createQuery(qc);

        List<SynsetHistory> synsetHistoryList = query.setFirstResult(filter.getStart())
                .setMaxResults(filter.getEnd())
                .getResultList();
        synsetHistoryList.forEach(s -> {
                    s.setSenses(senseQueryService.findSensesBySynsetId(s.getId()));
                    if (s.getRevType() == 1)
                        s.setBeforeHistory(
                                findSynsetHistoryBeforeRev(s.getId(), s.getRevisionsInfo().getId()).map(BeforeHistory::new).orElse(null)
                        );
                });

        return synsetHistoryList;
    }

    public Long countSynsetHistoryByFilter(TrackerSearchFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> qc = cb.createQuery(Long.class);
        Root<SynsetHistory> senseHistoryRoot = qc.from(SynsetHistory.class);

        qc.select(cb.countDistinct(senseHistoryRoot.get("concatKeys")));
        qc.where(synsetHistoryByFilterPredicates(filter, senseHistoryRoot, cb, qc));
        return em.createQuery(qc)
                .getSingleResult();
    }

    private List<Order> synsetHistoryByFilterOrders(Root<SynsetHistory> root, CriteriaBuilder cb) {
        return List.of(
                cb.desc(root.get("revisionsInfo").get("timestamp")),
                cb.asc(root.get("id"))
        );
    }

    private Predicate[] synsetHistoryByFilterPredicates(TrackerSearchFilter filter,
                                                        Root<SynsetHistory> root,
                                                        CriteriaBuilder cb,
                                                        CriteriaQuery<?> qc) {
        return List.of(
                SynsetHistorySpecification.byFilter(filter).toPredicate(root, qc, cb)
        ).toArray(new Predicate[0]);
    }

    public List<SynsetAttributesHistory> findSynsetAttributesHistoryByFilter(TrackerSearchFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SynsetAttributesHistory> qc = cb.createQuery(SynsetAttributesHistory.class);
        Root<SynsetAttributesHistory> synsetAttributesHistoryRoot = qc.from(SynsetAttributesHistory.class);
        synsetAttributesHistoryRoot.fetch("revisionsInfo");

        qc.select(synsetAttributesHistoryRoot);
        qc.distinct(true);
        qc.orderBy(synsetHistoryAttributesByFilterOrders(synsetAttributesHistoryRoot, cb));
        qc.where(synsetHistoryAttributesByFilterPredicates(filter, synsetAttributesHistoryRoot, cb, qc));

        TypedQuery<SynsetAttributesHistory> query = em.createQuery(qc);

        List<SynsetAttributesHistory> synsetAttributesHistoryList = query.setFirstResult(filter.getStart())
                .setMaxResults(filter.getEnd())
                .getResultList();
        synsetAttributesHistoryList.forEach(s -> {
            s.setSenses(senseQueryService.findSensesBySynsetId(s.getId()));
            if (s.getRevType() == 1)
                s.setBeforeHistory(
                        findSynsetAttributesHistoryBeforeRev(s.getId(), s.getRevisionsInfo().getId()).map(BeforeHistory::new).orElse(null)
                );
        });

        return synsetAttributesHistoryList;
    }

    public Long countSynsetAttributesHistoryByFilter(TrackerSearchFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> qc = cb.createQuery(Long.class);
        Root<SynsetAttributesHistory> synsetAttributesHistoryRoot = qc.from(SynsetAttributesHistory.class);

        qc.select(cb.countDistinct(synsetAttributesHistoryRoot.get("concatKeys")));
        qc.where(synsetHistoryAttributesByFilterPredicates(filter, synsetAttributesHistoryRoot, cb, qc));
        return em.createQuery(qc)
                .getSingleResult();
    }

    private List<Order> synsetHistoryAttributesByFilterOrders(Root<SynsetAttributesHistory> root, CriteriaBuilder cb) {
        return List.of(
                cb.desc(root.get("revisionsInfo").get("timestamp")),
                cb.asc(root.get("id"))
        );
    }

    private Predicate[] synsetHistoryAttributesByFilterPredicates(TrackerSearchFilter filter,
                                                                  Root<SynsetAttributesHistory> root,
                                                                  CriteriaBuilder cb,
                                                                  CriteriaQuery<?> qc) {
        return List.of(
                SynsetHistorySpecification.attributesByFilter(filter).toPredicate(root, qc, cb)
        ).toArray(new Predicate[0]);
    }

    public List<SynsetRelationHistory> findSynsetRelationsHistoryByFilter(TrackerSearchFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SynsetRelationHistory> qc = cb.createQuery(SynsetRelationHistory.class);
        Root<SynsetRelationHistory> synsetRelationHistoryRoot = qc.from(SynsetRelationHistory.class);
        synsetRelationHistoryRoot.fetch("revisionsInfo");

        qc.select(synsetRelationHistoryRoot);
        qc.distinct(true);
        qc.orderBy(List.of(
                cb.desc(synsetRelationHistoryRoot.get("revisionsInfo").get("timestamp"))
        ));
        qc.where(synsetRelationsHistoryByFilterPredicates(filter, synsetRelationHistoryRoot, cb, qc));

        TypedQuery<SynsetRelationHistory> query = em.createQuery(qc);

        return query.setFirstResult(filter.getStart())
                .setMaxResults(filter.getEnd())
                .getResultList();
    }

    public Long countSynsetRelationsHistoryByFilter(TrackerSearchFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> qc = cb.createQuery(Long.class);
        Root<SynsetRelationHistory> synsetRelationHistoryRoot = qc.from(SynsetRelationHistory.class);

        qc.select(cb.countDistinct(synsetRelationHistoryRoot.get("concatKeys")));
        qc.where(synsetRelationsHistoryByFilterPredicates(filter, synsetRelationHistoryRoot, cb, qc));

        return em.createQuery(qc)
                .getSingleResult();
    }

    private Predicate[] synsetRelationsHistoryByFilterPredicates(TrackerSearchFilter filter,
                                                                 Root<SynsetRelationHistory> root,
                                                                 CriteriaBuilder cb,
                                                                 CriteriaQuery<?> qc) {
        return List.of(
                SynsetHistorySpecification.relationsByFilter(filter).toPredicate(root, qc, cb)
        ).toArray(new Predicate[0]);
    }

    public List<SynsetHistory> findSynsetHistory(UUID synsetId) {
        List<SynsetHistory> synsetHistories = em.createNamedQuery(SynsetHistory.FIND_BY_ID, SynsetHistory.class)
                .setParameter("id", synsetId)
                .getResultList();

        List<SynsetAttributesHistory> synsetAttributesHistoryList = em.createNamedQuery(
                            SynsetAttributesHistory.FIND_BY_ID, SynsetAttributesHistory.class)
                .setParameter("id", synsetId)
                .getResultList();

        addOrMergeAttributes(synsetHistories, synsetAttributesHistoryList);
        synsetHistories.forEach(s -> s.setSenses(senseQueryService.findSensesBySynsetId(s.getId())));

        return synsetHistories;
    }

    private void addOrMergeAttributes(List<SynsetHistory> synsetHistoryList,
                                      List<SynsetAttributesHistory> synsetAttributesHistoryList) {
        for (SynsetAttributesHistory synsetAttributesHistory : synsetAttributesHistoryList) {
            boolean contains = false;
            for (int senseIter = 0; senseIter < synsetHistoryList.size() && !contains; senseIter++)
                if(synsetHistoryList.get(senseIter)
                        .getRevisionsInfo().getId() == synsetAttributesHistory.getRevisionsInfo().getId()) {
                    contains = true;
                    synsetHistoryList.get(senseIter).setAttributes(synsetAttributesHistory);
                }
            if (!contains)
                synsetHistoryList.add(new SynsetHistory(synsetAttributesHistory));
        }
    }

    public List<SynsetRelationHistory> findSynsetIncomingRelationsHistory(UUID synsetId) {
        return em.createNamedQuery(SynsetRelationHistory.FIND_SYNSET_HISTORY_INCOMING_RELATIONS, SynsetRelationHistory.class)
                .setParameter("synsetId", synsetId)
                .getResultList();
    }

    public List<SynsetRelationHistory> findSynsetOutgoingRelationsHistory(UUID synsetId) {
        return em.createNamedQuery(SynsetRelationHistory.FIND_SYNSET_HISTORY_OUTGOING_RELATIONS, SynsetRelationHistory.class)
                .setParameter("synsetId", synsetId)
                .getResultList();
    }

    public List<SynsetHistory> findSynsetHistoryByTimestamps(long begin, long end) {
        List<SynsetHistory> synsetHistories = em.createNamedQuery(SynsetHistory.FIND_BY_TIMESTAMP, SynsetHistory.class)
                .setParameter("timestamp_start", begin)
                .setParameter("timestamp_end", end)
                .getResultList();

        List<SynsetAttributesHistory> synsetAttributesHistoryList = em.createNamedQuery(
                            SynsetAttributesHistory.FIND_BY_TIMESTAMP, SynsetAttributesHistory.class)
                .setParameter("timestamp_start", begin)
                .setParameter("timestamp_end", end)
                .getResultList();

        addOrMergeAttributes(synsetHistories, synsetAttributesHistoryList);
        synsetHistories.forEach(s -> s.setSenses(senseQueryService.findSensesBySynsetId(s.getId())));

        return synsetHistories;
    }

    public List<SynsetRelationHistory> findSynsetRelationHistoryByTimestamps(long begin, long end) {
        return em.createNamedQuery(SynsetRelationHistory.FIND_BY_TIMESTAMP, SynsetRelationHistory.class)
                .setParameter("timestamp_start", begin)
                .setParameter("timestamp_end", end)
                .getResultList();
    }

    public Optional<SynsetAttributesHistory> findSynsetAttributesHistoryBeforeRev(UUID synsetId, int rev) {
        try {
            return Optional.of(
                    em.createNamedQuery(SynsetAttributesHistory.FIND_BEFORE_REV, SynsetAttributesHistory.class)
                            .setParameter("id", synsetId)
                            .setParameter("rev", rev)
                            .getSingleResult());
        } catch (
                NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<SynsetHistory> findSynsetHistoryBeforeRev(UUID synsetId, int rev) {
        try {
            return Optional.of(
                    em.createNamedQuery(SynsetHistory.FIND_BEFORE_REV, SynsetHistory.class)
                            .setParameter("id", synsetId)
                            .setParameter("rev", rev)
                            .getSingleResult());
        } catch (
                NoResultException e) {
            return Optional.empty();
        }
    }
}
