package pl.edu.pwr.wordnetloom.client.ui.emotionsform;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.itemlist.ItemList;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.model.EmotionalAnnotation;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SensePropertiesDialogScope;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static pl.edu.pwr.wordnetloom.client.service.Dictionaries.MARKEDNESS_DICTIONARY;


public class EmotionFormViewModel implements ViewModel {

    static final String NOTHING_SELECTED_MARKER = "----------";

    @InjectScope
    SensePropertiesDialogScope sensePropertiesDialogScope;

    private EmotionalAnnotation emotionalAnnotation;

    private ObservableList<Dictionary> emotionsList;
    private ObservableList<Dictionary> valuationsList;
    private List<BooleanProperty> emotionsProperties;
    private List<BooleanProperty> valuationsProperties;

    private ObservableList<String> markednessesList;
    private final ObjectProperty<Dictionary> markednessProperty = new SimpleObjectProperty<>();
    private final StringProperty selectedMarkedness = new SimpleStringProperty(NOTHING_SELECTED_MARKER);
    private ItemList<Dictionary> markednessItemList;

    private BooleanProperty neutralProperty;
    private BooleanProperty superProperty;

    private StringProperty example1Property;
    private StringProperty example2Property;

    public void initialize() {
        if (sensePropertiesDialogScope.getEmotionalAnnotationToEdit() != null) {
            emotionalAnnotation = sensePropertiesDialogScope.getEmotionalAnnotationToEdit();
        } else {
            emotionalAnnotation = new EmotionalAnnotation();
        }
        initEmotions();
        initValuations();
        initMarkednesses();
        initRadioButtons();
        initExamples();
        sensePropertiesDialogScope.subscribe(SensePropertiesDialogScope.COMMIT, (key, payload) -> commitChanges());
    }

    private void initEmotions(){
        emotionsList = Dictionaries.getDictionary(Dictionaries.EMOTION_DICTIONARY);
        emotionsProperties = new ArrayList<>();
        for(int i = 0; i < emotionsList.size(); i++) {
            emotionsProperties.add(new SimpleBooleanProperty());
            if (emotionalAnnotation.getEmotions().contains(emotionsList.get(i).getId()))
                emotionsProperties.get(i).setValue(true);
        }
    }

    private void initValuations(){
        valuationsList = Dictionaries.getDictionary(Dictionaries.VALUATION_DICTIONARY);
        valuationsProperties = new ArrayList<>();
        for(int i = 0; i < valuationsList.size(); i++){
            valuationsProperties.add(new SimpleBooleanProperty());
            if (emotionalAnnotation.getValuations().contains(valuationsList.get(i).getId()))
                valuationsProperties.get(i).setValue(true);
        }
    }

    private void initMarkednesses() {
        markednessItemList = Dictionaries.initDictionaryItemList(Dictionaries.MARKEDNESS_DICTIONARY);
        ObservableList<String> mappedList = markednessItemList.getTargetList();
        markednessesList = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        markednessesList.addListener((ListChangeListener<String>) p->selectedMarkedness.set(NOTHING_SELECTED_MARKER));

        if (emotionalAnnotation.getMarkedness() != null) {
            selectedMarkedness.set(Dictionaries.getDictionaryItemById(MARKEDNESS_DICTIONARY, emotionalAnnotation.getMarkedness()));
        }
    }

    private void initRadioButtons() {
        neutralProperty = new SimpleBooleanProperty();
        superProperty = new SimpleBooleanProperty();

        neutralProperty.setValue(emotionalAnnotation.isEmotionalCharacteristic());
        superProperty.setValue(emotionalAnnotation.isSuperAnnotation());
    }

    private void initExamples() {
        example1Property = new SimpleStringProperty();
        example2Property = new SimpleStringProperty();

        if (emotionalAnnotation.getExample1() != null)
            example1Property.setValue(emotionalAnnotation.getExample1());

        if (emotionalAnnotation.getExample2() != null)
            example2Property.set(emotionalAnnotation.getExample2());
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

    public BooleanProperty neutralProperty() {
        return neutralProperty;
    }

    public BooleanProperty superProperty() {
        return superProperty;
    }

    public StringProperty example1Property() {
        return example1Property;
    }

    public StringProperty example2Property() {
        return example2Property;
    }

    private void commitChanges() {
        emotionalAnnotation.setMarkedness(Dictionaries.getDictionaryItemIdByName(MARKEDNESS_DICTIONARY, selectedMarkedness.getValue()));
        emotionalAnnotation.setExample1(example1Property.getValue());
        emotionalAnnotation.setExample2(example2Property.getValue());
        emotionalAnnotation.setEmotionalCharacteristic(neutralProperty.getValue());
        emotionalAnnotation.setSuperAnnotation(superProperty.getValue());

        if (sensePropertiesDialogScope.getSenseToEdit().getId() != null)
            emotionalAnnotation.setSenseId(sensePropertiesDialogScope.getSenseToEdit().getId());

        emotionalAnnotation.setEmotions(new LinkedList<>());
        emotionalAnnotation.setValuations(new LinkedList<>());

        for (int i = 0; i < emotionsList.size(); i++) {
            if (emotionsProperties.get(i).getValue())
                emotionalAnnotation.addEmotion(emotionsList.get(i).getId());
        }

        for (int i = 0; i < valuationsList.size(); i++) {
            if (valuationsProperties.get(i).getValue())
                emotionalAnnotation.addValuation(valuationsList.get(i).getId());
        }

        sensePropertiesDialogScope.setEmotionalAnnotationToEdit(emotionalAnnotation);
    }
}
