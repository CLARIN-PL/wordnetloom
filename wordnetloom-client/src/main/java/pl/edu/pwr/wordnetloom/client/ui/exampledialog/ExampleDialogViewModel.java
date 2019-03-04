package pl.edu.pwr.wordnetloom.client.ui.exampledialog;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.wordnetloom.client.model.Example;
import pl.edu.pwr.wordnetloom.client.model.Sense;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.scopes.ExampleDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SensePropertiesDialogScope;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ResourceBundle;

public class ExampleDialogViewModel implements ViewModel {

    private static final Logger LOG = LoggerFactory.getLogger(ExampleDialogViewModel.class);

    public static final String CLOSE_DIALOG_NOTIFICATION = "closeDialog";

    static final String TITLE_LABEL_KEY = "example.dialog.title";

    private StringProperty title = new SimpleStringProperty();

    @Inject
    ResourceBundle defaultResourceBundle;

    @InjectScope
    ExampleDialogScope dialogScope;

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

        dialogScope.subscribe(ExampleDialogScope.CLOSE, (s, objects) -> {
            publish(CLOSE_DIALOG_NOTIFICATION);
        });
    }

    private void save() {
        dialogScope.publish(ExampleDialogScope.COMMIT, dialogScope.isSynsetExample());
    }

    public StringProperty titleProperty() {
        return title;
    }
}
