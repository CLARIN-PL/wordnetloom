package pl.edu.pwr.wordnetloom.client.ui.senserelationform;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pl.edu.pwr.wordnetloom.client.ui.search.SearchListItemView;

public class SenseRelationFormView implements FxmlView<SenseRelationFormViewModel> {

    @FXML
    public ComboBox<String> topRelationTypeComboBox, subRelationTypeComboBox;

    @FXML
    public TextArea relationDescriptionAreasText;

    @FXML
    public ListView<TestListItemViewModel> testsListView;

    @FXML
    public Button swapSensesButton;

    @FXML
    public Label sourceSenseLabel, targetSenseLabel;

    @InjectViewModel
    private SenseRelationFormViewModel viewModel;

    public void initialize() {
        initIcons();

        sourceSenseLabel.textProperty().bindBidirectional(viewModel.sourceSenseProperty());
        targetSenseLabel.textProperty().bindBidirectional(viewModel.targetSenseProperty());

        testsListView.setItems(viewModel.getTestList());
        testsListView.setCellFactory(CachedViewModelCellFactory.createForFxmlView(TestListItemView.class));
        viewModel.selectedTestListItemProperty().bind(testsListView.getSelectionModel().selectedItemProperty());

        topRelationTypeComboBox.setItems(viewModel.topRelationTypeList());
        topRelationTypeComboBox.valueProperty().bindBidirectional(viewModel.selectedTopRelationTypeProperty());
        relationDescriptionAreasText.textProperty().bindBidirectional(viewModel.descriptionProperty());
        subRelationTypeComboBox.setItems(viewModel.subRelationTypeList());
        subRelationTypeComboBox.valueProperty().bindBidirectional(viewModel.selectedSubRelationTypeProperty());
        subRelationTypeComboBox.disableProperty().bind(viewModel.subRelationTypeInputDisabledProperty());
    }

    public void initIcons() {
        AwesomeDude.setIcon(swapSensesButton, AwesomeIcon.REFRESH, "12");
    }

    @FXML
    public void swap() {
        viewModel.getSwapCommand().execute();
    }
}
