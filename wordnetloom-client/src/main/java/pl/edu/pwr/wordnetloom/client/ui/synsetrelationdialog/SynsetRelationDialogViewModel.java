package pl.edu.pwr.wordnetloom.client.ui.synsetrelationdialog;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.wordnetloom.client.events.ShowSynsetRelationsEvent;
import pl.edu.pwr.wordnetloom.client.events.UpdateCursorEvent;
import pl.edu.pwr.wordnetloom.client.model.SynsetRelation;
import pl.edu.pwr.wordnetloom.client.model.Relation;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.service.ValidationException;
import pl.edu.pwr.wordnetloom.client.ui.alerts.AlertDialogHandler;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.control.VisualisationPopupGraphMousePlugin;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SynsetRelationScope;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ResourceBundle;

public class SynsetRelationDialogViewModel implements ViewModel {

    private static final Logger LOG = LoggerFactory.getLogger(SynsetRelationDialogViewModel.class);

    public static final String CLOSE_DIALOG_NOTIFICATION = "closeDialog";

    static final String TITLE_LABEL_KEY = "synset.relation.dialog.title";

    private StringProperty title = new SimpleStringProperty();

    @Inject
    ResourceBundle defaultResourceBundle;

    @Inject
    RemoteService service;

    @Inject
    AlertDialogHandler dialogHandler;

    @Inject
    SynsetRelationScope synsetRelationScope;

    @Inject
    Event<ShowSynsetRelationsEvent> eventPublisher;

    private Command okCommand;

    @Inject
    Event<UpdateCursorEvent> updateCursorEvent;

    public Command getOkCommand() {
        return okCommand;
    }

    public void initialize() {
        okCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                ok();
            }
        });
        title.set(defaultResourceBundle.getString(TITLE_LABEL_KEY));
    }

    private void ok() {
        try {
            Relation sr = service.addSynsetRelation(new SynsetRelation(
                    synsetRelationScope.getParentSynset().getId(),
                    synsetRelationScope.getChildSynset().getId(),
                    synsetRelationScope.getRelationType().getId()));
            eventPublisher.fire(new ShowSynsetRelationsEvent(sr));
            publish(CLOSE_DIALOG_NOTIFICATION);
        } catch (Exception e) {
            dialogHandler.handleErrors(e);
        }
    }

    public void reset() {
        updateCursorEvent.fire(new UpdateCursorEvent(VisualisationPopupGraphMousePlugin.DEFAULT_CURSOR));
        synsetRelationScope.reset();
    }

    public StringProperty titleProperty() {
        return title;
    }
}
