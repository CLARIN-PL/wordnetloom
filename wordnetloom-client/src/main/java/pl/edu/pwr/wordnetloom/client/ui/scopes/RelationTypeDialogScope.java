package pl.edu.pwr.wordnetloom.client.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import pl.edu.pwr.wordnetloom.client.model.RelationType;
import pl.edu.pwr.wordnetloom.client.model.Synset;

public class RelationTypeDialogScope implements Scope {

    public static final String LOAD_PARTS_OF_SPEECH = "load_parts_of_speech";
    public static final String REFRESH_MODEL = "refresh_model_view";
    public static final String LOAD_LEXICONS = "load_lexicons";
    public static String RESET_DIALOG_PAGE = "relation_type_reset_dialog_page";
    public static String SHOW_RELATION = "show_relation";
    public static String OK_BEFORE_COMMIT = "relation_type_ok_before_commit";
    public static String COMMIT = "relation_type_commit";
    public static String RESET_FORMS = "reset";
    public static String SHOW_NEW_RELATION ="show_new_rel";
    public static String RELOAD_RELATIONS = "reload_relations";

    private final ObjectProperty<RelationType> relationTypeToEdit = new SimpleObjectProperty<>(this, "synset_relation_type");

    public RelationType getRelationTypeToEdit() {
        return relationTypeToEdit.get();
    }

    public ObjectProperty<RelationType> relationTypeToEditProperty() {
        return relationTypeToEdit;
    }

    public void setRelationTypeToEdit(RelationType relationTypeToEdit) {
        this.relationTypeToEdit.set(relationTypeToEdit);
    }
}
