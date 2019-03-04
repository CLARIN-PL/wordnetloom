package pl.edu.pwr.wordnetloom.server.business.corpusexample.boundary;

import pl.edu.pwr.wordnetloom.server.business.corpusexample.entity.CorpusExample;
import pl.edu.pwr.wordnetloom.server.business.lexicon.entity.Lexicon;

import javax.ejb.CreateException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class CorpusExampleQueryService {

    @PersistenceContext
    EntityManager em;

    public List<CorpusExample> findAllByWord(String word) {
    return em.createNamedQuery(CorpusExample.FIND_BY_WORD, CorpusExample.class)
                            .setParameter("word", word)
                            .getResultList();
    }
}
