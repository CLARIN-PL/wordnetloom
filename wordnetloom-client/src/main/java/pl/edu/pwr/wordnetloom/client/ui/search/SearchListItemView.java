package pl.edu.pwr.wordnetloom.client.ui.search;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import pl.edu.pwr.wordnetloom.client.ui.tooltip.ListTooltip;

import java.util.Timer;
import java.util.TimerTask;


public class SearchListItemView implements FxmlView<SearchListItemViewModel> {

	private final int TOOLTIP_DELAY = 1000;
	private final int TOOLTIP_PERIOD = 5000;

	@FXML
	public Label mainLabel;

	@FXML
	public ImageView img;

	@FXML
	private void openInNewTab() {
		viewModel.getOpenInNewTabCommand().execute();
	}

	@InjectViewModel
	private SearchListItemViewModel viewModel;

	private Tooltip tooltip;

	private Boolean mouseEntered = false;
	private boolean tooltipLoaded = false;


	public void initialize() {
		mainLabel.textProperty().bind(viewModel.labelProperty());
		initTooltip();
		mainLabel.setOnMouseExited(event->tooltip.hide());
		img.imageProperty().bind(viewModel.imgProperty());

		if(!viewModel.getSearchListItem().hasSynset()) {
			mainLabel.setTextFill(Color.RED);
		}
	}

	private void initTooltip() {
		tooltip = new ListTooltip();

		mainLabel.addEventFilter(MouseEvent.MOUSE_ENTERED, event->{
			mouseEntered = true;
			if (!tooltipLoaded) {
				new Timer().schedule(
						new TimerTask() {
							@Override
							public void run() {
								Platform.runLater(()->{
									if(tooltip.getText().isEmpty() && mouseEntered){
										tooltip.setText(viewModel.getTooltipText());
										tooltipLoaded = true;
									}
								});
							}
						}, TOOLTIP_DELAY, TOOLTIP_PERIOD
				);
			}
		});
		mainLabel.addEventFilter(MouseEvent.MOUSE_EXITED, event -> mouseEntered = false);

		Tooltip.install(mainLabel, tooltip);
	}
}
