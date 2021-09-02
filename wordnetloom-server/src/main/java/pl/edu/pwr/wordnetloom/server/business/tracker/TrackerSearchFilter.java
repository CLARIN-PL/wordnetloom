package pl.edu.pwr.wordnetloom.server.business.tracker;

import java.util.UUID;

public class TrackerSearchFilter {

    private Long dateFromTimestamp;

    private Long dateToTimestamp;

    private String user;

    private Long partOfSpeech;

    private UUID senseUuid;

    private UUID synsetUuid;

    private UUID relationTypeId;

    private int start;

    private int end;

    public Long getDateFromTimestamp() {
        return dateFromTimestamp;
    }

    public void setDateFromTimestamp(Long dateFromTimestamp) {
        this.dateFromTimestamp = dateFromTimestamp;
    }

    public Long getDateToTimestamp() {
        return dateToTimestamp;
    }

    public void setDateToTimestamp(Long dateToTimestamp) {
        this.dateToTimestamp = dateToTimestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(Long partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public UUID getSenseUuid() {
        return senseUuid;
    }

    public void setSenseUuid(UUID senseUuid) {
        this.senseUuid = senseUuid;
    }

    public UUID getSynsetUuid() {
        return synsetUuid;
    }

    public void setSynsetUuid(UUID synsetUuid) {
        this.synsetUuid = synsetUuid;
    }

    public UUID getRelationTypeId() {
        return relationTypeId;
    }

    public void setRelationTypeId(UUID relationTypeId) {
        this.relationTypeId = relationTypeId;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "TrackerSearchFilter{" +
                "dateFrom=" + dateFromTimestamp +
                ", dateTo=" + dateToTimestamp +
                ", user='" + user + '\'' +
                ", partOfSpeech='" + partOfSpeech + '\'' +
                ", senseUuid=" + senseUuid +
                ", synsetUuid=" + synsetUuid +
                ", relationType='" + relationTypeId + '\'' +
                '}';
    }
}
