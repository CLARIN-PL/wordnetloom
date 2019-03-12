package pl.edu.pwr.wordnetloom.client.ui.yiddishpropertiesform;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pl.edu.pwr.wordnetloom.client.ui.dictionaryform.DictionaryListItemView;
import pl.edu.pwr.wordnetloom.client.ui.dictionaryform.DictionaryListItemViewModel;

public class YiddishPropertiesFormView implements FxmlView<YiddishPropertiesFormViewModel> {

    @FXML
    public ComboBox<String> variantTypeCombo, dialectCombo;

    @FXML
    public Button addVariant,removeVariant;

    @FXML
    public TextField yivoField, latinFiled, yiddishField;

    @FXML
    public ListView<TranscriptionListItemViewModel> transcriptionList;

    @FXML
    public ComboBox<String> transcriptionType;

    @FXML
    public TextField transcriptionField;

    @FXML
    public Button addTranscriptionButton, removeTranscriptionButton;

    @FXML
    public ListView<SemanticFieldListItemViewModel> semanticList;

    @FXML
    public ComboBox<String> semanticFiledCombo, semanticFiledModCombo;

    @FXML
    public Button addSemanticFiledButton, removeSemanticRemoveButton;

    @FXML
    public TextArea meaningArea;

    @FXML
    public ComboBox<String> grammaticalGenderCombo, styleCombo, lexicalCharacteristicCombo,
            statusCombo, ageCombo, sourceCombo, inflectionCombo;
    @FXML
    public TextField etymologyField;

    @FXML
    public ListView<DictionaryListItemViewModel> sourceList;

    @FXML
    public Button addSrcButton, removeSrcButton;

    @FXML
    public ListView<InflectionListItemViewModel> inflectionList;

    @FXML
    public Button addInflectionButton, removeInflectionButton;

    @FXML
    public TextField inflectionField, etymologicalRootField;

    @FXML
    public TextArea commentArea, contextArea;

    @FXML
    public ListView<ParticleListItemViewModel> particlesList;

    @FXML
    public ComboBox<String> particleTypeCombo, particleCombo;

    @FXML
    public TextField rootFiled;

    @FXML
    public Button addParticleButton, removeParticleButton;

    @InjectViewModel
    private YiddishPropertiesFormViewModel viewModel;


