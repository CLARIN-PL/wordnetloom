package pl.edu.pwr.wordnetloom.client.ui.yiddishpropertiesform;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.edu.pwr.wordnetloom.client.model.YiddishTranscription;


public class TranscriptionListItemViewModel implements ViewModel {

    private YiddishTranscription field;
    private StringProperty nameProperty = new SimpleStringProperty();

    public TranscriptionListItemViewModel(YiddishTranscription i) {
        this.field = i;
        StringBuilder txt = new StringBuilder();
        txt.append(i.getName());
        if (i.getPhonography() != null) {
            txt.append(" (")
                    .append(i.getPhonography()).append(")");
        }
        nameProperty.set(txt.toString());
    }

    public YiddishTranscription getItem() {
        return field;
    }

    public Property<String> dictionaryProperty() {
        return nameProperty;
    }
}

