package pl.edu.pwr.wordnetloom.sense.model;

import pl.edu.pwr.wordnetloom.common.model.GenericEntity;
import pl.edu.pwr.wordnetloom.domain.model.Domain;
import pl.edu.pwr.wordnetloom.lexicon.model.Lexicon;
import pl.edu.pwr.wordnetloom.partofspeech.model.PartOfSpeech;
import pl.edu.pwr.wordnetloom.senserelation.model.SenseRelation;
import pl.edu.pwr.wordnetloom.synset.model.Synset;
import pl.edu.pwr.wordnetloom.word.model.Word;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sense")
public class Sense extends GenericEntity {

    private static final long serialVersionUID = 800201228216890725L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_id", referencedColumnName = "id", nullable = false)
    private Domain domain;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", referencedColumnName = "id", nullable = false)
    private Word word;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_of_speech_id", referencedColumnName = "id", nullable = false)
    @NotNull
    private PartOfSpeech partOfSpeech;

    @NotNull
    @Column(name = "variant", nullable = false, columnDefinition = "int default 1")
    private Integer variant = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "synset_id", referencedColumnName = "id")
    private Synset synset;

    @Column(name = "synset_position", columnDefinition = "int default 0")
    private Integer synsetPosition = 0;

    @OneToOne(mappedBy = "sense", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Valid
    @NotNull
    private SenseAttributes senseAttributes;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lexicon_id", referencedColumnName = "id", nullable = false)
    private Lexicon lexicon;

    @OneToMany(mappedBy = "child", fetch = FetchType.LAZY)
    private final Set<SenseRelation> incomingRelations = new HashSet<>();

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private final Set<SenseRelation> outgoingRelations = new HashSet<>();

    public Sense() {
        super();
    }

    public Sense(Sense sense) {
        domain = sense.domain;
        word = sense.word;
        partOfSpeech = sense.partOfSpeech;
        variant = sense.variant;
        lexicon = sense.lexicon;
        senseAttributes = new SenseAttributes(sense.senseAttributes);
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

    public SenseAttributes getSenseAttributes() {
        return senseAttributes;
    }

    public void setSenseAttributes(SenseAttributes senseAttributes) {
        this.senseAttributes = senseAttributes;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Sense)) {
            return false;
        }
        Sense e = (Sense) o;

        if (id == null) {
            return false;
        }
        return id.equals(e.getId());
    }

    @Override
    public int hashCode() {
        int hashCode = (id.hashCode());
        if (hashCode == 0) {
            return super.hashCode();
        }
        return hashCode;
    }

}
