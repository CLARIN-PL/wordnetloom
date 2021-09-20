package pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity;

import org.hibernate.annotations.Formula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Status;
import pl.edu.pwr.wordnetloom.server.business.lexicon.entity.Lexicon;
import pl.edu.pwr.wordnetloom.server.business.revisions.entity.RevisionsInfo;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.Sense;

import javax.persistence.*;
import org.hibernate.annotations.NamedQuery;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "tbl_synset_AUD")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)

@NamedQuery(name = SynsetHistory.FIND_BY_ID,
        query = "SELECT DISTINCT s " +
                "FROM SynsetHistory s " +
                "LEFT JOIN FETCH s.lexicon " +
                "WHERE s.id = :id")

@NamedQuery(name = SynsetHistory.FIND_BY_TIMESTAMP,
        query = "SELECT s FROM SynsetHistory s " +
                "WHERE s.revisionsInfo.timestamp >= :timestamp_start AND s.revisionsInfo.timestamp <= :timestamp_end")

public class SynsetHistory implements Serializable {

    public static final String FIND_BY_ID = "SynsetHistory.findById";
    public static final String FIND_BY_TIMESTAMP = "SynsetHistory.findByTimestamp";

    public SynsetHistory() {
    }

    public SynsetHistory(SynsetAttributesHistory attributes) {
        this.attributes = attributes;
        id = attributes.getId();
        revisionsInfo = attributes.getRevisionsInfo();
        revType = attributes.getRevType();
    }

    public SynsetHistory(SynsetAttributesHistory attributes, List<Sense> senses) {
        this.attributes = attributes;
        id = attributes.getId();
        revisionsInfo = attributes.getRevisionsInfo();
        revType = attributes.getRevType();
        this.senses = senses;
    }

    @Id
    @Column
    protected UUID id;

    @Id
    @Column
    protected int rev;

    @ManyToOne
    @MapsId("rev")
    @JoinColumn(name = "rev")
    private RevisionsInfo revisionsInfo;

    @Transient
    private List<Sense> senses = new LinkedList<>();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lexicon_id", referencedColumnName = "id", nullable = false)
    private Lexicon lexicon;

    @Basic
    @Column(name = "abstract")
    private Boolean isAbstract = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private Status status;

    @Column(name = "REVTYPE", insertable = false, updatable = false)
    private int revType;

    @Transient
    private SynsetAttributesHistory attributes;

    @Formula("concat(BIN_TO_UUID(id), '.', REV)")
    @NotAudited
    private String concatKeys;

    public UUID getId() {
        return id;
    }

    public List<Sense> getSenses() {
        return senses;
    }

    public Lexicon getLexicon() {
        return lexicon;
    }

    public Boolean getAbstract() {
        return isAbstract;
    }

    public Status getStatus() {
        return status;
    }

    public SynsetAttributesHistory getAttributes() {
        return attributes;
    }

    public RevisionsInfo getRevisionsInfo() {
        return revisionsInfo;
    }

    public int getRevType() {
        return revType;
    }

    public void setSenses(List<Sense> senses) {
        this.senses = senses;
    }

    public String getConcatKeys() {
        return concatKeys;
    }

    public void setAttributes(SynsetAttributesHistory attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return senses.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SynsetHistory that = (SynsetHistory) o;
        return rev == that.rev &&
                revType == that.revType &&
                Objects.equals(id, that.id) &&
                Objects.equals(revisionsInfo, that.revisionsInfo) &&
                Objects.equals(senses, that.senses) &&
                Objects.equals(lexicon, that.lexicon) &&
                Objects.equals(isAbstract, that.isAbstract) &&
                Objects.equals(status, that.status) &&
                Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rev, revisionsInfo, senses, lexicon, isAbstract, status, revType, attributes);
    }
}
