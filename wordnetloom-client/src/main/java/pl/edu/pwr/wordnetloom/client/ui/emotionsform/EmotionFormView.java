package pl.edu.pwr.wordnetloom.client.ui.emotionsform;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;

import java.util.List;

public class EmotionFormView implements FxmlView<EmotionFormViewModel> {

    private final int CHECK_BOXES_COLUMNS = 2;

    @InjectViewModel
    private EmotionFormViewModel viewModel;

    @FXML
    private GridPane emotionsGrid;

    @FXML
    private GridPane valuationsGrid;

    @FXML
    private ComboBox markednessCombo;

    @FXML
    private RadioButton neutralRadioButton;

    @FXML
    private RadioButton superRadioButton;

    @FXML
    private TextArea example1;

    @FXML
    private TextArea example2;

    public void initialize() {
        initEmotionsCheckBoxes();
        initValuationCheckBoxes();
        initMarkednessComboBox();
        initRadioButtons();
        initExamples();
    }

    private void initEmotionsCheckBoxes() {
        List<Dictionary> emotions = viewModel.getEmotionsList();
        for(int i = 0; i < emotions.size(); i++){
            String name = emotions.get(i).getName();
            CheckBox checkBox = new CheckBox(name);
            checkBox.selectedProperty().bindBidirectional(viewModel.getEmotionProperty(i));
            insertCheckBox(emotionsGrid, checkBox, i);
        }
    }

    private void insertCheckBox(GridPane grid, CheckBox checkBox, int position) {
        grid.add(checkBox, position %CHECK_BOXES_COLUMNS, position /CHECK_BOXES_COLUMNS);
    }

    private void initValuationCheckBoxes() {
        List<Dictionary> valuations = viewModel.getValuationsList();
        for(int i = 0; i < valuations.size(); i++) {
            String name = valuations.get(i).getName();
            CheckBox checkBox = new CheckBox(name);
            checkBox.selectedProperty().bindBidirectional(viewModel.getValuationProperty(i));
            insertCheckBox(valuationsGrid, checkBox, i);
        }
    }

    private void initMarkednessComboBox(){
        markednessCombo.setItems(viewModel.getMarkednessList());
        markednessCombo.valueProperty().bindBidirectional(viewModel.selectedMarkednessProperty());
    }

    private void initRadioButtons() {
        superRadioButton.selectedProperty().bindBidirectional(viewModel.superProperty());
        neutralRadioButton.selectedProperty().bindBidirectional(viewModel.neutralProperty());
    }

    private void initExamples() {
        example1.textProperty().bindBidirectional(viewModel.example1Property());
        example2.textProperty().bindBidirectional(viewModel.example2Property());
    }
}