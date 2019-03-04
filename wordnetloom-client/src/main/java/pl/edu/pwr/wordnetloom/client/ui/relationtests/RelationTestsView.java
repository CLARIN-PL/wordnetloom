package pl.edu.pwr.wordnetloom.client.ui.relationtests;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.*;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import pl.edu.pwr.wordnetloom.client.ui.DialogHelper;
import pl.edu.pwr.wordnetloom.client.ui.passwordchangedialog.ChangePasswordDialogView;
import pl.edu.pwr.wordnetloom.client.ui.passwordchangedialog.ChangePasswordDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.relationtestform.RelationTestDialogView;
import pl.edu.pwr.wordnetloom.client.ui.relationtestform.RelationTestDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.senserelationform.TestListItemView;

import javax.inject.Inject;

public class RelationTestsView implements FxmlView<RelationTestsViewModel> {

    @FXML
    public Button  addTestButton, editTestButton, deleteTestButton; //testUpButton, testDownButton,

    @FXML
    public ListView testsListView;

    @InjectViewModel
    private RelationTestsViewModel viewModel;

    @Inject
    Stage primaryStage;

    @InjectContext
    Context context;

    public void initialize() {
//      AwesomeDude.setIcon(testUpButton, AwesomeIcon.ARROW_UP, "11");
//      AwesomeDude.setIcon(testDownButton, AwesomeIcon.ARROW_DOWN, "11");
        AwesomeDude.setIcon(addTestButton, AwesomeIcon.PLUS, "11");
        AwesomeDude.setIcon(editTestButton, AwesomeIcon.EDIT, "11");
        AwesomeDude.setIcon(deleteTestButton, AwesomeIcon.TRASH, "11");

        testsListView.setItems(viewModel.getTestList());
        testsListView.setCellFactory(CachedViewModelCellFactory.createForFxmlView(TestListItemView.class));
        viewModel.selectedTestListItemProperty().bind(testsListView.getSelectionModel().selectedItemProperty());

        viewModel.subscribe(RelationTestsViewModel.OPEN_RELATION_TEST_DIALOG, (key, payload) -> {

            ViewTuple<RelationTestDialogView, RelationTestDialogViewModel> load = FluentViewLoader
                    .fxmlView(RelationTestDialogView.class)
                    .context(context)
                    .load();
            Parent view = load.getView();
            Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/wordnetloom.css");
            load.getCodeBehind().setDisplayingStage(showDialog);
            showDialog.toFront();
        });
    }

    public void add() {
        viewModel.getAddCommand().execute();
    }

    public void delete() {
        viewModel.getDeleteCommand().execute();
    }

    public void edit() {
        viewModel.getEditCommand().execute();
    }
}
