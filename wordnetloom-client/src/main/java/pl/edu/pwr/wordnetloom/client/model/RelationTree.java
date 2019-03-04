package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RelationTree {

    @JsonProperty("relation_type_id")
    private Long relationTypeId;

    @JsonProperty("relation_type_name")
    private String relationTypeName;

    private List<SenseRelation> rows;

    public Long getRelationTypeId() {
        return relationTypeId;
    }

    public void setRelationTypeId(Long relationTypeId) {
        this.relationTypeId = relationTypeId;
    }

    public String getRelationTypeName() {
        return relationTypeName;
    }

    public void setRelationTypeName(String relationTypeName) {
        this.relationTypeName = relationTypeName;
    }

    public List<SenseRelation> getRows() {
        return rows;
    }

    public void setRows(List<SenseRelation> rows) {
        this.rows = rows;
    }
}
