package pl.edu.pwr.wordnetloom.client.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.model.Example;

public class DictionaryDialogScope implements Scope {

    public static String RESET_DIALOG_PAGE = "exa_reset_dialog_page";
    public static String OK_BEFORE_COMMIT = "example_ok_before_commit";
    public static String COMMIT = "example_commit";
    public static String RESET_FORMS = "example_reset";

    private final ObjectProperty<Dictionary> dictionaryToEdit = new SimpleObjectProperty<>(this, "dictionary");
    private final BooleanProperty dictionaryFormValid = new SimpleBooleanProperty();

    public Dictionary getDictionaryToEdit() {
        return dictionaryToEdit.get();
    }

    public ObjectProperty<Dictionary> dictionaryToEditProperty() {
        return dictionaryToEdit;
    }

    public void setDictionaryToEdit(Dictionary dictionaryToEdit) {
        this.dictionaryToEdit.set(dictionaryToEdit);
    }

    public boolean isDictionaryFormValid() {
        return dictionaryFormValid.get();
    }

    public BooleanProperty dictionaryFormValidProperty() {
        return dictionaryFormValid;
    }

    public void setDictionaryFormValid(boolean dictionaryFormValid) {
        this.dictionaryFormValid.set(dictionaryFormValid);
    }
}
