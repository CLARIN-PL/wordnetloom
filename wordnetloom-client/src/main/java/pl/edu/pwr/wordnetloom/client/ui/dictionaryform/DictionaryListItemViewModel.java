package pl.edu.pwr.wordnetloom.client.ui.dictionaryform;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.model.RelationTest;
import pl.edu.pwr.wordnetloom.client.model.Sense;

import java.util.Arrays;


public class DictionaryListItemViewModel implements ViewModel {

    private Dictionary dictionaryItem;
    private StringProperty nameProperty = new SimpleStringProperty();

    public DictionaryListItemViewModel(Dictionary d) {
        this.dictionaryItem = d;
        nameProperty.set(d.getName());
    }

    public Dictionary getDictionaryItem() {
        return dictionaryItem;
    }

    public Property<String> dictionaryProperty() {
        return nameProperty;
    }
}

