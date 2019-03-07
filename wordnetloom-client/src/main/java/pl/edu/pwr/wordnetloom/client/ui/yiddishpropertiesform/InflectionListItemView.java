package pl.edu.pwr.wordnetloom.client.ui.yiddishpropertiesform;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InflectionListItemView implements FxmlView<InflectionListItemViewModel> {

    @FXML
    public Label label;

    @InjectViewModel
    private InflectionListItemViewModel viewModel;

    public void initialize(){
        label.textProperty().bindBidirectional(viewModel.dictionaryProperty());
    }

}
