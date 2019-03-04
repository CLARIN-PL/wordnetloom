package pl.edu.pwr.wordnetloom.client.ui.scopes;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import pl.edu.pwr.wordnetloom.client.model.RelationType;
import pl.edu.pwr.wordnetloom.client.model.Synset;

import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public class SynsetRelationScope {

    private final BooleanProperty makeSynsetRelationMode = new SimpleBooleanProperty(false);
    private final ObjectProperty<UUID> firstSelectedSynset = new SimpleObjectProperty<>();
    private final ObjectProperty<UUID> secondSelectedSynset = new SimpleObjectProperty<>();

    private final ObjectProperty<Synset>  parentSynset = new SimpleObjectProperty<>();
    private final ObjectProperty<Synset>  childSynset = new SimpleObjectProperty<>();
    private final ObjectProperty<RelationType>  relationType = new SimpleObjectProperty<>();

    public UUID getFirstSelectedSynset() {
        return firstSelectedSynset.get();
    }

    public ObjectProperty<UUID> firstSelectedSynsetProperty() {
        return firstSelectedSynset;
    }

    public void setFirstSelectedSynset(UUID firstSelectedSynset) {
        this.firstSelectedSynset.set(firstSelectedSynset);
    }

    public UUID getSecondSelectedSynset() {
        return secondSelectedSynset.get();
    }

    public ObjectProperty<UUID> secondSelectedSynsetProperty() {
        return secondSelectedSynset;
    }

    public void setSecondSelectedSynset(UUID secondSelectedSynset) {
        this.secondSelectedSynset.set(secondSelectedSynset);
    }

    public boolean isMakeSynsetRelationMode() {
        return makeSynsetRelationMode.get();
    }

    public BooleanProperty makeSynsetRelationModeProperty() {
        return makeSynsetRelationMode;
    }

    public void setMakeSynsetRelationMode(boolean makeSynsetRelationMode) {
        this.makeSynsetRelationMode.set(makeSynsetRelationMode);
    }

    public Synset getParentSynset() {
        return parentSynset.get();
    }

    public ObjectProperty<Synset> parentSynsetProperty() {
        return parentSynset;
    }

    public void setParentSynset(Synset parentSynset) {
        this.parentSynset.set(parentSynset);
    }

    public Synset getChildSynset() {
        return childSynset.get();
    }

    public ObjectProperty<Synset> childSynsetProperty() {
        return childSynset;
    }

    public void setChildSynset(Synset childSynset) {
        this.childSynset.set(childSynset);
    }

    public RelationType getRelationType() {
        return relationType.get();
    }

    public ObjectProperty<RelationType> relationTypeProperty() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType.set(relationType);
    }

    public void reset(){
        makeSynsetRelationMode.set(false);
        firstSelectedSynset.set(null);
        secondSelectedSynset.set(null);
        parentSynset.set(null);
        childSynset.set(null);
        relationType.set(null);
    }

    public void swapSynsets(){
        Synset p = getParentSynset();
        Synset c= getChildSynset();

        setParentSynset(c);
        setChildSynset(p);
    }
}
