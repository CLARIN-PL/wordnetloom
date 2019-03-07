package pl.edu.pwr.wordnetloom.client.ui.yiddishpropertiesform;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.edu.pwr.wordnetloom.client.model.YiddishSemanticField;


public class SemanticFieldListItemViewModel implements ViewModel {

    private YiddishSemanticField semanticField;
    private StringProperty nameProperty = new SimpleStringProperty();

    public SemanticFieldListItemViewModel(YiddishSemanticField sf) {
        this.semanticField = sf;
        StringBuilder txt = new StringBuilder();
        txt.append(sf.getDomain().getName());
        if (sf.getModifier() != null) {
            txt.append("(")
                    .append(sf.getModifier().getName())
                    .append(")");
        }
        nameProperty.set(txt.toString());
    }

    public YiddishSemanticField getItem() {
        return semanticField;
    }

    public Property<String> dictionaryProperty() {
        return nameProperty;
    }
}

