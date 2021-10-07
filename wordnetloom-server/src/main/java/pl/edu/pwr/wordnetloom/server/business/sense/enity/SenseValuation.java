package pl.edu.pwr.wordnetloom.server.business.sense.enity;

import org.hibernate.envers.Audited;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Valuation;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tbl_sense_valuations")
@Audited

@NamedQuery(name = SenseValuation.FIND_BY_EMOTIONAL_ID,
        query = "SELECT DISTINCT sv FROM SenseValuation sv " +
                "WHERE sv.annotation.id = :id")

public class SenseValuation implements Serializable {

    public static final String FIND_BY_EMOTIONAL_ID = "SenseValuation.FindByEmotionalId";

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annotation_id")
    private EmotionalAnnotation annotation;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "valuation")
    private Valuation valuation;

    public SenseValuation(){}

    public SenseValuation(EmotionalAnnotation annotation, Valuation valuation) {
        setEmotionalAnnotation(annotation);
        setValuation(valuation);
    }

    public EmotionalAnnotation getAnnotation() {
        return annotation;
    }

    public Valuation getValuation(){
        return valuation;
    }

    public void setEmotionalAnnotation(EmotionalAnnotation annotation) {
        this.annotation = annotation;
    }

    public void setValuation(Valuation valuation) {
        this.valuation = valuation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SenseValuation that = (SenseValuation) o;
        return Objects.equals(annotation, that.annotation) && Objects.equals(valuation, that.valuation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, valuation);
    }
}
