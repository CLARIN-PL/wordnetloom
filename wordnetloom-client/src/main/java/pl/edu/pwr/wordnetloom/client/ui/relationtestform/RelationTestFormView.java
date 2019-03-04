package pl.edu.pwr.wordnetloom.client.ui.relationtestform;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class RelationTestFormView implements FxmlView<RelationTestFormViewModel> {

    @FXML
    public ComboBox senseAPosCombo, senseBPosCombo;

    @FXML
    public TextArea testArea;

    @InjectViewModel
    private RelationTestFormViewModel viewModel;

    public void initialize() {
        testArea.textProperty().bindBidirectional(viewModel.testProperty());

        senseAPosCombo.setItems(viewModel.getPartsOfSpeechA());
        senseAPosCombo.valueProperty().bindBidirectional(viewModel.selectedPartOfSpeechAProperty());

        senseBPosCombo.setItems(viewModel.getPartsOfSpeechB());
        senseBPosCombo.valueProperty().bindBidirectional(viewModel.selectedPartOfSpeechBProperty());
    }
}
