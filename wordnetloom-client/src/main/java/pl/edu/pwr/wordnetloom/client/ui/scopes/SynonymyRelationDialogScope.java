package pl.edu.pwr.wordnetloom.client.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import pl.edu.pwr.wordnetloom.client.model.RelationType;
import pl.edu.pwr.wordnetloom.client.model.Sense;
import pl.edu.pwr.wordnetloom.client.model.Synset;

public class SynonymyRelationDialogScope implements Scope {

    public static final String REFRESH_SENSES = "set_sense_and_synset";
    public static String RESET_DIALOG_PAGE = "sense_relation_reset_dialog_page";
    public static String OK_BEFORE_COMMIT = "sense_relation_ok_before_commit";
    public static String COMMIT = "sense_relation_commit";
    public static String RESET_FORMS = "sense_relation_reset";

    private final ObjectProperty<Synset> synset = new SimpleObjectProperty<>(this, "synset");
    private final ObjectProperty<Sense> sense = new SimpleObjectProperty<>(this, "sense");

    private final BooleanProperty senseSearchFormValid = new SimpleBooleanProperty();
    private final BooleanProperty synonymyRelationFormValid = new SimpleBooleanProperty();
    private final BooleanProperty bothFormsValid = new SimpleBooleanProperty();
    private final StringProperty dialogTitle = new SimpleStringProperty();

    public Synset getSynset() {
        return synset.get();
    }

    public ObjectProperty<Synset> synsetProperty() {
        return synset;
    }

    public void setSynset(Synset synset) {
        this.synset.set(synset);
    }

    public Sense getSense() {
        return sense.get();
    }

    public ObjectProperty<Sense> senseProperty() {
        return sense;
    }

    public void setSense(Sense sense) {
        this.sense.set(sense);
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

    public boolean isSynonymyRelationFormValid() {
        return synonymyRelationFormValid.get();
    }

    public BooleanProperty synonymyRelationFormValidProperty() {
        return synonymyRelationFormValid;
    }

    public void setSynonymyRelationFormValid(boolean synonymyRelationFormValid) {
        this.synonymyRelationFormValid.set(synonymyRelationFormValid);
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
}
