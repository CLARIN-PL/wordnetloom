package pl.edu.pwr.wordnetloom.client.ui.profiledialog;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

public class ProfileDialogView implements FxmlView<ProfileDialogViewModel> {

    @FXML
    public Button closeButton, saveButton;

    @InjectViewModel
    private ProfileDialogViewModel viewModel;

    public Stage showDialog;

    public void initialize() {
        initIcons();
        viewModel.subscribe(ProfileDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
            close();
        });

        viewModel.subscribe(ProfileDialogViewModel.SUCCESS_NOTIFICATION, (key, payload) -> {
                    Notifications.create()
                            .title("Success")
                            .owner(showDialog)
                            .darkStyle()
                            .position(Pos.CENTER)
                            .text("Profile saved").showInformation();
                });

        viewModel.disableSaveProperty().bind(saveButton.disabledProperty());
    }

    public void setDisplayingStage(Stage showDialog) {
        this.showDialog = showDialog;
        this.showDialog.titleProperty().bind(viewModel.titleProperty());
    }

    private void initIcons() {
        AwesomeDude.setIcon(closeButton, AwesomeIcon.TIMES, "11");
        AwesomeDude.setIcon(saveButton, AwesomeIcon.SAVE, "11");
    }

    public void close() {
        showDialog.close();
    }

    public void save() {
        viewModel.getSaveCommand().execute();
    }

}
