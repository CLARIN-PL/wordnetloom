package pl.edu.pwr.wordnetloom.client.ui.search;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.*;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.CustomTextField;
import pl.edu.pwr.wordnetloom.client.events.LoadGraphEvent;
import pl.edu.pwr.wordnetloom.client.ui.DialogHelper;
import pl.edu.pwr.wordnetloom.client.ui.sensepropertiesdialog.SensePropertiesDialogView;
import pl.edu.pwr.wordnetloom.client.ui.sensepropertiesdialog.SensePropertiesDialogViewModel;

import javax.inject.Inject;

public class SearchView implements FxmlView<SearchViewModel> {

    @FXML
    public ComboBox<String> grammaticalGenderCombo, semanticFieldCombo;

    @FXML
    public ComboBox<String> semanticFieldModCombo, yiddishStatusCombo;

    @FXML
    public ComboBox<String> ageCombo,sourceCombo,lexicalCharacteristicCombo;

    @FXML
    public TextField etymologyFiled;

    @InjectViewModel
    private SearchViewModel viewModel;

    @Inject
    Stage primaryStage;

    @InjectContext
    Context context;

    @FXML
    public MenuButton modeButton;

    @FXML
    public Button searchButton, resetButton, addSenseWithSynsetButton, addSenseButton,
            addSenseToNewSynset, deleteSenseButton;

    @FXML
    public RadioMenuItem senseMode, synsetMode;

    @FXML
    public CheckBox onlyWithoutSynset;

    @FXML
    public TitledPane generalPanel,yiddishPanel, sensePanel, synsetPanel;

    public ComboBox synsetType;

    @FXML
    public TextField synsetIdInput, definitionInput, commentInput, exampleInput;

    @FXML
    private CustomTextField fieldSearch;

    @FXML
    public Label unitsCount;

    @FXML
    private ComboBox<String> lexiconInput, partOfSpeechInput,
            domainInput, statusInput, registerInput, senseRelationTypeInput, synsetRelationTypeInput;

    @FXML
    private LazyListView<SearchListItemViewModel> searchResultList;

    @FXML
    public StackPane progressOverlay;

    @FXML
    private void search() {
        searchResultList.reset();
        viewModel.getSearchCommand(searchResultList.getLimit()).execute();
    }

    @FXML
    private void reset() {
        viewModel.getResetCommand().execute();
    }

    @FXML
    public void addSenseWithSynset() {
        viewModel.getAddSenseWithSynsetCommand().execute();
    }

    @FXML
    public void addSense() {
        viewModel.getAddSenseCommand().execute();
    }

    @FXML
    public void deleteSense() {
        viewModel.deleteSenseCommand().execute();
    }

    @FXML
    public void addSenseToNewSynset() {
        viewModel.addSenseToNewSynsetCommand().execute();
        refreshSearchResultList();
        // load graph of created synset
        viewModel.loadGraphEvent.fireAsync(new LoadGraphEvent(viewModel.selectedSearchListItemProperty()
                .get().getSearchListItem()
                .getLinks().getSynsetGraph(), false,false));
    }

    private void refreshSearchResultList() {
        setListCells();
    }

    private void setListCells() {
        searchResultList.setCellFactory(CachedViewModelCellFactory.createForFxmlView(SearchListItemView.class));
    }

    private final int SEARCH_LIMIT = 100;

