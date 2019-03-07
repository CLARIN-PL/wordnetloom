package pl.edu.pwr.wordnetloom.client.model;

public class YiddishParticle {

    private long particle_id;

    private String type;

    private long id;

    private String value;

    public long getParticle_id() {
        return particle_id;
    }

    public void setParticle_id(long particle_id) {
        this.particle_id = particle_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "YiddishParticle{" +
                "particle_id=" + particle_id +
                ", type='" + type + '\'' +
                ", id=" + id +
                ", value='" + value + '\'' +
                '}';
    }
}
