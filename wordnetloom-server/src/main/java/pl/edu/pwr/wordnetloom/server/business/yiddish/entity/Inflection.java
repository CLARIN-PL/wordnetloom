package pl.edu.pwr.wordnetloom.server.business.yiddish.entity;

import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.InflectionDictionary;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "yiddish_inflection")
public class Inflection implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prefix_id", referencedColumnName = "id")
    private InflectionDictionary inflectionDictionary;

    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InflectionDictionary getInflectionDictionary() {
        return inflectionDictionary;
    }

    public void setInflectionDictionary(InflectionDictionary inflectionDictionary) {
        this.inflectionDictionary = inflectionDictionary;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return inflectionDictionary.getName() + " " + text;
    }
}
