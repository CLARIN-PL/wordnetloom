package pl.edu.pwr.wordnetloom.client.ui.main;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import pl.edu.pwr.wordnetloom.client.events.OpenMainApplicationEvent;
import pl.edu.pwr.wordnetloom.client.ui.scopes.*;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ResourceBundle;

@Singleton
@ScopeProvider(scopes = {
        SensePropertiesDialogScope.class,
        SenseRelationDialogScope.class,
        SynonymyRelationDialogScope.class,
        SynsetPropertiesDialogScope.class
})
public class MainViewModel implements ViewModel {

    public static final String CLOSE_DIALOG_NOTIFICATION = "close_dialog";

    @Inject
    private ResourceBundle resourceBundle;

    @Inject
    private Stage primaryStage;

    public void openMainApplication(@Observes OpenMainApplicationEvent event) {

        primaryStage.setTitle(resourceBundle.getString("window.title"));

        ViewTuple<MainView, MainViewModel> main = FluentViewLoader
                .fxmlView(MainView.class)
                .load();

        Scene rootScene = new Scene(main.getView());

        rootScene.getStylesheets().add("/wordnetloom.css");

        primaryStage.setScene(rootScene);
        primaryStage.show();

        primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent ketEve) -> {
            if (KeyCode.ESCAPE == ketEve.getCode() && primaryStage.isFocused()) {
                if(primaryStage.isFullScreen() || primaryStage.isMaximized()){
                    primaryStage.setFullScreen(false);
                    primaryStage.setMaximized(false);
                }else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to quit?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait();
                    if (alert.getResult() == ButtonType.YES) {
                        primaryStage.close();
                    }
                }
            }
        });

    }
}
