package pl.edu.pwr.wordnetloom.client.ui.dictionarydialog;

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
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.model.Sense;
import pl.edu.pwr.wordnetloom.client.ui.alerts.AlertDialogHandler;
import pl.edu.pwr.wordnetloom.client.ui.scopes.DictionaryDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.scopes.ExampleDialogScope;

import javax.inject.Inject;
import java.util.ResourceBundle;
@ScopeProvider(scopes = {DictionaryDialogScope.class})
public class DictionaryDialogViewModel implements ViewModel {

    private static final Logger LOG = LoggerFactory.getLogger(DictionaryDialogViewModel.class);

    public static final String CLOSE_DIALOG_NOTIFICATION = "closeDialog";

    static final String TITLE_LABEL_KEY = "dictionary.dialog.title";

    private StringProperty title = new SimpleStringProperty();

    @Inject
    ResourceBundle defaultResourceBundle;

    @InjectScope
    DictionaryDialogScope dialogScope;

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
    }

    private void save() {
        dialogScope.publish(DictionaryDialogScope.COMMIT);
        Dictionary d = dialogScope.getDictionaryToEdit();
        try {
            if (d.getId() != null) {
                // TODO add implementation
            } else {
                // TODO add implementation
            }
        } catch (Exception e) {
            dialogHandler.handleErrors(e);
        }
    }

    public StringProperty titleProperty() {
        return title;
    }
}
