package pl.edu.pwr.wordnetloom.client.ui.relationtypes;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.TreeView;

public class RelationTypesView implements FxmlView<RelationTypesViewModel> {

    @FXML
    public TreeView senseRelationTypeTree, synsetRelationTypeTree;

    @InjectViewModel
    private RelationTypesViewModel viewModel;

    public void initialize() {
        synsetRelationTypeTree.rootProperty().bindBidirectional(viewModel.synsetRootProperty());
        senseRelationTypeTree.rootProperty().bindBidirectional(viewModel.senseRootProperty());
        viewModel.selectedSynsetTreeModelProperty().bindBidirectional(synsetRelationTypeTree.selectionModelProperty());
        viewModel.selectedSenseTreeModelProperty().bindBidirectional(senseRelationTypeTree.selectionModelProperty());
        viewModel.selectedSenseTreeItemProperty().bind(senseRelationTypeTree.getSelectionModel().selectedItemProperty());
        viewModel.selectedSynsetTreeItemProperty().bind(synsetRelationTypeTree.getSelectionModel().selectedItemProperty());

    }
}
