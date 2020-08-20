package pl.edu.pwr.wordnetloom.client;

import java.util.Locale;
import java.util.ResourceBundle;

import de.saxsys.mvvmfx.*;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.stage.Stage;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.mvvmfx.cdi.MvvmfxCdiApplication;
import pl.edu.pwr.wordnetloom.client.events.TriggerShutdownEvent;
import pl.edu.pwr.wordnetloom.client.ui.DialogHelper;
import pl.edu.pwr.wordnetloom.client.ui.logindialog.LoginDialogView;
import pl.edu.pwr.wordnetloom.client.ui.logindialog.LoginDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.scopes.LoginDialogScope;

public class Application extends MvvmfxCdiApplication {

	private static final Logger LOG = LoggerFactory.getLogger(Application.class);

	public static void main(String... args) {
		Locale.setDefault(Locale.ENGLISH);
		launch(args);
	}

	@Inject
	private LoginDialogScope scopeInstance;

	@Inject
	private ResourceBundle resourceBundle;

	@Override
	public void startMvvmfx(Stage stage) throws Exception {
		LOG.info("Starting the Application");

		MvvmFX.setGlobalResourceBundle(resourceBundle);

		ViewTuple<LoginDialogView, LoginDialogViewModel> load = FluentViewLoader
				.fxmlView(LoginDialogView.class)
				.providedScopes(scopeInstance)
				.load();

		Parent view = load.getView();
		Stage showDialog = DialogHelper.showDialog(view, stage, "/wordnetloom.css");
		load.getCodeBehind().setDisplayingStage(showDialog);
		showDialog.toFront();
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
	}

	/**
	 * The shutdown of the application can be triggered by firing the
	 * {@link TriggerShutdownEvent} CDI events.
	 */
	public void triggerShutdown(@Observes TriggerShutdownEvent event) {
		LOG.info("Application will now shut down");
		Platform.exit();
	}

}