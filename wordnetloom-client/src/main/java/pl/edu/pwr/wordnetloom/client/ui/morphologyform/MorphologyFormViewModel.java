package pl.edu.pwr.wordnetloom.client.ui.morphologyform;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.model.Morphology;
import pl.edu.pwr.wordnetloom.client.model.Sense;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SensePropertiesDialogScope;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

public class MorphologyFormViewModel implements ViewModel {

    private ObservableList<MorphologyListItemViewModel> morphologyList = FXCollections.observableArrayList();
    private ObjectProperty<MorphologyListItemViewModel> selectedMorphologyListItem = new SimpleObjectProperty<>();

    private Command deleteMorphology;

    @Inject
    RemoteService service;

    @InjectScope
    SensePropertiesDialogScope sensePropertiesDialogScope;

    private Morphology selectedMorphology;

    public void initialize() {
        deleteMorphology = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                deleteMorphology();
            }
        });

        selectedMorphologyListItem.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedMorphology = newValue.getMorphology();
            }
        });

        if (sensePropertiesDialogScope.getSenseToEdit() != null)
            initWithSense(sensePropertiesDialogScope.getSenseToEdit());
    }

    public void initWithSense(Sense sense) {
        morphologyList.clear();

        List<Morphology> morphologies = service.findMorphologies(sense.getLinks().getMorphologies());

        morphologyList.addAll(
                morphologies.stream()
                        .map(MorphologyListItemViewModel::new)
                        .collect(Collectors.toList()));
    }

    public void deleteMorphology() {
        if (selectedMorphology != null) {
            selectedMorphology.getAction()
                    .stream().filter(act -> act.getName().equals("delete-morphology"))
                    .findFirst()
                    .ifPresent(ac -> {
                        boolean success = service.executeAction(ac, Response.Status.NO_CONTENT);
                        if (success) {
                            int current = morphologyList.indexOf(selectedMorphologyListItem.get());
                            if (current > 0) {
                                morphologyList.remove(current);
                            }
                        }
                    });
        }
    }

    public ObservableList<MorphologyListItemViewModel> getMorphologyList() {
        return morphologyList;
    }

    public ObjectProperty<MorphologyListItemViewModel> selectedMorphologyListItemProperty() {
        return selectedMorphologyListItem;
    }

    public Command getDeleteMorphology() {
        return deleteMorphology;
    }
}
