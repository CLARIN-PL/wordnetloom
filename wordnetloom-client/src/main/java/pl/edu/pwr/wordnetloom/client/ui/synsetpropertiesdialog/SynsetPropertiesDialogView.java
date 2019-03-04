package pl.edu.pwr.wordnetloom.client.ui.synsetpropertiesdialog;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class SynsetPropertiesDialogView implements FxmlView<SynsetPropertiesDialogViewModel> {

    @InjectViewModel
    private SynsetPropertiesDialogViewModel viewModel;

    public Stage showDialog;

    public void initialize() {
        viewModel.subscribe(SynsetPropertiesDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
            showDialog.close();
        });

    }

    public void setDisplayingStage(Stage showDialog) {
        this.showDialog = showDialog;
        showDialog.titleProperty().bindBidirectional(viewModel.titleProperty());
        showDialog.setOnCloseRequest(event -> {
            viewModel.refresh();
        });
    }

    @FXML
    public void save() {
        viewModel.getSaveCommand().execute();
    }

    @FXML
    public void cancel() {
        viewModel.getCancelCommand().execute();
        showDialog.close();
    }
}
