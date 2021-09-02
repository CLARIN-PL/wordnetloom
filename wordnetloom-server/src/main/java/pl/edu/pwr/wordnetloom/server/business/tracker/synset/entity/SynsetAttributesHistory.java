package pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity;

import org.hibernate.annotations.Formula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import pl.edu.pwr.wordnetloom.server.business.revisions.entity.RevisionsInfo;

import javax.persistence.*;
import org.hibernate.annotations.NamedQuery;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tbl_synset_attributes_AUD")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)

@NamedQuery(name = SynsetAttributesHistory.FIND_BY_ID,
        query = "SELECT DISTINCT s " +
                "FROM SynsetAttributesHistory s " +
                "WHERE s.id = :id")

@NamedQuery(name = SynsetAttributesHistory.FIND_BY_TIMESTAMP,
        query = "SELECT DISTINCT s " +
                "FROM SynsetAttributesHistory s " +
                "WHERE s.revisionsInfo.timestamp >= :timestamp_start AND s.revisionsInfo.timestamp <= :timestamp_end")

public class SynsetAttributesHistory implements Serializable {

    public static final String FIND_BY_ID = "SynsetAttributesHistory.findById";
    public static final String FIND_BY_TIMESTAMP = "SynsetAttributesHistory.findByTimestamp";

    @Id
    @Column(name = "synset_id")
    private UUID id;

    @Id
    @Column
    private int rev;

    @ManyToOne
    @MapsId("rev")
    @JoinColumn(name = "rev")
    private RevisionsInfo revisionsInfo;

    @Lob
    private String definition;

    @Lob
    private String comment;

    @Column(name = "error_comment")
    private String errorComment;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "princeton_id")
    private String princetonId;

    @Column(name = "ili_id")
    private String iliId;

    @Column(name = "REVTYPE", insertable = false, updatable = false)
    private int revType;

    @Formula("concat(BIN_TO_UUID(synset_id), '.', REV)")
    @NotAudited
    private String concatKeys;


    public SynsetAttributesHistory() {
    }

    public UUID getId() {
        return id;
    }

    public String getDefinition() {
        return definition;
    }

    public String getComment() {
        return comment;
    }

    public String getErrorComment() {
        return errorComment;
    }

    public String getUserName() {
        return userName;
    }

    public String getPrincetonId() {
        return princetonId;
    }

    public String getIliId() {
        return iliId;
    }

    public int getRevType() {
        return revType;
    }

    public RevisionsInfo getRevisionsInfo() {
        return revisionsInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SynsetAttributesHistory that = (SynsetAttributesHistory) o;
        return rev == that.rev &&
                revType == that.revType &&
                Objects.equals(id, that.id) &&
                Objects.equals(revisionsInfo, that.revisionsInfo) &&
                Objects.equals(definition, that.definition) &&
                Objects.equals(comment, that.comment) &&
                Objects.equals(errorComment, that.errorComment) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(princetonId, that.princetonId) &&
                Objects.equals(iliId, that.iliId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rev, revisionsInfo, definition, comment, errorComment, userName, princetonId, iliId, revType);
    }
}
