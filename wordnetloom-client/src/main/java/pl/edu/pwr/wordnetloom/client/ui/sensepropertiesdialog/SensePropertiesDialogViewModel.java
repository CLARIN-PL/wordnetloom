package pl.edu.pwr.wordnetloom.client.ui.sensepropertiesdialog;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.wordnetloom.client.events.LoadGraphEvent;
import pl.edu.pwr.wordnetloom.client.model.Sense;
import pl.edu.pwr.wordnetloom.client.model.YiddishProperties;
import pl.edu.pwr.wordnetloom.client.model.YiddishProperty;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.alerts.AlertDialogHandler;
import pl.edu.pwr.wordnetloom.client.ui.scopes.ExampleDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SensePropertiesDialogScope;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.ResourceBundle;

@ScopeProvider(scopes = ExampleDialogScope.class)
public class SensePropertiesDialogViewModel implements ViewModel {

    private static final Logger LOG = LoggerFactory.getLogger(SensePropertiesDialogViewModel.class);

    public static final String UPDATE_TAB_NAME = "update_tab_name";
    public static final String CLOSE_DIALOG_NOTIFICATION = "closeDialog";
    public static final String ADD_YIDDISH_PROPERTY = "yiddishProperty";
    public static final String REMOVE_YIDDISH_PROPERTY = "removeYiddishProperty";

    static final String TITLE_LABEL_KEY = "sense.properties.dialog.title";

    private StringProperty title = new SimpleStringProperty();

    @Inject
    ResourceBundle defaultResourceBundle;

    @InjectScope
    SensePropertiesDialogScope dialogScope;

    @Inject
    RemoteService service;

    @Inject
    AlertDialogHandler dialogHandler;

    @Inject
    Event<LoadGraphEvent> eventManager;

    private final ReadOnlyBooleanWrapper valid = new ReadOnlyBooleanWrapper();

    private Command saveCommand;
    private Command closeCommand;

    public Command getSaveCommand() {
        return saveCommand;
    }

    private YiddishProperties yp;

    public void initialize() {
        saveCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                save();
            }
        });
        closeCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                close();
            }
        });
        title.set(defaultResourceBundle.getString(TITLE_LABEL_KEY));
        valid.bind(dialogScope.senseFormValidProperty());

        Sense ss = dialogScope.getSenseToEdit();
        if (ss != null &&  ss.getLinks() != null && ss.getLinks().getYiddish() != null) {
            yp = service.findYiddishProperties(ss.getLinks().getYiddish());

        }
        dialogScope.subscribe(SensePropertiesDialogScope.ADD_YIDDISH_PROPERTY, (s, objects) -> {
            YiddishProperty p = (YiddishProperty) objects[0];
            yp.getRows().add(p);
            publish(ADD_YIDDISH_PROPERTY, p);
        });
        dialogScope.subscribe(SensePropertiesDialogScope.UPDATE_TAB_NAME, (s, objects) -> {
            publish(UPDATE_TAB_NAME, objects);
        });

        dialogScope.subscribe(SensePropertiesDialogScope.REMOVE_YIDDISH_PROPERTY, (s, objects) -> {
            publish(REMOVE_YIDDISH_PROPERTY, objects);
        });
    }

    private void close() {
        Sense s = dialogScope.getSenseToEdit();
        if(s != null && s.getSynset() != null){
            eventManager.fireAsync(new LoadGraphEvent(s.getSynset(),false,false));
        }
    }

    public YiddishProperties getYp() {
        return yp;
    }

    private void save() {
        dialogScope.publish(SensePropertiesDialogScope.COMMIT);
        Sense s = dialogScope.getSenseToEdit();
        try {
            if (s.getId() != null) {
                Sense us = service.updateSense(s);
                dialogScope.setSenseToEdit(us);
                dialogHandler.onShowSuccessNotification("Sense updated");
            } else {
                Sense ns = service.saveSense(s);
                dialogScope.setSenseToEdit(ns);
                dialogHandler.onShowSuccessNotification("Sense saved");
            }
        } catch (Exception e) {
            dialogHandler.handleErrors(e);
        }
    }

    public StringProperty titleProperty() {
        return title;
    }

    public ObservableBooleanValue saveButtonDisabledProperty() {
        return valid.not();
    }

    public Command getCloseCommand() {
        return closeCommand;
    }

}
