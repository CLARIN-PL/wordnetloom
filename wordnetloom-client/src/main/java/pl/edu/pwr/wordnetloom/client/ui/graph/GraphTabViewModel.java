package pl.edu.pwr.wordnetloom.client.ui.graph;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import pl.edu.pwr.wordnetloom.client.events.ShowCorpusExampleEvent;
import pl.edu.pwr.wordnetloom.client.events.ShowSynsetPropertiesEvent;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.GraphController;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.net.URI;
import java.util.UUID;

public class GraphTabViewModel implements ViewModel {

    @Inject
    GraphController graphController;

    @Inject
    Event<ShowSynsetPropertiesEvent> publisherShowSynsetProperties;

    @Inject
    Event<ShowCorpusExampleEvent> publisherShowCorpusExample;

    private final BooleanProperty progressOverlay = new SimpleBooleanProperty();

    private ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();

    public void initialize() {
        progressOverlay.setValue(false);
        graphController.setGraphTabViewModel(this);
    }

    public GraphController getGraphController() {
        return graphController;
    }

    public void loadGraph(UUID id) {
        graphController.refreshView(id);
    }

    public void showSynsetProperties(UUID id) {
        publisherShowSynsetProperties.fire(new ShowSynsetPropertiesEvent(id));
    }

    public void showCorpusExamples(String word) {
        publisherShowCorpusExample.fire(new ShowCorpusExampleEvent(word));
    }

    public Property<Boolean> progressOverlayProperty() {
        return progressOverlay;
    }

    public ObservableList<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(ObservableList<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
}
