package pl.edu.pwr.wordnetloom.server.business.relationtype.control;

import pl.edu.pwr.wordnetloom.server.business.localistaion.control.LocalisedStringsQueryService;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationArgument;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationTest;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.*;

@Transactional
@RequestScoped
public class RelationTypeQueryService {

    @PersistenceContext
    EntityManager em;

    @Inject
    LocalisedStringsQueryService localisedStringsQueryService;

    public List<RelationType> findAllRelationTypes() {
        return em.createNamedQuery(RelationType.FIND_ALL, RelationType.class)
                .getResultList();
    }

    public List<RelationType> findAllByParent(UUID id) {
        return em.createNamedQuery(RelationType.FIND_ALL_BY_PARENT, RelationType.class)
                .setParameter("id",id)
                .getResultList();
    }

    public List<RelationType> findAllByReverse(UUID id) {
        return em.createNamedQuery(RelationType.FIND_ALL_BY_REVERSE, RelationType.class)
                .setParameter("id",id)
                .getResultList();
    }

    public Map<RelationType, Set<RelationType>> findAllByRelationArgument(RelationArgument argument) {
        List<RelationType> relations = em.createNamedQuery(RelationType.FIND_BY_RELATION_ARGUMENT, RelationType.class)
                .setParameter("arg", argument)
                .getResultList();

        Map<RelationType, Set<RelationType>> relationsMap = new HashMap<>();
        relations.stream()
                .filter(rt -> rt.getParent() == null)
                .forEach(rt -> relationsMap.put(rt, new HashSet<>()));
        relations.stream()
                .filter(rt -> rt.getParent() != null)
                .forEach(rt -> relationsMap.get(rt.getParent()).add(rt));
        return relationsMap;
    }

    public Optional<RelationType> findRelationTypeById(UUID id) {

        try {
            return Optional.of(
                    em.createNamedQuery(RelationType.FIND_BY_ID, RelationType.class)
                            .setParameter("id", id)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Integer findRelationTestNextPositionById(UUID id) {
        try {
         Integer position = em.createNamedQuery(RelationTest.FIND_NEXT_POSITION, Integer.class)
                .setParameter("relId", id)
                .getSingleResult();
         if(position == null){
             position = 0;
         }else{
             position = position + 1;
         }
         return position;
        } catch (NoResultException e) {
            return 0;
        }
    }

    public Optional<RelationType> findRelationTypeByName(String name, Locale locale) {
        try {
            Long nameId = localisedStringsQueryService.findId(name, locale);
            return Optional.of(
                    em.createNamedQuery(RelationType.FIND_BY_NAME, RelationType.class)
                            .setParameter("name", nameId)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    // TODO wstawić szykanie po nazwie
    public List<RelationTest> findAllRelationTests(UUID relationTypeId) {
        return em.createNamedQuery(RelationTest.FIND_ALL_BY_RELATION_TYPE_ID, RelationTest.class)
                .setParameter("relId", relationTypeId)
                .getResultList();
    }

    public Optional<RelationTest> findRelationTest(UUID relationTypeId, long testId) {
        try {
            return Optional.of(
                    em.createNamedQuery(RelationTest.FIND_BY_ID_AND_RELATION_TYPE_ID, RelationTest.class)
                            .setParameter("relId", relationTypeId)
                            .setParameter("testId", testId)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
