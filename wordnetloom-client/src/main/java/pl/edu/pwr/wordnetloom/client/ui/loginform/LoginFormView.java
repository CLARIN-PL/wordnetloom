package pl.edu.pwr.wordnetloom.client.ui.loginform;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class LoginFormView implements FxmlView<LoginFormViewModel> {

	@InjectViewModel
	private LoginFormViewModel viewModel;

	@FXML
	public TextField usernameInput;

	@FXML
	public PasswordField passwordInput;

	@FXML
	public ComboBox<String> languageInput;

	@FXML
	private void signIn() {
		viewModel.signInAction();
	}

	public void initialize() {
		usernameInput.textProperty().bindBidirectional(viewModel.usernameProperty());
		passwordInput.textProperty().bindBidirectional(viewModel.passwordProperty());

		languageInput.setItems(viewModel.languagesList());
		languageInput.valueProperty().bindBidirectional(viewModel.selectedLanguageProperty());
	}
}
