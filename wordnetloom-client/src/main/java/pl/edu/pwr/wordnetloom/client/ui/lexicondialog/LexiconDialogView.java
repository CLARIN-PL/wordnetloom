package pl.edu.pwr.wordnetloom.client.ui.lexicondialog;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.inject.Singleton;

@Singleton
public class LexiconDialogView implements FxmlView<LexiconDialogViewModel> {

    @FXML
    public Button  closeButton, saveButton;

	@InjectViewModel
	private LexiconDialogViewModel viewModel;

	public Stage showDialog;

	public void initialize() {
	    initIcons();
		viewModel.subscribe(LexiconDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
			showDialog.close();
		});
		viewModel.disableSaveProperty().bind(saveButton.disabledProperty());
	}

	public void setDisplayingStage(Stage showDialog) {
		this.showDialog = showDialog;
		this.showDialog.titleProperty().bind(viewModel.titleProperty());
	}

	private void initIcons(){
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
