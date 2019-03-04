package pl.edu.pwr.wordnetloom.client.ui.dictionaryform;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pl.edu.pwr.wordnetloom.client.ui.search.SearchListItemView;

public class DictionaryFormView implements FxmlView<DictionaryFormViewModel> {

    @FXML
    public ComboBox<String> dictionaryTypesCombo;

    @FXML
    public ListView<DictionaryListItemViewModel> dictionaryItemsList;

    @FXML
    public TextField nameFiled, descFiled;

    @FXML
    public CheckBox defaultCheckbox;

    @FXML
    public ColorPicker colorPicker;

    @FXML
    public Button addItemButton, removeItemButton;

    @InjectViewModel
    private DictionaryFormViewModel viewModel;

    public void initialize() {

        AwesomeDude.setIcon(addItemButton, AwesomeIcon.PLUS, "12");
        AwesomeDude.setIcon(removeItemButton, AwesomeIcon.TRASH, "12");

        dictionaryTypesCombo.setItems(viewModel.getDictionariesTypes());
        dictionaryTypesCombo.valueProperty().bindBidirectional(viewModel.selectedDictionaryTypeProperty());

        nameFiled.textProperty().bindBidirectional(viewModel.nameProperty());
        descFiled.textProperty().bindBidirectional(viewModel.descriptionProperty());
        defaultCheckbox.selectedProperty().bindBidirectional(viewModel.isDefaultValueProperty());
        colorPicker.valueProperty().bindBidirectional(viewModel.colorProperty());

        dictionaryItemsList.setItems(viewModel.getItemsList());
        dictionaryItemsList.setCellFactory(CachedViewModelCellFactory.createForFxmlView(DictionaryListItemView.class));
        viewModel.selectedItemProperty().bind(dictionaryItemsList.getSelectionModel().selectedItemProperty());
    }

    @FXML
    public void addItem() {
        viewModel.getAddItemCommand().execute();
    }

    @FXML
    public void removeItem() {
        viewModel.getRemoveItemCommand().execute();
    }
}