    public void initialize() {
        initIcons();
        senseMode();

        synsetMode.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                synsetMode();
            }
        });

        senseMode.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                senseMode();
            }
        });

        viewModel.subscribe(SearchViewModel.OPEN_ADD_SENSE_DIALOG, (key, payload) -> {
            ViewTuple<SensePropertiesDialogView, SensePropertiesDialogViewModel> load = FluentViewLoader
                    .fxmlView(SensePropertiesDialogView.class)
                    .context(context)
                    .load();

            Parent view = load.getView();
            Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/wordnetloom.css");
            load.getCodeBehind().setDisplayingStage(showDialog);
            showDialog.toFront();
        });

        senseMode.selectedProperty().bindBidirectional(viewModel.selectedSenseModeProperty());
        synsetMode.selectedProperty().bindBidirectional(viewModel.selectedSynsetModeProperty());

        progressOverlay.visibleProperty().bindBidirectional(viewModel.progressOverlayProperty());

        initSearchResultList();

        addSenseToNewSynset.setDisable(true);

        viewModel.selectedSearchListItemProperty().bind(searchResultList.getSelectionModel().selectedItemProperty());

        fieldSearch.textProperty().bindBidirectional(viewModel.fieldSearchProperty());
        definitionInput.textProperty().bindBidirectional(viewModel.definitionProperty());
        commentInput.textProperty().bindBidirectional(viewModel.commentProperty());
        exampleInput.textProperty().bindBidirectional(viewModel.exampleProperty());
        synsetIdInput.textProperty().bindBidirectional(viewModel.synsetIdProperty());
        unitsCount.textProperty().bindBidirectional(viewModel.unitsCount());
        lexiconInput.setItems(viewModel.lexiconList());
        lexiconInput.valueProperty().bindBidirectional(viewModel.selectedLexiconProperty());

        partOfSpeechInput.setItems(viewModel.partOfSpeechList());
        partOfSpeechInput.valueProperty().bindBidirectional(viewModel.selectedPartOfSpeechProperty());

        domainInput.setItems(viewModel.domainList());
        domainInput.valueProperty().bindBidirectional(viewModel.selectedDomainProperty());

        statusInput.setItems(viewModel.statusList());
        statusInput.valueProperty().bindBidirectional(viewModel.selectedStatusProperty());

        registerInput.setItems(viewModel.registerList());
        registerInput.valueProperty().bindBidirectional(viewModel.selectedRegisterProperty());

        senseRelationTypeInput.setItems(viewModel.senseRelationTypeList());
        senseRelationTypeInput.valueProperty().bindBidirectional(viewModel.selectedSenseRelationTypeProperty());

        synsetRelationTypeInput.setItems(viewModel.synsetRelationTypeList());
        synsetRelationTypeInput.valueProperty().bindBidirectional(viewModel.selectedSynsetRelationTypeProperty());

        onlyWithoutSynset.selectedProperty().bindBidirectional(viewModel.senseOnlyWithoutSynsetProperty());

        etymologyFiled.textProperty().bindBidirectional(viewModel.etymologyProperty());

        ageCombo.setItems(viewModel.getAges());
        ageCombo.valueProperty().bindBidirectional(viewModel.selectedAgeProperty());

        yiddishStatusCombo.setItems(viewModel.getYiddishStatuses());
        yiddishStatusCombo.valueProperty().bindBidirectional(viewModel.selectedYiddishStatusProperty());

        grammaticalGenderCombo.setItems(viewModel.getGrammaticalGenders());
        grammaticalGenderCombo.valueProperty().bindBidirectional(viewModel.selectedGrammaticalGenderProperty());

        semanticFieldCombo.setItems(viewModel.getSemanticFields());
        semanticFieldCombo.valueProperty().bindBidirectional(viewModel.selectedSemanticFieldProperty());

        semanticFieldModCombo.setItems(viewModel.getSemanticFieldMods());
        semanticFieldModCombo.valueProperty().bindBidirectional(viewModel.selectedSemanticFieldModProperty());

        sourceCombo.setItems(viewModel.getSources());
        sourceCombo.valueProperty().bindBidirectional(viewModel.selectedSourceProperty());

        lexicalCharacteristicCombo.setItems(viewModel.getLexicalCharacteristics());
        lexicalCharacteristicCombo.valueProperty().bindBidirectional(viewModel.selectedLexicalCharacteristicProperty());

        addChangeModeListener(senseMode);
        addChangeModeListener(synsetMode);
    }

    private void addChangeModeListener(RadioMenuItem senseMode) {
        senseMode.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                viewModel.clearList();
                addSenseToNewSynset.setDisable(true);
            }
        });
    }

    private void initSearchResultList() {
        searchResultList.setListItems(viewModel.searchListProperty());
        setListCells();
        searchResultList.setLimit(SEARCH_LIMIT);
        viewModel.setSearchLimit(SEARCH_LIMIT);

        searchResultList.setLoadListener((startIndex, limit) -> {
            synchronized (SearchView.this) {
                viewModel.getScrollCommand(startIndex, limit).execute();
            }
        });
        searchResultList.setOnMouseClicked(event -> {
            SearchListItemViewModel selectedItem = searchResultList.getSelectionModel().getSelectedItem();
            if (selectedItem != null && senseMode.isSelected()) {
                addSenseToNewSynset.setDisable(selectedItem.getSearchListItem().hasSynset());
            }

        });
        // only left mouse button select item
        searchResultList.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.isSecondaryButtonDown()) {
                event.consume();
            }
        });
    }

    private void initIcons() {
        AwesomeDude.setIcon(modeButton, AwesomeIcon.COG, "10");
        AwesomeDude.setIcon(searchButton, AwesomeIcon.SEARCH, "12");
        AwesomeDude.setIcon(resetButton, AwesomeIcon.TIMES, "12");
        AwesomeDude.setIcon(addSenseWithSynsetButton, AwesomeIcon.PLUS_SQUARE, "12");
        AwesomeDude.setIcon(addSenseButton, AwesomeIcon.PLUS, "12");
        AwesomeDude.setIcon(deleteSenseButton, AwesomeIcon.TRASH, "12");
        AwesomeDude.setIcon(addSenseToNewSynset, AwesomeIcon.SIGN_IN, "12");
    }

    private void senseMode() {
        sensePanel.setExpanded(false);
        synsetPanel.setExpanded(false);
        sensePanel.setDisable(false);
        sensePanel.setCollapsible(true);
        synsetPanel.setDisable(true);
        synsetPanel.setCollapsible(false);

    }

    private void synsetMode() {
        sensePanel.setExpanded(false);
        synsetPanel.setExpanded(false);
        sensePanel.setDisable(true);
        sensePanel.setCollapsible(false);
        synsetPanel.setDisable(false);
        synsetPanel.setCollapsible(true);
    }

}
