package pl.edu.pwr.wordnetloom.client.ui.exampleform;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.edu.pwr.wordnetloom.client.model.Example;
import pl.edu.pwr.wordnetloom.client.ui.scopes.ExampleDialogScope;

import javax.inject.Singleton;

public class ExampleFormViewModel implements ViewModel {

    private final StringProperty text = new SimpleStringProperty();

    @InjectScope
    ExampleDialogScope dialogScope;

    public void initialize(){

        dialogScope.subscribe(ExampleDialogScope.ON_LOAD, (s, objects) -> {
            ObjectProperty<Example> example = dialogScope.exampleToEditProperty();
            if (example.get() != null) {
                text.set(example.get().getExample());
            }
        });

        dialogScope.subscribe(ExampleDialogScope.COMMIT, (s, objects) -> {
            dialogScope.getExampleToEdit().setExample(text.get());
            dialogScope.publish(ExampleDialogScope.AFTER_COMMIT);
        });

    }

    public StringProperty textProperty() {
        return text;
    }
}