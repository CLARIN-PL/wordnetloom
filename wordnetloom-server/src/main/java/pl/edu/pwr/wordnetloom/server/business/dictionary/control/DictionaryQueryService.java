package pl.edu.pwr.wordnetloom.server.business.dictionary.control;

import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.*;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Stateless
public class DictionaryQueryService {

    @PersistenceContext
    EntityManager em;

    public <T> List<Dictionary> findDictionaryByClass(Class<T> clazz) {
        return em.createNamedQuery(Dictionary.FIND_ALL_BY_TYPE, Dictionary.class)
                .setParameter("type", clazz)
                .getResultList();
    }

    public Optional<Dictionary> findDictionaryById(long id) {
        try {
            return Optional.of(
                    em.createNamedQuery(Dictionary.FIND_BY_ID, Dictionary.class)
                            .setParameter("id", id)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Domain> findAllDomains() {
        return em.createNamedQuery(Domain.FIND_ALL, Domain.class)
                 .getResultList();
    }

    public Optional<Domain> findDomain(long id) {
        try {
            return Optional.of(
                    em.createNamedQuery(Domain.FIND_BY_ID, Domain.class)
                            .setParameter("id", id)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Emotion> findAllEmotions(){
        List<Dictionary> dictionaries = em.createNamedQuery(Dictionary.FIND_ALL_BY_TYPE, Dictionary.class)
                .setParameter("type", Emotion.class)
                .getResultList();

        // TODO: temporary solution, change this
        List<Emotion> emotions = new ArrayList<>();
        for(Dictionary dictionary : dictionaries){
            emotions.add((Emotion)dictionary);
        }
        return emotions;
    }

    public Optional<Emotion> findEmotion(long id){
        try{
            return Optional.of(em.createNamedQuery(Emotion.FIND_BY_ID, Emotion.class)
                    .setParameter("type", Emotion.class)
                    .getSingleResult());
        } catch (NoResultException e){
            return Optional.empty();
        }
    }

//    public List<Dictionary> findAllEmotions(){
//        return em.createNamedQuery(Dictionary.FIND_ALL_BY_TYPE, Dictionary.class)
//                .setParameter("type", Emotion.class)
//                .getResultList();
//    }

    public List<PartOfSpeech> findAllPartsOfSpeech() {
        return em.createNamedQuery(PartOfSpeech.FIND_ALL, PartOfSpeech.class).getResultList();
    }

    public Optional<PartOfSpeech> findPartsOfSpeech(long id) {
        try {
            return Optional.of(
                    em.createNamedQuery(PartOfSpeech.FIND_BY_ID, PartOfSpeech.class)
                            .setParameter("id", id)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Status findStatusDefaultValue() {
        return em.createNamedQuery(Status.FIND_DEFAULT_STATUS_VALUE, Status.class)
                .getSingleResult();
    }
}
