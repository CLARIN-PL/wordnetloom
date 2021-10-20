package pl.edu.pwr.wordnetloom.client.ui.morphologyform;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.fxml.FXML;

import javafx.scene.control.ListView;
import javafx.scene.control.Button;

public class MorphologyFormView implements FxmlView<MorphologyFormViewModel> {

    @FXML
    public Button deleteButton;

    @FXML
    public ListView<MorphologyListItemViewModel> morphologyList;

    @InjectViewModel
    public MorphologyFormViewModel viewModel;

    public void initialize() {
        initIcons();

        morphologyList.setItems(viewModel.getMorphologyList());
        morphologyList.setCellFactory(CachedViewModelCellFactory.createForFxmlView(MorphologyListItemView.class));
        viewModel.selectedMorphologyListItemProperty().bind(morphologyList.getSelectionModel().selectedItemProperty());
    }

    private void initIcons() {
        AwesomeDude.setIcon(deleteButton, AwesomeIcon.REMOVE, "11");
    }

    public void deleteMorphology() {
        viewModel.getDeleteMorphology().execute();
    }
}
