package pl.edu.pwr.wordnetloom.client.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.edu.pwr.wordnetloom.client.model.User;

public class LoginDialogScope implements Scope {

	public static String RESET_DIALOG_PAGE = "user_reset_dialog_page";
	public static String OK_BEFORE_COMMIT = "user_ok_before_commit";
	public static String COMMIT = "user_commit";
	public static String RESET_FORMS = "user_reset";

	private final ObjectProperty<User> user = new SimpleObjectProperty<>(this, "user");

	private final BooleanProperty loginFormValid = new SimpleBooleanProperty();
	private final StringProperty dialogTitle = new SimpleStringProperty();

	public BooleanProperty loginFormValidProperty() {
		return this.loginFormValid;
	}

	public boolean isLoginFormValid() {
		return this.loginFormValidProperty().get();
	}

	public void setLoginFormValid(final boolean contactFormValid) {
		this.loginFormValidProperty().set(contactFormValid);
	}

	public ObjectProperty<User> userProperty() {
		return this.user;
	}

	public User getUser() {
		return this.userProperty().get();
	}

	public void setUser(final User contactToEdit) {
		this.userProperty().set(contactToEdit);
	}

	public final StringProperty dialogTitleProperty() {
		return this.dialogTitle;
	}

	public final java.lang.String getDialogTitle() {
		return this.dialogTitleProperty().get();
	}

	public final void setDialogTitle(final java.lang.String dialogTitle) {
		this.dialogTitleProperty().set(dialogTitle);
	}

}