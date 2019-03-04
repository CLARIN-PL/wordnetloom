package pl.edu.pwr.wordnetloom.client.ui.relationtypesdialog;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.wordnetloom.client.model.GlobalWordnetRelationType;
import pl.edu.pwr.wordnetloom.client.model.Links;
import pl.edu.pwr.wordnetloom.client.model.RelationArgument;
import pl.edu.pwr.wordnetloom.client.model.RelationType;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.NodeDirection;
import pl.edu.pwr.wordnetloom.client.ui.scopes.RelationTestDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.scopes.RelationTypeDialogScope;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

@ScopeProvider(scopes = {RelationTypeDialogScope.class, RelationTestDialogScope.class})
public class RelationTypesDialogViewModel implements ViewModel {

    public static final String SUCCESS_NOTIFICATION = "saved";
    private static final Logger LOG = LoggerFactory.getLogger(RelationTypesDialogViewModel.class);

    public static final String CLOSE_DIALOG_NOTIFICATION = "closeDialog";

    static final String TITLE_LABEL_KEY = "relation.type.dialog.title";

    private StringProperty title = new SimpleStringProperty();

    private Command saveCommand;
    private Command createCommand;
    private Command deleteCommand;

    @Inject
    ResourceBundle defaultResourceBundle;

    @Inject
    RemoteService service;

    @InjectScope
    RelationTypeDialogScope dialogScope;


    public void initialize() {
        title.set(defaultResourceBundle.getString(TITLE_LABEL_KEY));
        saveCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                save();
            }
        });
        deleteCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                delete();
            }
        });
        createCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                create();
            }
        });
    }

    public void save() {
        dialogScope.publish(RelationTypeDialogScope.COMMIT);
        RelationType rt = dialogScope.getRelationTypeToEdit();
        if (rt != null) {
            try {
                if (rt.getId() == null) {
                    RelationType r = service.addRelationType(rt);
                    dialogScope.setRelationTypeToEdit(r);
                    publish(SUCCESS_NOTIFICATION);

                } else {
                    service.updateRelationType(rt);
                    publish(SUCCESS_NOTIFICATION);
                }
            } catch (IOException e) {
                LOG.error("Error saving relation type", e);
            }
            dialogScope.publish(RelationTypeDialogScope.RELOAD_RELATIONS, rt);
        }
    }

    public void delete() {
        if(dialogScope.getRelationTypeToEdit() != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure? You will remove relation type.",
                     ButtonType.NO,ButtonType.YES);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                service.delete(dialogScope.getRelationTypeToEdit().getLinks().getSelf());
                dialogScope.publish(RelationTypeDialogScope.RELOAD_RELATIONS);
            }
        }
    }

    public void create() {
        RelationType rt = new RelationType();
        rt.setArgument(RelationArgument.SYNSET_RELATION);
        rt.setDirection(NodeDirection.IGNORE);
        rt.setGlobalWordnetRelationType(GlobalWordnetRelationType.other);
        rt.setAutoReverse(false);
        rt.setMultilingual(false);
        rt.setColor("#000000");
        rt.setLinks(new Links());
        rt.setAllowedLexicons(new ArrayList<>());
        rt.setAllowedPartsOfSpeech(new ArrayList<>());

        dialogScope.setRelationTypeToEdit(rt);
        dialogScope.publish(RelationTypeDialogScope.SHOW_NEW_RELATION);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public Command getSaveCommand() {
        return saveCommand;
    }

    public Command getCreateCommand() {
        return createCommand;
    }

    public Command getDeleteCommand() {
        return deleteCommand;
    }
}
