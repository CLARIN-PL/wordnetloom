package pl.edu.pwr.wordnetloom.client.ui.profiledialog;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.wordnetloom.client.events.UserLexiconUpdatedEvent;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.service.ValidationException;
import pl.edu.pwr.wordnetloom.client.ui.scopes.ProfileDialogScope;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ResourceBundle;

@ScopeProvider(scopes = ProfileDialogScope.class)
public class ProfileDialogViewModel implements ViewModel {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileDialogViewModel.class);

    public static final String SUCCESS_NOTIFICATION = "success_notification";
    public static final String CLOSE_DIALOG_NOTIFICATION = "closeDialog";

    private static final String TITLE_LABEL_KEY = "profile.dialog.title";

    private StringProperty title = new SimpleStringProperty();

    @Inject
    ResourceBundle defaultResourceBundle;

    @Inject
    RemoteService service;

    @InjectScope
    ProfileDialogScope dialogScope;

    @Inject
    Event<UserLexiconUpdatedEvent> eventManager;

    private Command saveCommand;

    private final ReadOnlyBooleanWrapper disableSave = new ReadOnlyBooleanWrapper();

    public void initialize() {
        title.set(defaultResourceBundle.getString(TITLE_LABEL_KEY));

        saveCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                save();
            }
        });
        dialogScope.userFormValidProperty().bind(disableSave);
    }

    private void save() {
        dialogScope.publish(ProfileDialogScope.COMMIT);
        try {
            if (dialogScope.userFormValidProperty().getValue()) {
                service.updateUser();
                Dictionaries.loadUserSelectedLexicons();
                eventManager.fire(new UserLexiconUpdatedEvent());
                publish(ProfileDialogViewModel.SUCCESS_NOTIFICATION);
            }
        } catch (ValidationException ve) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error Dialog");
            alert.setContentText("Invalid fields!");
            alert.showAndWait();
        } catch (IOException e) {
            LOG.error("Error while saving sense", e);
        }
    }

    public StringProperty titleProperty() {
        return title;
    }

    public Command getSaveCommand() {
        return saveCommand;
    }

    public ReadOnlyBooleanWrapper disableSaveProperty() {
        return disableSave;
    }
}
