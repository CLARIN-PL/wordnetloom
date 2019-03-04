package pl.edu.pwr.wordnetloom.client.ui.senserelations;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.*;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import pl.edu.pwr.wordnetloom.client.model.SenseRelation;
import pl.edu.pwr.wordnetloom.client.ui.DialogHelper;
import pl.edu.pwr.wordnetloom.client.ui.senserelationformdialog.SenseRelationDialogView;
import pl.edu.pwr.wordnetloom.client.ui.senserelationformdialog.SenseRelationDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.tooltip.ListTooltip;

import javax.inject.Inject;

public class SenseRelationsView implements FxmlView<SenseRelationsViewModel> {

    @InjectViewModel
    private SenseRelationsViewModel viewModel;

    @InjectContext
    Context context;

    @Inject
    Stage primaryStage;

    @FXML
    private Button addButton, removeButton;

    @FXML
    private TreeView<TreeItemObject> relationTree;

    private TreeItem<TreeItemObject> root = new TreeItem<>(new TreeItemObject("root", null, TreeItemType.ROOT));
    private TreeItem<TreeItemObject> incoming = new TreeItem<>(new TreeItemObject("incoming", null, TreeItemType.ROOT));
    private TreeItem<TreeItemObject> outgoing = new TreeItem<>(new TreeItemObject("outgoing", null, TreeItemType.ROOT));

    public void initialize() {
        initIcons();
        relationTree.setRoot(root);
        relationTree.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (event.getClickCount() == 2) {
                    viewModel.openInNewTabCommand().execute();
                }
            }
        });

        root.getChildren().add(incoming);
        root.getChildren().add(outgoing);
        root.setExpanded(true);
        incoming.setExpanded(true);
        outgoing.setExpanded(true);

        viewModel.selectedTreeItemProperty().bind(relationTree.getSelectionModel().selectedItemProperty());
        root.valueProperty().get().labelProperty().bind(viewModel.rootProperty());
        viewModel.setIncoming(incoming.getChildren());
        viewModel.setOutgoing(outgoing.getChildren());

        addButton.disableProperty().bind(viewModel.addButtonDisabledProperty());
        removeButton.disableProperty().bind(viewModel.removeButtonDisabledProperty());

        viewModel.subscribe(SenseRelationsViewModel.OPEN_ADD_SENSE_RELATION_DIALOG, (key, payload) -> {
            ViewTuple<SenseRelationDialogView, SenseRelationDialogViewModel> load = FluentViewLoader
                    .fxmlView(SenseRelationDialogView.class)
                    .context(context)
                    .load();

            Parent view = load.getView();
            Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/wordnetloom.css");
            load.getCodeBehind().setDisplayingStage(showDialog);
            showDialog.toFront();
        });

        initTooltip();
    }

    private void initIcons() {
        AwesomeDude.setIcon(addButton, AwesomeIcon.PLUS, "11");
        AwesomeDude.setIcon(removeButton, AwesomeIcon.TRASH, "11");
    }

    @FXML
    public void addSenseRelation() {
        viewModel.addSenseRelationCommand().execute();
    }

    @FXML
    public void removeSenseRelation() {
        viewModel.removeSenseRelationCommand().execute();
    }

    private void initTooltip() {
        relationTree.setCellFactory(tv -> {
            final Tooltip tooltip = new ListTooltip();
            return new TreeCell<TreeItemObject>() {
                @Override
                public void updateItem(TreeItemObject item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        setText(getTreeItem().getValue().getLabel());
                        if (item.getType().equals(TreeItemType.SENSE_RELATION)) {
                            tooltip.activatedProperty().addListener(observable -> {
                                SenseRelation senseRelation = (SenseRelation) getTreeItem().getValue().getItem();
                                tooltip.setText(viewModel.getTooltipText(senseRelation.getLinks().getSelf()));
                            });
                            setTooltip(tooltip);
                        }
                    } else {
                        setText(null);
                        setTooltip(null);
                    }
                }
            };
        });
    }
}
