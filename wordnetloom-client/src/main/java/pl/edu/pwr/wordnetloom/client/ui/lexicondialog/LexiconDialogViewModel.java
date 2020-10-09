package pl.edu.pwr.wordnetloom.client.ui.lexicondialog;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.wordnetloom.client.model.Lexicon;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.alerts.AlertDialogHandler;
import pl.edu.pwr.wordnetloom.client.ui.scopes.LexiconDialogScope;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ResourceBundle;

@ScopeProvider(scopes = LexiconDialogScope.class)
public class LexiconDialogViewModel implements ViewModel {

    private static final Logger LOG = LoggerFactory.getLogger(LexiconDialogViewModel.class);

    public static final String CLOSE_DIALOG_NOTIFICATION = "closeDialog";

    static final String TITLE_LABEL_KEY = "lexicon.dialog.title";

    private StringProperty title = new SimpleStringProperty();
    private final ReadOnlyBooleanWrapper disableSave = new ReadOnlyBooleanWrapper();

    @Inject
    ResourceBundle defaultResourceBundle;

    @InjectScope
    LexiconDialogScope lexiconDialogScope;

    @Inject
    RemoteService remoteService;

    @Inject
    AlertDialogHandler dialogHandler;

    private Command saveCommand;

    public Command getSaveCommand() {
        return saveCommand;
    }

    public void initialize() {
        title.set(defaultResourceBundle.getString(TITLE_LABEL_KEY));
        saveCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                save();
            }
        });
        lexiconDialogScope.lexiconFormValidProperty().bind(disableSave);
    }

    public void save() {
        lexiconDialogScope.publish(LexiconDialogScope.COMMIT);
        Lexicon l = lexiconDialogScope.getLexicon();
        try {
            if (l.getId() != null) {
                remoteService.updateLexicon(l);
                dialogHandler.onShowSuccessNotification("Saved");
            } else {
                remoteService.addLexicon(l);
            }
        } catch (IOException e) {
           dialogHandler.handleErrors(e);
        }
    }

    public StringProperty titleProperty() {
        return title;
    }

    public boolean isDisableSave() {
        return disableSave.get();
    }

    public ReadOnlyBooleanWrapper disableSaveProperty() {
        return disableSave;
    }

    public void setDisableSave(boolean disableSave) {
        this.disableSave.set(disableSave);
    }

}
