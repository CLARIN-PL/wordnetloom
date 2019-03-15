package pl.edu.pwr.wordnetloom.server.business.yiddish.entity;

import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.SuffixDictionary;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class SuffixParticle extends Particle {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "particle_id")
    private SuffixDictionary suffix;

    public SuffixDictionary getSuffix() {
        return suffix;
    }

    public void setSuffix(SuffixDictionary suffix) {
        this.suffix = suffix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SuffixParticle)) return false;
        if (!super.equals(o)) return false;

        SuffixParticle that = (SuffixParticle) o;

        return suffix != null ? suffix.equals(that.suffix) : that.suffix == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (suffix != null ? suffix.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return suffix.getName() + " ( Suffix )";
    }
}
