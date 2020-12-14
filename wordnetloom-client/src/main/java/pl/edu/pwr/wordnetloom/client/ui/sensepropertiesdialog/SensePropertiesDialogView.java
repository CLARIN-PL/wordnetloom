package pl.edu.pwr.wordnetloom.client.ui.sensepropertiesdialog;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class SensePropertiesDialogView implements FxmlView<SensePropertiesDialogViewModel> {

    @FXML
    public Button saveButton, closeButton;

    @FXML
	public Tab sensePropertiesTab;

    @FXML
	public TabPane tabs;

	@InjectViewModel
	private SensePropertiesDialogViewModel viewModel;

	public Stage showDialog;

	public void initialize() {
	    initIcons();
		viewModel.subscribe(SensePropertiesDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
			showDialog.close();
		});

		saveButton.disableProperty().bind(viewModel.saveButtonDisabledProperty());
	}

	public void setDisplayingStage(Stage showDialog) {
		this.showDialog = showDialog;
		this.showDialog.titleProperty().bind(viewModel.titleProperty());
	}

	private void initIcons(){
        AwesomeDude.setIcon(saveButton, AwesomeIcon.SAVE, "11");
        AwesomeDude.setIcon(closeButton, AwesomeIcon.TIMES, "11");
	}

    public void save() {
	    viewModel.getSaveCommand().execute();
    }

	public void close() {
		viewModel.getCloseCommand().execute();
		showDialog.close();
	}
}
