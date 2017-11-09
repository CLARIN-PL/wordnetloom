package pl.edu.pwr.wordnetloom.synset.service;

import pl.edu.pwr.wordnetloom.common.dto.DataEntry;
import pl.edu.pwr.wordnetloom.sense.model.Sense;
import pl.edu.pwr.wordnetloom.synset.model.Synset;

import java.util.List;
import java.util.Map;

public interface SynsetServiceRemote {

    void clone(Synset synset);

    boolean delete(Synset synset);

    Synset findSynsetBySense(Sense sense, List<Long> lexicons);

    Map<Long, DataEntry> prepareCacheForRootNode(Synset synset, List<Long> lexicons);
}
