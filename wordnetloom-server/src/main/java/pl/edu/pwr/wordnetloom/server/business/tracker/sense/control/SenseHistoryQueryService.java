package pl.edu.pwr.wordnetloom.server.business.tracker.sense.control;

import pl.edu.pwr.wordnetloom.server.business.sense.control.SenseQueryService;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.Word;
import pl.edu.pwr.wordnetloom.server.business.tracker.BeforeHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.TrackerSearchFilter;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.SenseAttributesHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.SenseHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.SenseRelationHistory;

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
import java.util.stream.Collectors;

@Transactional
@RequestScoped
public class SenseHistoryQueryService {

    @PersistenceContext
    EntityManager em;

    @Inject
    SenseQueryService senseQueryService;

    public List<SenseHistory> findSenseHistoryByFilter(TrackerSearchFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SenseHistory> qc = cb.createQuery(SenseHistory.class);
        Root<SenseHistory> senseHistoryRoot = qc.from(SenseHistory.class);
        senseHistoryRoot.fetch("revisionsInfo");
        senseHistoryRoot.fetch("partOfSpeech");

        qc.select(senseHistoryRoot);
        qc.distinct(true);
        qc.orderBy(senseHistoryByFilterOrders(senseHistoryRoot, cb));
        qc.where(senseHistoryByFilterPredicates(filter, senseHistoryRoot, cb, qc));

        TypedQuery<SenseHistory> query = em.createQuery(qc);

        List<SenseHistory> senseHistoryList = query.setFirstResult(filter.getStart())
                .setMaxResults(filter.getEnd())
                .getResultList();
        senseHistoryList.forEach(s -> {
            if (s.getRevType() == 1)
                s.setBeforeHistory(
                        findSenseHistoryBeforeRev(s.getId(), s.getRevisionsInfo().getId()).map(BeforeHistory::new).orElse(null)
                );
        });

        return senseHistoryList;
    }

    public Long countSenseHistoryByFilter(TrackerSearchFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> qc = cb.createQuery(Long.class);
        Root<SenseHistory> senseHistoryRoot = qc.from(SenseHistory.class);

        qc.select(cb.countDistinct(senseHistoryRoot.get("concatKeys")));
        qc.where(senseHistoryByFilterPredicates(filter, senseHistoryRoot, cb, qc));
        return em.createQuery(qc)
                .getSingleResult();
    }

    private List<Order> senseHistoryByFilterOrders(Root<SenseHistory> root, CriteriaBuilder cb) {
        return List.of(
                cb.desc(root.get("revisionsInfo").get("timestamp")),
                cb.asc(root.get("id"))
        );
    }

    private Predicate[] senseHistoryByFilterPredicates(TrackerSearchFilter filter,
                                                       Root<SenseHistory> root,
                                                       CriteriaBuilder cb,
                                                       CriteriaQuery<?> qc) {
        return List.of(
                SenseHistorySpecification.byFilter(filter).toPredicate(root, qc, cb)
        ).toArray(new Predicate[0]);
    }

    public List<SenseAttributesHistory> findSenseAttributesHistoryByFilter(TrackerSearchFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SenseAttributesHistory> qc = cb.createQuery(SenseAttributesHistory.class);
        Root<SenseAttributesHistory> senseAttributesHistoryRoot = qc.from(SenseAttributesHistory.class);
        senseAttributesHistoryRoot.fetch("revisionsInfo");

        qc.select(senseAttributesHistoryRoot);
        qc.distinct(true);
        qc.orderBy(senseHistoryAttributesByFilterOrders(senseAttributesHistoryRoot, cb));
        qc.where(senseHistoryAttributesByFilterPredicates(filter, senseAttributesHistoryRoot, cb, qc));

        TypedQuery<SenseAttributesHistory> query = em.createQuery(qc);

        List<SenseAttributesHistory> senseAttributesHistoryList = query.setFirstResult(filter.getStart())
                .setMaxResults(filter.getEnd())
                .getResultList();

        senseAttributesHistoryList.forEach(s -> {
            s.setLemma(senseQueryService.findSenseWordById(s.getId()).map(Word::getWord).orElse(" "));
            if (s.getRevType() == 1)
                s.setBeforeHistory(
                    findSenseAttributesHistoryBeforeRev(s.getId(), s.getRevisionsInfo().getId()).map(BeforeHistory::new).orElse(null)
                );
        });

        return senseAttributesHistoryList;
    }

