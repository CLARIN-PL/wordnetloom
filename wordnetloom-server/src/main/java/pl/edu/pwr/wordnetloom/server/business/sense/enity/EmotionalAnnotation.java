package pl.edu.pwr.wordnetloom.server.business.sense.enity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Markedness;
import pl.edu.pwr.wordnetloom.server.business.user.entity.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tbl_emotional_annotations")
public class EmotionalAnnotation implements Serializable{

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

    @OneToMany(mappedBy = "annotation", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<SenseEmotion> emotions;

    @OneToMany(mappedBy = "annotation", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<SenseValuations> valuations;

    @ManyToOne()
    @JoinColumn(name = "markedness_id")
    private Markedness markedness;

    private String example1;

    private String example2;

    @ManyToOne()
    @JoinColumn(name = "owner")
    private User owner;

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

    public Set<SenseValuations> getValuations() {
        return valuations;
    }

    public void setValuations(Set<SenseValuations> valuations) {
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
