package pl.edu.pwr.wordnetloom.server.business.yiddish.entity;

import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.InterfixDictionary;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class InterfixParticle extends Particle {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "particle_id")
    private InterfixDictionary interfix;

    public InterfixDictionary getInterfix() {
        return interfix;
    }

    public void setInterfix(InterfixDictionary interfix) {
        this.interfix = interfix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterfixParticle)) return false;
        if (!super.equals(o)) return false;

        InterfixParticle that = (InterfixParticle) o;

        return interfix != null ? interfix.equals(that.interfix) : that.interfix == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (interfix != null ? interfix.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return interfix.getName() + " ( Interfix )";
    }
}
