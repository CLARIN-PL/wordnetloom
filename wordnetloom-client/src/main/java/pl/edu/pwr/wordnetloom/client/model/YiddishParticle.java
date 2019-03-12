package pl.edu.pwr.wordnetloom.client.model;

public class YiddishParticle {

    private long id;

    private Dictionary particle;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Dictionary getParticle() {
        return particle;
    }

    public void setParticle(Dictionary particle) {
        this.particle = particle;
    }
}
