package pl.edu.pwr.wordnetloom.server.business.revisions;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import javax.persistence.*;
import java.util.Objects;

@Entity
@RevisionEntity(RevisionsListener.class)
@Table(name = "REVINFO")
public class RevisionsInfo {
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