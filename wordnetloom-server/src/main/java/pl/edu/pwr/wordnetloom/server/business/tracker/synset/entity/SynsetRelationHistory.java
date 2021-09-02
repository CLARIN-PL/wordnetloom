package pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity;

import org.hibernate.annotations.Formula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;

import javax.persistence.*;
import org.hibernate.annotations.NamedQuery;

import pl.edu.pwr.wordnetloom.server.business.revisions.entity.RevisionsInfo;
import pl.edu.pwr.wordnetloom.server.business.synset.entity.Synset;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tbl_synset_relation_AUD")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)

@NamedQuery(name = SynsetRelationHistory.FIND_SYNSET_HISTORY_OUTGOING_RELATIONS,
        query = "SELECT r FROM SynsetRelationHistory r " +
                "WHERE r.parent.id = :synsetId")

@NamedQuery(name = SynsetRelationHistory.FIND_SYNSET_HISTORY_INCOMING_RELATIONS,
        query = "SELECT r FROM SynsetRelationHistory r " +
                "WHERE r.child.id = :synsetId")

@NamedQuery(name = SynsetRelationHistory.FIND_BY_TIMESTAMP,
        query = "SELECT r FROM SynsetRelationHistory r " +
                "WHERE r.revisionsInfo.timestamp >= :timestamp_start AND r.revisionsInfo.timestamp <= :timestamp_end")

public class SynsetRelationHistory implements Serializable {

    public static final String FIND_SYNSET_HISTORY_OUTGOING_RELATIONS = "SynsetRelationHistory.findSynsetHistoryOutgoingRelations";
    public static final String FIND_SYNSET_HISTORY_INCOMING_RELATIONS = "SynsetRelationHistory.findSynsetHistoryIncomingRelations";
    public static final String FIND_BY_TIMESTAMP = "SynsetRelationHistory.findByTimestamp";

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

    @Id
    @Column
    private int rev;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rev")
    @JoinColumn(name = "rev")
    private RevisionsInfo revisionsInfo;

    @Column(name = "REVTYPE", insertable = false, updatable = false)
    private int revType;

    @Formula("concat(BIN_TO_UUID(synset_relation_type_id), BIN_TO_UUID(parent_synset_id), BIN_TO_UUID(child_synset_id), '.', REV)")
    @NotAudited
    private String concatKeys;


    public SynsetRelationHistory() {
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public Synset getParent() {
        return parent;
    }

    public Synset getChild() {
        return child;
    }

    public RevisionsInfo getRevisionsInfo() {
        return revisionsInfo;
    }

    public int getRevType() {
        return revType;
    }

    public String getConcatKeys() {
        return concatKeys;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SynsetRelationHistory that = (SynsetRelationHistory) o;
        return revType == that.revType &&
                Objects.equals(relationType, that.relationType) &&
                Objects.equals(parent, that.parent) &&
                Objects.equals(child, that.child) &&
                Objects.equals(revisionsInfo, that.revisionsInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relationType, parent, child, revisionsInfo, revType);
    }
}