    public Long countSenseAttributesHistoryByFilter(TrackerSearchFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> qc = cb.createQuery(Long.class);
        Root<SenseAttributesHistory> senseAttributesHistoryRoot = qc.from(SenseAttributesHistory.class);

        qc.select(cb.countDistinct(senseAttributesHistoryRoot.get("concatKeys")));
        qc.where(senseHistoryAttributesByFilterPredicates(filter, senseAttributesHistoryRoot, cb, qc));
        return em.createQuery(qc)
                .getSingleResult();
    }

    private List<Order> senseHistoryAttributesByFilterOrders(Root<SenseAttributesHistory> root, CriteriaBuilder cb) {
        return List.of(
                cb.desc(root.get("revisionsInfo").get("timestamp")),
                cb.asc(root.get("id"))
        );
    }

    private Predicate[] senseHistoryAttributesByFilterPredicates(TrackerSearchFilter filter,
                                                                 Root<SenseAttributesHistory> root,
                                                                 CriteriaBuilder cb,
                                                                 CriteriaQuery<?> qc) {
        return List.of(
                SenseHistorySpecification.attributesByFilter(filter).toPredicate(root, qc, cb)
        ).toArray(new Predicate[0]);
    }

    public List<SenseRelationHistory> findSenseRelationsByFilter(TrackerSearchFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SenseRelationHistory> qc = cb.createQuery(SenseRelationHistory.class);
        Root<SenseRelationHistory> senseRelationHistoryRoot = qc.from(SenseRelationHistory.class);
        senseRelationHistoryRoot.fetch("revisionsInfo");

        qc.select(senseRelationHistoryRoot);
        qc.distinct(true);
        qc.orderBy(List.of(
                cb.desc(senseRelationHistoryRoot.get("revisionsInfo").get("timestamp"))
        ));
        qc.where(senseRelationsHistoryByFilterPredicates(filter, senseRelationHistoryRoot, cb, qc));

        TypedQuery<SenseRelationHistory> query = em.createQuery(qc);

        return query.setFirstResult(filter.getStart())
                .setMaxResults(filter.getEnd())
                .getResultList();
    }

    public Long countSenseRelationsByFilter(TrackerSearchFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> qc = cb.createQuery(Long.class);
        Root<SenseRelationHistory> senseRelationHistoryRoot = qc.from(SenseRelationHistory.class);

        qc.select(cb.countDistinct(senseRelationHistoryRoot.get("concatKeys")));
        qc.where(senseRelationsHistoryByFilterPredicates(filter, senseRelationHistoryRoot, cb, qc));

        return em.createQuery(qc)
                .getSingleResult();
    }

    private Predicate[] senseRelationsHistoryByFilterPredicates(TrackerSearchFilter filter,
                                                                Root<SenseRelationHistory> root,
                                                                CriteriaBuilder cb,
                                                                CriteriaQuery<?> qc) {
        return List.of(
                SenseHistorySpecification.relationsByFilter(filter).toPredicate(root, qc, cb)
        ).toArray(new Predicate[0]);
    }

    public List<SenseHistory> findSenseHistory(UUID senseId) {
        List<SenseHistory> senseHistoryList = em.createNamedQuery(SenseHistory.FIND_BY_ID, SenseHistory.class)
                .setParameter("id", senseId)
                .getResultList();

        List<SenseAttributesHistory> senseAttributesHistoryList = em.createNamedQuery(
                            SenseAttributesHistory.FIND_BY_ID, SenseAttributesHistory.class)
                .setParameter("id", senseId)
                .getResultList();
        addOrMergeAttributes(senseHistoryList, senseAttributesHistoryList);

        return senseHistoryList;
    }

