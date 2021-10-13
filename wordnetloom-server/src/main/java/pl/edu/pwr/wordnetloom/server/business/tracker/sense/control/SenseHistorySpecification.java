package pl.edu.pwr.wordnetloom.server.business.tracker.sense.control;

import pl.edu.pwr.wordnetloom.server.business.tracker.TrackerSearchFilter;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.EmotionalAnnotationHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.SenseAttributesHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.SenseHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.SenseRelationHistory;


import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SenseHistorySpecification {
    public static HistorySpecification<SenseHistory> byFilter(TrackerSearchFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> criteria = new ArrayList<>();

            if (filter.getDateFromTimestamp() != null) {
                criteria.add(byDateFrom(filter.getDateFromTimestamp()).toPredicate(root, query, cb));
            }

            if (filter.getDateToTimestamp() != null) {
                criteria.add(byDateTo(filter.getDateToTimestamp()).toPredicate(root, query, cb));
            }

            if (filter.getSenseUuid() != null) {
                criteria.add(byId(filter.getSenseUuid()).toPredicate(root, query, cb));
            }

            if (filter.getUser() != null) {
                criteria.add(byUser(filter.getUser()).toPredicate(root, query, cb));
            }

            if (filter.getPartOfSpeech() != null) {
                criteria.add(byPartOfSpeech(filter.getPartOfSpeech()).toPredicate(root, query, cb));
            }

            return cb.and(criteria.toArray(new Predicate[0]));
        };
    }

    public static HistorySpecification<SenseAttributesHistory> attributesByFilter(TrackerSearchFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> criteria = new ArrayList<>();

            if (filter.getDateFromTimestamp() != null) {
                criteria.add(attributesByDateFrom(filter.getDateFromTimestamp()).toPredicate(root, query, cb));
            }

            if (filter.getDateToTimestamp() != null) {
                criteria.add(attributesByDateTo(filter.getDateToTimestamp()).toPredicate(root, query, cb));
            }

            if (filter.getSenseUuid() != null) {
                criteria.add(attributesById(filter.getSenseUuid()).toPredicate(root, query, cb));
            }

            if (filter.getUser() != null) {
                criteria.add(attributesByUser(filter.getUser()).toPredicate(root, query, cb));
            }

            return cb.and(criteria.toArray(new Predicate[0]));
        };
    }

    public static HistorySpecification<SenseRelationHistory> relationsByFilter(TrackerSearchFilter filter) {
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

            if (filter.getSenseUuid() != null) {
                criteria.add(relationBySenseId(filter.getSenseUuid()).toPredicate(root, query, cb));
            }

            return cb.and(criteria.toArray(new Predicate[0]));
        };
    }

    public static HistorySpecification<EmotionalAnnotationHistory> emotionalAnnotationByFilter(TrackerSearchFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> criteria = new ArrayList<>();

            if (filter.getUser() != null) {
                criteria.add(emotionalAnnotationByUser(filter.getUser()).toPredicate(root, query, cb));
            }

            if (filter.getSenseUuid() != null) {
                criteria.add(emotionalAnnotationBySenseId(filter.getSenseUuid()).toPredicate(root, query, cb));
            }

            return cb.and(criteria.toArray(new Predicate[0]));
        };
    }

    public static HistorySpecification<SenseHistory> byDateFrom(long dateFrom) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("revisionsInfo").get("timestamp"), dateFrom);
    }

    public static HistorySpecification<SenseHistory> byDateTo(long dateTo) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("revisionsInfo").get("timestamp"), dateTo);
    }

    public static HistorySpecification<SenseHistory> byId(UUID id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    public static HistorySpecification<SenseHistory> byUser(String user) {
        return (root, query, cb) -> cb.equal(root.get("revisionsInfo").get("userEmail"), user);
    }

    public static HistorySpecification<SenseHistory> byPartOfSpeech(Long posId) {
        return (root, query, cb) -> cb.equal(root.get("partOfSpeech").get("id"), posId);
    }

    public static HistorySpecification<SenseAttributesHistory> attributesByDateFrom(long dateFrom) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("revisionsInfo").get("timestamp"), dateFrom);
    }

    public static HistorySpecification<SenseAttributesHistory> attributesByDateTo(long dateTo) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("revisionsInfo").get("timestamp"), dateTo);
    }

    public static HistorySpecification<SenseAttributesHistory> attributesById(UUID id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    public static HistorySpecification<SenseAttributesHistory> attributesByUser(String user) {
        return (root, query, cb) -> cb.equal(root.get("revisionsInfo").get("userEmail"), user);
    }

    public static HistorySpecification<SenseRelationHistory> relationByDateFrom(long dateFrom) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("revisionsInfo").get("timestamp"), dateFrom);
    }

    public static HistorySpecification<SenseRelationHistory> relationByDateTo(long dateTo) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("revisionsInfo").get("timestamp"), dateTo);
    }

    public static HistorySpecification<SenseRelationHistory> relationByType(UUID id) {
        return (root, query, cb) -> cb.equal(root.get("relationType").get("id"), id);
    }

    public static HistorySpecification<SenseRelationHistory> relationByUser(String user) {
        return (root, query, cb) -> cb.equal(root.get("revisionsInfo").get("userEmail"), user);
    }

    public static HistorySpecification<SenseRelationHistory> relationBySenseId(UUID id) {
        return (root, query, cb) -> cb.equal(root.get("parent").get("id"), id);
    }

    public static HistorySpecification<EmotionalAnnotationHistory> emotionalAnnotationByUser(String user) {
        return (root, query, cb) -> cb.equal(root.get("revisionsInfo").get("userEmail"), user);
    }

    public static HistorySpecification<EmotionalAnnotationHistory> emotionalAnnotationBySenseId(UUID id) {
        return (root, query, cb) -> cb.equal(root.get("senseId"), id);
    }
}
