package pl.edu.pwr.wordnetloom.client.ui.synsetform;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.*;
import de.saxsys.mvvmfx.utils.notifications.NotificationObserver;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pl.edu.pwr.wordnetloom.client.ui.DialogHelper;
import pl.edu.pwr.wordnetloom.client.ui.example.ExampleListItemView;
import pl.edu.pwr.wordnetloom.client.ui.example.ExampleListItemViewModel;
import pl.edu.pwr.wordnetloom.client.ui.exampledialog.ExampleDialogView;
import pl.edu.pwr.wordnetloom.client.ui.exampledialog.ExampleDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.synsetpropertiesdialog.SynsetPropertiesDialogView;

import javax.inject.Inject;
import javax.inject.Singleton;

public class SynsetFormView implements FxmlView<SynsetFormViewModel> {

	@InjectViewModel
	private SynsetFormViewModel viewModel;

	@InjectContext
	private Context context;

	@FXML
	public Button addExampleButton ,editExampleButton,
			removeExampleButton;
	@FXML
	private TextField linkInput;

	@FXML
	public Label owner;

	@FXML
	private ComboBox<String> lexiconCombo, statusCombo;

	@FXML
	private TextArea definitionArea, commentArea, technicalCommentArea;

	@FXML
	private CheckBox artificial;

	@FXML
	public ListView<ExampleListItemViewModel> exampleList;

	@Inject
	Stage primary;

	public void initialize() {
		AwesomeDude.setIcon(addExampleButton, AwesomeIcon.PLUS, "11");
		AwesomeDude.setIcon(editExampleButton, AwesomeIcon.EDIT, "11");
		AwesomeDude.setIcon(removeExampleButton, AwesomeIcon.TRASH, "11");

		viewModel.subscribe(SynsetFormViewModel.OPEN_EXAMPLE_DIALOG, (key, payload)->{
            ViewTuple<ExampleDialogView, ExampleDialogViewModel> load = FluentViewLoader
                    .fxmlView(ExampleDialogView.class)
                    .context(context)
                    .load();

            Parent view = load.getView();
            Stage showDialog = DialogHelper.showDialog(view, primary,"/wordnetloom.css");
            load.getCodeBehind().setDisplayingStage(showDialog);
            showDialog.toFront();
		});

		linkInput.textProperty().bindBidirectional(viewModel.linkProperty());
		definitionArea.textProperty().bindBidirectional(viewModel.definitionProperty());
		owner.textProperty().bindBidirectional(viewModel.ownerProperty());
		commentArea.textProperty().bindBidirectional(viewModel.commentProperty());
		technicalCommentArea.textProperty().bindBidirectional(viewModel.technicalCommentProperty());
		artificial.selectedProperty().bindBidirectional(viewModel.artificialProperty());

		lexiconCombo.setItems(viewModel.lexiconList());
		lexiconCombo.valueProperty().bindBidirectional(viewModel.selectedLexiconProperty());

		statusCombo.setItems(viewModel.statusList());
		statusCombo.valueProperty().bindBidirectional(viewModel.selectedStatusProperty());

		exampleList.setItems(viewModel.exampleListProperty());
		exampleList.setCellFactory(CachedViewModelCellFactory.createForFxmlView(ExampleListItemView.class));

		viewModel.selectedExampleListItemProperty().bind(exampleList.getSelectionModel().selectedItemProperty());
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
