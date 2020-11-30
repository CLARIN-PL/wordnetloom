package pl.edu.pwr.wordnetloom.client.ui.users;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pl.edu.pwr.wordnetloom.client.model.UserSimple;

public class NewUserView implements FxmlView<NewUserViewModel> {


    @FXML
    public TextField firstname, lastname, email;
    
    @FXML
    public PasswordField password;
    
    @FXML
    public Button saveButton, closeButton;

    @InjectViewModel
    private NewUserViewModel viewModel;

    public Stage showDialog;

    public void initialize() {
    }

    public void setDisplayingStage(Stage showDialog) {
        this.showDialog = showDialog;
    }

    public void save(ActionEvent actionEvent) {
        UserSimple us = new UserSimple();

    }

    public void close(ActionEvent actionEvent) {
    }
}
