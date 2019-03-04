package pl.edu.pwr.wordnetloom.client.ui.emotionsform;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import pl.edu.pwr.wordnetloom.client.ui.loginform.LoginFormViewModel;

public class EmotionFormView implements FxmlView<EmotionFormViewModel> {

	@InjectViewModel
	private EmotionFormViewModel viewModel;

	public void initialize() {
	}
}
