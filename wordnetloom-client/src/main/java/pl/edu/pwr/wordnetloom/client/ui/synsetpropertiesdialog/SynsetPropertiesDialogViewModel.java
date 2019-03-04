package pl.edu.pwr.wordnetloom.client.ui.synsetpropertiesdialog;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.wordnetloom.client.events.ShowSynsetPropertiesEvent;
import pl.edu.pwr.wordnetloom.client.model.Synset;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.service.ValidationException;
import pl.edu.pwr.wordnetloom.client.ui.alerts.AlertDialogHandler;
import pl.edu.pwr.wordnetloom.client.ui.scopes.ExampleDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SynsetPropertiesDialogScope;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.util.ResourceBundle;

@ScopeProvider(scopes =  ExampleDialogScope.class)
public class SynsetPropertiesDialogViewModel implements ViewModel {

    @Inject
    private RemoteService remoteService;

    @Inject
    ResourceBundle defaultResourceBundle;

    @Inject
    AlertDialogHandler dialogHandler;

    @InjectScope
    SynsetPropertiesDialogScope dialogScope;

    @Inject
    Event<ShowSynsetPropertiesEvent> showSynsetPropertiesEvent;

    private Command saveCommand;
    private Command cancelCommand;

    private static final Logger LOG = LoggerFactory.getLogger(SynsetPropertiesDialogViewModel.class);

    public static final String CLOSE_DIALOG_NOTIFICATION = "synsetCloseDialog";

    static final String TITLE_LABEL_KEY = "synset.properties.dialog.title";

    private StringProperty title = new SimpleStringProperty();

    public void initialize() {
        title.set(defaultResourceBundle.getString(TITLE_LABEL_KEY));
        saveCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                save();
            }
        });
        cancelCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                refresh();
            }
        });
    }

    public Command getSaveCommand() {
        return saveCommand;
    }

    public Command getCancelCommand() {
        return cancelCommand;
    }

    public void save() {
        dialogScope.publish(SynsetPropertiesDialogScope.COMMIT);
        Synset synset = dialogScope.getSynsetToEdit();
        try {
            remoteService.updateSynset(synset);
            dialogHandler.onShowSuccessNotification("Synset saved!");
        } catch (ValidationException ve) {
            dialogHandler.onShowValidationErrorMsg(ve);
        } catch (IOException ioe) {
            dialogHandler.onShowErrorMsg(ioe);
        }
    }

    public void refresh() {
        showSynsetPropertiesEvent.fire(new ShowSynsetPropertiesEvent(dialogScope.getSynsetToEdit().getId()));
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }
}
