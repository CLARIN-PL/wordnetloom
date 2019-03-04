package pl.edu.pwr.wordnetloom.server.business.sense.enity;

import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Register;
import pl.edu.pwr.wordnetloom.server.business.user.entity.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tbl_sense_attributes")
@NamedQuery(name = SenseAttributes.FIND_BY_ID,
        query = "SELECT DISTINCT s FROM SenseAttributes s " +
                "LEFT JOIN FETCH s.examples " +
                "LEFT JOIN FETCH s.register " +
                "WHERE s.id= :id")
public class SenseAttributes implements Serializable {

    public static final String FIND_BY_ID = "SenseAttributes.findById";

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sense_id")
    @MapsId
    private Sense sense;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "senseAttributes", orphanRemoval = true)
    private Set<SenseExample> examples;

    @Lob
    private String definition;

    @Lob
    private String comment;

    @ManyToOne
    @JoinColumn(name = "register_id", referencedColumnName = "id")
    private Register register;

    private String link;

    @Column(name = "error_comment")
    private String errorComment;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User owner;

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

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Sense getSense() {
        return sense;
    }

    public void setSense(Sense sense) {
        this.sense = sense;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getErrorComment() {
        return errorComment;
    }

    public void setErrorComment(String errorComment) {
        this.errorComment = errorComment;
    }

    public Set<SenseExample> getExamples() {
        return examples;
    }

    public void setExamples(Set<SenseExample> examples) {
        this.examples = examples;
    }

    public void addExample(SenseExample e) {
        if (examples == null) {
            examples = new HashSet<>();
        }
        examples.add(e);
    }
}
