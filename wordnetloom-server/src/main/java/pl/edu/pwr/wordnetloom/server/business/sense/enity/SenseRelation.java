package pl.edu.pwr.wordnetloom.server.business.sense.enity;

import org.hibernate.envers.Audited;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;

import javax.persistence.*;
import org.hibernate.annotations.NamedQuery;
import java.io.Serializable;

@Entity
@Table(name = "tbl_sense_relation")
@Audited

@NamedQuery(name = SenseRelation.FIND_BY_KEY, query = "SELECT r FROM SenseRelation r " +
        "JOIN FETCH r.parent p " +
        "JOIN FETCH r.child c " +
        "JOIN FETCH r.relationType rt " +
        "JOIN FETCH p.word " +
        "JOIN FETCH p.domain " +
        "JOIN FETCH c.word " +
        "JOIN FETCH c.domain " +
        "WHERE p.id = :source AND c.id= :target AND rt.id = :relationType")
@NamedQuery(name = SenseRelation.FIND_BY_PARENT_LEXICON_ID,
        query = "SELECT r FROM SenseRelation r " +
                "JOIN FETCH r.parent p " +
                "JOIN FETCH r.child c " +
                "JOIN FETCH c.partOfSpeech " +
                "JOIN FETCH p.lexicon pl " +
                "JOIN FETCH p.synset ps " +
                "JOIN FETCH c.lexicon cl " +
                "JOIN FETCH c.synset cs " +
                "JOIN FETCH r.relationType  " +
                "WHERE pl.id = :lexId")

@NamedQuery(name = SenseRelation.FIND_SENSE_RELATIONS_BY_TYPE,
        query = "SELECT r FROM SenseRelation r " +
                "WHERE r.relationType.id  = :relTypeId")
public class SenseRelation implements Serializable {

    public static final String FIND_BY_KEY = "SenseRelation.findByKey";
    public static final String FIND_BY_PARENT_LEXICON_ID = "SenseRelation.findByParentLexiconId";
    public static final String FIND_SENSE_RELATIONS_BY_TYPE = "SenseRelation.findSenseRelationsByType";

    @Id
    @ManyToOne
    @JoinColumn(name = "sense_relation_type_id", referencedColumnName = "id", nullable = false)
    private RelationType relationType;

    @Id
    @ManyToOne
    @JoinColumn(name = "parent_sense_id", referencedColumnName = "id", nullable = false)
    private Sense parent;

    @Id
    @ManyToOne
    @JoinColumn(name = "child_sense_id", referencedColumnName = "id", nullable = false)
    private Sense child;


    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    public Sense getParent() {
        return parent;
    }

    public void setParent(Sense parent) {
        this.parent = parent;
    }

    public Sense getChild() {
        return child;
    }

    public void setChild(Sense child) {
        this.child = child;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SenseRelation)) return false;

        SenseRelation that = (SenseRelation) o;

        if (relationType != null ? !relationType.equals(that.relationType) : that.relationType != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        return child != null ? child.equals(that.child) : that.child == null;
    }

    @Override
    public int hashCode() {
        int result = relationType != null ? relationType.hashCode() : 0;
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (child != null ? child.hashCode() : 0);
        return result;
    }
}
