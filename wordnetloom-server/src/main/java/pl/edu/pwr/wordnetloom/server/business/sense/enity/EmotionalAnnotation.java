package pl.edu.pwr.wordnetloom.server.business.sense.enity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NamedQuery;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Markedness;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@NamedQuery(name = EmotionalAnnotation.FIND_BY_SENSE_ID,
        query = "SELECT DISTINCT e FROM EmotionalAnnotation e " +
                "WHERE e.sense.id = :id ")

@Entity
@Table(name = "tbl_emotional_annotations")
public class EmotionalAnnotation implements Serializable{

    public static final String FIND_BY_SENSE_ID = "EmotionalAnnotation.FindBySenseId";

    @Id
    @GeneratedValue (generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sense_id")
    private Sense sense;

    @Column(name = "super_anotation")
    private boolean superAnnotation;

    @Column(name = "has_emotional_characteristic")
    private boolean emotionalCharacteristic;

    @OneToMany(mappedBy = "annotation")
    private Set<SenseEmotion> emotions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "annotation")
    private Set<SenseValuation> valuations = new LinkedHashSet<>();

    @ManyToOne()
    @JoinColumn(name = "markedness_id")
    private Markedness markedness;

    @Column(name = "example1")
    private String example1;

    @Column(name = "example2")
    private String example2;

    @Column(name = "user_name")
    private String owner;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmotionalAnnotation that = (EmotionalAnnotation) o;
        return superAnnotation == that.superAnnotation && emotionalCharacteristic == that.emotionalCharacteristic && Objects.equals(id, that.id) && Objects.equals(sense, that.sense);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sense, superAnnotation, emotionalCharacteristic);
    }
}