    public void initialize() {

        yivoField.textProperty().bindBidirectional(viewModel.yivoProperty());
        latinFiled.textProperty().bindBidirectional(viewModel.latinProperty());
        yiddishField.textProperty().bindBidirectional(viewModel.yiddishProperty());

        transcriptionField.textProperty().bindBidirectional(viewModel.transcriptionProperty());

        meaningArea.textProperty().bindBidirectional(viewModel.meaningProperty());

        etymologyField.textProperty().bindBidirectional(viewModel.etymologyProperty());

        inflectionField.textProperty().bindBidirectional(viewModel.inflectionProperty());

        etymologicalRootField.textProperty().bindBidirectional(viewModel.etymologicalRootProperty());

        commentArea.textProperty().bindBidirectional(viewModel.commentProperty());
        contextArea.textProperty().bindBidirectional(viewModel.contextProperty());
        rootFiled.textProperty().bindBidirectional(viewModel.rootProperty());

        variantTypeCombo.setItems(viewModel.getVariantTypes());
        variantTypeCombo.valueProperty().bindBidirectional(viewModel.selectedVariantTypeProperty());

        particleTypeCombo.setItems(viewModel.getParticleTypes());
        particleTypeCombo.valueProperty().bindBidirectional(viewModel.selectedParticleTypeProperty());

        ageCombo.setItems(viewModel.getAges());
        ageCombo.valueProperty().bindBidirectional(viewModel.selectedAgeProperty());

        statusCombo.setItems(viewModel.getStatuses());
        statusCombo.valueProperty().bindBidirectional(viewModel.selectedStatusProperty());

        transcriptionType.setItems(viewModel.getTranscriptions());
        transcriptionType.valueProperty().bindBidirectional(viewModel.selectedTranscriptionProperty());

        styleCombo.setItems(viewModel.getStyles());
        styleCombo.valueProperty().bindBidirectional(viewModel.selectedStyleProperty());

        lexicalCharacteristicCombo.setItems(viewModel.getLexicalCharacteristics());
        lexicalCharacteristicCombo.valueProperty().bindBidirectional(viewModel.selectedLexicalCharacteristicProperty());

        grammaticalGenderCombo.setItems(viewModel.getGrammaticalGenders());
        grammaticalGenderCombo.valueProperty().bindBidirectional(viewModel.selectedGrammaticalGenderProperty());

        sourceCombo.setItems(viewModel.getSources());
        sourceCombo.valueProperty().bindBidirectional(viewModel.selectedSourceProperty());

        inflectionCombo.setItems(viewModel.getInflectionsCmb());
        inflectionCombo.valueProperty().bindBidirectional(viewModel.selectedInflectionCmbProperty());

        semanticFiledCombo.setItems(viewModel.getSemanticFields());
        semanticFiledCombo.valueProperty().bindBidirectional(viewModel.selectedSemanticFieldProperty());

        semanticFiledModCombo.setItems(viewModel.getSemanticFieldMods());
        semanticFiledModCombo.valueProperty().bindBidirectional(viewModel.selectedSemanticFieldModProperty());

        sourceList.setItems(viewModel.getSourceList());
        sourceList.setCellFactory(CachedViewModelCellFactory.createForFxmlView(DictionaryListItemView.class));
        viewModel.selectedSourceListItemProperty().bind(sourceList.getSelectionModel().selectedItemProperty());

        semanticList.setItems(viewModel.getSemanticFiledList());
        semanticList.setCellFactory(CachedViewModelCellFactory.createForFxmlView(SemanticFieldListItemView.class));
        viewModel.selectedSemanticFieldListItemProperty().bind(semanticList.getSelectionModel().selectedItemProperty());

        inflectionList.setItems(viewModel.getInflectionFiledList());
        inflectionList.setCellFactory(CachedViewModelCellFactory.createForFxmlView(InflectionListItemView.class));
        viewModel.selectedInflectionListItemProperty().bind(inflectionList.getSelectionModel().selectedItemProperty());

        transcriptionList.setItems(viewModel.getTranscriptionList());
        transcriptionList.setCellFactory(CachedViewModelCellFactory.createForFxmlView(TranscriptionListItemView.class));
        viewModel.selectedTranscriptionListItemProperty().bind(transcriptionList.getSelectionModel().selectedItemProperty());

        particlesList.setItems(viewModel.getParticleList());
        particlesList.setCellFactory(CachedViewModelCellFactory.createForFxmlView(ParticleListItemView.class));
        viewModel.selectedParticleListItemProperty().bind(particlesList.getSelectionModel().selectedItemProperty());
    }

    @FXML
    public void addVariant() {
        viewModel.getAddVariantCommand().execute();
    }

    @FXML
    public void removeVariant() {
        viewModel.getRemoveVariantCommand().execute();
    }

    @FXML
    public void addTranscription() {
        viewModel.getAddTranscriptionCommand().execute();
    }

    @FXML
    public void removeTranscription() {
        viewModel.getRemoveTranscriptionCommand().execute();
    }

    @FXML
    public void addSemanticField() {
        viewModel.getAddSemanticFieldCommand().execute();
    }

    @FXML
    public void removeSemanticField() {
        viewModel.getRemoveSemanticFieldCommand().execute();
    }

    @FXML
    public void addSource() {
        viewModel.getAddSourceCommand().execute();
    }

    @FXML
    public void removeSource() {
        viewModel.getRemoveSourceCommand().execute();
    }

    @FXML
    public void addInflection() {
        viewModel.getAddInflectionCommand().execute();
    }

    @FXML
    public void removeInflection() {
        viewModel.getRemoveInflectionCommand().execute();
    }

    @FXML
    public void addParticle() {
        viewModel.getAddParticleCommand().execute();
    }

    @FXML
    public void removeParticle() {
        viewModel.getRemoveParticleCommand().execute();
    }
}
