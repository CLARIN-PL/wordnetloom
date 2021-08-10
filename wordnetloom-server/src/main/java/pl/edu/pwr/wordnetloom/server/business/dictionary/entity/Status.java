package pl.edu.pwr.wordnetloom.server.business.dictionary.entity;

import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

@NamedQuery(
        name = Status.FIND_DEFAULT_STATUS_VALUE,
        query = "SELECT s FROM Status s WHERE s.isDefault = True")
@Entity
@Audited
public class Status extends Dictionary {

    public final static String FIND_DEFAULT_STATUS_VALUE = "Status.findDefaultValue";

    @Column(name = "color")
    private String color;

    public Status() {
        super();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
