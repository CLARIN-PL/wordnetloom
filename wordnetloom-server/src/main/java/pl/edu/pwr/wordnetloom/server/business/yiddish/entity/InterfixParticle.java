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

    public InterfixParticle() {
    }

    public InterfixParticle(InterfixParticle p, YiddishSenseExtension ext) {
        setExtension(ext);
        setPosition(p.getPosition());
        interfix = p.getInterfix();
    }

    public InterfixParticle(InterfixDictionary dic) {
        interfix = dic;
    }

    public InterfixDictionary getInterfix() {
        return interfix;
    }

    public void setInterfix(InterfixDictionary interfix) {
        this.interfix = interfix;
    }

    @Override
    public String toString() {
        return interfix.getName() + " ( Interfix )";
    }
}
