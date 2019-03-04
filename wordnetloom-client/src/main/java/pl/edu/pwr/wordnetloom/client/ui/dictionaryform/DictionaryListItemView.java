package pl.edu.pwr.wordnetloom.client.ui.dictionaryform;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DictionaryListItemView implements FxmlView<DictionaryListItemViewModel> {

    @FXML
    public Label label;

    @InjectViewModel
    private DictionaryListItemViewModel viewModel;

    public void initialize(){
        label.textProperty().bindBidirectional(viewModel.dictionaryProperty());
    }

}
