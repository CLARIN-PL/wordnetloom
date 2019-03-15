package pl.edu.pwr.wordnetloom.server.business.yiddish.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class ConstituentParticle extends Particle {

    @Column
    private String constituent;

    public String getConstituent() {
        return constituent;
    }

    public void setConstituent(String constituent) {
        this.constituent = constituent;
    }

    @Override
    public String toString() {
        return constituent + " ( Constituent )";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConstituentParticle)) return false;
        if (!super.equals(o)) return false;

        ConstituentParticle that = (ConstituentParticle) o;

        return constituent != null ? constituent.equals(that.constituent) : that.constituent == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (constituent != null ? constituent.hashCode() : 0);
        return result;
    }
}
