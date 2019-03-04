package pl.edu.pwr.wordnetloom.client.ui.relationtypeform;

import de.saxsys.mvvmfx.*;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pl.edu.pwr.wordnetloom.client.ui.DialogHelper;
import pl.edu.pwr.wordnetloom.client.ui.lexiconselectiondialog.LexiconSelectionDialogView;
import pl.edu.pwr.wordnetloom.client.ui.lexiconselectiondialog.LexiconSelectionDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.partsofspeechdialog.PartsOfSpeechDialogView;
import pl.edu.pwr.wordnetloom.client.ui.partsofspeechdialog.PartsOfSpeechDialogViewModel;

import javax.inject.Inject;

public class RelationTypeFormView implements FxmlView<RelationTypeFormViewModel> {

    @FXML
    public TextField nameFiled, shortcutFiled, displayField;

    @FXML
    public TextField lexiconField, posField;

    @FXML
    public TextArea descriptionArea;

    @FXML
    public CheckBox interlingualCheckBox,  autoReverseCheckBox;

    @FXML
    public ComboBox<String> globalWordnet, directionCombo,typeCombo, parentCombo, reverseCombo;

    @FXML
    public ColorPicker colorCombo;

    @FXML
    private Button selectPosButton;

    @InjectViewModel
    private RelationTypeFormViewModel viewModel;

    @Inject
    Stage primaryStage;

    @InjectContext
    Context context;

    public void initialize() {

        directionCombo.setItems(viewModel.getDirections());
        directionCombo.valueProperty().bindBidirectional(viewModel.selectedDirectionProperty());

        parentCombo.setItems(viewModel.getParents());
        parentCombo.valueProperty().bindBidirectional(viewModel.selectedParentProperty());

        reverseCombo.setItems(viewModel.getReverses());
        reverseCombo.valueProperty().bindBidirectional(viewModel.selectedReverseProperty());

        globalWordnet.setItems(viewModel.getGlobalWordnetRelationTypes());
        globalWordnet.valueProperty().bindBidirectional(viewModel.selectedGlobalWordnetProperty());

        typeCombo.setItems(viewModel.getTypes());
        typeCombo.valueProperty().bindBidirectional(viewModel.selectedTypeProperty());

        nameFiled.textProperty().bindBidirectional(viewModel.nameProperty());
        shortcutFiled.textProperty().bindBidirectional(viewModel.shortcutProperty());
        displayField.textProperty().bindBidirectional(viewModel.displayProperty());
        lexiconField.textProperty().bindBidirectional(viewModel.lexiconProperty());
        posField.textProperty().bindBidirectional(viewModel.posFieldProperty());
        descriptionArea.textProperty().bindBidirectional(viewModel.descriptionProperty());
        interlingualCheckBox.selectedProperty().bindBidirectional(viewModel.interlingualProperty());
        autoReverseCheckBox.selectedProperty().bindBidirectional(viewModel.autoReverseProperty());
        colorCombo.valueProperty().bindBidirectional(viewModel.colorProperty());

        viewModel.subscribe(RelationTypeFormViewModel.OPEN_PART_OF_SPEECH_DIALOG, (key, payload) -> {

            ViewTuple<PartsOfSpeechDialogView, PartsOfSpeechDialogViewModel> load = FluentViewLoader
                    .fxmlView(PartsOfSpeechDialogView.class)
                    .context(context)
                    .load();

            Parent view = load.getView();
            Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/wordnetloom.css");
            load.getCodeBehind().setDisplayingStage(showDialog);
            showDialog.toFront();
        });

        viewModel.subscribe(RelationTypeFormViewModel.OPEN_LEXICONS_DIALOG, (key, payload) -> {

            ViewTuple<LexiconSelectionDialogView, LexiconSelectionDialogViewModel> load = FluentViewLoader
                    .fxmlView(LexiconSelectionDialogView.class)
                    .context(context)
                    .load();

            Parent view = load.getView();
            Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/wordnetloom.css");
            load.getCodeBehind().setDisplayingStage(showDialog);
            showDialog.toFront();
        });
    }


    @FXML
    public void selectPartsOfSpeech() {
        viewModel.getSelectPartOfSpeechCommand().execute();
    }

    @FXML
    public void selectLexicons() {
        viewModel.getSelectLexiconsCommand().execute();
    }
}
