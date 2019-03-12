package pl.edu.pwr.wordnetloom.client.ui.senserelations;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.Validator;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import pl.edu.pwr.wordnetloom.client.events.LoadGraphEvent;
import pl.edu.pwr.wordnetloom.client.events.RefreshSenseRelationsEvent;
import pl.edu.pwr.wordnetloom.client.events.ShowSenseRelationsEvent;
import pl.edu.pwr.wordnetloom.client.model.*;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SenseRelationDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.tooltip.SenseTooltipCreator;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.util.Objects;

@Singleton
public class SenseRelationsViewModel implements ViewModel {

    public static final String OPEN_ADD_SENSE_RELATION_DIALOG = "open_sense_relation";

    @Inject
    RemoteService service;

    @InjectScope
    SenseRelationDialogScope senseRelationDialogScope;

    @Inject
    Event<LoadGraphEvent> loadGraphEventPublisher;

    private StringProperty root = new SimpleStringProperty(this, "root", "root");

    private ObservableList<TreeItem<TreeItemObject>> incoming;
    private ObservableList<TreeItem<TreeItemObject>> outgoing;
    private ObjectProperty<TreeItem<TreeItemObject>> selectedTreeListItem = new SimpleObjectProperty<>();
    private TreeItemObject selectedNode;

    public Property<String> rootProperty() {
        return root;
    }

    private Command addSenseRelationCommand;
    private Command removeSenseRelationCommand;
    private Command openInNewTabCommand;
    private Command showSenseVisualisationCommand;

    private ObjectProperty<Sense> activeSense = new SimpleObjectProperty<>();

    private final ReadOnlyBooleanWrapper valid = new ReadOnlyBooleanWrapper();
    private final CompositeValidator formValidator = new CompositeValidator();

    private Validator activeSenseValidator;

    public SenseRelationsViewModel() {
        activeSenseValidator = new FunctionBasedValidator<>(
                activeSenseProperty(),
                Objects::nonNull,
                ValidationMessage.error("Sense must be selected")
        );

        formValidator.addValidators(
                activeSenseValidator
        );
    }

    public void initialize() {
        valid.bind(formValidator.getValidationStatus().validProperty());

        addSenseRelationCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                addSenseRelation();
            }
        });

        showSenseVisualisationCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                showSenseVisualisation();
            }
        });

        removeSenseRelationCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                deleteSenseRelation();
            }
        });

        openInNewTabCommand = new DelegateCommand(()-> new Action() {
            @Override
            protected void action() throws Exception {
                if(loadGraphEventPublisher != null){
                    openInNewTab();
                }
            }
        });

        selectedTreeItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                this.selectedNode = newValue.getValue();
            }
        });
    }

    private void addSenseRelation() {
        senseRelationDialogScope.setParentSense(activeSense.get());
        if (activeSense != null) {
            publish(OPEN_ADD_SENSE_RELATION_DIALOG);
        }
    }

    private  void showSenseVisualisation(){
        if(activeSense.isNotNull().get()) {
            URI link = activeSense.get().getLinks().getSenseGraph();
            loadGraphEventPublisher.fireAsync(new LoadGraphEvent(link, true, true));
        }
    }


    public ObjectProperty<TreeItem<TreeItemObject>> selectedTreeItemProperty() {
        return selectedTreeListItem;
    }

    private void deleteSenseRelation() {
        if(selectedNode.getType().equals(TreeItemType.SENSE_RELATION)){
            Relation sr = service.findRelation(((SenseRelation) selectedNode.getItem()).getLinks().getSelf());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + sr.getRelationType().getLabel()
                    + " relation between "+sr.getSource().getLabel()+" and "+ sr.getTarget().getLabel()+"?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                service.delete(sr.getLinks().getSelf());
                loadRelations(activeSense.get());
            }
        }
    }

    private void loadRelations(Sense s) {

        Platform.runLater(this::clearRelations);
        SenseRelations relations = service.findSenseRelations(s.getLinks().getRelations());

        Platform.runLater(() -> {
            root.set(relations.getRoot());
            clearRelations();

            relations.getIncoming()
                    .forEach(rt -> {
                        TreeItem<TreeItemObject> name = new TreeItem<>(new TreeItemObject(rt.getRelationTypeName(), rt, TreeItemType.RELATION_TYPE));
                        name.setExpanded(true);
                        rt.getRows().forEach(i -> {
                            TreeItem<TreeItemObject> n =  new TreeItem<>(new TreeItemObject(i.getLabel(), i, TreeItemType.SENSE_RELATION));
                            name.getChildren().add(n);
                        });
                        incoming.add(name);
                    });

            relations.getOutgoing()
                    .forEach(rt -> {
                        TreeItem<TreeItemObject> name = new TreeItem<>(new TreeItemObject(rt.getRelationTypeName(), rt, TreeItemType.RELATION_TYPE));
                        name.setExpanded(true);
                        rt.getRows().forEach(i -> {
                            TreeItem<TreeItemObject> n =  new TreeItem<>(new TreeItemObject(i.getLabel(), i, TreeItemType.SENSE_RELATION));
                            name.getChildren().add(n);
                        });
                        outgoing.add(name);
                    });
        });
    }

    public void onRefreshSenseRelations(@Observes RefreshSenseRelationsEvent event) {
        loadRelations(activeSense.get());
    }

    public void onShowSenseRelations(@Observes ShowSenseRelationsEvent event) {
        if (event.getSense() != null) {
            this.activeSense.set(event.getSense());
        } else {
            this.activeSense.set(null);
        }
        if (event.getRelationsLink() == null) {
            Platform.runLater(this::clearRelations);
            return;
        }
        loadRelations(activeSense.get());
    }

    private void clearRelations() {
        incoming.clear();
        outgoing.clear();
    }

    private void openInNewTab(){
        if (selectedTreeListItem != null) {
            TreeItem<TreeItemObject> selectedItem = selectedTreeListItem.get();
            if (incoming.contains(selectedItem.getParent()) || outgoing.contains(selectedItem.getParent())) {

                Relation senseRelation = service.findSenseRelation(((SenseRelation) selectedItem.getValue().getItem()).getLinks().getSelf());
                Sense sense = service.findSense(senseRelation.getTarget().getId());
                Synset synset = service.findSynset(sense.getLinks().getSynset());
                loadGraphEventPublisher.fireAsync(new LoadGraphEvent(synset.getLinks().getSynsetGraph(), true,false));
            }
        }
    }

    public String getTooltipText(URI senseRelationURI) {
        Relation senseRelation = service.findSenseRelation(senseRelationURI);
        return SenseTooltipCreator.create(senseRelation.getTarget().getId(), service);
    }

    public ObjectProperty<Sense> activeSenseProperty() {
        return activeSense;
    }

    public void setIncoming(ObservableList<TreeItem<TreeItemObject>> incoming) {
        this.incoming = incoming;
    }

    public void setOutgoing(ObservableList<TreeItem<TreeItemObject>> outgoing) {
        this.outgoing = outgoing;
    }

    public Command addSenseRelationCommand() {
        return addSenseRelationCommand;
    }

    public Command removeSenseRelationCommand() {
        return removeSenseRelationCommand;
    }

    public Command openInNewTabCommand(){
        return openInNewTabCommand;
    }

    public ObservableBooleanValue addButtonDisabledProperty() {
        return valid.not();
    }

    public ObservableBooleanValue removeButtonDisabledProperty() {
        return valid.not();
    }

    public Command getShowSenseVisualisationCommand() {
        return showSenseVisualisationCommand;
    }
}
