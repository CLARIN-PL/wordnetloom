package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.control;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Command;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class SynsetNodeListViewModel implements ViewModel {

    private ObservableList<SynsetNodeListItemViewModel> synsetNodeList = FXCollections.observableArrayList();
    private ObjectProperty<SynsetNodeListItemViewModel> selectedSynsetNodeListItem = new SimpleObjectProperty<>();

    private Command addSynsetCommand;

    public ObservableList<SynsetNodeListItemViewModel> synsetNodesProperty() {
        return synsetNodeList;
    }

    public ObjectProperty<SynsetNodeListItemViewModel> selectedSynsetNodeItemProperty() {
        return selectedSynsetNodeListItem;
    }

    public void addSynsetCommand(Command command){
        this.addSynsetCommand = command;
    }

    public Command getAddSynsetCommand() {
        return addSynsetCommand;
    }
}
