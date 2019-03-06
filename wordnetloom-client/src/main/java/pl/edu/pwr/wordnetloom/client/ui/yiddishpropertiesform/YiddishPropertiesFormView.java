package pl.edu.pwr.wordnetloom.client.ui.yiddishpropertiesform;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class YiddishPropertiesFormView implements FxmlView<YiddishPropertiesFormViewModel> {

    @FXML
    public ComboBox variantTypeCombo, dialectCombo;

    @FXML
    public Button addVariant,removeVariant;

    @FXML
    public TextField yivoField, latinFiled, yiddishField;

    @FXML
    public ListView transcriptionList;

    @FXML
    public ComboBox transcriptionType;

    @FXML
    public TextField transcriptionField;

    @FXML
    public Button addTranscriptionButton, removeTranscriptionButton;

    @FXML
    public ListView semanticList;

    @FXML
    public ComboBox semanticFiledCombo, semanticFiledModCombo;

    @FXML
    public Button addSemanticFiledButton, removeSemanticRemoveButton;

    @FXML
    public TextArea meaningArea;

    @FXML
    public ComboBox grammaticalGenderCombo, styleCombo, lexicalCharacteristicCombo,
            statusCombo, ageCombo;
    @FXML
    public TextField etymologyField;
    @FXML
    public ListView sourceList;

    @FXML
    public ComboBox sourceCombo;

    @FXML
    public Button addSrcButton, removeSrcButton;

    @FXML
    public ListView inflectionList;

    @FXML
    public ComboBox inflectionCombo;

    @FXML
    public Button addInflectionButton, removeInflectionButton;

    @FXML
    public TextField inflectionField, etymologicalRootField;

    @FXML
    public TextArea commentArea, contextArea;

    @FXML
    public ListView particlesList;

    @FXML
    public ComboBox particleTypeCombo, particleCombo;

    @FXML
    public TextField rootFiled;

    @FXML
    public Button addParticleButton, removeParticleButton;

    @InjectViewModel
    private YiddishPropertiesFormViewModel viewModel;


    public void initialize() {

    }

}
