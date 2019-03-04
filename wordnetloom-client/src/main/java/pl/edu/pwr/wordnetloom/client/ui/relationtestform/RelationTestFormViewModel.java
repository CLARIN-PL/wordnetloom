package pl.edu.pwr.wordnetloom.client.ui.relationtestform;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.itemlist.ItemList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.model.RelationTest;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.ui.scopes.RelationTestDialogScope;

public class RelationTestFormViewModel implements ViewModel {

    static final String NOTHING_SELECTED_MARKER = "----------";

    private StringProperty test = new SimpleStringProperty();

    private ObservableList<String> partsOfSpeechA;
    private final ObjectProperty<Dictionary> partOfSpeechA = new SimpleObjectProperty<>();
    private final StringProperty selectedPartOfSpeechA = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> partsOfSpeechB;
    private final ObjectProperty<Dictionary> partOfSpeechB = new SimpleObjectProperty<>();
    private final StringProperty selectedPartOfSpeechB = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ItemList<Dictionary> partOfSpeechAItemList;
    private ItemList<Dictionary> partOfSpeechBItemList;

    @InjectScope
    RelationTestDialogScope dialogScope;

    public void initialize() {
        initPartsOfSpeechAList();
        initPartsOfSpeechBList();

        selectedPartOfSpeechA.addListener((obs, oldV, newV) -> {
            Dictionaries.dictionarySelected(obs, oldV, newV, Dictionaries.PART_OF_SPEECH_DICTIONARY, NOTHING_SELECTED_MARKER, partOfSpeechA);
        });

        selectedPartOfSpeechB.addListener((obs, oldV, newV) -> {
            Dictionaries.dictionarySelected(obs, oldV, newV, Dictionaries.PART_OF_SPEECH_DICTIONARY, NOTHING_SELECTED_MARKER, partOfSpeechB);
        });

        dialogScope.subscribe(RelationTestDialogScope.COMMIT, (s, objects) -> {
            commitChanges();
        });

        dialogScope.subscribe(RelationTestDialogScope.OK_BEFORE_COMMIT, (s, objects) -> {
            mapRelationTest();
        });
    }

    private void mapRelationTest() {
        RelationTest t = dialogScope.relationTestToEditProperty().get();
        test.set(t.getTest());
        if(t.getSenseBPos() != null) {
            selectedPartOfSpeechA.set(Dictionaries.getDictionaryItemById(Dictionaries.PART_OF_SPEECH_DICTIONARY, t.getSenseAPos()));
        }else{
            selectedPartOfSpeechA.set(NOTHING_SELECTED_MARKER);
        }
        if(t.getSenseBPos() != null) {
            selectedPartOfSpeechB.set(Dictionaries.getDictionaryItemById(Dictionaries.PART_OF_SPEECH_DICTIONARY, t.getSenseBPos()));
        }else{
            selectedPartOfSpeechB.set(NOTHING_SELECTED_MARKER);
        }
    }

    private void commitChanges() {
        RelationTest t = dialogScope.relationTestToEditProperty().get();
        t.setSenseAPos(partOfSpeechA.get().getId());
        t.setSenseBPos(partOfSpeechB.get().getId());
        t.setTest(test.get());
        dialogScope.setRelationTestToEdit(t);
    }

    private void initPartsOfSpeechAList() {
        partOfSpeechAItemList = Dictionaries.initDictionaryItemList(Dictionaries.PART_OF_SPEECH_DICTIONARY);
        ObservableList<String> mappedList = partOfSpeechAItemList.getTargetList();
        partsOfSpeechA = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        partsOfSpeechA.addListener((ListChangeListener<String>) p -> selectedPartOfSpeechA.set(NOTHING_SELECTED_MARKER));
    }

    private void initPartsOfSpeechBList() {
        partOfSpeechBItemList = Dictionaries.initDictionaryItemList(Dictionaries.PART_OF_SPEECH_DICTIONARY);
        ObservableList<String> mappedList = partOfSpeechBItemList.getTargetList();
        partsOfSpeechB = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        partsOfSpeechB.addListener((ListChangeListener<String>) p -> selectedPartOfSpeechB.set(NOTHING_SELECTED_MARKER));
    }

    public String getTest() {
        return test.get();
    }

    public StringProperty testProperty() {
        return test;
    }

    public void setTest(String test) {
        this.test.set(test);
    }

    public ObservableList<String> getPartsOfSpeechA() {
        return partsOfSpeechA;
    }

    public void setPartsOfSpeechA(ObservableList<String> partsOfSpeechA) {
        this.partsOfSpeechA = partsOfSpeechA;
    }

    public Dictionary getPartOfSpeechA() {
        return partOfSpeechA.get();
    }

    public ObjectProperty<Dictionary> partOfSpeechAProperty() {
        return partOfSpeechA;
    }

    public void setPartOfSpeechA(Dictionary partOfSpeechA) {
        this.partOfSpeechA.set(partOfSpeechA);
    }

    public String getSelectedPartOfSpeechA() {
        return selectedPartOfSpeechA.get();
    }

    public StringProperty selectedPartOfSpeechAProperty() {
        return selectedPartOfSpeechA;
    }

    public void setSelectedPartOfSpeechA(String selectedPartOfSpeechA) {
        this.selectedPartOfSpeechA.set(selectedPartOfSpeechA);
    }

    public ObservableList<String> getPartsOfSpeechB() {
        return partsOfSpeechB;
    }

    public void setPartsOfSpeechB(ObservableList<String> partsOfSpeechB) {
        this.partsOfSpeechB = partsOfSpeechB;
    }

    public Dictionary getPartOfSpeechB() {
        return partOfSpeechB.get();
    }

    public ObjectProperty<Dictionary> partOfSpeechBProperty() {
        return partOfSpeechB;
    }

    public void setPartOfSpeechB(Dictionary partOfSpeechB) {
        this.partOfSpeechB.set(partOfSpeechB);
    }

    public String getSelectedPartOfSpeechB() {
        return selectedPartOfSpeechB.get();
    }

    public StringProperty selectedPartOfSpeechBProperty() {
        return selectedPartOfSpeechB;
    }

    public void setSelectedPartOfSpeechB(String selectedPartOfSpeechB) {
        this.selectedPartOfSpeechB.set(selectedPartOfSpeechB);
    }

    public ItemList<Dictionary> getPartOfSpeechAItemList() {
        return partOfSpeechAItemList;
    }

    public void setPartOfSpeechAItemList(ItemList<Dictionary> partOfSpeechAItemList) {
        this.partOfSpeechAItemList = partOfSpeechAItemList;
    }

    public ItemList<Dictionary> getPartOfSpeechBItemList() {
        return partOfSpeechBItemList;
    }

    public void setPartOfSpeechBItemList(ItemList<Dictionary> partOfSpeechBItemList) {
        this.partOfSpeechBItemList = partOfSpeechBItemList;
    }
}

