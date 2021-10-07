package pl.edu.pwr.wordnetloom.client.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import pl.edu.pwr.wordnetloom.client.model.EmotionalAnnotation;
import pl.edu.pwr.wordnetloom.client.model.Sense;

public class SensePropertiesDialogScope implements Scope {

    public static String RESET_DIALOG_PAGE = "sense_reset_dialog_page";
    public static String OK_BEFORE_COMMIT = "sense_ok_before_commit";
    public static String COMMIT = "sense_commit";
    public static String RESET_FORMS = "sense_reset";

    private final ObjectProperty<Sense> senseToEdit = new SimpleObjectProperty<>(this, "sense");
    private final ObjectProperty<EmotionalAnnotation> emotionalAnnotationToEdit = new SimpleObjectProperty<>(this, "emotionalAnnotation");
    private final BooleanProperty senseFormValid = new SimpleBooleanProperty();

    private final StringProperty sensePropertiesTitle = new SimpleStringProperty();

    public Sense getSenseToEdit() {
        return senseToEdit.get();
    }

    public ObjectProperty<Sense> senseToEditProperty() {
        return senseToEdit;
    }

    public EmotionalAnnotation getEmotionalAnnotationToEdit() {
        return emotionalAnnotationToEdit.get();
    }

    public void setEmotionalAnnotationToEdit(EmotionalAnnotation emotionalAnnotationToEdit) {
        this.emotionalAnnotationToEdit.set(emotionalAnnotationToEdit);
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
