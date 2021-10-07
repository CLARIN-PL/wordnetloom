package pl.edu.pwr.wordnetloom.server.business.sense.enity;

import org.hibernate.envers.Audited;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Emotion;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tbl_sense_emotions")
@Audited

@NamedQuery(name = SenseEmotion.FIND_BY_EMOTIONAL_ID,
        query = "SELECT DISTINCT se FROM SenseEmotion se " +
                "WHERE se.annotation.id = :id ")

public class SenseEmotion implements Serializable {

    public static final String FIND_BY_EMOTIONAL_ID = "SenseEmotion.FindByEmotionalId";

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annotation_id")
    private EmotionalAnnotation annotation;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion")
    private Emotion emotion;

    public SenseEmotion(){}

    public SenseEmotion(EmotionalAnnotation annotation, Emotion emotion) {
        setAnnotation(annotation);
        setEmotion(emotion);
    }

    public EmotionalAnnotation getAnnotation() {
        return annotation;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public void setAnnotation(EmotionalAnnotation annotation) {
        this.annotation = annotation;
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SenseEmotion that = (SenseEmotion) o;
        return Objects.equals(annotation, that.annotation) && Objects.equals(emotion, that.emotion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, emotion);
    }
}
