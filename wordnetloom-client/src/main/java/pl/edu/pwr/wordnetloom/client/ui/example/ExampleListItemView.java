package pl.edu.pwr.wordnetloom.client.ui.example;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ExampleListItemView implements FxmlView<ExampleListItemViewModel> {

	@FXML
	public Label example, type;

	@InjectViewModel
	private ExampleListItemViewModel viewModel;

	public void initialize() {
		example.textProperty().bind(viewModel.exampleProperty());
		type.textProperty().bind(viewModel.typeProperty());
	}
}
