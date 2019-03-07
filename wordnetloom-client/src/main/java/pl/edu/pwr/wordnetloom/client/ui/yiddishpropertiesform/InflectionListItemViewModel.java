package pl.edu.pwr.wordnetloom.client.ui.yiddishpropertiesform;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.edu.pwr.wordnetloom.client.model.YiddishInflection;
import pl.edu.pwr.wordnetloom.client.model.YiddishSemanticField;


public class InflectionListItemViewModel implements ViewModel {

    private YiddishInflection field;
    private StringProperty nameProperty = new SimpleStringProperty();

    public InflectionListItemViewModel(YiddishInflection i) {
        this.field = i;
        StringBuilder txt = new StringBuilder();
        txt.append(i.getName());
        if (i.getText() != null) {
            txt.append(" ")
                    .append(i.getText());
        }
        nameProperty.set(txt.toString());
    }

    public YiddishInflection getItem() {
        return field;
    }

    public Property<String> dictionaryProperty() {
        return nameProperty;
    }
}

