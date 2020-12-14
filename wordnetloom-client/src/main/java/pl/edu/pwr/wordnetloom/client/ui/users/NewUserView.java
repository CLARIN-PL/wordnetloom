package pl.edu.pwr.wordnetloom.client.ui.users;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXML;

public class NewUserView implements FxmlView<NewUserViewModel> {


    @FXML
    public TextField firstname, lastname, email;
    
    @FXML
    public PasswordField password;
    
    @FXML
    public Button saveButton, closeButton;

    @FXML
    public ComboBox<String> role;

    @InjectViewModel
    private NewUserViewModel viewModel;

    public Stage showDialog;

    public void initialize() {
        viewModel.subscribe(NewUserViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
            showDialog.close();
        });

        firstname.textProperty().bindBidirectional(viewModel.firstnameProperty());
        lastname.textProperty().bindBidirectional(viewModel.lastnameProperty());
        email.textProperty().bindBidirectional(viewModel.emailProperty());
        password.textProperty().bindBidirectional(viewModel.passwordProperty());

        role.setItems(viewModel.getRoles());
        role.valueProperty().bindBidirectional(viewModel.selectedRoleProperty());
    }

    public void setDisplayingStage(Stage showDialog) {
        this.showDialog = showDialog;
    }

    public void save(ActionEvent actionEvent) {
        viewModel.getSaveCommand().execute();
    }

    public void close(ActionEvent actionEvent) {
            showDialog.close();
    }
}
