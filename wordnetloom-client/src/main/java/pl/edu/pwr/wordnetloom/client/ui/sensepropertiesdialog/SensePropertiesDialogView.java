package pl.edu.pwr.wordnetloom.client.ui.sensepropertiesdialog;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.*;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import pl.edu.pwr.wordnetloom.client.model.YiddishProperty;
import pl.edu.pwr.wordnetloom.client.ui.DialogHelper;
import pl.edu.pwr.wordnetloom.client.ui.exampledialog.ExampleDialogView;
import pl.edu.pwr.wordnetloom.client.ui.exampledialog.ExampleDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.yiddishpropertiesform.YiddishPropertiesFormView;
import pl.edu.pwr.wordnetloom.client.ui.yiddishpropertiesform.YiddishPropertiesFormViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

public class SensePropertiesDialogView implements FxmlView<SensePropertiesDialogViewModel> {

    @FXML
    public Button saveButton, closeButton;

    @FXML
	public Tab sensePropertiesTab;

    @FXML
	public TabPane tabs;

	@InjectViewModel
	private SensePropertiesDialogViewModel viewModel;

	public Stage showDialog;

	@InjectContext
	Context context;

	public void initialize() {
	    initIcons();
		viewModel.subscribe(SensePropertiesDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
			showDialog.close();
		});
		saveButton.disableProperty().bind(viewModel.saveButtonDisabledProperty());
	}

	public void setDisplayingStage(Stage showDialog) {
		this.showDialog = showDialog;
		this.showDialog.titleProperty().bind(viewModel.titleProperty());
	}

	public void loadYiddish(){
	    if(viewModel.getYp() != null) {
            viewModel.getYp().getRows().forEach(y ->{

                ViewTuple<YiddishPropertiesFormView, YiddishPropertiesFormViewModel> load = FluentViewLoader
                        .fxmlView(YiddishPropertiesFormView.class)
                        .context(context)
                        .load();

                load.getViewModel().setYiddishProperty(y);
                Parent view = load.getView();

                Tab t = new Tab();
                t.setText(y.getVariantType().name());
                t.setContent(view);
                tabs.getTabs().add(t);
            });
        }
    }
	private void initIcons(){
        AwesomeDude.setIcon(saveButton, AwesomeIcon.SAVE, "11");
        AwesomeDude.setIcon(closeButton, AwesomeIcon.TIMES, "11");
	}

    public void save() {
	    viewModel.getSaveCommand().execute();
    }

	public void close() {
		viewModel.getCloseCommand().execute();
		showDialog.close();
	}
}
