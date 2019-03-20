package pl.edu.pwr.wordnetloom.client.ui.emotionsform;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public class EmotionFormViewModel implements ViewModel {

    @Inject
    private RemoteService remoteService;


    ObservableList<Dictionary> emotionsList;
    private List<BooleanProperty> emotionsProperties;

    public List<String> getEmotionsNames() {
        emotionsList =  Dictionaries.getDictionary(Dictionaries.EMOTION_DICTIONARY);
        System.out.println("Wielkość emocji " +emotionsList.size());
        List<String> emotionsNames = new ArrayList<>();
        emotionsList.forEach(dictionary -> emotionsNames.add(dictionary.getName()));
        // TODO: przemyśleć to
        emotionsProperties = new ArrayList<>();
        for(int i=0; i<emotionsList.size(); i++) {
            emotionsProperties.add(new SimpleBooleanProperty());
        }
        return emotionsNames;
    }

    public BooleanProperty getEmotionProperty(int index) {
        return emotionsProperties.get(index);
    }

}

