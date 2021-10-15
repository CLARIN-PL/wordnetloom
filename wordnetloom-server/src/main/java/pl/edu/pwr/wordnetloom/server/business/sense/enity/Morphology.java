package pl.edu.pwr.wordnetloom.server.business.sense.enity;

import org.hibernate.annotations.NamedQuery;

import javax.persistence.*;

@NamedQuery(name = Morphology.FIND_BY_SENSE_ID,
        query = "SELECT DISTINCT m FROM Morphology m " +
                "WHERE m.sense.id = :id " +
                "ORDER BY m.id DESC ")

@NamedQuery(name = Morphology.FIND_BY_ID,
        query = "SELECT DISTINCT m FROM Morphology m " +
                "WHERE m.id = :id ")

@Entity
@Table(name = "tbl_morphology")
public class Morphology {

    public static final String FIND_BY_SENSE_ID = "Morphology.FindBySenseId";
    public static final String FIND_BY_ID = "Morphology.FindById";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sense_id")
    private Sense sense;

    @Column(name = "word_form")
    private String wordForm;

    @Column(name = "morphological_tag")
    private String morphologicalTag;


    public Long getId() {
        return id;
    }

    public Sense getSense() {
        return sense;
    }

    public void setSense(Sense sense) {
        this.sense = sense;
    }

    public String getWordForm() {
        return wordForm;
    }

    public String getMorphologicalTag() {
        return morphologicalTag;
    }
}
