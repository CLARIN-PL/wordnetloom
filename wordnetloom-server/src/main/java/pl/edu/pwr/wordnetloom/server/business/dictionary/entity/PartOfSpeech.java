package pl.edu.pwr.wordnetloom.server.business.dictionary.entity;

import javax.persistence.*;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.envers.Audited;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "tbl_part_of_speech")
@Audited

@NamedQuery(name = PartOfSpeech.FIND_BY_ID, query = "SELECT p FROM PartOfSpeech p WHERE p.id = :id")
@NamedQuery(name = PartOfSpeech.FIND_ALL, query = "SELECT p FROM PartOfSpeech p")
public class PartOfSpeech implements Serializable {

    public static final String FIND_ALL = "PartOfSpeech.findAll";
    public static final String FIND_BY_ID = "PartOfSpeech.findById";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "name_id")
    private Long name;

    private String color;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getName() {
        return name;
    }

    public void setName(Long name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
