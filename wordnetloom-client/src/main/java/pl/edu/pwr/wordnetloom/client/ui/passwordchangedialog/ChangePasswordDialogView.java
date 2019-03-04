package pl.edu.pwr.wordnetloom.client.ui.passwordchangedialog;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class ChangePasswordDialogView implements FxmlView<ChangePasswordDialogViewModel> {

    @FXML
    public Button saveButton, closeButton;

    @FXML
    public PasswordField confirmPasswordFiled, newPasswordFiled;

    public Stage showDialog;

    private ValidationVisualizer validationVisualizer = new ControlsFxVisualizer();

    @InjectViewModel
    private ChangePasswordDialogViewModel viewModel;

    public void initialize() {
        AwesomeDude.setIcon(saveButton, AwesomeIcon.SAVE, "11");
        AwesomeDude.setIcon(closeButton, AwesomeIcon.TIMES, "11");
        confirmPasswordFiled.textProperty().bindBidirectional(viewModel.confirmPasswordProperty());
        newPasswordFiled.textProperty().bindBidirectional(viewModel.newPasswordProperty());
        validationVisualizer.initVisualization(viewModel.getNewPasswordValidator(),newPasswordFiled, true);
        validationVisualizer.initVisualization(viewModel.getConfirmPasswordValidator(), confirmPasswordFiled, true);
    }

    public void save() {
        viewModel.getSaveCommand().execute();
    }

    public void close() {
        showDialog.close();
    }

    public void setDisplayingStage(Stage showDialog) {
        this.showDialog = showDialog;
        this.showDialog.titleProperty().bind(viewModel.titleProperty());
    }
}
