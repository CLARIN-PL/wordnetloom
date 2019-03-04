package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RelationTest {

    private Long id;

    private String test;

    @JsonProperty("sense_a_pos")
    private Long senseAPos;

    @JsonProperty("sense_b_pos")
    private Long senseBPos;

    private Integer position;

    @JsonProperty("_links")
    private Links links;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public Long getSenseAPos() {
        return senseAPos;
    }

    public void setSenseAPos(Long senseAPos) {
        this.senseAPos = senseAPos;
    }

    public Long getSenseBPos() {
        return senseBPos;
    }

    public void setSenseBPos(Long senseBPos) {
        this.senseBPos = senseBPos;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "RelationTest{" +
                "id=" + id +
                ", test='" + test + '\'' +
                ", senseAPos=" + senseAPos +
                ", senseBPos=" + senseBPos +
                ", position=" + position +
                ", links=" + links +
                '}';
    }
}
