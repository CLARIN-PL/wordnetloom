package pl.edu.pwr.wordnetloom.client.ui.emotionsform;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

import java.util.List;

public class EmotionFormView implements FxmlView<EmotionFormViewModel> {

    @InjectViewModel
    private EmotionFormViewModel viewModel;

    @FXML
    private GridPane emotionGrid;

    public void initialize() {
        initEmotionsCheckBoxes();
    }

    private void initEmotionsCheckBoxes() {
        final int COLUMNS = 2;
        List<String> emotionsNames = viewModel.getEmotionsNames();
        for(int i =0; i<emotionsNames.size(); i++){
            String name = emotionsNames.get(i);
            CheckBox checkBox = new CheckBox();
            checkBox.setText(name);
            checkBox.selectedProperty().bindBidirectional(viewModel.getEmotionProperty(i));
            emotionGrid.add(checkBox, i%COLUMNS, i/COLUMNS);
        }
    }
}
