package pl.edu.pwr.wordnetloom.server.business.sense.control;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.Word;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Optional;
import java.util.UUID;

@Transactional
@RequestScoped
public class WordQueryService {

    @PersistenceContext
    EntityManager em;

    public Optional<Word> findById(UUID word) {
        try {
            return Optional.of(
                    em.find(Word.class, word));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Word> findByWord(String word) {
        try {
            //temporary for sa becouse of invalid duplicated words
             return em.createNamedQuery(Word.FIND_BY_WORD, Word.class)
                            .setParameter("word", word)
                            .getResultList().stream().findFirst();
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Long countByWord(String word) {
        return em.createNamedQuery(Word.COUNT_BY_WORD, Long.class)
                            .setParameter("word", word)
                            .getSingleResult();
    }

}
