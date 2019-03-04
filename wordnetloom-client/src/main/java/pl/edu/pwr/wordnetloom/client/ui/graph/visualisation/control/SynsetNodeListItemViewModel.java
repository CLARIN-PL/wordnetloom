package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.control;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableStringValue;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.SynsetNode;

public class SynsetNodeListItemViewModel implements ViewModel {

    private ReadOnlyStringWrapper label = new ReadOnlyStringWrapper();
    private SynsetNode synsetNode;

    public SynsetNodeListItemViewModel(SynsetNode node) {
        this.synsetNode = node;
        label.set(synsetNode.getLabel());
    }

    public SynsetNode getSynsetNode() {
        return synsetNode;
    }

    public ObservableStringValue labelProperty() {
        return label.getReadOnlyProperty();
    }
}

