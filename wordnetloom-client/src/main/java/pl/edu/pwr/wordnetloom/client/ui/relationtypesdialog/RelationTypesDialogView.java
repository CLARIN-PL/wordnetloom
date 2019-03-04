package pl.edu.pwr.wordnetloom.client.ui.relationtypesdialog;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import pl.edu.pwr.wordnetloom.client.ui.profiledialog.ProfileDialogViewModel;

import javax.inject.Singleton;

public class RelationTypesDialogView implements FxmlView<RelationTypesDialogViewModel> {

    @FXML
    public Button closeButton, saveButton, createButton, deleteButton;

    @InjectViewModel
    private RelationTypesDialogViewModel viewModel;

    public Stage showDialog;

    public void initialize() {
        initIcons();
        viewModel.subscribe(RelationTypesDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
            showDialog.close();
        });

        viewModel.subscribe(RelationTypesDialogViewModel.SUCCESS_NOTIFICATION, (key, payload) -> {
            Notifications.create()
                    .title("Operation successful")
                    .owner(showDialog)
                    .darkStyle()
                    .position(Pos.CENTER)
                    .text("Relation saved").showInformation();
        });
    }

    public void setDisplayingStage(Stage showDialog) {
        this.showDialog = showDialog;
        this.showDialog.titleProperty().bind(viewModel.titleProperty());
    }

    private void initIcons() {
        AwesomeDude.setIcon(closeButton, AwesomeIcon.TIMES, "11");
        AwesomeDude.setIcon(saveButton, AwesomeIcon.SAVE, "11");
        AwesomeDude.setIcon(deleteButton, AwesomeIcon.TRASH, "11");
        AwesomeDude.setIcon(createButton, AwesomeIcon.PLUS, "11");
    }

    public void close() {
        showDialog.close();
    }

    public void save() {
        viewModel.getSaveCommand().execute();
    }

    public void create() {
        viewModel.getCreateCommand().execute();
    }

    public void delete() {
        viewModel.getDeleteCommand().execute();
    }
}
