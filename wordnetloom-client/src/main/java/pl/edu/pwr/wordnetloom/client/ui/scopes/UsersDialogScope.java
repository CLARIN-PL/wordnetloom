package pl.edu.pwr.wordnetloom.client.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import pl.edu.pwr.wordnetloom.client.model.Lexicon;
import pl.edu.pwr.wordnetloom.client.model.User;
import pl.edu.pwr.wordnetloom.client.model.UserSimple;

public class UsersDialogScope implements Scope {

    public static String RESET_DIALOG_PAGE = "users_reset_dialog_page";
    public static String OK_BEFORE_COMMIT = "users_ok_before_commit";
    public static String COMMIT = "users_commit";
    public static String RESET_FORMS = "users_reset";

    private final ObjectProperty<UserSimple> user = new SimpleObjectProperty<>(this, "user");

    private final BooleanProperty userFormValid = new SimpleBooleanProperty();
    private final StringProperty dialogTitle = new SimpleStringProperty();

    public ObjectProperty<UserSimple> userProperty() {
        return this.user;
    }

    public UserSimple getUser() {
        return this.user.get();
    }

    public void setUser(final UserSimple userToEdit) {
        this.userProperty().set(userToEdit);
    }

    public final StringProperty dialogTitleProperty() {
        return this.dialogTitle;
    }

    public final String getDialogTitle() {
        return this.dialogTitleProperty().get();
    }

    public final void setDialogTitle(final String dialogTitle) {
        this.dialogTitleProperty().set(dialogTitle);
    }
}
