package pl.edu.pwr.wordnetloom.server.business.tracker.synset.control;

import pl.edu.pwr.wordnetloom.server.business.tracker.TrackerSearchFilter;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.control.HistorySpecification;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity.SynsetAttributesHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity.SynsetHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity.SynsetRelationHistory;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SynsetHistorySpecification {
    public static HistorySpecification<SynsetHistory> byFilter(TrackerSearchFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> criteria = new ArrayList<>();

            if (filter.getDateFromTimestamp() != null) {
                criteria.add(byDateFrom(filter.getDateFromTimestamp()).toPredicate(root, query, cb));
            }

            if (filter.getDateToTimestamp() != null) {
                criteria.add(byDateTo(filter.getDateToTimestamp()).toPredicate(root, query, cb));
            }

            if (filter.getSynsetUuid() != null) {
                criteria.add(byId(filter.getSynsetUuid()).toPredicate(root, query, cb));
            }

            if (filter.getUser() != null) {
                criteria.add(byUser(filter.getUser()).toPredicate(root, query, cb));
            }

            return cb.and(criteria.toArray(new Predicate[0]));
        };
    }

    public static HistorySpecification<SynsetAttributesHistory> attributesByFilter(TrackerSearchFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> criteria = new ArrayList<>();

            if (filter.getDateFromTimestamp() != null) {
                criteria.add(attributesByDateFrom(filter.getDateFromTimestamp()).toPredicate(root, query, cb));
            }

            if (filter.getDateToTimestamp() != null) {
                criteria.add(attributesByDateTo(filter.getDateToTimestamp()).toPredicate(root, query, cb));
            }

            if (filter.getSynsetUuid() != null) {
                criteria.add(attributesById(filter.getSynsetUuid()).toPredicate(root, query, cb));
            }

            if (filter.getUser() != null) {
                criteria.add(attributesByUser(filter.getUser()).toPredicate(root, query, cb));
            }

            return cb.and(criteria.toArray(new Predicate[0]));
        };
    }

    public static HistorySpecification<SynsetRelationHistory> relationsByFilter(TrackerSearchFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> criteria = new ArrayList<>();

            if (filter.getDateFromTimestamp() != null) {
                criteria.add(relationByDateFrom(filter.getDateFromTimestamp()).toPredicate(root, query, cb));
            }

            if (filter.getDateToTimestamp() != null) {
                criteria.add(relationByDateTo(filter.getDateToTimestamp()).toPredicate(root, query, cb));
            }

            if (filter.getUser() != null) {
                criteria.add(relationByUser(filter.getUser()).toPredicate(root, query, cb));
            }

            if (filter.getRelationTypeId() != null) {
                criteria.add(relationByType(filter.getRelationTypeId()).toPredicate(root, query, cb));
            }

            if (filter.getSynsetUuid() != null) {
                criteria.add(relationBySynsetId(filter.getSynsetUuid()).toPredicate(root, query, cb));
            }

            return cb.and(criteria.toArray(new Predicate[0]));
        };
    }

    public static HistorySpecification<SynsetHistory> byDateFrom(long dateFrom) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("revisionsInfo").get("timestamp"), dateFrom);
    }

    public static HistorySpecification<SynsetHistory> byDateTo(long dateTo) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("revisionsInfo").get("timestamp"), dateTo);
    }

    public static HistorySpecification<SynsetHistory> byId(UUID id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    public static HistorySpecification<SynsetHistory> byUser(String user) {
        return (root, query, cb) -> cb.equal(root.get("revisionsInfo").get("userEmail"), user);
    }

    public static HistorySpecification<SynsetAttributesHistory> attributesByDateFrom(long dateFrom) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("revisionsInfo").get("timestamp"), dateFrom);
    }

    public static HistorySpecification<SynsetAttributesHistory> attributesByDateTo(long dateTo) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("revisionsInfo").get("timestamp"), dateTo);
    }

    public static HistorySpecification<SynsetAttributesHistory> attributesById(UUID id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    public static HistorySpecification<SynsetAttributesHistory> attributesByUser(String user) {
        return (root, query, cb) -> cb.equal(root.get("revisionsInfo").get("userEmail"), user);
    }

    public static HistorySpecification<SynsetRelationHistory> relationByDateFrom(long dateFrom) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("revisionsInfo").get("timestamp"), dateFrom);
    }

    public static HistorySpecification<SynsetRelationHistory> relationByDateTo(long dateTo) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("revisionsInfo").get("timestamp"), dateTo);
    }

    public static HistorySpecification<SynsetRelationHistory> relationByType(UUID id) {
        return (root, query, cb) -> cb.equal(root.get("relationType").get("id"), id);
    }

    public static HistorySpecification<SynsetRelationHistory> relationByUser(String user) {
        return (root, query, cb) -> cb.equal(root.get("revisionsInfo").get("userEmail"), user);
    }

    public static HistorySpecification<SynsetRelationHistory> relationBySynsetId(UUID id) {
        return (root, query, cb) -> cb.equal(root.get("parent").get("id"), id);
    }
}
