package pl.edu.pwr.wordnetloom.client.ui.graph;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class GraphTabView implements FxmlView<GraphTabViewModel> {

    private static final Logger LOG = LoggerFactory.getLogger(GraphTabView.class);

    @InjectViewModel
    private GraphTabViewModel viewModel;

    @FXML
    private SwingNode graph;

    @FXML
    private ScrollPane graphTab;

    @FXML
    public StackPane progressOverlay;

    @FXML
    public ContextMenu contextMenu;

    public void initialize() {
        viewModel.setMenuItems(contextMenu.getItems());
        SwingUtilities.invokeLater(() -> graph.setContent(viewModel.getGraphController().getSampleGraphViewer()));
        progressOverlay.visibleProperty().bindBidirectional(viewModel.progressOverlayProperty());
    }

}
