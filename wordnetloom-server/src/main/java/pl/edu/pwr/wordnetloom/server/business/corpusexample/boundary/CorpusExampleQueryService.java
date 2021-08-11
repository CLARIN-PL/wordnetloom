package pl.edu.pwr.wordnetloom.server.business.corpusexample.boundary;

import pl.edu.pwr.wordnetloom.server.business.corpusexample.entity.CorpusExample;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional
@RequestScoped
public class CorpusExampleQueryService {

    @PersistenceContext
    EntityManager em;

    public List<CorpusExample> findAllByWord(String word) {
    return em.createNamedQuery(CorpusExample.FIND_BY_WORD, CorpusExample.class)
                            .setParameter("word", word)
                            .getResultList();
    }
}
