package pl.edu.pwr.wordnetloom.server.business.sense.enity;

import org.hibernate.annotations.GenericGenerator;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Domain;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.PartOfSpeech;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Status;
import pl.edu.pwr.wordnetloom.server.business.lexicon.entity.Lexicon;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.Synset;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tbl_sense")
@NamedQuery(name = Sense.FIND_BY_ID_WITH_RELATIONS_AND_DOMAINS,
        query = "SELECT DISTINCT s  FROM Sense s " +
                "LEFT JOIN FETCH s.word " +
                "LEFT JOIN FETCH s.domain " +
                "LEFT JOIN FETCH s.incomingRelations inr " +
                "LEFT JOIN FETCH s.outgoingRelations outr " +
                "LEFT JOIN FETCH inr.parent p " +
                "LEFT JOIN FETCH outr.child c " +
                "LEFT JOIN FETCH p.domain  " +
                "LEFT JOIN FETCH c.domain  " +
                "LEFT JOIN FETCH p.word  " +
                "LEFT JOIN FETCH c.word " +
                "WHERE s.id = :id")

@NamedQuery(name = Sense.FIND_BY_ID_WITH_ATTRIBUTES,
        query = "SELECT DISTINCT s FROM Sense s " +
                "LEFT JOIN FETCH s.word " +
                "LEFT JOIN FETCH s.domain " +
                "LEFT JOIN FETCH s.partOfSpeech " +
                "LEFT JOIN FETCH s.lexicon " +
                "LEFT JOIN FETCH s.synset WHERE s.id = :id")

@NamedQuery(name = Sense.FIND_ALL_WITH_ATTRIBUTES,
        query = "SELECT DISTINCT s FROM Sense s " +
                "LEFT JOIN FETCH s.word " +
                "LEFT JOIN FETCH s.domain " +
                "LEFT JOIN FETCH s.partOfSpeech " +
                "LEFT JOIN FETCH s.lexicon " +
                "LEFT JOIN FETCH s.synset")

@NamedQuery(name = Sense.FIND_BY_UUID_WITH_WORD_AND_DOMAIN,
        query = "SELECT s FROM Sense s LEFT JOIN s.word w LEFT JOIN s.domain d  WHERE s.id = :id")

@NamedQuery(name = Sense.FIND_LEMMA_COUNT_BY_LEXICON_AND_POS,
        query = "SELECT COUNT(DISTINCT w.word) FROM Sense s " +
                "LEFT JOIN s.word w " +
                "WHERE s.lexicon.id = :lexiconId AND s.partOfSpeech.id = :posId")

@NamedQuery(name = Sense.FIND_SENSE_COUNT_BY_LEXICON_AND_POS,
        query = "SELECT COUNT(s.id)FROM Sense s " +
                "LEFT JOIN s.word w " +
                "WHERE s.lexicon.id = :lexiconId AND s.partOfSpeech.id = :posId")

@NamedQuery(name = Sense.FIND_SYNSET_COUNT_BY_LEXICON_AND_POS,
        query = "SELECT COUNT(DISTINCT syn.id)FROM Sense s " +
                "LEFT JOIN s.synset syn " +
                "WHERE s.lexicon.id = :lexiconId AND s.partOfSpeech.id = :posId")

@NamedQuery(name = Sense.FIND_ALL_BY_LEXICON_WITH_ATTRIBUTES,
        query = "SELECT DISTINCT s  FROM Sense s " +
                "LEFT JOIN FETCH s.attributes sa "+
                "LEFT JOIN FETCH sa.examples " +
                "LEFT JOIN FETCH sa.aspect " +
                "LEFT JOIN FETCH sa.register " +
                "LEFT JOIN FETCH s.word " +
                "LEFT JOIN FETCH s.domain " +
                "LEFT JOIN FETCH s.partOfSpeech " +
                "LEFT JOIN FETCH s.synset " +
                "LEFT JOIN FETCH s.lexicon l " +
                "WHERE l.id = :id")

@NamedQuery(name = Sense.FIND_NEXT_VARIANT,
        query = "SELECT MAX(s.variant) FROM Sense AS s WHERE s.word.id = :wordId " +
                "AND s.partOfSpeech.id = :posId AND s.lexicon.id = :lex")

