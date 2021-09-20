package pl.edu.pwr.wordnetloom.server.business.synset.entity;

import org.hibernate.envers.Audited;
import pl.edu.pwr.wordnetloom.server.business.graph.entity.NodeDirection;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;

import javax.persistence.*;
import org.hibernate.annotations.NamedQuery;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "tbl_synset_relation")
@Audited

@NamedQuery(name = SynsetRelation.FIND_BY_KEY,
        query = "SELECT r FROM SynsetRelation r " +
                "JOIN FETCH r.parent p " +
                "JOIN FETCH r.child c " +
                "JOIN FETCH r.relationType rt " +
                "WHERE p.id = :source AND c.id = :target AND rt.id= :relationType")

@NamedQuery(name = SynsetRelation.FIND_BY_PARENT_LEXICON_ID,
        query = "SELECT r FROM SynsetRelation r " +
                "JOIN FETCH r.parent p " +
                "JOIN FETCH r.child c " +
                "JOIN FETCH c.attributes " +
                "JOIN FETCH p.lexicon pl " +
                "JOIN FETCH c.lexicon  cl " +
                "JOIN FETCH r.relationType " +
                "WHERE pl.id = :lexId")

@NamedQuery(name = SynsetRelation.FIND_PARENT_SYNSET_BY_RELATION_TYPE,
        query = "SELECT r FROM SynsetRelation r " +
                "JOIN FETCH r.parent p " +
                "JOIN FETCH r.child c " +
                "JOIN FETCH p.lexicon pl " +
                "JOIN FETCH c.lexicon cl " +
                "WHERE r.relationType.id IN :relTypeId")

@NamedQuery(name = SynsetRelation.FIND_SYNSET_RELATIONS_BY_TYPE,
        query = "SELECT r FROM SynsetRelation r " +
                "WHERE r.relationType.id  = :relTypeId")

@NamedQuery(name = SynsetRelation.FIND_RELATED_SYNSETS_IDS,
        query = "SELECT DISTINCT r.child.id FROM SynsetRelation r " +
                "WHERE r.parent.id = :id AND r.relationType.id = :relation")

@NamedQuery(name = SynsetRelation.FIND_ALL_RELATIONS,
        query = "SELECT DISTINCT r.relationType FROM SynsetRelation r ")

@NamedQuery(name = SynsetRelation.FIND_SYNSET_INCOMING_RELATIONS,
        query = "SELECT DISTINCT r FROM SynsetRelation r " +
                "WHERE r.child.id = :synsetId")

@NamedQuery(name = SynsetRelation.FIND_SYNSET_OUTGOING_RELATIONS,
        query = "SELECT DISTINCT r FROM SynsetRelation r " +
                "WHERE r.parent.id = :synsetId")

public class SynsetRelation implements Serializable {

    public static final String FIND_BY_KEY = "SynsetRelation.findByKey";
    public static final String FIND_BY_PARENT_LEXICON_ID = "SynsetRelation.findByParentLexiconId";
    public static final String FIND_PARENT_SYNSET_BY_RELATION_TYPE= "SynsetRelation.findParentSynsetByRelationType";
    public static final String FIND_RELATED_SYNSETS_IDS = "SynsetRelation.findRelatedSynsetsIds";
    public static final String FIND_SYNSET_RELATIONS_BY_TYPE = "SynsetRelation.findSynsetRelationsByType";
    public static final String FIND_ALL_RELATIONS = "SynsetRelation.findAllRelations";
    public static final String FIND_SYNSET_OUTGOING_RELATIONS = "SynsetRelation.findSynsetOutgoingRelations";
    public static final String FIND_SYNSET_INCOMING_RELATIONS = "SynsetRelation.findSynsetIncomingRelations";

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "synset_relation_type_id", referencedColumnName = "id", nullable = false)
    private RelationType relationType;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_synset_id", referencedColumnName = "id", nullable = false)
    private Synset parent;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_synset_id", referencedColumnName = "id", nullable = false)
    private Synset child;

    public SynsetRelation() {
    }

    public SynsetRelation(UUID relationTypeId, UUID parentId, UUID childId, NodeDirection direction) {
        relationType = new RelationType();
        relationType.setId(relationTypeId);
        relationType.setNodePosition(direction);
        parent = new Synset();
        parent.setId(parentId);
        child = new Synset();
        child.setId(childId);

    }

    public Synset getParent() {
        return parent;
    }

    public void setParent(Synset parent) {
        this.parent = parent;
    }

    public Synset getChild() {
        return child;
    }

    public void setChild(Synset child) {
        this.child = child;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SynsetRelation)) return false;

        SynsetRelation that = (SynsetRelation) o;

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
