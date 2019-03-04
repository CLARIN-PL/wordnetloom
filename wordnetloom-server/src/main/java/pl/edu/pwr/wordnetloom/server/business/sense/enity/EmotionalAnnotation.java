package pl.edu.pwr.wordnetloom.server.business.sense.enity;

import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Markedness;
import pl.edu.pwr.wordnetloom.server.business.user.entity.User;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tbl_emotional_annotations")
@NamedQuery(name = EmotionalAnnotation.FIND_ALL_BY_SENSE_ID,
        query = "SELECT ea FROM EmotionalAnnotation ea WHERE ea.sense.id = :id")
@NamedQuery(name = EmotionalAnnotation.FIND_BY_ID,
        query = "SELECT ea FROM EmotionalAnnotation ea WHERE ea.id = :id")
public class EmotionalAnnotation{

    public static final String FIND_ALL_BY_SENSE_ID = "EmotionalAnnotation.findAllBySenseId";
    public static final String FIND_BY_ID = "EmotionalAnnotation.findById";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sense_id")
    private Sense sense;

    @Column(name = "super_anotation")
    private boolean superAnnotation;

    @Column(name = "has_emotional_characteristic")
    private boolean emotionalCharacteristic;

    @OneToMany(mappedBy = "annotation", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<SenseEmotion> emotions;

    @OneToMany(mappedBy = "annotation", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<SenseValuation> valuations;

    @ManyToOne()
    @JoinColumn(name = "markedness_id")
    private Markedness markedness;

    private String example1;

    private String example2;

    @ManyToOne()
    @JoinColumn(name = "owner")
    private User owner;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Sense getSense() {
        return sense;
    }

    public void setSense(Sense sense) {
        this.sense = sense;
    }

    public boolean isSuperAnnotation() {
        return superAnnotation;
    }

    public void setSuperAnnotation(boolean superAnnotation) {
        this.superAnnotation = superAnnotation;
    }

    public boolean isEmotionalCharacteristic() {
        return emotionalCharacteristic;
    }

    public void setEmotionalCharacteristic(boolean emotionalCharacteristic) {
        this.emotionalCharacteristic = emotionalCharacteristic;
    }

    public Set<SenseEmotion> getEmotions() {
        return emotions;
    }

    public void setEmotions(Set<SenseEmotion> emotions) {
        this.emotions = emotions;
    }

    public Set<SenseValuation> getValuations() {
        return valuations;
    }

    public void setValuations(Set<SenseValuation> valuations) {
        this.valuations = valuations;
    }

    public Markedness getMarkedness() {
        return markedness;
    }

    public void setMarkedness(Markedness markedness) {
        this.markedness = markedness;
    }

    public String getExample1() {
        return example1;
    }

    public void setExample1(String example1) {
        this.example1 = example1;
    }

    public String getExample2() {
        return example2;
    }

    public void setExample2(String example2) {
        this.example2 = example2;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
