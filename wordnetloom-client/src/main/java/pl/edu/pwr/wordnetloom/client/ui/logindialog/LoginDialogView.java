package pl.edu.pwr.wordnetloom.client.ui.logindialog;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.stage.Stage;

public class LoginDialogView implements FxmlView<LoginDialogViewModel> {

	@InjectViewModel
	private LoginDialogViewModel viewModel;

	private Stage showDialog;

	public void initialize() {
		viewModel.subscribe(LoginDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
			showDialog.close();
		});
	}

	public void setDisplayingStage(Stage showDialog) {
		this.showDialog = showDialog;
	}
}