@NamedQuery(name = Sense.COUNT_SENSE_BY_WORD_ID, query = "SELECT COUNT(s.id) FROM Sense s WHERE s.word.id = :id")
@NamedQuery(name = Sense.COUNT_SENSES_BY_SYNSET_ID , query = "SELECT COUNT(s.id) FROM Sense s WHERE s.synset.id = :id")
@NamedQuery(name = Sense.FIND_SENSE_BY_SYNSET_ID_AND_POSITION,
        query = "SELECT s FROM Sense s WHERE s.synset.id = :id and s.synsetPosition = :position ")
public class Sense implements Serializable {

    public static final String FIND_BY_ID_WITH_ATTRIBUTES = "Sense.findByIdWithAttributes";
    public static final String FIND_ALL_WITH_ATTRIBUTES = "Sense.findAllWithAttributes";
    public static final String FIND_BY_ID_WITH_RELATIONS_AND_DOMAINS = "Sense.findByIdWithRelationsAndDomains";
    public static final String FIND_BY_UUID_WITH_WORD_AND_DOMAIN = "Sense.findByIdWithWordAndDomain";
    public static final String FIND_LEMMA_COUNT_BY_LEXICON_AND_POS = "Sense.findLemmaCountByLexiconIdAndPos";
    public static final String FIND_SENSE_COUNT_BY_LEXICON_AND_POS = "Sense.findSenseCountByLexiconIdAndPos";
    public static final String FIND_SYNSET_COUNT_BY_LEXICON_AND_POS = "Sense.findSynsetCountByLexiconIdAndPos";
    public static final String FIND_ALL_BY_LEXICON_WITH_ATTRIBUTES = "Sense.findAllByLexiconWithAttributes";
    public static final String FIND_NEXT_VARIANT = "Sense.findNextVariant";
    public static final String COUNT_SENSE_BY_WORD_ID = "Sense.countSenseByWordId";
    public static final String FIND_SENSE_BY_SYNSET_ID_AND_POSITION = "Sense.findSenseBySynsetIdAndPosition";
    public static final String COUNT_SENSES_BY_SYNSET_ID = "Sense.countSensesBySynsetId";

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lexicon_id", referencedColumnName = "id", nullable = false)
    private Lexicon lexicon;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", referencedColumnName = "id", nullable = false)
    private Word word;

    @NotNull
    @Column(name = "variant", nullable = false, columnDefinition = "int default 1")
    private Integer variant = 1;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_of_speech_id", referencedColumnName = "id", nullable = false)
    private PartOfSpeech partOfSpeech;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_id", referencedColumnName = "id", nullable = false)
    private Domain domain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "synset_id", referencedColumnName = "id")
    private Synset synset;

    @Column(name = "synset_position", columnDefinition = "int default 0")
    private Integer synsetPosition = 0;

    @OneToMany(mappedBy = "child", cascade = CascadeType.REMOVE)
    private final Set<SenseRelation> incomingRelations = new HashSet<>();

    @OneToMany(mappedBy = "parent",  cascade = CascadeType.REMOVE)
    private final Set<SenseRelation> outgoingRelations = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private Status status;

    @OneToOne(mappedBy = "sense", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private SenseAttributes attributes;

    public UUID getId(){
        return id;
    }

    public void setId(UUID id){
        this.id = id;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public Lexicon getLexicon() {
        return lexicon;
    }

    public void setLexicon(Lexicon lexicon) {
        this.lexicon = lexicon;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public Integer getVariant() {
        return variant;
    }

    public void setVariant(Integer variant) {
        this.variant = variant;
    }

    public Synset getSynset() {
        return synset;
    }

    public void setSynset(Synset synset) {
        this.synset = synset;
    }

    public Integer getSynsetPosition() {
        return synsetPosition;
    }

    public void setSynsetPosition(Integer synsetPposition) {
        synsetPosition = synsetPposition;
    }

    public Set<SenseRelation> getIncomingRelations() {
        return incomingRelations;
    }

    public Set<SenseRelation> getOutgoingRelations() {
        return outgoingRelations;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public SenseAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(SenseAttributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sense)) return false;

        Sense sense = (Sense) o;

        return id != null ? id.equals(sense.id) : sense.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}