    private void addOrMergeAttributes(List<SenseHistory> senseHistoryList,
                                      List<SenseAttributesHistory> senseAttributesHistoryList) {
        for (SenseAttributesHistory senseAttributesHistory : senseAttributesHistoryList) {
            boolean contains = false;
            for (int senseIter = 0; senseIter < senseHistoryList.size() && !contains; senseIter++)
                if(senseHistoryList.get(senseIter)
                        .getRevisionsInfo().getId() == senseAttributesHistory.getRevisionsInfo().getId()) {
                    contains = true;
                    senseHistoryList.get(senseIter).setSenseAttributesHistory(senseAttributesHistory);
                }
            if (!contains)
                senseHistoryList.add(new SenseHistory(senseAttributesHistory));
        }
    }

    public List<SenseRelationHistory> findSenseIncomingRelationsHistory(UUID senseId) {
        return em.createNamedQuery(SenseRelationHistory.FIND_SENSE_HISTORY_INCOMING_RELATIONS, SenseRelationHistory.class)
                .setParameter("senseId", senseId)
                .getResultList();
    }

    public List<SenseRelationHistory> findSenseOutgoingRelationsHistory(UUID senseId) {
        return em.createNamedQuery(SenseRelationHistory.FIND_SENSE_HISTORY_OUTGOING_RELATIONS, SenseRelationHistory.class)
                .setParameter("senseId", senseId)
                .getResultList();
    }

    public List<SenseHistory> findBySynsetId(UUID synsetId) {
        List<SenseHistory> senseHistoryList = em.createNamedQuery(SenseHistory.FIND_BY_SYSNET_ID, SenseHistory.class)
                .setParameter("id", synsetId)
                .getResultList();

        List<UUID> uuidList = senseHistoryList.stream().map(SenseHistory::getId).distinct().collect(Collectors.toList());
        for (UUID uuid : uuidList) {
            List<SenseAttributesHistory> senseAttributesHistoryList = em.createNamedQuery(SenseAttributesHistory.FIND_BY_ID,
                    SenseAttributesHistory.class)
                    .setParameter("id", uuid)
                    .getResultList();
            addOrMergeAttributes(senseHistoryList, senseAttributesHistoryList);
        }

        return senseHistoryList;
    }

    public List<SenseHistory> findSenseHistoryByTimestamps(long begin, long end) {
        List<SenseHistory> senseHistoryList =  em.createNamedQuery(SenseHistory.FIND_BY_TIMESTAMP, SenseHistory.class)
                .setParameter("timestamp_start", begin)
                .setParameter("timestamp_end", end)
                .getResultList();

        List<SenseAttributesHistory> senseAttributesHistoryList = em.createNamedQuery(SenseAttributesHistory.FIND_BY_TIMESTAMP,
                SenseAttributesHistory.class)
                .setParameter("timestamp_start", begin)
                .setParameter("timestamp_end", end)
                .getResultList();
        addOrMergeAttributes(senseHistoryList, senseAttributesHistoryList);

        return senseHistoryList;
    }

    public List<SenseRelationHistory> findSenseRelationHistoryByTimestamps(long begin, long end) {
        return em.createNamedQuery(SenseRelationHistory.FIND_BY_TIMESTAMP, SenseRelationHistory.class)
                .setParameter("timestamp_start", begin)
                .setParameter("timestamp_end", end)
                .getResultList();
    }

    public Optional<SenseAttributesHistory> findSenseAttributesHistoryBeforeRev(UUID senseId, int rev) {
        try {
            return Optional.of(
                    em.createNamedQuery(SenseAttributesHistory.FIND_BEFORE_REV, SenseAttributesHistory.class)
                        .setParameter("id", senseId)
                        .setParameter("rev", rev)
                        .getSingleResult());
        } catch (
        NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<SenseHistory> findSenseHistoryBeforeRev(UUID senseId, int rev) {
        try {
            return Optional.of(
                    em.createNamedQuery(SenseHistory.FIND_BEFORE_REV, SenseHistory.class)
                            .setParameter("id", senseId)
                            .setParameter("rev", rev)
                            .getSingleResult());
        } catch (
                NoResultException e) {
            return Optional.empty();
        }
    }
}
