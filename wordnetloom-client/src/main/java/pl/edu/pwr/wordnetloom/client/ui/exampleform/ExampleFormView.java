package pl.edu.pwr.wordnetloom.client.ui.exampleform;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import javax.inject.Singleton;

public class ExampleFormView implements FxmlView<ExampleFormViewModel> {

	@FXML
	public TextArea example;

	@InjectViewModel
	private ExampleFormViewModel viewModel;

	public void initialize() {
		example.textProperty().bindBidirectional(viewModel.textProperty());
	}
}
