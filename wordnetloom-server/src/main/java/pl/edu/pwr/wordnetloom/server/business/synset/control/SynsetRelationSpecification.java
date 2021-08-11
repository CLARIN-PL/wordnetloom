package pl.edu.pwr.wordnetloom.server.business.synset.control;

import pl.edu.pwr.wordnetloom.server.business.sense.control.Specification;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.SynsetRelation;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SynsetRelationSpecification {

    public static Specification<SynsetRelation> byFilter(UUID source, UUID target, UUID relType) {
        return (root, query, cb) -> {
            List<Predicate> criteria = new ArrayList<>();

            if (source != null) {
                criteria.add(byParent(source).toPredicate(root, query, cb));
            }
            if (target != null) {
                criteria.add(byChild(target).toPredicate(root, query, cb));
            }
            if (relType != null) {
                criteria.add(byRelType(relType).toPredicate(root, query, cb));
            }

            return cb.and(criteria.toArray(new Predicate[0]));
        };
    }

    private static Specification<SynsetRelation> byRelType(UUID relType) {
        return (root, query, cb) -> cb.equal(root.get("relationType").get("id"), relType);
    }

    private static Specification<SynsetRelation> byChild(UUID child) {
        return (root, query, cb) -> cb.equal(root.get("child").get("id"), child);
    }

    private static Specification<SynsetRelation> byParent(UUID parent) {
        return (root, query, cb) -> cb.equal(root.get("parent").get("id"), parent);
    }
}
