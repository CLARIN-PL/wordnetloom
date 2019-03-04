package pl.edu.pwr.wordnetloom.client.ui.synsetrelationremovedialog;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import pl.edu.pwr.wordnetloom.client.model.*;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.graph.GraphViewModel;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SynsetRelationRemoveDialogViewModel implements ViewModel {

    @Inject
    RemoteService service;

    @Inject
    GraphViewModel graphViewModel;

    private StringProperty title = new SimpleStringProperty("Remove relation");
    private ObservableList<Relation> relationList = FXCollections.observableArrayList();

    public void initialize() {
    }

    public void setRelations(List<Relation> r){
        relationList.clear();
        relationList.addAll(r);
    }

    public ObservableList<Relation> getRelationList() {
        return relationList;
    }

    public void loadRelations(UUID source, UUID target){
        Relations from = service.findSynsetRelationBetween(source, target);
        Relations to = service.findSynsetRelationBetween(target, source);
        relationList.addAll(from.getRows());
        relationList.addAll(to.getRows());
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void removeRelations(List<Relation> relations){
        graphViewModel.removeRelations(relations);
    }
}

