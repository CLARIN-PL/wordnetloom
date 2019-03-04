package pl.edu.pwr.wordnetloom.client.ui.synsetproperties;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import pl.edu.pwr.wordnetloom.client.ui.tooltip.ListTooltip;

import java.util.Timer;
import java.util.TimerTask;

public class SenseListItemView implements FxmlView<SenseListItemViewModel> {

	@FXML
	public Label mainLabel;

	@InjectViewModel
	private SenseListItemViewModel viewModel;

	private Tooltip tooltip;
	private Boolean mouseEntered = false;

	public void initialize() {
		mainLabel.textProperty().bind(viewModel.labelProperty());

		initTooltip();
	}

	private void initTooltip(){
		tooltip = new ListTooltip();
		tooltip.activatedProperty().addListener(observable-> tooltip.setText(viewModel.getTooltipText()));
		Tooltip.install(mainLabel, tooltip);
	}
}
