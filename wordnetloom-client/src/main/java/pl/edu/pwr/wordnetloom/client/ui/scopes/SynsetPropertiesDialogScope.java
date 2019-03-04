package pl.edu.pwr.wordnetloom.client.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import pl.edu.pwr.wordnetloom.client.model.Sense;
import pl.edu.pwr.wordnetloom.client.model.Synset;

import javax.inject.Singleton;

public class SynsetPropertiesDialogScope implements Scope {

    public static String RESET_DIALOG_PAGE = "synset_reset_dialog_page";
    public static String OK_BEFORE_COMMIT = "synset_ok_before_commit";
    public static String COMMIT = "synset_commit";
    public static String RESET_FORMS = "synset_reset";

    private final ObjectProperty<Synset> synsetToEdit = new SimpleObjectProperty<>(this, "synset");
    private final BooleanProperty synsetFormValid = new SimpleBooleanProperty();
    private final StringProperty synsetPropertiesTitle = new SimpleStringProperty();

    public Synset getSynsetToEdit() {
        return synsetToEdit.get();
    }

    public ObjectProperty<Synset> synsetToEditProperty() {
        return synsetToEdit;
    }

    public void setSynsetToEdit(Synset synsetToEdit) {
        this.synsetToEdit.set(synsetToEdit);
    }

    public boolean isSynsetFormValid() {
        return synsetFormValid.get();
    }

    public BooleanProperty synsetFormValidProperty() {
        return synsetFormValid;
    }

    public void setSynsetFormValid(boolean synsetFormValid) {
        this.synsetFormValid.set(synsetFormValid);
    }

    public String getSynsetPropertiesTitle() {
        return synsetPropertiesTitle.get();
    }

    public StringProperty synsetPropertiesTitleProperty() {
        return synsetPropertiesTitle;
    }

    public void setSynsetPropertiesTitle(String synsetPropertiesTitle) {
        this.synsetPropertiesTitle.set(synsetPropertiesTitle);
    }
}
