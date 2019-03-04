package pl.edu.pwr.wordnetloom.client.ui.relationtestform;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.wordnetloom.client.model.RelationTest;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.service.ValidationException;
import pl.edu.pwr.wordnetloom.client.ui.scopes.RelationTestDialogScope;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ResourceBundle;

public class RelationTestDialogViewModel implements ViewModel {

    private static final Logger LOG = LoggerFactory.getLogger(RelationTestDialogViewModel.class);

    public static final String SUCCESS_NOTIFICATION = "success_notification";
    public static final String CLOSE_DIALOG_NOTIFICATION = "closeDialog";

    private static final String TITLE_LABEL_KEY = "relation.test.dialog.title";

    private StringProperty title = new SimpleStringProperty();

    @Inject
    ResourceBundle defaultResourceBundle;

    @Inject
    RemoteService service;

    @InjectScope
    RelationTestDialogScope dialogScope;

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
    }

    private void save() {
        dialogScope.publish(RelationTestDialogScope.COMMIT);
        try {

            RelationTest t = dialogScope.getRelationTestToEdit();
            if (t.getId() == null) {
                service.addRelationTest(t);
            } else {
                service.updateRelationTest(t);
            }
            dialogScope.publish(RelationTestDialogScope.RELOAD_TESTS);
            publish(RelationTestDialogViewModel.SUCCESS_NOTIFICATION);
            publish(CLOSE_DIALOG_NOTIFICATION);

        } catch (ValidationException ve) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error Dialog");
            alert.setContentText("Invalid fields!");
            alert.showAndWait();

        } catch (IOException e) {
            LOG.error("Error while saving relation test", e);
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
