package pl.edu.pwr.wordnetloom.client.ui.partsofspeechdialog;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.model.Relation;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SenseRelationDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.senserelationformdialog.SenseRelationDialogViewModel;

public class PartsOfSpeechDialogView implements FxmlView<PartsOfSpeechDialogViewModel> {

    @FXML
    public CheckListView<Dictionary> partsOfSpeechListView;

    @FXML
    public Button okButton, cancelButton;

    @InjectViewModel
    PartsOfSpeechDialogViewModel viewModel;

    public Stage showDialog;

    public void initialize(){
        viewModel.subscribe(PartsOfSpeechDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
            showDialog.close();
        });

        partsOfSpeechListView.setItems(viewModel.getPartsOfSpeechList());
        viewModel.setCheckModel(partsOfSpeechListView.getCheckModel());
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
