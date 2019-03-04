package pl.edu.pwr.wordnetloom.client.ui.senserelationformdialog;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Pagination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SenseRelationDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.searchsenseform.SearchSenseFormView;
import pl.edu.pwr.wordnetloom.client.ui.searchsenseform.SearchSenseFormViewModel;
import pl.edu.pwr.wordnetloom.client.ui.senserelationform.SenseRelationFormView;
import pl.edu.pwr.wordnetloom.client.ui.senserelationform.SenseRelationFormViewModel;

public class SenseRelationDialogView implements FxmlView<SenseRelationDialogViewModel> {

    @FXML
    public Button previousButton, nextButton, okButton;

    @FXML
    public Pagination formPagination;

    @InjectContext
    private Context context;

    @InjectViewModel
    private SenseRelationDialogViewModel viewModel;

    public Stage showDialog;

    public void initialize() {
        initIcons();

        viewModel.subscribe(SenseRelationDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
            viewModel.publish(SenseRelationDialogScope.RESET_DIALOG_PAGE);
            showDialog.close();
        });

        ViewTuple<SearchSenseFormView, SearchSenseFormViewModel> senseSearchFormTuple = FluentViewLoader
                .fxmlView(SearchSenseFormView.class)
                .context(context)
                .load();

        ViewTuple<SenseRelationFormView, SenseRelationFormViewModel> senseRelationFormTuple = FluentViewLoader
                .fxmlView(SenseRelationFormView.class)
                .context(context)
                .load();

        formPagination.getStyleClass().add("invisible-pagination-control");

        formPagination.setPageFactory(index -> {
            if (index == 0) {
                return senseSearchFormTuple.getView();
            } else {
                return senseRelationFormTuple.getView();
            }
        });

        formPagination.currentPageIndexProperty().bindBidirectional(viewModel.dialogPageProperty());

        okButton.disableProperty().bind(viewModel.okButtonDisabledProperty());
        okButton.visibleProperty().bind(viewModel.okButtonVisibleProperty());
        okButton.managedProperty().bind(viewModel.okButtonVisibleProperty());

        nextButton.disableProperty().bind(viewModel.nextButtonDisabledProperty());
        nextButton.visibleProperty().bind(viewModel.nextButtonVisibleProperty());
        nextButton.managedProperty().bind(viewModel.nextButtonVisibleProperty());

        previousButton.disableProperty().bind(viewModel.previousButtonDisabledProperty());
        previousButton.visibleProperty().bind(viewModel.previousButtonVisibleProperty());
        previousButton.managedProperty().bind(viewModel.previousButtonVisibleProperty());

    }

    public void setDisplayingStage(Stage showDialog) {
        this.showDialog = showDialog;
        this.showDialog.titleProperty().bind(viewModel.titleTextProperty());

        this.showDialog.setOnCloseRequest(event -> {
            viewModel.publish(SenseRelationDialogScope.RESET_DIALOG_PAGE);
        });

        showDialog.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent e) -> {
            if (KeyCode.ESCAPE == e.getCode()) {
                viewModel.publish(SenseRelationDialogScope.RESET_DIALOG_PAGE);
                showDialog.close();
            }
        });

    }

    private void initIcons() {
        AwesomeDude.setIcon(okButton, AwesomeIcon.CHECK, "11");
        AwesomeDude.setIcon(nextButton, AwesomeIcon.CHEVRON_RIGHT, "11", ContentDisplay.RIGHT);
        AwesomeDude.setIcon(previousButton, AwesomeIcon.CHEVRON_LEFT, "11");
    }

    @FXML
    private void previous() {
        viewModel.previousAction();
    }

    @FXML
    private void next() {
        viewModel.nextAction();
    }

    @FXML
    private void ok() {
        viewModel.okAction();
    }
}
