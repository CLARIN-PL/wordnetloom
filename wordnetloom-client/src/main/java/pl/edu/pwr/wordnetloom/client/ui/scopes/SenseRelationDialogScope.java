package pl.edu.pwr.wordnetloom.client.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import pl.edu.pwr.wordnetloom.client.model.RelationType;
import pl.edu.pwr.wordnetloom.client.model.Sense;
import pl.edu.pwr.wordnetloom.client.model.Synset;

public class SenseRelationDialogScope implements Scope {

    public static final String REFRESH_SENSES = "refresh_senses";
    public static String RESET_DIALOG_PAGE = "sense_relation_reset_dialog_page";
    public static String OK_BEFORE_COMMIT = "sense_relation_ok_before_commit";
    public static String COMMIT = "sense_relation_commit";
    public static String RESET_FORMS = "sense_relation_reset";

    private final ObjectProperty<Sense> parentSense = new SimpleObjectProperty<>(this, "parent_sense");
    private final ObjectProperty<Sense> childSense = new SimpleObjectProperty<>(this, "child_sense");
    private final ObjectProperty<RelationType> relationType = new SimpleObjectProperty<>(this, "relation_type");

    private final BooleanProperty senseSearchFormValid = new SimpleBooleanProperty();
    private final BooleanProperty senseRelationFormValid = new SimpleBooleanProperty();
    private final BooleanProperty bothFormsValid = new SimpleBooleanProperty();
    private final StringProperty dialogTitle = new SimpleStringProperty();

    public RelationType getRelationType() {
        return relationType.get();
    }

    public ObjectProperty<RelationType> relationTypeProperty() {
        return relationType;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType.set(relationType);
    }

    public void setParentSense(Sense parentSense) {
        this.parentSense.set(parentSense);
    }

    public void setChildSense(Sense childSense) {
        this.childSense.set(childSense);
    }

    public Sense getParentSense() {
        return parentSense.get();
    }

    public ObjectProperty<Sense> parentSenseProperty() {
        return parentSense;
    }

    public Sense getChildSense() {
        return childSense.get();
    }

    public ObjectProperty<Sense> childSenseProperty() {
        return childSense;
    }

    public boolean isSenseSearchFormValid() {
        return senseSearchFormValid.get();
    }

    public BooleanProperty senseSearchFormValidProperty() {
        return senseSearchFormValid;
    }

    public void setSenseSearchFormValid(boolean senseSearchFormValid) {
        this.senseSearchFormValid.set(senseSearchFormValid);
    }

    public boolean isSenseRelationFormValid() {
        return senseRelationFormValid.get();
    }

    public BooleanProperty senseRelationFormValidProperty() {
        return senseRelationFormValid;
    }

    public void setSenseRelationFormValid(boolean senseRelationFormValid) {
        this.senseRelationFormValid.set(senseRelationFormValid);
    }

    public boolean isBothFormsValid() {
        return bothFormsValid.get();
    }

    public BooleanProperty bothFormsValidProperty() {
        return bothFormsValid;
    }

    public void setBothFormsValid(boolean bothFormsValid) {
        this.bothFormsValid.set(bothFormsValid);
    }

    public String getDialogTitle() {
        return dialogTitle.get();
    }

    public StringProperty dialogTitleProperty() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle.set(dialogTitle);
    }

    @Override
    public String toString() {
        return "SenseRelationDialogScope{" +
                "parentSense=" + parentSense.get() +
                ", childSense=" + childSense.get() +
                ", relationType=" + relationType.get() +
                '}';
    }
}
