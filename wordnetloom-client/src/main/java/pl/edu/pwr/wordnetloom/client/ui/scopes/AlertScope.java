package pl.edu.pwr.wordnetloom.client.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import pl.edu.pwr.wordnetloom.client.model.User;

public class AlertScope implements Scope {

	public static final String RESET_MESSAGE = "reset_alert";
	public static String SHOW_ERROR_MESSAGE = "show_error_message";
	public static String SHOW_SUCCESS_MESSAGE = "show_success_message";
	public static String SHOW_INFO_MESSAGE = "show_info_message";

	private final ObjectProperty<String> message = new SimpleObjectProperty<>(this, "message");

	public ObjectProperty<String> messageProperty() {
		return this.message;
	}

	public String getMessage() {
		return this.messageProperty().get();
	}

	public void setMessage(final String message) {
		this.messageProperty().set(message);
	}
}