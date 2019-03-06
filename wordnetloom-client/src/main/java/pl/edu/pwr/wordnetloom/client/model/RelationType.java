package pl.edu.pwr.wordnetloom.client.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import pl.edu.pwr.wordnetloom.client.service.UuidAdapter;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.NodeDirection;

import javax.json.bind.annotation.JsonbTypeAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RelationType {

    @JsonbTypeAdapter(UuidAdapter.class)
    private UUID id;

    private NodeDirection direction;

    @JsonProperty("global_wordnet_type")
    private GlobalWordnetRelationType globalWordnetRelationType;

    private String name;

    @JsonProperty("short_name")
    private String shortName;

    private String description;

    private String color;

    private String display;

    private RelationArgument argument;

    private Boolean multilingual;

    @JsonProperty("auto_reverse")
    private Boolean autoReverse;

    @JsonProperty("reverse_relation")
    private RelationType reverseRelation;

    @JsonProperty("parent_relation")
    private RelationType parentRelation;

    @JsonProperty("allowed_parts_of_speech")
    private List<Dictionary> allowedPartsOfSpeech = new ArrayList<>();

    @JsonProperty("allowed_lexicons")
    private List<Dictionary> allowedLexicons = new ArrayList<>();

    @JsonbTypeAdapter(UuidAdapter.class)
    @JsonProperty("reverse")
    private UUID reverseRelationTypeId;

    private List<RelationType> subrelations;

    @JsonProperty("_links")
    private Links links;

    @JsonProperty("_actions")
    private List<Action> actions;

    public GlobalWordnetRelationType getGlobalWordnetRelationType() {
        return globalWordnetRelationType;
    }

    public void setGlobalWordnetRelationType(GlobalWordnetRelationType globalWordnetRelationType) {
        this.globalWordnetRelationType = globalWordnetRelationType;
    }

    @JsonIgnore
    public String getNameWithType(){
        return  name != null? name :"" +" - "+ argument != null? argument.getStr():"";
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public NodeDirection getDirection() {
        return direction;
    }

    public void setDirection(NodeDirection direction) {
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public RelationArgument getArgument() {
        return argument;
    }

    public void setArgument(RelationArgument argument) {
        this.argument = argument;
    }

    public Boolean getMultilingual() {
        return multilingual;
    }

    public void setMultilingual(Boolean multilingual) {
        this.multilingual = multilingual;
    }

    public Boolean getAutoReverse() {
        return autoReverse;
    }

    public void setAutoReverse(Boolean autoReverse) {
        this.autoReverse = autoReverse;
    }

    public RelationType getReverseRelation() {
        return reverseRelation;
    }

    public void setReverseRelation(RelationType reverseRelation) {
        this.reverseRelation = reverseRelation;
    }

    public RelationType getParentRelation() {
        return parentRelation;
    }

    public void setParentRelation(RelationType parentRelation) {
        this.parentRelation = parentRelation;
    }

    public List<Dictionary> getAllowedPartsOfSpeech() {
        return allowedPartsOfSpeech;
    }

    public void setAllowedPartsOfSpeech(List<Dictionary> allowedPartsOfSpeech) {
        this.allowedPartsOfSpeech = allowedPartsOfSpeech;
    }

    public List<Dictionary> getAllowedLexicons() {
        return allowedLexicons;
    }

    public void setAllowedLexicons(List<Dictionary> allowedLexicons) {
        this.allowedLexicons = allowedLexicons;
    }

    public UUID getReverseRelationTypeId() {
        return reverseRelationTypeId;
    }

    public void setReverseRelationTypeId(UUID reverseRelationTypeId) {
        this.reverseRelationTypeId = reverseRelationTypeId;
    }

    public List<RelationType> getSubrelations() {
        return subrelations;
    }

    public void setSubrelations(List<RelationType> subrelations) {
        this.subrelations = subrelations;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public String toString() {
        return "RelationType{" +
                "id=" + id +
                ", direction=" + direction +
                ", globalWordnetRelationType=" + globalWordnetRelationType +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", description='" + description + '\'' +
                ", color='" + color + '\'' +
                ", display='" + display + '\'' +
                ", argument=" + argument +
                ", multilingual=" + multilingual +
                ", autoReverse=" + autoReverse +
                ", reverseRelation=" + reverseRelation +
                ", parentRelation=" + parentRelation +
                ", allowedPartsOfSpeech=" + allowedPartsOfSpeech +
                ", allowedLexicons=" + allowedLexicons +
                ", reverseRelationTypeId=" + reverseRelationTypeId +
                ", subrelations=" + subrelations +
                ", links=" + links +
                ", actions=" + actions +
                '}';
    }
}
