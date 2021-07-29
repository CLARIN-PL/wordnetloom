package pl.edu.pwr.wordnetloom.server.business.lexicon.control;

import pl.edu.pwr.wordnetloom.server.business.lexicon.entity.Lexicon;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Transactional
@RequestScoped
public class LexiconQueryService {

    @PersistenceContext
    EntityManager em;

    public List<Lexicon> findAll() {
        return em.createNamedQuery(Lexicon.FIND_ALL, Lexicon.class)
                .getResultList();
    }

    public List<Long> findLexiconIdsAll() {
        return em.createNamedQuery(Lexicon.FIND_ALL_ID, Long.class)
                .getResultList();
    }

    public Optional<Lexicon> findById(long id) {
        try {
            return Optional.of(
                    em.createNamedQuery(Lexicon.FIND_BY_ID, Lexicon.class)
                            .setParameter("id", id)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
