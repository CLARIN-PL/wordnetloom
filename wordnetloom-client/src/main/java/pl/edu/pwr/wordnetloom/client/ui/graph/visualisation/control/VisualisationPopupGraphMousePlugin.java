package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.control;

import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.picking.PickedState;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuItem;
import pl.edu.pwr.wordnetloom.client.events.LoadGraphEvent;
import pl.edu.pwr.wordnetloom.client.events.PathToHyperonymEvent;
import pl.edu.pwr.wordnetloom.client.events.RemoveRelationEvent;
import pl.edu.pwr.wordnetloom.client.events.UpdateCursorEvent;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.GraphController;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.*;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SynsetRelationScope;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class VisualisationPopupGraphMousePlugin extends AbstractPopupGraphMousePlugin {

    private GraphController graphController;

    @Inject
    Event<LoadGraphEvent> loadGraphEvent;

    @InjectContext
    Context context;

    @Inject
    ResourceBundle resourceBundle;

    @Inject
    SynsetRelationScope synsetRelationDialogScope;

    @Inject
    Event<UpdateCursorEvent> updateCursorEvent;

    @Inject
    Event<RemoveRelationEvent> removeRelationEvent;

    @Inject
    Event<PathToHyperonymEvent> pathToHyperonymEvent;

    private List<MenuItem> currentMenuItems;

    public void setGraphController(GraphController gc) {
        this.graphController = gc;
    }

    public static final Cursor MAKE_RELATION_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    public static final Cursor MERGE_SYNSETS_CURSOR = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    public static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

    @Override
    protected void handlePopup(MouseEvent e) {
        Platform.runLater(
                () -> {
                    graphController.getGraphTabViewModel().getMenuItems().clear();
                }
        );

        currentMenuItems = new ArrayList<>();

        VisualizationViewer<Node, Edge> vv
                = (VisualizationViewer<Node, Edge>) e.getSource();
        Layout<Node, Edge> layout = vv.getGraphLayout();
        Graph<Node, Edge> graph = layout.getGraph();
        Point2D p = e.getPoint();
        Point2D ivp = p;
        GraphElementAccessor<Node, Edge> pickSupport = vv.getPickSupport();

        if (synsetRelationDialogScope.isMakeSynsetRelationMode()) {
            updateCursorState();
            return;
        }

        // exit make relation mode
/*        ViWordNetService s
                = ((ViWordNetService) graphController.getWorkbench().getService(
                "pl.edu.pwr.wordnetloom.client.plugins.viwordnet.ViWordNetService"));

        if (s.isMergeSynsetsModeOn()) {
            s.switchMergeSynsetsMode();
            return;
        }*/

        if (pickSupport != null) {
            Node vertex = pickSupport.getVertex(layout, ivp.getX(), ivp.getY());
            Edge edge = pickSupport.getEdge(layout, ivp.getX(), ivp.getY());
            PickedState<Node> pickedVertexState = vv.getPickedVertexState();
            PickedState<Edge> pickedEdgeState = vv.getPickedEdgeState();

            if (vertex instanceof WordNode) {
                // addCreateRelationWithOption(menu);

            } else if (vertex instanceof SynsetNode) {
                if(((SynsetNode) vertex).isSynsetMode()) {
                    createSynsetNodeMenu(vertex, pickedVertexState);
                }
            } else if (vertex instanceof NodeSet) {
                createNodeSetMenu(vertex);
            } else if (edge != null) {
                if(((SynsetNode) vertex).isSynsetMode()) {
                    createEdgeMenu(vv, graph, edge);
                }
            }
        }
        Platform.runLater(
                () -> {
                    graphController.getGraphTabViewModel().getMenuItems().addAll(currentMenuItems);
                }
        );
    }

    private void updateCursorState() {
        if (synsetRelationDialogScope.isMakeSynsetRelationMode()) {
            updateCursorEvent.fire(new UpdateCursorEvent(VisualisationPopupGraphMousePlugin.MAKE_RELATION_CURSOR));
            graphController.setCursor(VisualisationPopupGraphMousePlugin.MAKE_RELATION_CURSOR);
        } else {
            updateCursorEvent.fire(new UpdateCursorEvent(VisualisationPopupGraphMousePlugin.DEFAULT_CURSOR));
        }
    }

    private void createEdgeMenu(VisualizationViewer<Node, Edge> vv, Graph<Node, Edge> graph, Edge edge) {
        addFollowEdgeOption(vv, graph, edge);
        if (edge instanceof SynsetEdge) {
            addRemoveRelationOption(edge);
        }
    }

    private void addRemoveRelationOption(Edge edge) {
        MenuItem removeRelation = new MenuItem(resourceBundle.getString("context.menu.remove.relation"));
        removeRelation.setOnAction(event -> {
            Pair<Node> c = graphController.getGraph().getEndpoints(edge);
            HashSet<Edge> rel = new HashSet<>(graphController.getGraph().findEdgeSet(c.getFirst(), c.getSecond()));
            rel.addAll(graphController.getGraph().findEdgeSet(c.getSecond(), c.getFirst()));
            rel.stream().findFirst()
                    .filter( r -> r instanceof SynsetEdge)
                    .map(r -> (SynsetEdge)r)
                    .ifPresent(r -> removeRelationEvent.fire(new RemoveRelationEvent(r.getSource(), r.getTarget())));
        });
        currentMenuItems.add(removeRelation);
    }

    private void addFollowEdgeOption(VisualizationViewer<Node, Edge> vv, Graph<Node, Edge> graph, Edge edge) {
        MenuItem followEdgeItem = new MenuItem(resourceBundle.getString("context.menu.follow.edge"));
        followEdgeItem.setOnAction(event -> {
            Node vn = graph.getDest(edge);
            VisualisationPopupGraphMousePlugin.this.graphController.setSelectedNode(vn);
            VisualisationPopupGraphMousePlugin.this.graphController.center();
            vv.repaint();
        });
        currentMenuItems.add(followEdgeItem);
    }

    private void createNodeSetMenu(Node vertex) {
        NodeSet set = (NodeSet) vertex;
        List<SynsetNode> col = new ArrayList<>(set.getSynsets());
        col.sort(new NodeAlphabeticComparator());

        CustomMenuItem cmi = new CustomMenuItem();
        ViewTuple<SynsetNodeListView, SynsetNodeListViewModel> load = FluentViewLoader
                .fxmlView(SynsetNodeListView.class)
                .context(context)
                .load();

        Parent view = load.getView();
        cmi.setContent(view);
        load.getViewModel().synsetNodesProperty().clear();
        col.forEach(sn -> load.getViewModel().synsetNodesProperty().add(new SynsetNodeListItemViewModel(sn)));

        load.getViewModel().addSynsetCommand(new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {

                graphController.deselectAll();
                SynsetNode other = load.getViewModel().selectedSynsetNodeItemProperty().get().getSynsetNode();
                graphController.addSynsetFromSetToGraph(other);

                Node p2 = vertex;
                Graph<Node, Edge> g = graphController.getGraph();
                Node parent = vertex.getNode();
                boolean dissapear = true;
                for (Edge edge : g.getIncidentEdges(parent)) {
                    Node opposite = g.getOpposite(parent, edge);
                    if (parent.equals(opposite.getNode())
                            && (opposite.getNodeDirection() != null)) {
                        if (opposite == vertex) {
                            dissapear = false;
                        }
                    }
                }
                if (dissapear) {
                    if (other != null) {
                        p2 = other;
                    } else {
                        p2 = vertex.getNode();
                    }
                }
                graphController.checkMissing();
                graphController.recreateLayoutWithFix(vertex, p2);
            }

        }));

        currentMenuItems.add(cmi);
    }

    private void createSynsetNodeMenu(Node vertex, PickedState<Node> pickedVertexState) {
        SynsetNode synsetNode = (SynsetNode) vertex;
        addPathToHiperonimOption(synsetNode);
        createOpenInNewTabOption(synsetNode);
        addCreateRelationWithOption(synsetNode);
        if (vertex != graphController.getRootNode()) {
            addGroupSynsetsOption(vertex, pickedVertexState);
        }
    }

    private void createOpenInNewTabOption(SynsetNode vertex) {
        MenuItem openInNewTab = new MenuItem(resourceBundle.getString("context.menu.open.in.new.tab"));
        openInNewTab.setOnAction(event -> {
            loadGraphEvent.fireAsync(new LoadGraphEvent(vertex.getSynsetId(), true));
        });
        currentMenuItems.add(openInNewTab);
    }

    private void addPathToHiperonimOption(SynsetNode vertex) {
        MenuItem pathToHiperonymItem = new MenuItem(resourceBundle.getString("context.menu.path.to.hyperonym"));
        pathToHiperonymItem.setOnAction(event -> {
            pathToHyperonymEvent.fireAsync(new PathToHyperonymEvent(vertex.getSynsetId()));
        });
        currentMenuItems.add(pathToHiperonymItem);
    }

    private void addCreateRelationWithOption(SynsetNode vertex) {
        MenuItem createRelationItem = new MenuItem(resourceBundle.getString("context.menu.create.synset.relation"));
        createRelationItem.setOnAction(event -> {
            synsetRelationDialogScope.setFirstSelectedSynset(vertex.getSynsetId());
            synsetRelationDialogScope.setMakeSynsetRelationMode(true);
            updateCursorState();
        });
        currentMenuItems.add(createRelationItem);
    }

    private void addGroupSynsetsOption(Node vertex, PickedState<Node> pickedVertexState) {
        MenuItem groupItem = new MenuItem(resourceBundle.getString("context.menu.group"));
        groupItem.setOnAction(event -> groupSelectedNodes(vertex, pickedVertexState));
        currentMenuItems.add(groupItem);
    }

    private void groupSelectedNodes(Node vertex, PickedState<Node> pickedVertexState) {
        pickedVertexState.getPicked().forEach(node -> {
            if (node instanceof SynsetNode) {
                NodeSet set = graphController.addSynsetToSet((SynsetNode) node);
                graphController.recreateLayoutWithFix(vertex, set);
            }
        });
        graphController.center();
    }
}


