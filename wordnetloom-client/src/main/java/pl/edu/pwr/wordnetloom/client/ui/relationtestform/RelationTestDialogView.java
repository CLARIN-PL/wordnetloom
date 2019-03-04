package pl.edu.pwr.wordnetloom.client.ui.relationtestform;

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
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import pl.edu.pwr.wordnetloom.client.ui.DialogHelper;
import pl.edu.pwr.wordnetloom.client.ui.passwordchangedialog.ChangePasswordDialogView;
import pl.edu.pwr.wordnetloom.client.ui.passwordchangedialog.ChangePasswordDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.profiledialog.ProfileDialogViewModel;

public class RelationTestDialogView implements FxmlView<RelationTestDialogViewModel> {

    @FXML
    public Button closeButton, saveButton;

    @InjectViewModel
    private RelationTestDialogViewModel viewModel;

    public Stage showDialog;

    public void initialize() {
        initIcons();
        viewModel.subscribe(RelationTestDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
            close();
        });

        viewModel.subscribe(RelationTestDialogViewModel.SUCCESS_NOTIFICATION, (key, payload) -> {
                    Notifications.create()
                            .title("Operation successful")
                            .owner(showDialog)
                            .darkStyle()
                            .position(Pos.CENTER)
                            .text("Relation test saved").showInformation();
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
