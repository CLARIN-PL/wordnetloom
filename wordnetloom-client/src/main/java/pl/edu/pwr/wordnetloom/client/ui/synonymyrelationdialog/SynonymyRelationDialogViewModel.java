package pl.edu.pwr.wordnetloom.client.ui.synonymyrelationdialog;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import pl.edu.pwr.wordnetloom.client.events.ShowSynsetPropertiesEvent;
import pl.edu.pwr.wordnetloom.client.events.UpdateSearchItemSynsetEvent;
import pl.edu.pwr.wordnetloom.client.model.Sense;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.service.ValidationException;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SynonymyRelationDialogScope;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ResourceBundle;

public class SynonymyRelationDialogViewModel implements ViewModel {

    public static final String CLOSE_DIALOG_NOTIFICATION = "closeDialog";

    @InjectScope
    SynonymyRelationDialogScope dialogScope;

    @Inject
    Event<ShowSynsetPropertiesEvent> eventManager;

    @Inject
    Event<UpdateSearchItemSynsetEvent> updateSearchItemSynsetEvent;

    @Inject
    RemoteService service;

    @Inject
    ResourceBundle resourceBundle;

    private final IntegerProperty dialogPage = new SimpleIntegerProperty(0);
    private final ReadOnlyBooleanWrapper valid = new ReadOnlyBooleanWrapper();
    private final StringProperty titleText = new SimpleStringProperty();

    public void initialize() {
        valid.bind(
                Bindings.and(dialogScope.senseSearchFormValidProperty(),
                        dialogScope.synonymyRelationFormValidProperty()));

        dialogScope.bothFormsValidProperty().bind(valid);

        dialogScope.subscribe(SynonymyRelationDialogScope.RESET_DIALOG_PAGE,
                (key, payload) -> resetDialogPage());
        titleText.set(resourceBundle.getString("attach.sense.to.synset.dialog.title"));
    }

    public void okAction() {
        dialogScope.publish(SynonymyRelationDialogScope.OK_BEFORE_COMMIT);
        try {
           Sense s = service.attachSenseToSynset(dialogScope.getSense().getId(),
                    dialogScope.getSynset().getId());
           eventManager.fire(new ShowSynsetPropertiesEvent(s.getSynset()));
           updateSearchItemSynsetEvent.fire(new UpdateSearchItemSynsetEvent(s.getSynset(), s.getId()));
        } catch (ValidationException | IOException e) {
            e.printStackTrace();
        }
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
        return dialogScope.synonymyRelationFormValidProperty().not();
    }

    public ReadOnlyBooleanProperty validProperty() {
        return valid;
    }

    public StringProperty titleTextProperty() {
        return titleText;
    }
}
