package pl.edu.pwr.wordnetloom.server.business.revisions.entity;

import org.hibernate.envers.*;
import pl.edu.pwr.wordnetloom.server.business.revisions.control.RevisionsListener;

import javax.persistence.*;
import javax.xml.crypto.Data;

import org.hibernate.annotations.NamedQuery;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Entity
@RevisionEntity(RevisionsListener.class)
@Table(name = "REVINFO")

@NamedQuery(name = RevisionsInfo.FIND_ALL_USERS,
        query = "SELECT r.userEmail " +
                "FROM RevisionsInfo r " +
                "GROUP BY r.userEmail")

@NamedQuery(name = RevisionsInfo.FIND_USERS_BY_TIMESTAMPS,
        query = "SELECT r " +
                "FROM RevisionsInfo r " +
                "WHERE r.timestamp > :timestamp_start AND r.timestamp < :timestamp_end ")

@NamedQuery(name = RevisionsInfo.FIND_BY_ID,
        query = "SELECT distinct r " +
                "FROM RevisionsInfo r " +
                "WHERE r.id = :id")

public class RevisionsInfo {

    public static final String FIND_ALL_USERS = "RevisionsInfo.FindAllUsers";
    public static final String FIND_USERS_BY_TIMESTAMPS = "RevisionsInfo.FindUsersByTimestamp";
    public static final String FIND_BY_ID = "RevisionsInfo.FindById";

    @Id
    @GeneratedValue
    @RevisionNumber
    @Column(name = "REV")
    private int id;

    @RevisionTimestamp
    @Column(name = "REVTSTMP")
    private long timestamp;

    @Column(name = "user_email")
    private String userEmail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserWithDateHour() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        return userEmail + "." + calendar.get(Calendar.YEAR) + "."
                + calendar.get(Calendar.DAY_OF_YEAR) + "."
                + calendar.get(Calendar.HOUR_OF_DAY);
    }

    public String getUserWithDateDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));

        return userEmail + "." + calendar.get(Calendar.YEAR) + "."
                + calendar.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RevisionsInfo that = (RevisionsInfo) o;
        return id == that.id && timestamp == that.timestamp && Objects.equals(userEmail, that.userEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, userEmail);
    }
}