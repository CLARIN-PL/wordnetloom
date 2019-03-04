package pl.edu.pwr.wordnetloom.client.ui.senserelationformdialog;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.wordnetloom.client.events.LoadGraphEvent;
import pl.edu.pwr.wordnetloom.client.events.RefreshSenseRelationsEvent;
import pl.edu.pwr.wordnetloom.client.model.SenseRelation;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.alerts.AlertDialogHandler;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SenseRelationDialogScope;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ResourceBundle;

public class SenseRelationDialogViewModel implements ViewModel {

    public static final String CLOSE_DIALOG_NOTIFICATION = "closeDialog";

    @InjectScope
    SenseRelationDialogScope dialogScope;

    @Inject
    ResourceBundle resourceBundle;

    @Inject
    RemoteService service;

    @Inject
    public Event<RefreshSenseRelationsEvent> eventPublisher;

    private final IntegerProperty dialogPage = new SimpleIntegerProperty(0);
    private final ReadOnlyBooleanWrapper valid = new ReadOnlyBooleanWrapper();
    private final StringProperty titleText = new SimpleStringProperty();

    public void initialize() {
        valid.bind(
                Bindings.and(dialogScope.senseSearchFormValidProperty(), dialogScope.senseRelationFormValidProperty()));

        dialogScope.bothFormsValidProperty().bind(valid);

        dialogScope.subscribe(SenseRelationDialogScope.RESET_DIALOG_PAGE,
                (key, payload) -> resetDialogPage());

        titleText.set(resourceBundle.getString("add.sense.relation.dialog.title"));
    }

    public void okAction() {
        dialogScope.publish(SenseRelationDialogScope.OK_BEFORE_COMMIT);
        try {
            service.addSenseRelation(new SenseRelation(
                    dialogScope.getParentSense().getId(),
                    dialogScope.getChildSense().getId(),
                    dialogScope.getRelationType().getId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        eventPublisher.fire(new RefreshSenseRelationsEvent());
        publish(CLOSE_DIALOG_NOTIFICATION);
    }

    public void previousAction() {
        if (dialogPage.get() == 1) {
            dialogPage.set(0);
        }
    }

    public void nextAction() {
        if (dialogPage.get() == 0) {
            dialogPage.set(1);
        }
    }

    private void resetDialogPage() {
        dialogPage.set(0);
    }

    public IntegerProperty dialogPageProperty() {
        return dialogPage;
    }

    public ObservableBooleanValue okButtonDisabledProperty() {
        return valid.not();
    }

    public ObservableBooleanValue okButtonVisibleProperty() {
        return dialogPage.isEqualTo(1);
    }

    public ObservableBooleanValue nextButtonDisabledProperty() {
        return dialogScope.senseSearchFormValidProperty().not();
    }

    public ObservableBooleanValue nextButtonVisibleProperty() {
        return dialogPage.isEqualTo(0);
    }

    public ObservableBooleanValue previousButtonVisibleProperty() {
        return dialogPage.isEqualTo(1);
    }

    public ObservableBooleanValue previousButtonDisabledProperty() {
        return dialogScope.senseRelationFormValidProperty().not();
    }

    public ReadOnlyBooleanProperty validProperty() {
        return valid;
    }

    public StringProperty titleTextProperty() {
        return titleText;
    }
}
