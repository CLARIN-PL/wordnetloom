package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.edu.pwr.wordnetloom.client.service.UuidAdapter;

import javax.json.bind.annotation.JsonbTypeAdapter;
import java.awt.*;
import java.util.UUID;

public class SynsetRelation {

    @JsonbTypeAdapter(UuidAdapter.class)
    private UUID source;

    @JsonbTypeAdapter(UuidAdapter.class)
    private UUID target;

    @JsonbTypeAdapter(UuidAdapter.class)
    @JsonProperty("relation_type")
    private UUID relationType;

    private Color color;

    public SynsetRelation() {
    }

    public SynsetRelation(UUID source, UUID target, UUID relationType) {
        this.target = target;
        this.source = source;
        this.relationType = relationType;
    }

    public SynsetRelation withSource(UUID id) {
        this.source = id;
        return this;
    }

    public SynsetRelation withTarget(UUID id) {
        this.target = id;
        return this;
    }

    public SynsetRelation withRelationType(UUID id) {
        this.relationType = id;
        return this;
    }

    public SynsetRelation withColor(Color color) {
        this.color = color;
        return this;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public UUID getSource() {
        return source;
    }

    public void setSource(UUID source) {
        this.source = source;
    }

    public UUID getTarget() {
        return target;
    }

    public void setTarget(UUID target) {
        this.target = target;
    }

    public UUID getRelationType() {
        return relationType;
    }

    public void setRelationType(UUID relationType) {
        this.relationType = relationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SynsetRelation)) return false;

        SynsetRelation that = (SynsetRelation) o;

        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        if (target != null ? !target.equals(that.target) : that.target != null) return false;
        return relationType != null ? relationType.equals(that.relationType) : that.relationType == null;
    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (relationType != null ? relationType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SynsetRelation{" +
                "source=" + source +
                ", target=" + target +
                ", relationType=" + relationType +
                '}';
    }
}
