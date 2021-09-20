package pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity;

import org.hibernate.annotations.Formula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import pl.edu.pwr.wordnetloom.server.business.relationtype.entity.RelationType;

import javax.persistence.*;
import org.hibernate.annotations.NamedQuery;
import pl.edu.pwr.wordnetloom.server.business.revisions.entity.RevisionsInfo;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.Sense;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tbl_sense_relation_AUD")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)

@NamedQuery(name = SenseRelationHistory.FIND_SENSE_HISTORY_OUTGOING_RELATIONS,
        query = "SELECT DISTINCT r FROM SenseRelationHistory r " +
                "WHERE r.parent.id = :senseId")

@NamedQuery(name = SenseRelationHistory.FIND_SENSE_HISTORY_INCOMING_RELATIONS,
        query = "SELECT DISTINCT r FROM SenseRelationHistory r " +
                "WHERE r.child.id = :senseId")

@NamedQuery(name = SenseRelationHistory.FIND_BY_TIMESTAMP,
        query = "SELECT r FROM SenseRelationHistory r " +
                "WHERE r.revisionsInfo.timestamp >= :timestamp_start AND r.revisionsInfo.timestamp <= :timestamp_end")

public class SenseRelationHistory implements Serializable {

    public static final String FIND_SENSE_HISTORY_OUTGOING_RELATIONS = "SenseRelationHistory.findSenseHistoryOutgoingRelations";
    public static final String FIND_SENSE_HISTORY_INCOMING_RELATIONS = "SenseRelationHistory.findSenseHistoryIncomingRelations";
    public static final String FIND_BY_TIMESTAMP = "SenseRelationHistory,findByTimestamp";

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sense_relation_type_id", referencedColumnName = "id", nullable = false)
    private RelationType relationType;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_sense_id", referencedColumnName = "id", nullable = false)
    private Sense parent;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_sense_id", referencedColumnName = "id", nullable = false)
    private Sense child;

    @Id
    @Column
    private int rev;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rev")
    @JoinColumn(name = "rev")
    private RevisionsInfo revisionsInfo;

    @Column(name = "REVTYPE", insertable = false, updatable = false)
    private int revType;

    @Formula("concat(BIN_TO_UUID(sense_relation_type_id), BIN_TO_UUID(parent_sense_id), BIN_TO_UUID(child_sense_id), '.', REV)")
    @NotAudited
    private String concatKeys;

    public RelationType getRelationType() {
        return relationType;
    }

    public Sense getParent() {
        return parent;
    }

    public Sense getChild() {
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
        SenseRelationHistory that = (SenseRelationHistory) o;
        return rev == that.rev &&
                revType == that.revType &&
                Objects.equals(relationType, that.relationType) &&
                Objects.equals(parent, that.parent) &&
                Objects.equals(child, that.child) &&
                Objects.equals(revisionsInfo, that.revisionsInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relationType, parent, child, rev, revisionsInfo, revType);
    }
}
