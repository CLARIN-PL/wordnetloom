package pl.edu.pwr.wordnetloom.client.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import pl.edu.pwr.wordnetloom.client.model.Lexicon;

public class LexiconDialogScope implements Scope {

    public static String RESET_DIALOG_PAGE = "lexicon_reset_dialog_page";
    public static String OK_BEFORE_COMMIT = "lexicon_ok_before_commit";
    public static String COMMIT = "lexicon_commit";
    public static String RESET_FORMS = "lexicon_reset";

    private final ObjectProperty<Lexicon> lexicon = new SimpleObjectProperty<>(this, "lexicon");

    private final BooleanProperty lexiconFormValid = new SimpleBooleanProperty();
    private final StringProperty dialogTitle = new SimpleStringProperty();

    public BooleanProperty lexiconFormValidProperty() {
        return this.lexiconFormValid;
    }

    public boolean getLexiconFormValid() {
        return this.lexiconFormValidProperty().get();
    }

    public void setLexiconFormValid(final boolean contactFormValid) {
        this.lexiconFormValidProperty().set(contactFormValid);
    }

    public ObjectProperty<Lexicon> lexiconProperty() {
        return this.lexicon;
    }

    public Lexicon getLexicon() {
        return this.lexiconProperty().get();
    }

    public void setLexicon(final Lexicon lexiconToEdit) {
        this.lexiconProperty().set(lexiconToEdit);
    }

    public final StringProperty dialogTitleProperty() {
        return this.dialogTitle;
    }

    public final java.lang.String getDialogTitle() {
        return this.dialogTitleProperty().get();
    }

    public final void setDialogTitle(final String dialogTitle) {
        this.dialogTitleProperty().set(dialogTitle);
    }
}
