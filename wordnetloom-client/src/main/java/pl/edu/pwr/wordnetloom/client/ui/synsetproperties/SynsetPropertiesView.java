package pl.edu.pwr.wordnetloom.client.ui.synsetproperties;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.*;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import pl.edu.pwr.wordnetloom.client.ui.DialogHelper;
import pl.edu.pwr.wordnetloom.client.ui.synonymyrelationdialog.SynonymyRelationDialogView;
import pl.edu.pwr.wordnetloom.client.ui.synonymyrelationdialog.SynonymyRelationDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.sensepropertiesdialog.SensePropertiesDialogView;
import pl.edu.pwr.wordnetloom.client.ui.sensepropertiesdialog.SensePropertiesDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.synsetpropertiesdialog.SynsetPropertiesDialogView;
import pl.edu.pwr.wordnetloom.client.ui.synsetpropertiesdialog.SynsetPropertiesDialogViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.Callable;

@Singleton
public class SynsetPropertiesView implements FxmlView<SynsetPropertiesViewModel> {

    @FXML
    public Label definitionInput,
            semanticExamplesInput, synsetIdInput,
            princetonIdInput, iliIdInput, ownerInput,
            statusInput,  linkInput;

    @FXML
    public Label auxillaryLabel;

    @FXML
    public Button editButton, moveSenseUpButton, moveSenseDownButton,
            attachSenseButton, detachSenseButton;

    @FXML
    private ListView<SenseListItemViewModel> senseList;

    @InjectViewModel
    private SynsetPropertiesViewModel viewModel;

    @InjectContext
    private Context context;

    @Inject
    private Stage primaryStage;

    public void initialize() {
        initIcons();

        senseList.setItems(viewModel.senseListProperty());
        senseList.setCellFactory(CachedViewModelCellFactory.createForFxmlView(SenseListItemView.class));
        viewModel.selectedSenseListItemProperty().bind(senseList.getSelectionModel().selectedItemProperty());

        viewModel.subscribe(SynsetPropertiesViewModel.OPEN_EDIT_SENSE_DIALOG, (key, payload) -> {

            ViewTuple<SensePropertiesDialogView, SensePropertiesDialogViewModel> load = FluentViewLoader
                    .fxmlView(SensePropertiesDialogView.class)
                    .context(context)
                    .load();

            Parent view = load.getView();
            Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/wordnetloom.css");
            load.getCodeBehind().setDisplayingStage(showDialog);
            showDialog.toFront();
        });

        viewModel.subscribe(SynsetPropertiesViewModel.OPEN_EDIT_SYNSET_DIALOG, (key, payload) -> {
            ViewTuple<SynsetPropertiesDialogView, SynsetPropertiesDialogViewModel> load = FluentViewLoader
                    .fxmlView(SynsetPropertiesDialogView.class)
                    .context(context)
                    .load();

            Parent view = load.getView();
            Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/wordnetloom.css");
            load.getCodeBehind().setDisplayingStage(showDialog);
            showDialog.toFront();
        });

        viewModel.subscribe(SynsetPropertiesViewModel.OPEN_ATTACH_SENSE_DIALOG, (key, payload) -> {
            ViewTuple<SynonymyRelationDialogView, SynonymyRelationDialogViewModel> load = FluentViewLoader
                    .fxmlView(SynonymyRelationDialogView.class)
                    .context(context)
                    .load();

            Parent view = load.getView();
            Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/wordnetloom.css");
            load.getCodeBehind().setDisplayingStage(showDialog);
            showDialog.toFront();
        });


        senseList.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY && event.getTarget() instanceof Text) {
                senseList.getItems()
                        .stream()
                        .filter(i -> i.getSense().getLabel().equals(((Text) event.getTarget()).getText()))
                        .map(i -> i.getSense().getLinks().getSelf())
                        .findFirst().ifPresent(s -> viewModel.showSenseProperties(s));
            }
        });

        synsetIdInput.textProperty().bind(viewModel.synsetIdProperty());
        princetonIdInput.textProperty().bind(viewModel.princetonProperty());
        iliIdInput.textProperty().bind(viewModel.iliProperty());

        definitionInput.textProperty().bind(viewModel.definitionProperty());
        semanticExamplesInput.textProperty().bind(viewModel.exampleProperty());
        linkInput.textProperty().bind(viewModel.linkProperty());

        statusInput.textProperty().bind(viewModel.statusProperty());
        ownerInput.textProperty().bind(viewModel.ownerProperty());

        auxillaryLabel.visibleProperty().bind(
                Bindings.createBooleanBinding(()->viewModel.artificialProperty().get())
        );

    }

    private void initIcons() {
        AwesomeDude.setIcon(editButton, AwesomeIcon.EDIT, "11");
        AwesomeDude.setIcon(moveSenseUpButton, AwesomeIcon.ARROW_UP, "11");
        AwesomeDude.setIcon(moveSenseDownButton, AwesomeIcon.ARROW_DOWN, "11");
        AwesomeDude.setIcon(attachSenseButton, AwesomeIcon.PLUS, "11");
        AwesomeDude.setIcon(detachSenseButton, AwesomeIcon.MINUS, "11");
    }

    public void editSynset() {
        viewModel.editSynsetCommand().execute();
    }

    public void moveSenseUp() {
        viewModel.moveSenseUpCommand().execute();
    }

    public void moveSenseDown() {
        viewModel.moveSenseDownCommand().execute();
    }

    public void attachSense() {
        viewModel.attachSenseCommand().execute();
    }

    public void detachSense() {
        viewModel.detachSenseCommand().execute();
    }

}
