package pl.edu.pwr.wordnetloom.client.ui.exampledialog;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import pl.edu.pwr.wordnetloom.client.ui.scopes.ExampleDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.sensepropertiesdialog.SensePropertiesDialogViewModel;

import javax.inject.Singleton;

public class ExampleDialogView implements FxmlView<ExampleDialogViewModel> {

    @FXML
    public Button saveButton, cancelButton;

	@InjectViewModel
	private ExampleDialogViewModel viewModel;

	private Stage showDialog;

	public void initialize() {
	    initIcons();
		viewModel.subscribe(ExampleDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
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
