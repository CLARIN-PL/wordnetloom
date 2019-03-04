package pl.edu.pwr.wordnetloom.server.business.dictionary.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Aspect extends Dictionary {

    @Column(name = "tag")
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
