package pl.edu.pwr.wordnetloom.client.ui.preview;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;

import javax.inject.Singleton;
import javax.swing.*;

@Singleton
public class PreviewView implements FxmlView<PreviewViewModel> {

	@InjectViewModel
	private PreviewViewModel viewModel;

	@FXML
	private SwingNode satellite;

	public void initialize() {
		SwingUtilities.invokeLater(() ->
		    satellite.setContent(viewModel.getSatelliteController().getSatelliteGraphViewer()));
	}
}
