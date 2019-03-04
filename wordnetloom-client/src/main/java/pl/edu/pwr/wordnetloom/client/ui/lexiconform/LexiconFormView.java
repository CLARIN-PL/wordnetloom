package pl.edu.pwr.wordnetloom.client.ui.lexiconform;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import pl.edu.pwr.wordnetloom.client.ui.dictionaryform.DictionaryListItemView;
import pl.edu.pwr.wordnetloom.client.ui.dictionaryform.DictionaryListItemViewModel;

public class LexiconFormView implements FxmlView<LexiconViewModel> {

    @FXML
    public Button addButton, removeButton;

    @FXML
    public ListView<DictionaryListItemViewModel> lexiconsViewList;

    @FXML
    public TextField nameField, identifierField, languageField, referenceUrlField;

    @FXML
    public TextField versionField, languageShortNameField, licenseField, emailField, confidenceScoreField, citationField;

    @InjectViewModel
    private LexiconViewModel viewModel;

    private ValidationVisualizer validationVisualizer = new ControlsFxVisualizer();

    public void initialize() {
        AwesomeDude.setIcon(addButton, AwesomeIcon.PLUS, "11");
        AwesomeDude.setIcon(removeButton, AwesomeIcon.TRASH, "11");

        lexiconsViewList.setItems(viewModel.getLexiconList());
        lexiconsViewList.setCellFactory(CachedViewModelCellFactory.createForFxmlView(DictionaryListItemView.class));
        viewModel.selectedLexiconListItemProperty().bind(lexiconsViewList.getSelectionModel().selectedItemProperty());

        nameField.textProperty().bindBidirectional(viewModel.nameProperty());
        identifierField.textProperty().bindBidirectional(viewModel.identifierProperty());
        languageField.textProperty().bindBidirectional(viewModel.languageProperty());
        languageShortNameField.textProperty().bindBidirectional(viewModel.shortNameProperty());
        versionField.textProperty().bindBidirectional(viewModel.versionProperty());
        licenseField.textProperty().bindBidirectional(viewModel.licenseProperty());
        emailField.textProperty().bindBidirectional(viewModel.emailProperty());

        citationField.textProperty().bindBidirectional(viewModel.citationProperty());
        confidenceScoreField.textProperty().bindBidirectional(viewModel.confidenceScoreProperty());
        referenceUrlField.textProperty().bindBidirectional(viewModel.referenceUrlProperty());

        removeButton.disableProperty().bind(viewModel.removeButtonDisabledProperty());


        validationVisualizer.initVisualization(viewModel.getNameValidator(), nameField, true);
        validationVisualizer.initVisualization(viewModel.getIdentifierValidator(), identifierField, true);
        validationVisualizer.initVisualization(viewModel.getLanguageValidator(), languageField, true);

    }

    @FXML
    public void add() {
        viewModel.getAddCommand().execute();
    }

    @FXML
    public void remove() {
        viewModel.getRemoveCommand().execute();
    }
}
