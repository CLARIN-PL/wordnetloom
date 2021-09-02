package pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity;

import org.hibernate.annotations.Formula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Register;
import pl.edu.pwr.wordnetloom.server.business.revisions.entity.RevisionsInfo;

import javax.persistence.*;
import org.hibernate.annotations.NamedQuery;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.SenseAttributes;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tbl_sense_attributes_AUD")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)

@NamedQuery(name = SenseAttributesHistory.FIND_BY_ID,
        query = "SELECT DISTINCT s FROM SenseAttributesHistory s " +
                "LEFT JOIN FETCH s.register " +
                "WHERE s.id = :id")

@NamedQuery(name = SenseAttributesHistory.FIND_BY_TIMESTAMP,
        query = "SELECT DISTINCT s FROM SenseAttributesHistory s " +
                "WHERE s.revisionsInfo.timestamp >= :timestamp_start AND s.revisionsInfo.timestamp <= :timestamp_end")

public class SenseAttributesHistory implements Serializable {

    public static final String FIND_BY_ID = "SenseAttributesHistory.findById";
    public static final String FIND_BY_TIMESTAMP = "SenseAttributesHistory.findByTimestamp";

    public SenseAttributesHistory() {
    }

    @Id
    @Column(name = "sense_id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Id
    @Column
    private int rev;

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

    @Column(name = "user_name")
    private String userName;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rev")
    @JoinColumn(name = "rev")
    private RevisionsInfo revisionsInfo;

    @Column(name = "REVTYPE", insertable = false, updatable = false)
    private int revType;

    @Formula("concat(BIN_TO_UUID(sense_id), '.', REV)")
    @NotAudited
    private String concatKeys;

    public UUID getId() {
        return id;
    }

    public String getDefinition() {
        return definition;
    }

    public String getComment() {
        return comment;
    }

    public Register getRegister() {
        return register;
    }

    public String getLink() {
        return link;
    }

    public String getErrorComment() {
        return errorComment;
    }

    public String getUserName() {
        return userName;
    }

    public RevisionsInfo getRevisionsInfo() {
        return revisionsInfo;
    }

    public int getRevType() {
        return revType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SenseAttributesHistory that = (SenseAttributesHistory) o;
        return revType == that.revType && Objects.equals(id, that.id) &&
                Objects.equals(definition, that.definition) && Objects.equals(comment, that.comment) &&
                Objects.equals(register, that.register) && Objects.equals(link, that.link) &&
                Objects.equals(errorComment, that.errorComment) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(revisionsInfo, that.revisionsInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rev, definition, comment, register, link, errorComment, userName, revisionsInfo, revType);
    }
}
