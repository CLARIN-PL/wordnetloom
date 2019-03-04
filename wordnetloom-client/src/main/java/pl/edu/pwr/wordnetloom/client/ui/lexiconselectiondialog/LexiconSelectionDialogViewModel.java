package pl.edu.pwr.wordnetloom.client.ui.lexiconselectiondialog;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.IndexedCheckModel;
import pl.edu.pwr.wordnetloom.client.events.UserLexiconUpdatedEvent;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.ui.scopes.RelationTypeDialogScope;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.List;
import java.util.ResourceBundle;

public class LexiconSelectionDialogViewModel implements ViewModel {

    public static final String CLOSE_DIALOG_NOTIFICATION = "close_dialog";
    private StringProperty title = new SimpleStringProperty();
    private ObservableList<Dictionary> lexiconsList = FXCollections.observableArrayList();

    private Command okCommand;

    @InjectScope
    RelationTypeDialogScope relationTypeDialogScope;

    @Inject
    ResourceBundle defaultResourceBundle;

    private IndexedCheckModel<Dictionary> checkModel;

    public void initialize() {
        titleProperty().set(defaultResourceBundle.getString("lexicon_selection_dialog_title"));
        loadLexicons();
        okCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                ok();
            }
        });

        relationTypeDialogScope.subscribe(RelationTypeDialogScope.LOAD_LEXICONS, (s, objects) -> {
            setSelectedLexicons(relationTypeDialogScope.getRelationTypeToEdit().getAllowedLexicons());
        });
    }

    public void onUserLexiconUpdate(@Observes UserLexiconUpdatedEvent evn){
        loadLexicons();
    }

    public void loadLexicons(){
        Platform.runLater(() -> {
            lexiconsList.addAll(Dictionaries.getDictionary(Dictionaries.LEXICON_DICTIONARY));
        });
    }

    public void setSelectedLexicons(List<Dictionary> selected) {
        if (selected != null) {
            Platform.runLater(() -> {
                selected.forEach(d -> checkModel.check(d));
            });
        }
    }

    public void ok() {
        relationTypeDialogScope.getRelationTypeToEdit()
                .setAllowedLexicons(checkModel.getCheckedItems());
        relationTypeDialogScope.publish(RelationTypeDialogScope.REFRESH_MODEL);
        publish(CLOSE_DIALOG_NOTIFICATION);
    }

    public ObservableList<Dictionary> getLexiconsList() {
        return lexiconsList;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public Command getOkCommand() {
        return okCommand;
    }

    public IndexedCheckModel<Dictionary> getCheckModel() {
        return checkModel;
    }

    public void setCheckModel(IndexedCheckModel<Dictionary> checkModel) {
        this.checkModel = checkModel;
    }
}

