package pl.edu.pwr.wordnetloom.client.ui.alerts;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class AlertView implements FxmlView<AlertViewModel> {

	@InjectViewModel
	private AlertViewModel viewModel;

	@FXML
	public HBox alert;

	@FXML
	public Text messageInput;

	public void initialize() {
		messageInput.textProperty().bindBidirectional(viewModel.messageProperty());
		messageInput.styleProperty().bindBidirectional(viewModel.textStyleProperty());
		alert.visibleProperty().bindBidirectional(viewModel.visibleProperty());
		alert.styleProperty().bindBidirectional(viewModel.styleProperty());
	}


}
