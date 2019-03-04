package pl.edu.pwr.wordnetloom.client.ui.senserelationform;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.text.TextFlow;
import pl.edu.pwr.wordnetloom.client.ui.corpusexamples.CorpusExampleListItemViewModel;

public class TestListItemView implements FxmlView<TestListItemViewModel> {

	@FXML
	public TextFlow textFlow;

	@InjectViewModel
	private TestListItemViewModel viewModel;

	public void initialize() {
		textFlow.getChildren().addAll(viewModel.getChildren());
	}

}
