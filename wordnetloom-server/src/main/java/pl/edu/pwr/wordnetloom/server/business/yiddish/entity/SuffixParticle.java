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

    public SuffixParticle() {
    }

    public SuffixParticle(SuffixParticle p, YiddishSenseExtension ext) {
        setExtension(ext);
        setPosition(p.getPosition());
        suffix = p.getSuffix();
    }

    public SuffixParticle(SuffixDictionary dic) {
        suffix = dic;
    }

    public SuffixDictionary getSuffix() {
        return suffix;
    }

    public void setSuffix(SuffixDictionary suffix) {
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return suffix.getName() + " ( Suffix )";
    }
}
