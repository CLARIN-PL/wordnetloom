package pl.edu.pwr.wordnetloom.client.ui.corpusexamples;

import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.events.ShowCorpusExampleEvent;
import pl.edu.pwr.wordnetloom.client.model.CorpusExamples;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.stream.Collectors;

@Singleton
public class CorpusExampleViewModel implements ViewModel {

    @Inject
    RemoteService service;

    private ObservableList<CorpusExampleListItemViewModel> exampleList = FXCollections.observableArrayList();
    private ObjectProperty<CorpusExampleListItemViewModel> selectedExampleListItem = new SimpleObjectProperty<>();

    public ObservableList<CorpusExampleListItemViewModel> examplesProperty() {
        return exampleList;
    }

    public ObjectProperty<CorpusExampleListItemViewModel> selectedCorpusExampleItemProperty() {
        return  selectedExampleListItem;
    }

    public void onOnShowCorpusExamples(@Observes ShowCorpusExampleEvent event){
        try {
            String w = event.getWord().substring(0, event.getWord().indexOf("("));
            w = w.trim().replaceAll("\\d+$", "");
            CorpusExamples ce = service.getCorpusExamples(w.trim());
            Platform.runLater(() -> {
                exampleList.clear();
                exampleList.addAll(ce.getRows()
                        .stream()
                        .map(CorpusExampleListItemViewModel::new)
                        .collect(Collectors.toList()));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
