package pl.edu.pwr.wordnetloom.client.ui.users;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.text.TextFlow;

public class UserListItemView implements FxmlView<UserListItemViewModel> {

	@FXML
	public TextFlow textFlow;

	@InjectViewModel
	private UserListItemViewModel viewModel;

	public void initialize() {
		textFlow.getChildren().addAll(viewModel.getChildren());
	}

}
