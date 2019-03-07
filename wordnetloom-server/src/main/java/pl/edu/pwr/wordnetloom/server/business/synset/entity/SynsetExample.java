package pl.edu.pwr.wordnetloom.server.business.synset.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Audited
@Entity
@Table(name = "tbl_synset_examples")
@NamedQuery(name = SynsetExample.FIND_BY_ID, query = "SELECT ex FROM SynsetExample ex WHERE ex.id = :id")
public class SynsetExample implements Serializable {

    public static final String FIND_BY_ID = "SynsetExample.finaById";

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "synset_attribute_id")
    private SynsetAttributes synsetAttributes;

    @Column(name = "example")
    private String example;

    @Column(name = "type")
    private String type;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public SynsetAttributes getSynsetAttributes() {
        return synsetAttributes;
    }

    public void setSynsetAttributes(SynsetAttributes ssynsetAttributes) {
        synsetAttributes = ssynsetAttributes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SynsetExample)) return false;

        SynsetExample that = (SynsetExample) o;

        if (!id.equals(that.id)) return false;
        if (!synsetAttributes.equals(that.synsetAttributes)) return false;
        if (!example.equals(that.example)) return false;
        return type != null ? type.equals(that.type) : that.type == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + synsetAttributes.hashCode();
        result = 31 * result + example.hashCode();
        return result;
    }
}
