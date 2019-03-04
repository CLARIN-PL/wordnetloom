package pl.edu.pwr.wordnetloom.client.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import pl.edu.pwr.wordnetloom.client.model.RelationTest;
import pl.edu.pwr.wordnetloom.client.model.RelationType;

public class RelationTestDialogScope implements Scope {

    public static final String RELOAD_TESTS = "reload_tests";
    public static String RESET_DIALOG_PAGE = "relation_test_reset_dialog_page";
    public static String OK_BEFORE_COMMIT = "relation_test_ok_before_commit";
    public static String COMMIT = "relation_test_commit";
    public static String RESET_FORMS = "reset";

    private final ObjectProperty<RelationTest> relationTestToEdit = new SimpleObjectProperty<>(this, "synset_relation_test");

    public RelationTest getRelationTestToEdit() {
        return relationTestToEdit.get();
    }

    public ObjectProperty<RelationTest> relationTestToEditProperty() {
        return relationTestToEdit;
    }

    public void setRelationTestToEdit(RelationTest relationTestToEdit) {
        this.relationTestToEdit.set(relationTestToEdit);
    }

}
