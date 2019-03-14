package pl.edu.pwr.wordnetloom.client.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import pl.edu.pwr.wordnetloom.client.model.Sense;
import pl.edu.pwr.wordnetloom.client.model.YiddishProperties;
import pl.edu.pwr.wordnetloom.client.model.YiddishProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SensePropertiesDialogScope implements Scope {

    public static final String UPDATE_TAB_NAME = "update_tab_name";
    public static final String REMOVE_YIDDISH_PROPERTY = "remove_yiddish_property";
    public static String RESET_DIALOG_PAGE = "sense_reset_dialog_page";
    public static String OK_BEFORE_COMMIT = "sense_ok_before_commit";
    public static String COMMIT = "sense_commit";
    public static String RESET_FORMS = "sense_reset";
    public static String ADD_YIDDISH_PROPERTY = "add_yiddish_property";

    private final ObjectProperty<Sense> senseToEdit = new SimpleObjectProperty<>(this, "sense");

    private final BooleanProperty senseFormValid = new SimpleBooleanProperty();

    private final StringProperty sensePropertiesTitle = new SimpleStringProperty();

    public Sense getSenseToEdit() {
        return senseToEdit.get();
    }

    public ObjectProperty<Sense> senseToEditProperty() {
        return senseToEdit;
    }

    public void setSenseToEdit(Sense senseToEdit) {
        this.senseToEdit.set(senseToEdit);
    }

    public boolean isSenseFormValid() {
        return senseFormValid.get();
    }

    public BooleanProperty senseFormValidProperty() {
        return senseFormValid;
    }

    public void setSenseFormValid(boolean senseFormValid) {
        this.senseFormValid.set(senseFormValid);
    }

    public String getSensePropertiesTitle() {
        return sensePropertiesTitle.get();
    }

    public StringProperty sensePropertiesTitleProperty() {
        return sensePropertiesTitle;
    }

    public void setSensePropertiesTitle(String sensePropertiesTitle) {
        this.sensePropertiesTitle.set(sensePropertiesTitle);
    }

}
