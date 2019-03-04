package pl.edu.pwr.wordnetloom.client.ui.synsetproperties;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableStringValue;
import pl.edu.pwr.wordnetloom.client.model.Sense;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.tooltip.SenseTooltipCreator;



public class SenseListItemViewModel implements ViewModel {

    private ReadOnlyStringWrapper label = new ReadOnlyStringWrapper();
    private Sense sense;

    private RemoteService remoteService;

    public SenseListItemViewModel(Sense sense, RemoteService remoteService) {
        this.sense = sense;
        label.set(sense.getLabel());
        this.remoteService = remoteService;
    }

    public Sense getSense() {
        return sense;
    }

    public ObservableStringValue labelProperty() {
        return label.getReadOnlyProperty();
    }

    public String getTooltipText() {
        return SenseTooltipCreator.create(sense, remoteService);
    }

}

