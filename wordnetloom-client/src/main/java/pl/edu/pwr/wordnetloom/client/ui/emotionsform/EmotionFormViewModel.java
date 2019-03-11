package pl.edu.pwr.wordnetloom.client.ui.emotionsform;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;

import javax.inject.Inject;
import java.util.List;


public class EmotionFormViewModel implements ViewModel {

    @Inject
    RemoteService remoteService;

    ObservableList<Dictionary> emotionsList;

    public List<BooleanProperty> getEmotionsProperties() {
        emotionsList =  Dictionaries.getDictionary(Dictionaries.EMOTION_DICTIONARY);
        // TODO
        return null;
    }
}

