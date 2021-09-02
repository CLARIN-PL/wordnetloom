package pl.edu.pwr.wordnetloom.server.business.tracker.statistic.entity;

import pl.edu.pwr.wordnetloom.server.business.revisions.entity.RevisionsInfo;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.SenseHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.sense.entity.SenseRelationHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity.SynsetHistory;
import pl.edu.pwr.wordnetloom.server.business.tracker.synset.entity.SynsetRelationHistory;

import java.util.Calendar;
import java.util.Date;

public class UserStats {

    private String user;
    private Date date;

    private int senseCreated;
    private int senseModified;
    private int senseRemoved;

    private int senseRelationCreated;
    private int senseRelationModified;
    private int senseRelationRemoved;

    private int synsetCreated;
    private int synsetModified;
    private int synsetRemoved;

    private int synsetRelationCreated;
    private int synsetRelationModified;
    private int synsetRelationRemoved;

    public UserStats() {
    }

    public UserStats(RevisionsInfo revisionsInfo) {
        this.user = revisionsInfo.getUserEmail();
        this.date = new Date(revisionsInfo.getTimestamp());
    }

    public void addSenseHistory(SenseHistory senseHistory) {
        switch (senseHistory.getRevType()) {
            case 0: senseCreated++; break;
            case 1: senseModified++; break;
            case 2: senseRemoved++; break;
        }
    }

    public void addSenseRelationsHistory(SenseRelationHistory senseRelationHistory) {
        switch (senseRelationHistory.getRevType()) {
            case 0: senseRelationCreated++; break;
            case 1: senseRelationModified++; break;
            case 2: senseRelationRemoved++; break;
        }
    }

    public void addSynsetHistory(SynsetHistory synsetHistory) {
        switch (synsetHistory.getRevType()) {
            case 0: synsetCreated++; break;
            case 1: synsetModified++; break;
            case 2: synsetRemoved++; break;
        }
    }

    public void addSynsetRelationsHistory(SynsetRelationHistory synsetRelationHistory) {
        switch (synsetRelationHistory.getRevType()) {
            case 0: synsetRelationCreated++; break;
            case 1: synsetRelationModified++; break;
            case 2: synsetRelationRemoved++; break;
        }
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean checkSameDateAndHour(Date date) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(this.date);
        cal2.setTime(date);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY);
    }

    public int getSenseCreated() {
        return senseCreated;
    }

    public int getSenseModified() {
        return senseModified;
    }

    public int getSenseRemoved() {
        return senseRemoved;
    }

    public int getSenseRelationCreated() {
        return senseRelationCreated;
    }

    public int getSenseRelationModified() {
        return senseRelationModified;
    }

    public int getSenseRelationRemoved() {
        return senseRelationRemoved;
    }

    public int getSynsetCreated() {
        return synsetCreated;
    }

    public int getSynsetModified() {
        return synsetModified;
    }

    public int getSynsetRemoved() {
        return synsetRemoved;
    }

    public int getSynsetRelationCreated() {
        return synsetRelationCreated;
    }

    public int getSynsetRelationModified() {
        return synsetRelationModified;
    }

    public int getSynsetRelationRemoved() {
        return synsetRelationRemoved;
    }
}
