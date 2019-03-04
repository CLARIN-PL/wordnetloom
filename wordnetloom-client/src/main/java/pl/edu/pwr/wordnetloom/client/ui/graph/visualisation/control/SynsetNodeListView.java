package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.control;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class SynsetNodeListView implements FxmlView<SynsetNodeListViewModel> {

    @InjectViewModel
    private SynsetNodeListViewModel viewModel;

    @FXML
    public ListView<SynsetNodeListItemViewModel> synsetNodeList;

    public void initialize(){
        synsetNodeList.setItems(viewModel.synsetNodesProperty());
        synsetNodeList.setCellFactory(CachedViewModelCellFactory.createForFxmlView(SynsetNodeListItemView.class));
        viewModel.selectedSynsetNodeItemProperty().bind(synsetNodeList.getSelectionModel().selectedItemProperty());
    }

    @FXML
    public void addSynset(ActionEvent actionEvent) {
        viewModel.getAddSynsetCommand().execute();
    }
}
