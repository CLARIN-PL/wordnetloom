package pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity;

import org.hibernate.annotations.NamedQuery;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Valuation;
import pl.edu.pwr.wordnetloom.server.business.revisions.entity.RevisionsInfo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tbl_sense_valuations_AUD")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)

@NamedQuery(name = SenseValuationHistory.FIND_BY_EMOTIONAL_ID,
        query = "SELECT DISTINCT sv FROM SenseValuationHistory sv " +
                "WHERE sv.annotation = :id")

@NamedQuery(name = SenseValuationHistory.FIND_BY_EMOTIONAL_AND_REV,
        query = "SELECT DISTINCT sv FROM SenseValuationHistory sv " +
                "WHERE sv.annotation = :id AND sv.rev = :rev ")

public class SenseValuationHistory implements Serializable {

    public static final String FIND_BY_EMOTIONAL_ID = "SenseValuationHistory.FindByEmotionalId";
    public static final String FIND_BY_EMOTIONAL_AND_REV = "SenseValuationHistory.FindByEmotionalAndRev";

    @Id
    @Column(name = "annotation_id")
    private UUID annotation;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "valuation")
    private Valuation valuation;

    @Id
    @Column
    private int rev;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rev")
    @JoinColumn(name = "rev")
    private RevisionsInfo revisionsInfo;

    @Column(name = "REVTYPE", insertable = false, updatable = false)
    private int revType;

    public UUID getAnnotation() {
        return annotation;
    }

    public Valuation getValuation() {
        return valuation;
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
        SenseValuationHistory that = (SenseValuationHistory) o;
        return revType == that.revType && Objects.equals(annotation, that.annotation) && Objects.equals(valuation, that.valuation) && Objects.equals(revisionsInfo, that.revisionsInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, valuation, revisionsInfo, revType);
    }
}
