package pl.edu.pwr.wordnetloom.client.ui.yiddishpropertiesform;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TranscriptionListItemView implements FxmlView<TranscriptionListItemViewModel> {

    @FXML
    public Label label;

    @InjectViewModel
    private TranscriptionListItemViewModel viewModel;

    public void initialize(){
        label.textProperty().bindBidirectional(viewModel.dictionaryProperty());
    }

}
