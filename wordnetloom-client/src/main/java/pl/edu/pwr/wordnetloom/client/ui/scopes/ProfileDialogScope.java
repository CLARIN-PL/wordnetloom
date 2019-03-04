package pl.edu.pwr.wordnetloom.client.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ProfileDialogScope implements Scope {

    public static String OK_BEFORE_COMMIT = "user_ok_before_commit";
    public static String COMMIT = "user_commit";
    public static String RESET_FORMS = "user_reset";

    private final BooleanProperty userFormValid = new SimpleBooleanProperty();

    public boolean isUserFormValid() {
        return userFormValid.get();
    }

    public BooleanProperty userFormValidProperty() {
        return userFormValid;
    }

}
