package pl.edu.pwr.wordnetloom.server.business.relationtype.entity;

import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.PartOfSpeech;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "tbl_relation_tests")
@NamedQuery(name = RelationTest.FIND_ALL_BY_RELATION_TYPE_ID,
        query = "SELECT rt  FROM RelationTest rt WHERE rt.relationType.id = :relId")
@NamedQuery(name = RelationTest.FIND_BY_ID_AND_RELATION_TYPE_ID,
        query = "SELECT rt  FROM RelationTest rt WHERE rt.relationType.id = :relId and rt.id =:testId")
@NamedQuery(name = RelationTest.FIND_NEXT_POSITION,
        query = "SELECT MAX(rt.position)  FROM RelationTest rt WHERE rt.relationType.id = :relId")
public class RelationTest implements Serializable {

    public static final String FIND_ALL_BY_RELATION_TYPE_ID = "RelationTest.findAllByRelationTypeId";
    public static final String FIND_BY_ID_AND_RELATION_TYPE_ID = "RelationTest.findByIdAndRelationTypeId";
    public static final String FIND_NEXT_POSITION = "RelationTest.findNextPosition";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    private String test;

    @ManyToOne
    @JoinColumn(name = "element_A_part_of_speech_id", referencedColumnName = "id")
    private PartOfSpeech senseApartOfSpeech;

    @ManyToOne
    @JoinColumn(name = "element_B_part_of_speech_id", referencedColumnName = "id")
    private PartOfSpeech senseBpartOfSpeech;

    @Column(name = "position", nullable = false, columnDefinition = "int default 0")
    private Integer position;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "relation_type_id", nullable = false)
    private RelationType relationType;

    public RelationTest() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RelationTest(RelationType relationType) {
        this.relationType = relationType;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public PartOfSpeech getSenseApartOfSpeech() {
        return senseApartOfSpeech;
    }

    public void setSenseApartOfSpeech(PartOfSpeech senseApartOfSpeech) {
        this.senseApartOfSpeech = senseApartOfSpeech;
    }

    public PartOfSpeech getSenseBpartOfSpeech() {
        return senseBpartOfSpeech;
    }

    public void setSenseBpartOfSpeech(PartOfSpeech senseBpartOfSpeech) {
        this.senseBpartOfSpeech = senseBpartOfSpeech;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    @Override
    public String toString() {
        return  test;
    }
}
