package pl.edu.pwr.wordnetloom.server.business.sense.boundary;

import pl.edu.pwr.wordnetloom.server.business.sense.control.SenseQueryService;
import pl.edu.pwr.wordnetloom.server.business.sense.control.WordQueryService;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.Word;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;
import java.util.UUID;

@Transactional
@RequestScoped
public class WordCommandService {

    @PersistenceContext
    EntityManager em;

    @Inject
    WordQueryService wordQueryService;

    @Inject
    SenseQueryService senseQueryService;

    public Word save(String lemma) {
        Optional<Word> word = wordQueryService.findByWord(lemma);
        return word.orElseGet(() -> {
            Word nw = new Word(lemma);
            em.persist(nw);
            return nw;
        });
    }

    public void remove(UUID id) {
        if(senseQueryService.countByWordId(id) == 0){
            wordQueryService.findById(id)
                    .ifPresent(word -> em.remove(word));
        }
    }
}
