package pl.edu.pwr.wordnetloom.client.ui.profiledialog;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckListView;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;

public class ProfileFormView implements FxmlView<ProfileFormViewModel> {

    @FXML
    public Button updateButton;

    @FXML
    public TextField firstname, lastname, email;

    @FXML
    public CheckBox markersCheckBox, tooltipsCheckBox;

    @FXML
    public CheckListView<Dictionary> lexicons;

    @InjectViewModel
    private ProfileFormViewModel viewModel;

    private ValidationVisualizer validationVisualizer = new ControlsFxVisualizer();

    public void initialize() {
        firstname.textProperty().bindBidirectional(viewModel.firstNameProperty());
        lastname.textProperty().bindBidirectional(viewModel.lastNameProperty());
        email.textProperty().bindBidirectional(viewModel.emailProperty());
        email.setEditable(false);
        email.setDisable(true);
        lexicons.setItems(viewModel.getLexicons());
        viewModel.setCheckModel(lexicons.getCheckModel());
        markersCheckBox.selectedProperty().bindBidirectional(viewModel.markersProperty());
        tooltipsCheckBox.selectedProperty().bindBidirectional(viewModel.tooltipsProperty());

        validationVisualizer.initVisualization(viewModel.getFirstnameValidator(), firstname, true);
        validationVisualizer.initVisualization(viewModel.getLastnameValidator(), lastname, true);
        validationVisualizer.initVisualization(viewModel.getEmailValidator(), email, true);
    }


}
