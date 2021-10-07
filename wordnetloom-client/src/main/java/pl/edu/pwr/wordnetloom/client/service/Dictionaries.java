package pl.edu.pwr.wordnetloom.client.service;

import de.saxsys.mvvmfx.utils.itemlist.ItemList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.wordnetloom.client.events.OpenMainApplicationEvent;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.model.RelationType;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Dictionaries {

    private static final Logger LOG = LoggerFactory.getLogger(Dictionaries.class);

    public static final String PART_OF_SPEECH_DICTIONARY = "parts_of_speech";
    public static final String DOMAIN_DICTIONARY = "domains";
    public static final String STATUS_DICTIONARY = "statuses";
    public static final String REGISTER_DICTIONARY = "registers";
    public static final String LEXICON_DICTIONARY = "lexicons";
    public static final String EMOTION_DICTIONARY = "emotion";
    public static final String VALUATION_DICTIONARY = "valuation";
    public static final String MARKEDNESS_DICTIONARY = "markedness";

    private static final Map<String, ObservableList<Dictionary>> dictionaries = new ConcurrentHashMap<>();
    private static final ObservableList<Dictionary> userChosenLexicon = FXCollections.observableArrayList();
    public static ObservableList<Dictionary> getDictionary(String name) {
        return dictionaries.get(name);
    }

    RemoteService service;

    public void setService(RemoteService service) {
        this.service = service;
    }

    public static Set<String> getDictionaryTypes(){
        return  dictionaries.keySet();
    }

    public static void dictionarySelected(ObservableValue ods, String oldV, String newV,
                                          String dictionaryName, String NOTHING_SELECTED, ObjectProperty<Dictionary> obj) {
        if (newV != null && !newV.equals(NOTHING_SELECTED)) {
            Optional<Dictionary> matching = getDictionary(dictionaryName)
                    .stream()
                    .filter(d -> newV.equals(d.getName()))
                    .findFirst();
            matching.ifPresent(obj::set);
        } else if (NOTHING_SELECTED.equals(newV)) {
            obj.set(null);
        }
    }

    public static String getDictionaryItemById(String dic, Long id){
        return  getDictionary(dic)
                .stream()
                .filter(d -> d.getId().equals(id))
                .map(Dictionary::getName)
                .findFirst().orElse(null);
    }

    public static Dictionary getDictionaryById(String dic, Long id){
        return  getDictionary(dic)
                .stream()
                .filter(d -> d.getId().equals(id))
                .findFirst().orElse(null);
    }

    public static Long getDictionaryItemIdByName(String dic, String name){
        return getDictionary(dic)
                .stream()
                .filter(d -> d.getName().equals(name))
                .map(Dictionary::getId)
                .findFirst().orElse(null);
    }

    public static ItemList<Dictionary> initDictionaryItemList(String dictionary){
       return  new ItemList<>(getDictionary(dictionary), Dictionary::getName);
    }

    public static ItemList<Dictionary> initLexiconDictionaryUserChosenItemList(){
        return  new ItemList<>(userChosenLexicon, Dictionary::getName);
    }

    public static ObservableList<String> createListWithNothingSelectedMarker(ObservableList<String> source, String NOTHING_SELECTED) {
        final ObservableList<String> result = FXCollections.observableArrayList();
        result.add(NOTHING_SELECTED);
        result.addAll(source);

        // for sure there are better solutions for this but it's sufficient for our demo
        source.addListener((ListChangeListener<String>) c -> {
            result.clear();
            result.add(NOTHING_SELECTED);
            result.addAll(source);
        });
        return result;
    }

    void initializeDictionaries() throws Exception {
        service.mapDictionaries()
                .forEach((k, v) -> {
                    try {
                        dictionaries.put(k, service.fetchDictionaries(v));
                    } catch (IOException e) {
                        LOG.error("Unable to parse json:", e);
                    }
                });
        loadUserSelectedLexicons();
    }


    public static void loadUserSelectedLexicons(){
        userChosenLexicon.clear();
        List<Dictionary> dic= dictionaries.get(LEXICON_DICTIONARY)
                .stream()
                .filter(d -> RemoteService.activeUser().getLexiconsIds().contains(d.getId()))
                .collect(Collectors.toList());
        userChosenLexicon.addAll(dic);
    }

}
