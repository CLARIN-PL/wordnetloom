package pl.edu.pwr.wordnetloom.client.ui.profiledialog;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import pl.edu.pwr.wordnetloom.client.ui.DialogHelper;
import pl.edu.pwr.wordnetloom.client.ui.passwordchangedialog.ChangePasswordDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.passwordchangedialog.ChangePasswordDialogView;
import pl.edu.pwr.wordnetloom.client.ui.synsetproperties.SynsetPropertiesViewModel;

import javax.inject.Singleton;

public class ProfileDialogView implements FxmlView<ProfileDialogViewModel> {

    @FXML
    public Button closeButton, changePassword, saveButton;

    @InjectViewModel
    private ProfileDialogViewModel viewModel;

    public Stage showDialog;

    public void initialize() {
        initIcons();
        viewModel.subscribe(ProfileDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
            close();
        });

        viewModel.subscribe(ProfileDialogViewModel.OPEN_CHANGE_PASSWORD_DIALOG, (key, payload) -> {

            ViewTuple<ChangePasswordDialogView, ChangePasswordDialogViewModel> load = FluentViewLoader
                    .fxmlView(ChangePasswordDialogView.class)
                    .load();

            Parent view = load.getView();
            Stage passwordShowDialog = DialogHelper.showDialog(view, showDialog, "/wordnetloom.css");
            load.getCodeBehind().setDisplayingStage(passwordShowDialog);
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

    public void changePassword() {
        viewModel.getChangePasswordCommand().execute();
    }
}
