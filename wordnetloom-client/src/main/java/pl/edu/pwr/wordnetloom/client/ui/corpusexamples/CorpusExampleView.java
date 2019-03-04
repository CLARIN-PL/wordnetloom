package pl.edu.pwr.wordnetloom.client.ui.corpusexamples;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import pl.edu.pwr.wordnetloom.client.ui.search.SearchListItemView;
import pl.edu.pwr.wordnetloom.client.ui.search.SearchListItemViewModel;

import javax.inject.Singleton;

@Singleton
public class CorpusExampleView implements FxmlView<CorpusExampleViewModel> {

	@InjectViewModel
	private CorpusExampleViewModel viewModel;

	@FXML
	private ListView<CorpusExampleListItemViewModel> examples;

		public void initialize(){
			examples.setItems(viewModel.examplesProperty());
			examples.setCellFactory(CachedViewModelCellFactory.createForFxmlView(CorpusExampleListItemView.class));
			viewModel.selectedCorpusExampleItemProperty().bind(examples.getSelectionModel().selectedItemProperty());
		}
}
