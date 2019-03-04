package pl.edu.pwr.wordnetloom.client.ui.alerts;


import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import pl.edu.pwr.wordnetloom.client.ui.scopes.AlertScope;

public class AlertViewModel implements ViewModel {

    private final StringProperty message = new SimpleStringProperty();
    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    private final StringProperty style = new SimpleStringProperty();
    private final StringProperty textStyle = new SimpleStringProperty();

    @InjectScope
    private AlertScope alertScope;

    public void initialize() {
        alertScope.subscribe(AlertScope.SHOW_ERROR_MESSAGE, (key, payload) -> showErrorMessage());
        alertScope.subscribe(AlertScope.SHOW_SUCCESS_MESSAGE, (key, payload) -> showSuccessMessage());
        alertScope.subscribe(AlertScope.SHOW_INFO_MESSAGE, (key, payload) -> showInfoMessage());
        alertScope.subscribe(AlertScope.RESET_MESSAGE, (key, payload) -> hideMessage());
    }

    private void showInfoMessage() {
        this.style.set("-fx-border-color: #bee5eb; -fx-background-color: #d1ecf1; -fx-border-width: 1 0 1 0;");
        this.textStyle.set("-fx-fill: #0c5460;");
        showMessage();
    }

    private void showSuccessMessage() {
        this.style.set("-fx-border-color: #c3e6cb; -fx-background-color: #d4edda; -fx-border-width: 1 0 1 0;");
        this.textStyle.set("-fx-fill: #155724;");
        showMessage();
    }

    private void showErrorMessage() {
        this.style.set("-fx-border-color: #f5c6cb; -fx-background-color: #f8d7da; -fx-border-width: 1 0 1 0;");
        this.textStyle.set("-fx-fill: #721c24;");
        showMessage();
    }

    private void hideMessage() {
        this.visible.set(false);
        this.message.set("");
    }

    public void showMessage() {
        this.visible.set(true);
        this.message.set(alertScope.getMessage());
    }

    public StringProperty messageProperty() {
        return message;
    }

    public Property<Boolean> visibleProperty() {
        return visible;
    }

    public Property<String> textStyleProperty() {
        return textStyle;
    }

    public StringProperty styleProperty() {
        return style;
    }
}
