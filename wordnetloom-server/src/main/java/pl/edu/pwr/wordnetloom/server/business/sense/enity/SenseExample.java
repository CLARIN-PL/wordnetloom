package pl.edu.pwr.wordnetloom.server.business.sense.enity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Audited
@Entity
@Table(name = "tbl_sense_examples")
@NamedQuery(name = SenseExample.FIND_BY_ID, query = "SELECT ex FROM SenseExample ex WHERE ex.id = :id")
public class SenseExample implements Serializable {

    public static final String FIND_BY_ID = "SenseExample.findById";

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sense_attribute_id")
    private SenseAttributes senseAttributes;

    @Column(name = "example")
    private String example;

    @Column(name = "type")
    private String type;

    public SenseAttributes getSenseAttributes() {
        return senseAttributes;
    }

    public void setSenseAttributes(SenseAttributes senseAttributes) {
        this.senseAttributes = senseAttributes;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SenseExample)) return false;

        SenseExample example1 = (SenseExample) o;

        if (id != null ? !id.equals(example1.id) : example1.id != null) return false;
        if (senseAttributes != null ? !senseAttributes.equals(example1.senseAttributes) : example1.senseAttributes != null)
            return false;
        if (example != null ? !example.equals(example1.example) : example1.example != null) return false;
        return type != null ? type.equals(example1.type) : example1.type == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (senseAttributes != null ? senseAttributes.hashCode() : 0);
        result = 31 * result + (example != null ? example.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
