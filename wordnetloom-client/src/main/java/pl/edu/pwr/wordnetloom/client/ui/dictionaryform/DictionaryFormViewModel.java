package pl.edu.pwr.wordnetloom.client.ui.dictionaryform;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Command;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.alerts.AlertDialogHandler;
import pl.edu.pwr.wordnetloom.client.ui.scopes.DictionaryDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.search.SearchListItemViewModel;

import javax.inject.Inject;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DictionaryFormViewModel implements ViewModel {

    @InjectScope
    DictionaryDialogScope dialogScope;

    private ObservableList<String> dictionariesTypes = FXCollections.observableArrayList();

    private ObservableList<DictionaryListItemViewModel> itemsList = FXCollections.observableArrayList();
    private ObjectProperty<DictionaryListItemViewModel> selectedItem = new SimpleObjectProperty<>();

    private final StringProperty selectedDictionaryType = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final BooleanProperty isDefaultValue = new SimpleBooleanProperty();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();

    private Command addItemCommand;
    private Command removeItemCommand;

    @Inject
    RemoteService service;

    @Inject
    AlertDialogHandler dialogHandler;

    public void initialize(){
        dictionariesTypes.addAll(Dictionaries.getDictionaryTypes().stream()
                .filter(t -> !t.equals(Dictionaries.LEXICON_DICTIONARY))
                .collect(Collectors.toSet()));

        dictionariesTypes.addListener((ListChangeListener<String>) p -> selectedDictionaryType.set(Dictionaries.PART_OF_SPEECH_DICTIONARY));

        selectedDictionaryType.addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                itemsList.clear();
                Dictionaries.getDictionary(newValue)
                        .forEach(d -> itemsList.add(new DictionaryListItemViewModel(d)));
            }
        });

        selectedItem.addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                try {
                    Dictionary d = service.findDictionary(newValue.getDictionaryItem().getLinks().getSelf());
                    mapDictionary(d);
                } catch (IOException e) {
                    dialogHandler.onShowErrorMsg(e);
                }
            }
        });
    }

    private void mapDictionary(Dictionary d){
        name.set(d.getName());
        description.set(d.getDescription());
        if(d.getColor() != null) {
            color.set(javafx.scene.paint.Color.web(d.getColor()));
        }
        if(d.getColor() !=null) {
            isDefaultValue.set(d.getDefault());
        }
    }

    private  void commitChanges(){
        dialogScope.getDictionaryToEdit().setName(name.get());
    }

    public StringProperty selectedDictionaryTypeProperty() {
        return selectedDictionaryType;
    }

    public ObservableList<String> getDictionariesTypes() {
        return dictionariesTypes;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public boolean isIsDefaultValue() {
        return isDefaultValue.get();
    }

    public BooleanProperty isDefaultValueProperty() {
        return isDefaultValue;
    }

    public void setIsDefaultValue(boolean isDefaultValue) {
        this.isDefaultValue.set(isDefaultValue);
    }

    public Color getColor() {
        return color.get();
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public ObservableList<DictionaryListItemViewModel> getItemsList() {
        return itemsList;
    }

    public void setItemsList(ObservableList<DictionaryListItemViewModel> itemsList) {
        this.itemsList = itemsList;
    }

    public DictionaryListItemViewModel getSelectedItem() {
        return selectedItem.get();
    }

    public ObjectProperty<DictionaryListItemViewModel> selectedItemProperty() {
        return selectedItem;
    }

    public void setSelectedItem(DictionaryListItemViewModel selectedItem) {
        this.selectedItem.set(selectedItem);
    }

    public Command getAddItemCommand() {
        return addItemCommand;
    }

    public void setAddItemCommand(Command addItemCommand) {
        this.addItemCommand = addItemCommand;
    }

    public Command getRemoveItemCommand() {
        return removeItemCommand;
    }

    public void setRemoveItemCommand(Command removeItemCommand) {
        this.removeItemCommand = removeItemCommand;
    }
}

