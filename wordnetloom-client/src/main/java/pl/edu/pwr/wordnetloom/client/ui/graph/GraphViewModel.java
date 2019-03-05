package pl.edu.pwr.wordnetloom.client.ui.graph;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import pl.edu.pwr.wordnetloom.client.events.LoadGraphEvent;
import pl.edu.pwr.wordnetloom.client.events.PathToHyperonymEvent;
import pl.edu.pwr.wordnetloom.client.events.ShowSynsetRelationsEvent;
import pl.edu.pwr.wordnetloom.client.events.UpdateCursorEvent;
import pl.edu.pwr.wordnetloom.client.model.*;
import pl.edu.pwr.wordnetloom.client.service.RelationTypeService;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.service.SynsetDataStore;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.SynsetNode;
import pl.edu.pwr.wordnetloom.client.ui.preview.PreviewViewModel;

import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.event.Reception;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static pl.edu.pwr.wordnetloom.client.ui.graph.GraphView.SELECT_LAST_TAB;

@Singleton
public class GraphViewModel implements ViewModel {

    private List<Tab> tabsList = new ArrayList<>();
    private ObservableList<Tab> graphTabsList = FXCollections.observableArrayList(tabsList);
    private ObjectProperty<Tab> selectedGraphTab = new SimpleObjectProperty<>();
    private Map<String, GraphTabViewModel> tabs = new ConcurrentHashMap<>();

    @Inject
    SynsetDataStore store;

    @Inject
    PreviewViewModel previewViewModel;

    @Inject
    RemoteService remoteService;

    @Inject
    ResourceBundle resourceBundle;

    public void initialize() {
        selectedGraphTabProperty().addListener((observable, oldValue, newValue) -> {
            if (selectedGraphTab.get() != null) {
                String id = selectedGraphTab.get().getId();
                GraphTabViewModel activeTab = tabs.get(id);
                previewViewModel.getSatelliteController().set(activeTab.graphController);
                previewViewModel.getSatelliteController().refresh();
            }
        });
    }

    public ObservableList<Tab> graphTabsList() {
        return graphTabsList;
    }

    public ObservableList<Tab> merge(ObservableList<Tab> into, ObservableList<Tab>... lists) {
        final ObservableList<Tab> list = into;
        for (ObservableList<Tab> l : lists) {
            list.addAll(l);
            l.addListener((javafx.collections.ListChangeListener.Change<? extends Tab> c) -> {
                while (c.next()) {
                    if (c.wasAdded()) {
                        list.addAll(c.getAddedSubList());
                    }
                    if (c.wasRemoved()) {
                        list.removeAll(c.getRemoved());
                    }
                }
            });
        }
        return list;
    }

    public void removeRelations(List<Relation> relations){
        for(Relation relation : relations) {
            RelationType relationType = remoteService.getRelationType(relation.getRelationType().getLinks().getSelf());
            remoteService.delete(relation.getLinks().getSelf());
            removeRelationFromGraph(relation.getSource().getId(), relation.getTarget().getId(), relation.getRelationType().getId());
            if(relationType.getReverseRelation() != null && checkRemoveReverseRelation(relation, relationType)){
                UUID reverseRelationId = relationType.getReverseRelation().getId();
                remoteService.delete(relation.getLinks().getReverseRelation());
                removeRelationFromGraph(relation.getTarget().getId(), relation.getSource().getId(), reverseRelationId);
            }
        }

        // TODO: temporary solution
        showRelationsOnAllGraph(null, null, null);
    }

    private void removeRelationFromGraph(UUID source, UUID target, UUID relationType){
        tabs.forEach((k, v)-> v.getGraphController().removeRelation(source, target, relationType));
    }

    private void refreshAllGraphs(){
        tabs.forEach((k,v)->v.getGraphController().recreateLayoutWithFix(null, null));
    }

    private boolean checkRemoveReverseRelation(Relation relation, RelationType relationType) {
        return relationType.getAutoReverse() || showQuestionDialog(relation);
    }

    private boolean showQuestionDialog(Relation  relation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resourceBundle.getString("remove.synset.relation.dialog"));
        alert.setHeaderText(resourceBundle.getString("remove.synset.relation.dialog.confirm"));
        alert.setContentText(relation.getSource().getLabel() + "\n" + relation.getTarget().getLabel() + "\n" + relation.getRelationType().getLabel());

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public ObjectProperty<Tab> selectedGraphTabProperty() {
        return selectedGraphTab;
    }

    private String addTab() {
        final String id = UUID.randomUUID().toString();

        Tab t = new Tab();
        t.setText("");
        t.setId(id);
        t.setOnClosed(event -> tabs.remove(id));

        ViewTuple<GraphTabView, GraphTabViewModel> load = FluentViewLoader
                .fxmlView(GraphTabView.class)
                .load();

        t.setContent(load.getView());

        tabs.put(id, load.getViewModel());
        graphTabsList.add(t);
        publish(SELECT_LAST_TAB);
        return id;
    }

