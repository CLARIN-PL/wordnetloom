package pl.edu.pwr.wordnetloom.server.business.yiddish.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class RootParticle extends Particle {

    @Column
    private String root;

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RootParticle)) return false;
        if (!super.equals(o)) return false;

        RootParticle that = (RootParticle) o;

        return root != null ? root.equals(that.root) : that.root == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (root != null ? root.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return root + " ( Root )";
    }
}
