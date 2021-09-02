package pl.edu.pwr.wordnetloom.server.business.synset.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Status;
import pl.edu.pwr.wordnetloom.server.business.lexicon.entity.Lexicon;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.Sense;

import javax.persistence.*;
import org.hibernate.annotations.NamedQuery;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tbl_synset")
@Audited

@NamedQuery(name = Synset.FIND_BY_ID_WITH_LEXICON_AND_SENSES_WITH_DOMAIN,
        query = "SELECT DISTINCT s " +
                "FROM Synset s " +
                "LEFT JOIN FETCH s.lexicon " +
                "LEFT JOIN FETCH s.senses l " +
                "LEFT JOIN FETCH l.incomingRelations " +
                "LEFT JOIN FETCH l.outgoingRelations " +
                "LEFT JOIN FETCH l.word " +
                "LEFT JOIN FETCH l.domain " +
                "LEFT JOIN FETCH l.partOfSpeech " +
                "WHERE s.id = :id")

@NamedQuery(name = Synset.FIND_SYNSET_HEAD,
        query = "SELECT s FROM Synset s " +
                "LEFT JOIN FETCH s.senses se " +
                "LEFT JOIN FETCH se.word w " +
                "LEFT JOIN FETCH se.domain d " +
                "WHERE se.synsetPosition = :synsetPosition " +
                "and s.id = :synsetId"
)

@NamedQuery(name = Synset.FIND_BY_ID_WITH_EXAMPLES_AND_SYNSET_INCOMING_RELATIONS,
        query = "SELECT DISTINCT s " +
                "FROM Synset s " +
                "LEFT JOIN FETCH s.attributes a " +
                "LEFT JOIN FETCH a.examples " +
                "LEFT JOIN s.incomingRelations " +
                "WHERE s.id = :id")

@NamedQuery(name = Synset.FIND_BY_LEXICON_WITH_EXAMPLES,
        query = "SELECT DISTINCT s FROM Synset s " +
                "LEFT JOIN FETCH s.attributes a " +
                "LEFT JOIN FETCH s.lexicon l " +
                "LEFT JOIN FETCH a.examples " +
                "WHERE l.id = :lexId")
public class Synset implements Serializable {

    public static final String FIND_SYNSET_HEAD = "Synset.finaSynsetHead";
    public static final String FIND_BY_ID_WITH_LEXICON_AND_SENSES_WITH_DOMAIN = "Synset.findByIdWithLexiconAndSensesWithDomain";
    public static final String FIND_BY_ID_WITH_EXAMPLES_AND_SYNSET_INCOMING_RELATIONS = "Synset.findWithExamplesByIdAndSynsetIncomingRelations";
    public static final String FIND_BY_LEXICON_WITH_EXAMPLES = "Synset.findByLexiconWithExamples";
    public static final int SYNSET_HEAD_POSITION = 0;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToMany(mappedBy = "synset", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("synsetPosition")
    private Set<Sense> senses = new HashSet<>();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lexicon_id", referencedColumnName = "id", nullable = false)
    private Lexicon lexicon;

    @Basic
    @Column(name = "abstract")
    private Boolean isAbstract = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private Status status;

    @OneToMany(mappedBy = "child", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<SynsetRelation> incomingRelations = new HashSet<>();

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<SynsetRelation> outgoingRelations = new HashSet<>();

    @OneToOne (mappedBy="synset", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private SynsetAttributes attributes;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id){
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<Sense> getSenses() {
        return senses;
    }

    public void setSenses(Set<Sense> senses) {
        this.senses = senses;
    }

    public Lexicon getLexicon() {
        return lexicon;
    }

    public void setLexicon(Lexicon lexicon) {
        this.lexicon = lexicon;
    }

    public Set<SynsetRelation> getIncomingRelations() {
        return incomingRelations;
    }

    public void setIncomingRelations(Set<SynsetRelation> incomingRelations) {
        this.incomingRelations = incomingRelations;
    }

    public Set<SynsetRelation> getOutgoingRelations() {
        return outgoingRelations;
    }

    public void setOutgoingRelations(Set<SynsetRelation> outgoingRelations) {
        this.outgoingRelations = outgoingRelations;
    }

    public Boolean getAbstract() {
        return isAbstract;
    }

    public void setAbstract(Boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public SynsetAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(SynsetAttributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return senses.toString();
    }
}
