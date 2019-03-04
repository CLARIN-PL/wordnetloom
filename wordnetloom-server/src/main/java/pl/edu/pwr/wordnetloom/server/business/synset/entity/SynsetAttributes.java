package pl.edu.pwr.wordnetloom.server.business.synset.entity;

import pl.edu.pwr.wordnetloom.server.business.user.entity.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tbl_synset_attributes")
@NamedQuery(name = SynsetAttributes.FIND_BY_ID_WITH_EXAMPLES,
        query = "SELECT DISTINCT s " +
                "FROM SynsetAttributes s " +
                "LEFT JOIN FETCH s.examples " +
                "LEFT JOIN FETCH s.owner " +
                "WHERE s.id = :id")
public class SynsetAttributes implements Serializable {

    public static final String FIND_BY_ID_WITH_EXAMPLES = "SynsetAttributes.findWithExamplesById";

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "synset_id")
    @MapsId
    private Synset synset;

    @Lob
    private String definition;

    @Lob
    private String comment;

    @Column(name = "error_comment")
    private String errorComment;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "synsetAttributes", orphanRemoval = true)
    private Set<SynsetExample> examples;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    @Column(name = "princeton_id")
    private String princetonId;

    @Column(name = "ili_id")
    private String iliId;

    public SynsetAttributes() {
    }

    public SynsetAttributes(String definition, String comment, User owner) {
        this.definition = definition;
        this.comment = comment;
        this.owner = owner;
    }

    public SynsetAttributes(String definition, String comment, User owner, String princetonId) {
        this.definition = definition;
        this.comment = comment;
        this.owner = owner;
        this.princetonId = princetonId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Synset getSynset() {
        return synset;
    }

    public void setSynset(Synset synset) {
        this.synset = synset;
    }

    public String getPrincetonId() {
        return princetonId;
    }

    public void setPrincetonId(String princetonId) {
        this.princetonId = princetonId;
    }

    public void addExample(SynsetExample example) {
        if (examples == null) {
            examples = new HashSet<>();
        }
        examples.add(example);
    }

    public Set<SynsetExample> getExamples() {
        return examples;
    }

    public void setExamples(Set<SynsetExample> examples) {
        this.examples = examples;
    }

    public String getErrorComment() {
        return errorComment;
    }

    public void setErrorComment(String errorComment) {
        this.errorComment = errorComment;
    }

    public String getIliId() {
        return iliId;
    }

    public void setIliId(String iliId) {
        this.iliId = iliId;
    }

}
