package pl.edu.pwr.wordnetloom.client.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import pl.edu.pwr.wordnetloom.client.model.Example;
import pl.edu.pwr.wordnetloom.client.model.Sense;

import javax.inject.Singleton;

public class ExampleDialogScope implements Scope {

    public static String RESET_DIALOG_PAGE = "example_reset_dialog_page";
    public static String OK_BEFORE_COMMIT = "example_ok_before_commit";
    public static String ON_LOAD = "example_on_load";
    public static String COMMIT = "example_commit";
    public static String AFTER_COMMIT = "after_example_commit";
    public static String CLOSE = "example_close";
    public static String RESET_FORMS = "example_reset";

    private final ObjectProperty<Example> exampleToEdit = new SimpleObjectProperty<>(this, "example");
    private final BooleanProperty exampleFormValid = new SimpleBooleanProperty();
    private final BooleanProperty synsetExample = new SimpleBooleanProperty(false);

    public Example getExampleToEdit() {
        return exampleToEdit.get();
    }

    public ObjectProperty<Example> exampleToEditProperty() {
        return exampleToEdit;
    }

    public void setExampleToEdit(Example exampleToEdit) {
        this.exampleToEdit.set(exampleToEdit);
    }

    public boolean isExampleFormValid() {
        return exampleFormValid.get();
    }

    public BooleanProperty exampleFormValidProperty() {
        return exampleFormValid;
    }

    public void setExampleFormValid(boolean exampleFormValid) {
        this.exampleFormValid.set(exampleFormValid);
    }

    public boolean isSynsetExample() {
        return synsetExample.get();
    }

    public BooleanProperty synsetExampleProperty() {
        return synsetExample;
    }

    public void setSynsetExample(boolean synsetExample) {
        this.synsetExample.set(synsetExample);
    }
}
