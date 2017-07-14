package pl.edu.pwr.wordnetloom.sense.service;

import java.util.List;
import pl.edu.pwr.wordnetloom.lexicon.model.Lexicon;
import pl.edu.pwr.wordnetloom.partofspeech.model.PartOfSpeech;
import pl.edu.pwr.wordnetloom.sense.model.Sense;
import pl.edu.pwr.wordnetloom.sense.model.SenseCriteriaDTO;
import pl.edu.pwr.wordnetloom.synset.model.Synset;

public interface SenseServiceRemote {

    Sense clone(Sense sense);

    Sense persist(Sense sense);

    void delete(Sense sense);

    void deleteAll();

    Sense findById(Long id);

    List<Sense> findByCriteria(SenseCriteriaDTO dto);

    List<Sense> filterSenseByLexicon(final List<Sense> senses, final List<Long> lexicons);

    List<Sense> findByLikeLemma(String lemma, List<Long> lexicons);

    Integer findCountByLikeLemma(String filter);

    List<Sense> findBySynset(Synset synset, List<Long> lexicons);

    int findCountBySynset(Synset synset, List<Long> lexicons);

    int findNextVariant(String lemma, PartOfSpeech pos);

    int delete(List<Sense> list);

    int findInSynsetsCount();

    Integer countAll();

    int findHighestVariant(String word, Lexicon lexicon);

    List<Sense> findNotInAnySynset(String filter, PartOfSpeech pos);

    boolean checkIsInSynset(Sense unit);

    List<Long> getAllLemmasForLexicon(List<Long> lexicon);

    List<Sense> findSensesByWordId(Long id, Long lexicon);
}
