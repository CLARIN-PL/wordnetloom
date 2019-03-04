package pl.edu.pwr.wordnetloom.client.ui.corpusexamples;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.TextFlow;

public class CorpusExampleListItemView implements FxmlView<CorpusExampleListItemViewModel> {

	@FXML
	public TextFlow textFlow;

	@InjectViewModel
	private CorpusExampleListItemViewModel viewModel;

	public void initialize() {
		textFlow.getChildren().addAll(viewModel.getChildren());
	}
}
