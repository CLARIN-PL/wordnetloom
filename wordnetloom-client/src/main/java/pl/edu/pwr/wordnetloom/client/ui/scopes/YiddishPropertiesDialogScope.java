package pl.edu.pwr.wordnetloom.client.ui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import pl.edu.pwr.wordnetloom.client.model.Sense;

import java.util.UUID;

public class YiddishPropertiesDialogScope implements Scope {

    public static final String UPDATE_TAB_NAME = "update_tab_name";
    public static final String REMOVE_YIDDISH_PROPERTY = "remove_yiddish_property";
    public static String RESET_DIALOG_PAGE = "yiddish_reset_dialog_page";
    public static String OK_BEFORE_COMMIT = "yiddish_ok_before_commit";
    public static String COMMIT = "yiddish_commit";
    public static String ADD_YIDDISH_PROPERTY = "add_yiddish_property";

    private final ObjectProperty<UUID> senseId = new SimpleObjectProperty<>(this, "senseId");

    public UUID getSenseId() {
        return senseId.get();
    }

    public void setSenseId(UUID senseId) {
        this.senseId.set(senseId);
    }
}
