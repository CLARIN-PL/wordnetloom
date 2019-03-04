package pl.edu.pwr.wordnetloom.client.ui.logindialog;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.wordnetloom.client.events.OpenMainApplicationEvent;
import pl.edu.pwr.wordnetloom.client.model.User;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.scopes.AlertScope;
import pl.edu.pwr.wordnetloom.client.ui.scopes.LoginDialogScope;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Locale;
import java.util.ResourceBundle;

@Singleton
@ScopeProvider(scopes = AlertScope.class)
public class LoginDialogViewModel implements ViewModel {

    private static final Logger LOG = LoggerFactory.getLogger(LoginDialogViewModel.class);

    public static final String CLOSE_DIALOG_NOTIFICATION = "closeDialog";

    static final String TITLE_LABEL_KEY = "logindialog.title";

    @InjectScope
    LoginDialogScope dialogScope;

    @Inject
    ResourceBundle defaultResourceBundle;

    @InjectScope
    AlertScope alertScope;

    @Inject
    RemoteService remoteService;

    @Inject
    Event<OpenMainApplicationEvent> openMainApplicationEvent;

    public void initialize() {
        dialogScope.subscribe(LoginDialogScope.OK_BEFORE_COMMIT, (key, payload) -> {
            loginAction();
        });

        dialogScope.dialogTitleProperty().set(defaultResourceBundle.getString(TITLE_LABEL_KEY));
        dialogScope.publish(LoginDialogScope.RESET_FORMS);
        alertScope.publish(AlertScope.RESET_MESSAGE);

        User user = new User();
        dialogScope.setUser(user);
    }

    public void loginAction() {
        if (dialogScope.isLoginFormValid()) {

            dialogScope.publish(LoginDialogScope.COMMIT);
            dialogScope.publish(LoginDialogScope.RESET_DIALOG_PAGE);

            User user = dialogScope.getUser();
            try {
                remoteService.authorize(user);
                alertScope.setMessage("Operation success full");
                alertScope.publish(AlertScope.SHOW_SUCCESS_MESSAGE);
                dialogScope.setUser(null);

                Locale.setDefault(user.getLanguage().getLocale());
                MvvmFX.setGlobalResourceBundle(ResourceBundle.getBundle("default",user.getLanguage().getLocale()));
                openMainApplicationEvent.fire(new OpenMainApplicationEvent());
                publish(CLOSE_DIALOG_NOTIFICATION);
            } catch (Exception e) {
                LOG.debug("LoginAction:", e);
                alertScope.setMessage(e.getMessage());
                alertScope.publish(AlertScope.SHOW_ERROR_MESSAGE);
            }

        }
    }
}
