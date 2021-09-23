package pl.edu.pwr.wordnetloom.server.business.tracker;

import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Domain;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.PartOfSpeech;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Register;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Status;
import pl.edu.pwr.wordnetloom.server.business.lexicon.entity.Lexicon;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.Word;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.SenseAttributesHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.SenseHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity.SynsetAttributesHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity.SynsetHistory;

public class BeforeHistory {

    private String definition;
    private String comment;
    private Register register;
    private String errorComment;
    private Lexicon lexicon;
    private Word word;
    private Integer variant;
    private PartOfSpeech partOfSpeech;
    private Domain domain;
    private Status status;
    private Boolean isAbstract;
    private String princetonId;
    private String iliId;

    public BeforeHistory(SenseAttributesHistory senseAttributesHistory) {
        definition = senseAttributesHistory.getDefinition();
        comment = senseAttributesHistory.getComment();
        register = senseAttributesHistory.getRegister();
        errorComment = senseAttributesHistory.getErrorComment();
    }

    public BeforeHistory(SenseHistory senseHistory) {
        lexicon = senseHistory.getLexicon();
        word = senseHistory.getWord();
        variant = senseHistory.getVariant();
        partOfSpeech = senseHistory.getPartOfSpeech();
        domain = senseHistory.getDomain();
        status = senseHistory.getStatus();
    }

    public BeforeHistory(SynsetHistory synsetHistory) {
        lexicon = synsetHistory.getLexicon();
        isAbstract = synsetHistory.getAbstract();
        status = synsetHistory.getStatus();
    }

    public BeforeHistory(SynsetAttributesHistory synsetAttributesHistory) {
        definition = synsetAttributesHistory.getDefinition();
        comment = synsetAttributesHistory.getComment();
        errorComment = synsetAttributesHistory.getErrorComment();
        princetonId = synsetAttributesHistory.getPrincetonId();
        iliId = synsetAttributesHistory.getIliId();
    }

    public String getDefinition() {
        return definition;
    }

    public String getComment() {
        return comment;
    }

    public Register getRegister() {
        return register;
    }

    public String getErrorComment() {
        return errorComment;
    }

    public Lexicon getLexicon() {
        return lexicon;
    }

    public Word getWord() {
        return word;
    }

    public Integer getVariant() {
        return variant;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public Domain getDomain() {
        return domain;
    }

    public Status getStatus() {
        return status;
    }

    public Boolean getAbstract() {
        return isAbstract;
    }

    public String getPrincetonId() {
        return princetonId;
    }

    public String getIliId() {
        return iliId;
    }
}
