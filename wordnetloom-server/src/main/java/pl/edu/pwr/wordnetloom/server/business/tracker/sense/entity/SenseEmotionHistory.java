package pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity;

import org.hibernate.annotations.NamedQuery;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Emotion;
import pl.edu.pwr.wordnetloom.server.business.revisions.entity.RevisionsInfo;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tbl_sense_emotions_AUD")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)

@NamedQuery(name = SenseEmotionHistory.FIND_BY_EMOTIONAL_ID,
        query = "SELECT DISTINCT se FROM SenseEmotionHistory se " +
                "WHERE se.annotation = :id ")

@NamedQuery(name = SenseEmotionHistory.FIND_BY_EMOTIONAL_AND_REV,
        query = "SELECT DISTINCT se FROM SenseEmotionHistory se " +
                "WHERE se.annotation = :id AND se.rev = :rev ")

public class SenseEmotionHistory implements Serializable {

    public static final String FIND_BY_EMOTIONAL_ID = "SenseEmotionHistory.FindByEmotionalId";
    public static final String FIND_BY_EMOTIONAL_AND_REV = "SenseEmotionHistory.FindByEmotionalAndRev";

    @Id
    @Column(name = "annotation_id")
    private UUID annotation;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion")
    private Emotion emotion;

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

    public Emotion getEmotion() {
        return emotion;
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
        SenseEmotionHistory that = (SenseEmotionHistory) o;
        return revType == that.revType && Objects.equals(annotation, that.annotation) && Objects.equals(emotion, that.emotion) && Objects.equals(revisionsInfo, that.revisionsInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, emotion, revisionsInfo, revType);
    }
}
