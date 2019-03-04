package pl.edu.pwr.wordnetloom.client.ui.synsetrelationremovedialog;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;
import pl.edu.pwr.wordnetloom.client.model.Relation;

public class SynsetRelationRemoveDialogView implements FxmlView<SynsetRelationRemoveDialogViewModel> {

    @FXML
    public CheckListView<Relation> relationListView;

    @FXML
    public Button okButton, cancelButton, selectAllButton, deselectAllButton;

    @InjectViewModel
    SynsetRelationRemoveDialogViewModel viewModel;

    public Stage showDialog;

    public void initialize() {
        relationListView.setItems(viewModel.getRelationList());
    }

    public void ok() {
        viewModel.removeRelations(relationListView.getCheckModel().getCheckedItems());
        removeRemovedRelationsFromList();
        if (areRemovedAllRelations()) {
            close();
        }
    }

    private void removeRemovedRelationsFromList() {
        viewModel.getRelationList().removeAll(relationListView.getCheckModel().getCheckedItems());
    }

    private boolean areRemovedAllRelations() {
        return viewModel.getRelationList().isEmpty();
    }

    public void close() {
        showDialog.close();
    }

    public void selectAll() {
        checkAll(true);
    }

    public void deselectAll() {
        checkAll(false);
    }

    private void checkAll(boolean check) {
        for (int i = 0; i < relationListView.getCheckModel().getItemCount(); i++) {
            relationListView.getItemBooleanProperty(i).set(check);
        }
    }

    public void setDisplayingStage(Stage showDialog) {
        this.showDialog = showDialog;
        this.showDialog.titleProperty().bind(viewModel.titleProperty());
    }
}
