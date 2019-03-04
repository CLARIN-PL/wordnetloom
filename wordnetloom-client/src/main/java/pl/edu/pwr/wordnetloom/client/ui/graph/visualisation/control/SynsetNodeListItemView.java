package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.control;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;

public class SynsetNodeListItemView implements FxmlView<SynsetNodeListItemViewModel> {

	@FXML
	public Label label;

	@InjectViewModel
	private SynsetNodeListItemViewModel viewModel;

	public void initialize() {
			label.textProperty().bind(viewModel.labelProperty());
	}
}
