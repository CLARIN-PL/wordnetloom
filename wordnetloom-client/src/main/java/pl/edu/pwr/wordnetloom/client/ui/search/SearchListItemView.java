package pl.edu.pwr.wordnetloom.client.ui.search;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import pl.edu.pwr.wordnetloom.client.ui.tooltip.ListTooltip;


public class SearchListItemView implements FxmlView<SearchListItemViewModel> {

    @FXML
    public Label mainLabel;

    @FXML
    public ImageView img;

    @InjectViewModel
    private SearchListItemViewModel viewModel;

    private Tooltip tooltip;

    public void initialize() {
        mainLabel.textProperty().bind(viewModel.labelProperty());
        initTooltip();
        mainLabel.setOnMouseExited(event -> tooltip.hide());
        img.imageProperty().bind(viewModel.imgProperty());

        if (!viewModel.getSearchListItem().hasSynset()) {
            mainLabel.setTextFill(Color.RED);
        }
    }

    private void initTooltip() {
        tooltip = new ListTooltip();
        tooltip.activatedProperty().addListener(observable -> {
            tooltip.setText(viewModel.getTooltipText());
        });
        Tooltip.install(mainLabel, tooltip);
    }
    @FXML
    public void openSenseGraphInNewTab() {
        viewModel.getOpenSenseGraphInNewTabCommand().execute();
    }

    @FXML
    public void openSynsetGraphInNewTab() {
        viewModel.getOpenSynsetGraphInNewTabCommand().execute();
    }
}
