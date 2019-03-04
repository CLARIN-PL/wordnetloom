package pl.edu.pwr.wordnetloom.client.ui.searchsenseform;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.*;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import pl.edu.pwr.wordnetloom.client.ui.search.SearchListItemView;
import pl.edu.pwr.wordnetloom.client.ui.search.SearchListItemViewModel;
import pl.edu.pwr.wordnetloom.client.ui.search.LazyListView;

public class SearchSenseFormView implements FxmlView<SearchSenseFormViewModel> {

	@FXML
	public TextField searchTextFiled;

	@FXML
	public CheckBox showOnlySensesWithoutSynsetCheckBox;

	@FXML
	public Button searchSensesButton;

	@FXML
	private LazyListView<SearchListItemViewModel> searchResultList;

	@FXML
	public StackPane progressOverlay;

	@InjectViewModel
	private SearchSenseFormViewModel viewModel;

	private final int SEARCH_LIMIT = 100;

	public void initialize() {
		initIcons();
        showOnlySensesWithoutSynsetCheckBox.selectedProperty().bindBidirectional(viewModel.onlySensesWithoutSynsetProperty());
		progressOverlay.visibleProperty().bindBidirectional(viewModel.progressOverlayProperty());
		searchResultList.setListItems(viewModel.searchListProperty());
		searchResultList.setCellFactory(CachedViewModelCellFactory.createForFxmlView(SearchListItemView.class));
		searchResultList.setLimit(SEARCH_LIMIT);
		viewModel.setSearchLimit(SEARCH_LIMIT);

		searchResultList.setLoadListener((startIndex, limit)->{
			synchronized (SearchSenseFormView.this){
				viewModel.getScrollCommand(startIndex, limit).execute();
			}
		});

		viewModel.selectedSearchListItemProperty().bind(searchResultList.getSelectionModel().selectedItemProperty());
		searchTextFiled.textProperty().bindBidirectional(viewModel.searchTextProperty());
    }

	public void initIcons(){
		AwesomeDude.setIcon(searchSensesButton, AwesomeIcon.SEARCH, "11");
	}

	@FXML
	private void search() {
		searchResultList.reset();
		viewModel.getSearchCommand(searchResultList.getLimit()).execute();
	}

}
