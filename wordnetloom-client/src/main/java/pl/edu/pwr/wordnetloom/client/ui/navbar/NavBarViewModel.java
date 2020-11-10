package pl.edu.pwr.wordnetloom.client.ui.navbar;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.edu.pwr.wordnetloom.client.events.TriggerShutdownEvent;
import pl.edu.pwr.wordnetloom.client.events.UserLexiconUpdatedEvent;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;


public class NavBarViewModel implements ViewModel {

    public static final String OPEN_RELATION_TYPES_DIALOG = "open_relation_type_dialog";
    public static final String OPEN_LEXICON_DIALOG = "open_lexicon.dialog";
    public static final String OPEN_USERS_DIALOG = "open_users.dialog";
    public static final String OPEN_PROFILE_DIALOG = "open_profile_dialog";
    public static final String OPEN_DICTIONARIES_DIALOG = "open_dictionaries_dialog";

    private final StringProperty username = new SimpleStringProperty();

    @Inject
    Event<TriggerShutdownEvent> shutdownEvent;

    public void initialize() {
        loadUsername();
    }

    public void onRefreshUserProfile(@Observes(notifyObserver = Reception.ALWAYS) UserLexiconUpdatedEvent ev) {
        loadUsername();
    }

    private void loadUsername() {
        username.set(RemoteService.activeUser().getFullName().trim());
    }

    public Property<String> usernameProperty() {
        return username;
    }

    public void logoutAction() {
        shutdownEvent.fire(new TriggerShutdownEvent());
    }

    public void showRelationTypes() {
        publish(OPEN_RELATION_TYPES_DIALOG);
    }

    public void showLexiconDialog() {
        publish(OPEN_LEXICON_DIALOG);
    }

    public void showUsersDialog() {
        publish(OPEN_USERS_DIALOG);
    }

    public void showProfileDialog() {
        publish(OPEN_PROFILE_DIALOG);
    }

    public void showDictionariesDialog() {
            publish(OPEN_DICTIONARIES_DIALOG);
    }
}
