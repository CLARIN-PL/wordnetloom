package pl.edu.pwr.wordnetloom.client.ui.dictionarydialog;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DictionaryDialogView implements FxmlView<DictionaryDialogViewModel> {

    @FXML
    public Button saveButton, cancelButton;

	@InjectViewModel
	private DictionaryDialogViewModel viewModel;

	private Stage showDialog;

	public void initialize() {
	    initIcons();
		viewModel.subscribe(DictionaryDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
			showDialog.close();
		});
	}

	public void setDisplayingStage(Stage showDialog) {
		this.showDialog = showDialog;
		this.showDialog.titleProperty().bind(viewModel.titleProperty());
	}

	private void initIcons(){
        AwesomeDude.setIcon(saveButton, AwesomeIcon.SAVE, "11");
        AwesomeDude.setIcon(cancelButton, AwesomeIcon.TIMES, "11");
	}

    public void cancel() {
        showDialog.close();
    }

    public void save() {
	    viewModel.getSaveCommand().execute();
    }
}
