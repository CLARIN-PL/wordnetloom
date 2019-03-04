package pl.edu.pwr.wordnetloom.client.ui.example;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableStringValue;
import pl.edu.pwr.wordnetloom.client.model.Example;

public class ExampleListItemViewModel implements ViewModel {

    private ReadOnlyStringWrapper example = new ReadOnlyStringWrapper();
    private ReadOnlyStringWrapper type = new ReadOnlyStringWrapper();

    private Example e;

    public ExampleListItemViewModel(Example example) {
        this.e = example;
        this.setExampleText(e.getType(), e.getExample());
    }

    public void setExampleText(String type, String text) {
        this.example.set(text);
        if (type != null) {
            this.type.set("[" + type + "]");
        }
    }

    public Example getExample() {
        return e;
    }

    public ObservableStringValue exampleProperty() {
        return example.getReadOnlyProperty();
    }

    public ObservableStringValue typeProperty() {
        return type.getReadOnlyProperty();
    }

}