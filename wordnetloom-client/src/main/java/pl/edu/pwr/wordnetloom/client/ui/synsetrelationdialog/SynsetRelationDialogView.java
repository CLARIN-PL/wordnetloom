package pl.edu.pwr.wordnetloom.client.ui.synsetrelationdialog;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Singleton;

public class SynsetRelationDialogView implements FxmlView<SynsetRelationDialogViewModel> {

    @FXML
    public Button cancelButton ,okButton;

	@InjectViewModel
	private SynsetRelationDialogViewModel viewModel;

	public Stage showDialog;

	public void initialize() {
	    initIcons();
		viewModel.subscribe(SynsetRelationDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
			viewModel.reset();
			showDialog.close();
		});
	}

	public void setDisplayingStage(Stage showDialog) {
		this.showDialog = showDialog;
		this.showDialog.titleProperty().bind(viewModel.titleProperty());
        showDialog.setOnCloseRequest(event -> {
            viewModel.reset();
            showDialog.close();
        });
	}

	private void initIcons(){
        AwesomeDude.setIcon(okButton, AwesomeIcon.SAVE, "11");
        AwesomeDude.setIcon(cancelButton, AwesomeIcon.TIMES, "11");
	}

    public void cancel(){
		viewModel.reset();
		showDialog.close();
    }

    public void ok() {
	    viewModel.getOkCommand().execute();
    }

}
