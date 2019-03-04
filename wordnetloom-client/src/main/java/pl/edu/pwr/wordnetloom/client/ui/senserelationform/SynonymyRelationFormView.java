package pl.edu.pwr.wordnetloom.client.ui.senserelationform;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SynonymyRelationFormView implements FxmlView<SynonymyRelationFormViewModel> {

    @FXML
    public TextArea relationDescriptionAreasText;

    @FXML
    public ListView<TestListItemViewModel> testsListView;

    @FXML
    public Label sourceSenseLabel, targetSenseLabel;

    @InjectViewModel
    private SynonymyRelationFormViewModel viewModel;

    private ValidationVisualizer validationVisualizer = new ControlsFxVisualizer();

    public void initialize() {
        sourceSenseLabel.textProperty().bindBidirectional(viewModel.sourceSynsetProperty());
        targetSenseLabel.textProperty().bindBidirectional(viewModel.targetSenseProperty());

        testsListView.setItems(viewModel.getTestList());
        testsListView.setCellFactory(CachedViewModelCellFactory.createForFxmlView(TestListItemView.class));
        viewModel.selectedTestListItemProperty().bind(testsListView.getSelectionModel().selectedItemProperty());
        relationDescriptionAreasText.textProperty().bindBidirectional(viewModel.descriptionProperty());

        validationVisualizer.initVisualization(viewModel.senseInSynsetValidation(),
                sourceSenseLabel, true);
    }

}
