package pl.edu.pwr.wordnetloom.client.ui.senseform;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.*;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pl.edu.pwr.wordnetloom.client.ui.DialogHelper;
import pl.edu.pwr.wordnetloom.client.ui.example.ExampleListItemView;
import pl.edu.pwr.wordnetloom.client.ui.example.ExampleListItemViewModel;
import pl.edu.pwr.wordnetloom.client.ui.exampledialog.ExampleDialogView;
import pl.edu.pwr.wordnetloom.client.ui.exampledialog.ExampleDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.sensepropertiesdialog.SensePropertiesDialogView;
import pl.edu.pwr.wordnetloom.client.ui.sensepropertiesdialog.SensePropertiesDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.synsetproperties.SynsetPropertiesViewModel;

import javax.inject.Inject;

public class SenseFormView implements FxmlView<SenseFormViewModel> {

    @FXML
	public TextField lemmaInput,linkInput;

	@FXML
	public Label variantLabel, ownerLabel;

	@FXML
	public ComboBox<String> lexiconCombo, posCombo,
            domainCombo ,registerCombo, statusCombo;

	@FXML
	public TextArea definitionArea, commentArea, technicalCommentArea;

	@FXML
	public ListView<ExampleListItemViewModel> exampleList;

	@FXML
    public VBox webPanel;

	@FXML
    public HBox form;

	@FXML
    public Button addExample, editExample ,removeExample;

    @InjectViewModel
	private SenseFormViewModel viewModel;

    private ValidationVisualizer validationVisualizer = new ControlsFxVisualizer();

	@InjectContext
    Context context;

	@Inject
    SensePropertiesDialogView sensePropertiesDialogView;

	public void initialize() {
	    initIcons();

        viewModel.subscribe(SenseFormViewModel.OPEN_EXAMPLE_DIALOG, (key, payload) -> openExampleDialog());

        lemmaInput.textProperty().bindBidirectional(viewModel.lemmaProperty());
        linkInput.textProperty().bindBidirectional(viewModel.linkProperty());
        ownerLabel.textProperty().bindBidirectional(viewModel.ownerProperty());
        variantLabel.textProperty().bindBidirectional(viewModel.variantProperty());
        definitionArea.textProperty().bindBidirectional(viewModel.definitionProperty());
        commentArea.textProperty().bindBidirectional(viewModel.commentProperty());
        technicalCommentArea.textProperty().bindBidirectional(viewModel.technicalCommentProperty());
        lexiconCombo.setItems(viewModel.lexiconList());
        lexiconCombo.valueProperty().bindBidirectional(viewModel.selectedLexiconProperty());

        posCombo.setItems(viewModel.partOfSpeechList());
        posCombo.valueProperty().bindBidirectional(viewModel.selectedPartOfSpeechProperty());

        domainCombo.setItems(viewModel.domainList());
        domainCombo.valueProperty().bindBidirectional(viewModel.selectedDomainProperty());

        statusCombo.setItems(viewModel.statusList());
        statusCombo.valueProperty().bindBidirectional(viewModel.selectedStatusProperty());

        registerCombo.setItems(viewModel.registerList());
        registerCombo.valueProperty().bindBidirectional(viewModel.selectedRegisterProperty());

        exampleList.setItems(viewModel.exampleListProperty());
        exampleList.setCellFactory(CachedViewModelCellFactory.createForFxmlView(ExampleListItemView.class));
        viewModel.selectedExampleListItemProperty().bind(exampleList.getSelectionModel().selectedItemProperty());

        validationVisualizer.initVisualization(viewModel.lemmaValidation(), lemmaInput, true);
        validationVisualizer.initVisualization(viewModel.lexiconValidation(), lexiconCombo, true);
        validationVisualizer.initVisualization(viewModel.partOfSpeechValidation(), posCombo, true);
        validationVisualizer.initVisualization(viewModel.domainValidation(), domainCombo, true);
        validationVisualizer.initVisualization(viewModel.linkValidation(), linkInput, false);
    }

    private void openExampleDialog() {
        ViewTuple<ExampleDialogView, ExampleDialogViewModel> load = FluentViewLoader
                .fxmlView(ExampleDialogView.class)
                .context(context)
                .load();

        Parent view = load.getView();
        Stage showDialog = DialogHelper.showDialog(view, sensePropertiesDialogView.showDialog,"/wordnetloom.css");
        load.getCodeBehind().setDisplayingStage(showDialog);
        showDialog.toFront();
    }

    public void initIcons(){
        AwesomeDude.setIcon(addExample, AwesomeIcon.PLUS, "11");
        AwesomeDude.setIcon(editExample, AwesomeIcon.EDIT, "11");
        AwesomeDude.setIcon(removeExample, AwesomeIcon.TRASH, "11");
	}

    @FXML
    public void addExample() {
        viewModel.addExampleCommand().execute();
    }

    @FXML
    public void editExample() {
        viewModel.editExampleCommand().execute();
    }

    @FXML
    public void removeExample() {
        viewModel.removeExampleCommand().execute();
    }
}
