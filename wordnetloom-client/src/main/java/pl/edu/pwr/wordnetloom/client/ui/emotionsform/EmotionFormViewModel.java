package pl.edu.pwr.wordnetloom.client.ui.emotionsform;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.itemlist.ItemList;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public class EmotionFormViewModel implements ViewModel {

    static final String NOTHING_SELECTED_MARKER = "----------";

    @Inject
    private RemoteService remoteService;

    private ObservableList<Dictionary> emotionsList;
    private ObservableList<Dictionary> valuationsList;
    private List<BooleanProperty> emotionsProperties;
    private List<BooleanProperty> valuationsProperties;

    private ObservableList<String> markednessesList;
    private final ObjectProperty<Dictionary> markednessProperty = new SimpleObjectProperty<>();
    private final StringProperty selectedMarkedness = new SimpleStringProperty(NOTHING_SELECTED_MARKER);
    private ItemList<Dictionary> markednessItemList;

    public void initialize() {
        initEmotions();
        initValuations();
        initMarkednesses();
    }

    private void initEmotions(){
        emotionsList = Dictionaries.getDictionary(Dictionaries.EMOTION_DICTIONARY);
        emotionsProperties = new ArrayList<>();
        for(int i=0; i<emotionsList.size(); i++){
            emotionsProperties.add(new SimpleBooleanProperty());
        }
    }

    private void initValuations(){
        valuationsList = Dictionaries.getDictionary(Dictionaries.VALUATION_DICTIONARY);
        valuationsProperties = new ArrayList<>();
        for(int i=0; i<valuationsList.size(); i++){
            valuationsProperties.add(new SimpleBooleanProperty());
        }
    }

    private void initMarkednesses() {
        markednessItemList = Dictionaries.initDictionaryItemList(Dictionaries.MARKEDNESS_DICTIONARY);
        ObservableList<String> mappedList = markednessItemList.getTargetList();
        markednessesList = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        markednessesList.addListener((ListChangeListener<String>) p->selectedMarkedness.set(NOTHING_SELECTED_MARKER));
    }

    public BooleanProperty getEmotionProperty(int index) {
        if(checkPropertyIndex(emotionsProperties,index)){
            return emotionsProperties.get(index);
        }
        return null;
    }

    private boolean checkPropertyIndex(List<BooleanProperty> properties,int index) {
        return properties != null && index >= 0 && index < properties.size();
    }

    public BooleanProperty getValuationProperty(int index) {
        if(checkPropertyIndex(valuationsProperties, index)){
            return valuationsProperties.get(index);
        }
        return null;
    }

    public ObjectProperty<Dictionary> getMarkednessProperty() {
        return markednessProperty;
    }

    public List<Dictionary> getEmotionsList() {
        return emotionsList;
    }

    public List<Dictionary> getValuationsList() {
        return valuationsList;
    }

    public ObservableList<String> getMarkednessList() {
        return markednessesList;
    }

    public StringProperty selectedMarkednessProperty() {
        return selectedMarkedness;
    }
}

