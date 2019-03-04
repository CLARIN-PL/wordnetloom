package pl.edu.pwr.wordnetloom.client.ui.main;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import pl.edu.pwr.wordnetloom.client.ui.partsofspeechdialog.PartsOfSpeechDialogViewModel;

import javax.inject.Singleton;

public class MainView implements FxmlView<MainViewModel> {

	@InjectViewModel
	private MainViewModel viewModel;

}
