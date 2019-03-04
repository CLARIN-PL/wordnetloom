package pl.edu.pwr.wordnetloom.client.ui.lexiconselectiondialog;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;

public class LexiconSelectionDialogView implements FxmlView<LexiconSelectionDialogViewModel> {

    @FXML
    public CheckListView<Dictionary> lexiconsListView;

    @FXML
    public Button okButton, cancelButton;

    @InjectViewModel
    LexiconSelectionDialogViewModel viewModel;

    public Stage showDialog;

    public void initialize(){
        viewModel.subscribe(LexiconSelectionDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
            showDialog.close();
        });

        lexiconsListView.setItems(viewModel.getLexiconsList());
        viewModel.setCheckModel(lexiconsListView.getCheckModel());
    }

    public void setDisplayingStage(Stage showDialog) {
        this.showDialog = showDialog;
    }

    public void ok() {
        viewModel.getOkCommand().execute();
    }

    public void close() {
        showDialog.close();
    }
}
