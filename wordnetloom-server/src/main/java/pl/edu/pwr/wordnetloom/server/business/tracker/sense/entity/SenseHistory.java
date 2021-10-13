package pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity;

import org.hibernate.annotations.Formula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Domain;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.PartOfSpeech;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Status;
import pl.edu.pwr.wordnetloom.server.business.lexicon.entity.Lexicon;
import pl.edu.pwr.wordnetloom.server.business.revisions.entity.RevisionsInfo;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.Word;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.NamedQuery;
import pl.edu.pwr.wordnetloom.server.business.tracker.BeforeHistory;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tbl_sense_AUD")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@NamedQuery(name = SenseHistory.FIND_BY_ID,
        query = "SELECT DISTINCT s FROM SenseHistory s " +
                "LEFT JOIN FETCH s.word " +
                "LEFT JOIN FETCH s.domain " +
                "LEFT JOIN FETCH s.partOfSpeech " +
                "LEFT JOIN FETCH s.lexicon " +
                "LEFT JOIN FETCH s.status " +
                "WHERE s.id = :id " +
                "ORDER BY s.rev DESC ")

@NamedQuery(name = SenseHistory.FIND_BY_SYSNET_ID,
        query = "SELECT DISTINCT s FROM SenseHistory s " +
                "LEFT JOIN FETCH s.word " +
                "LEFT JOIN FETCH s.domain " +
                "LEFT JOIN FETCH s.partOfSpeech " +
                "LEFT JOIN FETCH s.lexicon " +
                "LEFT JOIN FETCH s.status " +
                "WHERE s.synsetId = :id " +
                "ORDER BY s.rev DESC ")

@NamedQuery(name = SenseHistory.FIND_BY_TIMESTAMP,
        query = "SELECT s FROM SenseHistory s " +
                "WHERE s.revisionsInfo.timestamp >= :timestamp_start AND s.revisionsInfo.timestamp <= :timestamp_end " +
                "ORDER BY s.rev DESC ")

@NamedQuery(name = SenseHistory.FIND_BEFORE_REV,
        query = "SELECT DISTINCT s FROM SenseHistory s " +
                "WHERE s.rev < :rev AND s.id = :id " +
                "ORDER BY s.rev DESC ")

public class SenseHistory implements Serializable {
    public static final String FIND_BY_ID = "SenseHistory.findById";
    public static final String FIND_BY_SYSNET_ID = "SenseHistory.findBySynsetId";
    public static final String FIND_BY_TIMESTAMP = "SenseHistory.findByTimestamp";
    public static final String FIND_BEFORE_REV = "SenseHistory.findBeforeRev";

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Id
    @Column
    private int rev;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lexicon_id", referencedColumnName = "id", nullable = false)
    private Lexicon lexicon;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", referencedColumnName = "id", nullable = false)
    private Word word;

    @NotNull
    @Column(name = "variant", nullable = false, columnDefinition = "int default 1")
    private Integer variant = 1;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_of_speech_id", referencedColumnName = "id", nullable = false)
    private PartOfSpeech partOfSpeech;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_id", referencedColumnName = "id", nullable = false)
    private Domain domain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rev")
    @JoinColumn(name = "rev")
    private RevisionsInfo revisionsInfo;

    @Column(name = "REVTYPE", insertable = false, updatable = false)
    private int revType;

    @Column(name = "synset_id", columnDefinition = "BINARY(16)")
    private UUID synsetId;

    @Transient
    private SenseAttributesHistory senseAttributesHistory;

    @Formula("concat(BIN_TO_UUID(id), '.', REV)")
    @NotAudited
    private String concatKeys;

    @Transient
    private BeforeHistory beforeHistory;

    public SenseHistory() {
    }

    public SenseHistory(SenseAttributesHistory senseAttributesHistory) {
        this.senseAttributesHistory = senseAttributesHistory;
        this.id = senseAttributesHistory.getId();
        this.revisionsInfo = senseAttributesHistory.getRevisionsInfo();
        this.revType = senseAttributesHistory.getRevType();
    }

    public UUID getId() {
        return id;
    }

    public Lexicon getLexicon() {
        return lexicon;
    }

    public Word getWord() {
        return word;
    }

    public Integer getVariant() {
        return variant;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public Domain getDomain() {
        return domain;
    }

    public Status getStatus() {
        return status;
    }

    public RevisionsInfo getRevisionsInfo() {
        return revisionsInfo;
    }

    public int getRevType() {
        return revType;
    }

    public SenseAttributesHistory getSenseAttributesHistory() {
        return senseAttributesHistory;
    }

    public UUID getSynsetId() {
        return synsetId;
    }

    public String getConcatKeys() {
        return concatKeys;
    }

    public void setSenseAttributesHistory(SenseAttributesHistory senseAttributesHistory) {
        this.senseAttributesHistory = senseAttributesHistory;
    }

    public BeforeHistory getBeforeHistory() {
        return beforeHistory;
    }

    public void setBeforeHistory(BeforeHistory beforeHistory) {
        this.beforeHistory = beforeHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SenseHistory that = (SenseHistory) o;
        return rev == that.rev &&
                revType == that.revType &&
                Objects.equals(id, that.id) &&
                Objects.equals(lexicon, that.lexicon) &&
                Objects.equals(word, that.word) &&
                Objects.equals(variant, that.variant) &&
                Objects.equals(partOfSpeech, that.partOfSpeech) &&
                Objects.equals(domain, that.domain) &&
                Objects.equals(status, that.status) &&
                Objects.equals(revisionsInfo, that.revisionsInfo) &&
                Objects.equals(synsetId, that.synsetId) &&
                Objects.equals(senseAttributesHistory, that.senseAttributesHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rev, lexicon, word, variant, partOfSpeech, domain, status, revisionsInfo, revType, synsetId, senseAttributesHistory);
    }
}
