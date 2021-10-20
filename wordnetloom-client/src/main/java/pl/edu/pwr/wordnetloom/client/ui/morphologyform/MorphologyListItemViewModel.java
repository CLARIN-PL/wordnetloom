package pl.edu.pwr.wordnetloom.client.ui.morphologyform;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ReadOnlyStringWrapper;
import pl.edu.pwr.wordnetloom.client.model.Morphology;

public class MorphologyListItemViewModel implements ViewModel {

    private ReadOnlyStringWrapper label = new ReadOnlyStringWrapper();
    private Morphology morphology;

    public MorphologyListItemViewModel(Morphology morphology) {
        this.morphology = morphology;
        label.set(morphology.getFullName());
    }

    public String getLabel() {
        return label.get();
    }

    public ReadOnlyStringWrapper labelProperty() {
        return label;
    }

    public Morphology getMorphology() {
        return morphology;
    }
}
