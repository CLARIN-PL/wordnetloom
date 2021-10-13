package pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import pl.edu.pwr.wordnetloom.server.business.revisions.entity.RevisionsInfo;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@NamedQuery(name = EmotionalAnnotationHistory.FIND_BY_SENSE_ID,
        query = "SELECT DISTINCT e FROM EmotionalAnnotationHistory e " +
                "WHERE e.senseId = :id " +
                "ORDER BY e.rev DESC ")

@NamedQuery(name = EmotionalAnnotationHistory.FIND_BEFORE_REV,
        query = "SELECT DISTINCT e FROM EmotionalAnnotationHistory e " +
                "WHERE e.rev < :rev AND e.id = :id " +
                "ORDER BY e.rev DESC ")

@Entity
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Table(name = "tbl_emotional_annotations_AUD")
public class EmotionalAnnotationHistory implements Serializable{

    public static final String FIND_BY_SENSE_ID = "EmotionalAnnotationHistory.FindBySenseId";
    public static final String FIND_BEFORE_REV = "EmotionalAnnotationHistory.FindBefore.Rev";

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Id
    @Column
    private int rev;

    @Column(name = "sense_id")
    private UUID senseId;

    @Column(name = "super_anotation")
    private boolean superAnnotation;

    @Column(name = "has_emotional_characteristic")
    private boolean emotionalCharacteristic;

    @Transient
    private List<SenseEmotionHistory> emotions = new LinkedList<>();

    @Transient
    private List<SenseValuationHistory> valuations = new LinkedList<>();

    @Column(name = "markedness_id")
    private Long markednessId;

    @Column(name = "example1")
    private String example1;

    @Column(name = "example2")
    private String example2;

    @Column(name = "user_name")
    private String owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rev")
    @JoinColumn(name = "rev")
    private RevisionsInfo revisionsInfo;

    @Column(name = "REVTYPE", insertable = false, updatable = false)
    private int revType;

    @Formula("concat(BIN_TO_UUID(id), '.', REV)")
    @NotAudited
    private String concatKeys;

    @Transient
    private EmotionalAnnotationHistory beforeHistory;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getRev() {
        return rev;
    }

    public void setRev(int rev) {
        this.rev = rev;
    }

    public UUID getSenseId() {
        return senseId;
    }

    public void setSenseId(UUID senseId) {
        this.senseId = senseId;
    }

    public boolean isSuperAnnotation() {
        return superAnnotation;
    }

    public void setSuperAnnotation(boolean superAnnotation) {
        this.superAnnotation = superAnnotation;
    }

    public boolean isEmotionalCharacteristic() {
        return emotionalCharacteristic;
    }

    public void setEmotionalCharacteristic(boolean emotionalCharacteristic) {
        this.emotionalCharacteristic = emotionalCharacteristic;
    }

    public List<SenseEmotionHistory> getEmotions() {
        return emotions;
    }

    public void setEmotions(List<SenseEmotionHistory> emotions) {
        this.emotions = emotions;
    }

    public List<SenseValuationHistory> getValuations() {
        return valuations;
    }

    public void setValuations(List<SenseValuationHistory> valuations) {
        this.valuations = valuations;
    }

    public Long getMarkednessId() {
        return markednessId;
    }

    public void setMarkednessId(Long markednessId) {
        this.markednessId = markednessId;
    }

    public String getExample1() {
        return example1;
    }

    public void setExample1(String example1) {
        this.example1 = example1;
    }

    public String getExample2() {
        return example2;
    }

    public void setExample2(String example2) {
        this.example2 = example2;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public RevisionsInfo getRevisionsInfo() {
        return revisionsInfo;
    }

    public void setRevisionsInfo(RevisionsInfo revisionsInfo) {
        this.revisionsInfo = revisionsInfo;
    }

    public int getRevType() {
        return revType;
    }

    public void setRevType(int revType) {
        this.revType = revType;
    }

    public String getConcatKeys() {
        return concatKeys;
    }

    public EmotionalAnnotationHistory getBeforeHistory() {
        return beforeHistory;
    }

    public void setBeforeHistory(EmotionalAnnotationHistory beforeHistory) {
        this.beforeHistory = beforeHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmotionalAnnotationHistory that = (EmotionalAnnotationHistory) o;
        return superAnnotation == that.superAnnotation && emotionalCharacteristic == that.emotionalCharacteristic && Objects.equals(id, that.id) && Objects.equals(senseId, that.senseId) && Objects.equals(emotions, that.emotions) && Objects.equals(valuations, that.valuations) && Objects.equals(markednessId, that.markednessId) && Objects.equals(example1, that.example1) && Objects.equals(example2, that.example2) && Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, senseId, superAnnotation, emotionalCharacteristic, emotions, valuations, markednessId, example1, example2, owner);
    }
}