    public void updateCursorState(@Observes(notifyObserver = Reception.ALWAYS) UpdateCursorEvent event) {
        tabs.forEach((k, v) -> v.getGraphController().setCursor(event.getCursor()));
    }

    // TODO: refactor
    public void onShowSynsetRelation(@Observes(notifyObserver = Reception.ALWAYS) ShowSynsetRelationsEvent event) {
        Optional<RelationType> rt = RelationTypeService.getSynsetRelationTypeById(event.getRelation().getRelationType().getId());
        if (rt.isPresent()) {
            store.load(event.getRelation().getSource().getId());
            store.load(event.getRelation().getTarget().getId());
            showRelationsOnAllGraph(event.getRelation().getSource().getId(), event.getRelation().getSource().getId(), event.getRelation().getRelationType().getId());
        }
    }

    private void showRelationsOnAllGraph(UUID sourceId, UUID targetId, UUID relationId) {
        tabs.forEach((k,v)->{
            // TODO: temporary solution, refreshing all graph
             v.getGraphController()
                    .refreshView(((SynsetNode)v.getGraphController().getRootNode()).getSynsetId());
        });
    }

    public void showPathToHyperonym(@ObservesAsync PathToHyperonymEvent event){
        Platform.runLater(()->{
            String tabId = selectedGraphTab.get().getId();
            GraphTabViewModel activeTab = tabs.get(tabId);
            activeTab.progressOverlayProperty().setValue(true);

            Task pathToHyperonymTask = new Task<UUID>() {
                @Override
                protected UUID call() throws Exception {
                    RelationType hyperonymRelationType = getHyperonymRelationType();
                    List<NodeExpanded> path = remoteService.findPathToHyperonym(event.getSynsetId());
                    List<DataEntry> entries = getAndLoadDataEntries(path);
                    updateView(hyperonymRelationType, entries);
                    activeTab.progressOverlayProperty().setValue(false);
                    return event.getSynsetId();
                }
            };

            Thread loadingThread = new Thread(pathToHyperonymTask, "path-to_hyperonym");
            loadingThread.setDaemon(true);
            loadingThread.start();

        });
    }

    private void updateView(RelationType hyperonymRelationType, List<DataEntry> entries) {
        String tabId = selectedGraphTab.get().getId();
        GraphTabViewModel activeTab = tabs.get(tabId);
        for(DataEntry entry : entries){
            activeTab.getGraphController().showRelation(entry.getId(), hyperonymRelationType);
        }
    }

    private List<DataEntry> getAndLoadDataEntries(List<NodeExpanded> path) {
        List<DataEntry> entries = new ArrayList<>();
        for(NodeExpanded nodeExpanded : path) {
            DataEntry dataEntry = store.load(nodeExpanded);
            store.insertData(dataEntry);
            entries.add(dataEntry);
        }
        return entries;
    }

    private RelationType getHyperonymRelationType() {
        final String HYPERONYM_RELATION_NAME = "hype"; // TODO: zmienić nazwę relacji. Relacja ustawiona dla Afryki
        return RelationTypeService.getSynsetRelationTypeByShortName(HYPERONYM_RELATION_NAME).get();
    }

    public void showGraph(@ObservesAsync LoadGraphEvent event) {
        Platform.runLater(() -> {
            if (event.getGraphLink() != null || event.getSynsetId() != null) {
                String tabId;
                if (tabs.size() == 0 || event.isOpenInNewTab()) {
                    tabId = addTab();
                } else {
                    tabId = selectedGraphTab.get().getId();
                }
                GraphTabViewModel activeTab = tabs.get(tabId);
                activeTab.progressOverlayProperty().setValue(true);

                Task graphLoader = new Task<DataEntry>() {

                    {
                        setOnSucceeded(workerStateEvent -> {
                            if (event.getGraphLink() != null || event.getSynsetId() != null) {
                                graphTabsList.stream().filter(t -> t.getId().equals(tabId))
                                        .findFirst().ifPresent(t -> t.setText(getValue().getLabel()));
                                activeTab.loadGraph(getValue().getId());
                            }
                            activeTab.progressOverlayProperty().setValue(false);
                        });
                        setOnFailed(workerStateEvent -> getException().printStackTrace());
                    }

                    @Override
                    protected DataEntry call() {
                        DataEntry root = null;
                        if (event.getGraphLink() != null) {
                            root = store.load(event.getGraphLink());
                        } else if (event.getSynsetId() != null) {
                            root = store.load(event.getSynsetId());
                        }
                        return root;
                    }
                };
                Thread loadingThread = new Thread(graphLoader, "graph-loader");
                loadingThread.setDaemon(true);
                loadingThread.start();
            }
        });
    }

}
