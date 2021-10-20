package pl.edu.pwr.wordnetloom.client.ui.morphologyform;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MorphologyListItemView implements FxmlView<MorphologyListItemViewModel> {

    @FXML
    public Label mainLabel;

    @InjectViewModel
    private MorphologyListItemViewModel viewModel;

    private Boolean mouseEntered = false;

    public void initialize() {
        mainLabel.textProperty().bind(viewModel.labelProperty());
    }
}
