package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.ViewTuple;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ViewScalingControl;
import edu.uci.ics.jung.visualization.decorators.ConstantDirectionalEdgeValueTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.apache.commons.collections15.Transformer;
import pl.edu.pwr.wordnetloom.client.events.RemoveRelationEvent;
import pl.edu.pwr.wordnetloom.client.events.UpdateCursorEvent;
import pl.edu.pwr.wordnetloom.client.model.DataEntry;
import pl.edu.pwr.wordnetloom.client.model.RelationType;
import pl.edu.pwr.wordnetloom.client.model.SynsetRelation;
import pl.edu.pwr.wordnetloom.client.service.SynsetDataStore;
import pl.edu.pwr.wordnetloom.client.ui.DialogHelper;
import pl.edu.pwr.wordnetloom.client.ui.graph.GraphTabViewModel;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.control.VisualisationModalGraphMouse;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.control.VisualisationPopupGraphMousePlugin;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.decorators.EdgeStrokeTransformer;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.decorators.VertexToolTipTransformer;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.layout.VisualisationLayout;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.listeners.GraphChangeListener;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.listeners.SynsetSelectionChangeListener;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.listeners.VisualisationGraphMouseListener;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.renderes.AstrideLabelRenderer;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.renderes.VertexFillColor;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.renderes.VertexRenderer;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.*;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SynsetRelationScope;
import pl.edu.pwr.wordnetloom.client.ui.synsetrelationdialog.SynsetRelationDialogView;
import pl.edu.pwr.wordnetloom.client.ui.synsetrelationdialog.SynsetRelationDialogViewModel;
import pl.edu.pwr.wordnetloom.client.ui.synsetrelationremovedialog.SynsetRelationRemoveDialogView;
import pl.edu.pwr.wordnetloom.client.ui.synsetrelationremovedialog.SynsetRelationRemoveDialogViewModel;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphController {

    private static final float EDGE_PICK_SIZE = 10f;
    private static final int MAX_SYNSETS_SHOWN = 4;

    private final DirectedGraph<Node, Edge> forest = new DirectedSparseMultigraph<>();

    private Map<UUID, SynsetNode> cache;

    private GraphTabViewModel graphViewModel;

    @Inject
    private VisualisationLayout layout;

    private VisualizationViewer<Node, Edge> vv;

    private RootNode rootNode;

    private Node selectedNode;

    private GraphZoomScrollPane panel;

    // Graph mouse listener, handles mouse clicks at vertices
    @Inject
    private VisualisationGraphMouseListener graphMouseListener;

    @Inject
    private AstrideLabelRenderer astrideLabelRenderer;

    @Inject
    private SynsetDataStore store;

    @Inject
    private VisualisationModalGraphMouse gm;

    @Inject
    SynsetRelationScope synsetRelationScope;

    @Inject
    Event<UpdateCursorEvent> updateCursorEvent;

    @InjectContext
    de.saxsys.mvvmfx.Context context;

    @Inject
    private Stage primaryStage;

    // Collection of object which listen for an event of synset selection
    // change.
    private Collection<SynsetSelectionChangeListener> synsetSelectionChangeListeners = new ArrayList<>();

    // Collection of objects which listen for an event of visualisation changes
    private Collection<GraphChangeListener> graphChangeListeners = new ArrayList<>();

    private final ScalingControl scalingControl = new ViewScalingControl();

    public void addGraphChangeListener(GraphChangeListener listener) {
        graphChangeListeners.add(listener);
    }

    @PostConstruct
    public void initialize() {

        this.cache = new ConcurrentHashMap<>();

        this.layout.setGraph(forest);

        this.vv = new VisualizationViewer<>(layout);

        this.vv.getRenderer().setVertexRenderer(
                new VertexRenderer(vv.getRenderer().getVertexRenderer()));

        this.graphMouseListener.setController(this);

        HashMap<RenderingHints.Key, Object> hints = new HashMap<>();

        hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        hints.put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        hints.put(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        hints.put(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        hints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);

        this.vv.setRenderingHints(hints);

        Transformer<Edge, Paint> edgeDrawColor = Edge::getColor;

        RenderContext<Node, Edge> rc = vv.getRenderContext();

        rc.setVertexShapeTransformer(Node::getShape);

        rc.setVertexFillPaintTransformer(new VertexFillColor(vv
                .getPickedVertexState(), rootNode));

        rc.setEdgeLabelClosenessTransformer(new ConstantDirectionalEdgeValueTransformer<>(
                0.5, 0.5));

        rc.setEdgeIncludePredicate((Context<Graph<Node, Edge>, Edge> context) -> true);

        rc.setEdgeDrawPaintTransformer(edgeDrawColor);
        rc.setArrowDrawPaintTransformer(edgeDrawColor);
        rc.setArrowFillPaintTransformer(edgeDrawColor);

        rc.setEdgeStrokeTransformer(new EdgeStrokeTransformer());
        ((ShapePickSupport<Node, Edge>) rc.getPickSupport())
                .setPickSize(EDGE_PICK_SIZE);

        this.vv.addGraphMouseListener(graphMouseListener);

        Transformer<Edge, String> stringer = Edge::toString;
        rc.setEdgeLabelTransformer(stringer);
        rc.setEdgeShapeTransformer(new EdgeShape.Line<>());


        gm.setGraphController(this);
        gm.loadPlugins();

        this.vv.addKeyListener(gm.getModeKeyListener());

        this.vv.getRenderer().setEdgeLabelRenderer(astrideLabelRenderer);

        this.vv.setGraphMouse(gm);

        this.vv.setVertexToolTipTransformer(new VertexToolTipTransformer());

        panel = new GraphZoomScrollPane(vv);
        panel.getHorizontalScrollBar().setVisible(false);
        panel.getVerticalScrollBar().setVisible(false);
        panel.add(vv);

    }


    public JPanel getSampleGraphViewer() {
        return panel;
    }

    public GraphTabViewModel getGraphTabViewModel() {
        return graphViewModel;
    }

    public void setGraphTabViewModel(GraphTabViewModel graphViewModel) {
        this.graphViewModel = graphViewModel;
    }

    public void refreshView(UUID synsetId) {
        // Clear the visualisation.
        clear();

        cache.clear();

        SynsetNode rootSynsetNode = new SynsetNode(synsetId, store);
        cache.put(synsetId, rootSynsetNode);
        rootNode = rootSynsetNode;

        vv.getRenderContext().setVertexFillPaintTransformer(
                new VertexFillColor(vv.getPickedVertexState(), rootNode));

        if (!forest.containsVertex(rootNode)) {
            forest.addVertex(rootNode);
        }

        NodeDirection.stream().forEach(d -> {
            if (rootNode instanceof SynsetNode) {
                ((SynsetNode) rootNode).setState(d, SynsetNode.State.EXPANDED);
            }
            showRelationGUI((SynsetNode) rootNode, d, null);
        });

        addMissingRelationInForest();
        checkAllStates();
        recreateLayout();
        resetCenter();
        vv.setVisible(true);
    }

    /**
     * Removes ghost relations where  relation type id is null
     */
    private void removeGhostRelations() {
        List<SynsetEdge> to = forest.getEdges()
                .stream()
                .filter(edge -> edge instanceof SynsetEdge)
                .map(edge -> (SynsetEdge) edge)
                .filter(se -> se.getRelationId() == null)
                .collect(Collectors.toList());
        to.forEach(forest::removeEdge);
    }


    /**
     * Recreate a visualisation layout. Currently fires graphChanged event, this should
     * be done somewhere else
     */
    public void recreateLayout() {
        removeGhostRelations();
        layout.mapNodesToPoints(rootNode);
        graphChanged();
    }

    /**
     * place selected node in the center of the screen
     */
    public void center() {
        center(selectedNode != null ? selectedNode : rootNode);
    }

    private void resetCenter() {
        center(rootNode);
    }

    private void center(Node centralNode) {
        assert centralNode != null;
        Point2D viewPosition = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(vv.getCenter());
        Point2D nodePosition = (Point2D) layout.transform(centralNode).clone();
        vv.getRenderContext().getMultiLayerTransformer()
                .getTransformer(Layer.LAYOUT)
                .translate(viewPosition.getX() - nodePosition.getX(), viewPosition.getY() - nodePosition.getY());
    }

    /**
     * deselects all visualisation nodes
     */
    public void deselectAll() {
        vv.getPickedVertexState().clear();
    }


    public void checkState(SynsetNode node, NodeDirection rel) {
        node.setState(rel, SynsetNode.State.NOT_EXPANDED);
        boolean all_ok = true;

        SynsetNode other;
        for (SynsetEdge e : node.getRelations(rel)) {
            if (e.getSource().equals(node.getSynsetId())) {
                other = cache.get(e.getTarget());
            } else {
                other = cache.get(e.getSource());
            }
            if (forest.containsVertex(other)) {
                node.setState(rel, SynsetNode.State.SEMI_EXPANDED);
            } else if (!node.getSynsetSet(rel).getSynsets().contains(other)) {
                all_ok = false;
            }
        }

        if (all_ok) {
            if (forest.containsVertex(node.getSynsetSet(rel))
                    || node.getSynsetSet(rel).getSynsets().isEmpty()) {
                node.setState(rel, SynsetNode.State.EXPANDED);
            }
        }
    }


    private SynsetEdge findRelation(SynsetNode s1, SynsetNode s2,
                                    NodeDirection rel) {
        for (SynsetEdge s : s1.getRelations(rel)) {
            if ((cache.get(s.getTarget()) != null && cache.get(s.getTarget())
                    .equals(s2))
                    || (cache.get(s.getSource()) != null && cache.get(
                    s.getSource()).equals(s2))) {
                return s;
            }
        }
        return null;
    }

    private void addEdge(SynsetEdge se, SynsetNode first, SynsetNode second) {
        se.setParentSynset(first);
        se.setChildSynset(second);
        forest.addEdge(se, first, second);
    }

    private Collection<SynsetNode> addMissingRelations(SynsetNode synset) {
        List<SynsetNode> changed = new ArrayList<>();

        NodeDirection.stream().forEach(nd -> {

            for (SynsetEdge e : synset.getRelations(nd)) {
                if (!forest.containsEdge(e)) {
                    final SynsetNode parent = cache.get(e.getSource());
                    final SynsetNode child = cache.get(e.getTarget());
                    final SynsetNode inner = synset == parent ? child : parent;

                    if (forest.containsVertex(parent) && forest.containsVertex(child)) {
                        addEdge(e, parent, child);

                        NodeDirection.stream().forEach(d -> {
                            if (inner.getSynsetSet(d).contains(synset)) {
                                inner.getSynsetSet(d).remove(synset);
                            }
                        });
                        changed.add(inner);
                    }
                }
            }
        });

        return changed;
    }

    private NodeSet getSetFrom(SynsetNode synset) {
        RootNode node = (RootNode) synset.getNode();
        return node.getSynsetSet(synset.getNodeDirection());
    }

    private void addEdgeSynsetSet(RootNode synset, NodeSet set,
                                  NodeDirection dir) {
        EdgeSet e = new EdgeSet();
        switch (dir) {
            case TOP:
            case BOTTOM:
                forest.addEdge(e, synset, set);
                break;
            case LEFT:
            case RIGHT:
                forest.addEdge(e, set, synset);
                break;
        }
    }

    private void insertSynsetFromSetToGraph(SynsetNode synset) {
        synset.setSet(null);
        forest.addVertex(synset);

        SynsetNode node = (SynsetNode) synset.getNode();
        NodeSet set = getSetFrom(synset);

        set.remove(synset);

        SynsetEdge edge = findRelation(node, synset, synset.getNodeDirection());

        if (edge != null) {
            addEdge(edge, buildSynsetNode(edge.getSource()),
                    buildSynsetNode(edge.getTarget()));
        }

        if (set.getSynsets().isEmpty()) {
            forest.removeVertex(set);
        }
    }

    public void addSynsetsFromSetToGraph(List<SynsetNode> synsets) {
        if (synsets.isEmpty()) {
            return;
        }
        for (SynsetNode synset : synsets) {
            store.load(synset.getSynsetId());
            synset.construct();
            addSynsetFromSetToGraph(synset);
        }
        addMissingRelationInForest();
        checkAllStates();
        recreateLayout();
    }

    public void addSynsetFromSetToGraph(SynsetNode synset) {
        insertSynsetFromSetToGraph(synset);
        vv.getPickedVertexState().pick(synset, true);
        if (getSetFrom(synset).getSynsets().size() == 1) {
            SynsetNode last = getSetFrom(synset).getSynsets().iterator()
                    .next();
            insertSynsetFromSetToGraph(last);
            vv.getPickedVertexState().pick(last, true);
        }
    }

    public NodeSet addSynsetToSet(SynsetNode synset) {

        NodeDirection.stream().forEach(d -> hideRelation(synset, d));

        forest.removeVertex(synset);
        NodeSet set = getSetFrom(synset);
        set.add(synset);
        synset.setSet(set);

        // if this is first synset, add the set to the visualisation
        if (set.getSynsets().size() == 1) {
            forest.addVertex(set);
            addEdgeSynsetSet((SynsetNode) synset.getNode(), set,
                    synset.getNodeDirection());
            set.setNode(synset.getNode(), synset.getNodeDirection());
        }
        selectedNode = set;

        checkAllStates();

        return set;
    }

    public void hideRelation(SynsetNode synsetNode, NodeDirection hide_dir) {
        boolean semi = false;
        boolean changed = false;
        for (SynsetEdge r : synsetNode.getRelations(hide_dir)) {
            SynsetNode rem = r.getParentSynset();
            if (rem != null && rem.equals(synsetNode)) {
                rem = r.getChildSynset();
            }

            if (rem != null && forest.containsVertex(rem)) {
                if (rem.getNode() != null
                        && rem.getNode().equals(synsetNode)) {

                    SynsetNode finalRem = rem;
                    NodeDirection.stream().forEach(nd -> hideRelation(finalRem, nd));

                    forest.removeEdge(r);
                    forest.removeVertex(rem);
                    changed = true;
                } else {
                    semi = true;
                }
            }
        }
        if (semi && changed) {
            synsetNode.setState(hide_dir, SynsetNode.State.SEMI_EXPANDED);
        } else if (changed) {
            synsetNode.setState(hide_dir, SynsetNode.State.NOT_EXPANDED);
        }
        synsetNode.getSynsetSet(hide_dir).removeAll();
        forest.removeVertex(synsetNode.getSynsetSet(hide_dir));
        checkAllStates();
    }

    private void checkAllStates() {
        forest.getVertices().stream()
                .filter(n -> n instanceof SynsetNode)
                .map(n -> (SynsetNode) n)
                .forEach(n -> NodeDirection.stream().forEach(d -> checkState((SynsetNode) n, d)));
    }

    public void addMissingRelationInForest() {
            List<Node> nodes = new ArrayList<>(forest.getVertices());
            nodes.forEach(node -> {
                if(node instanceof SynsetNode){
                    addMissingRelations((SynsetNode)node);
                }
            });
    }

    public SynsetNode showRelation(UUID synsetId, RelationType relationType) {
        SynsetNode synsetNode = new SynsetNode(synsetId, store);
        synsetNode.construct();
        cache.put(synsetId, synsetNode);
        forest.addVertex(synsetNode);
        showRelation(synsetNode, relationType.getDirection(), null);
        return synsetNode;
    }

    public void showRelation(final SynsetNode synsetNode, NodeDirection direction, RelationType relationType) {
        if (!synsetNode.isFullyLoaded()) {
            if(synsetNode.isSynsetMode()) {
                store.load(synsetNode.getSynsetId());
            }else{
                store.loadSense(synsetNode.getSynsetId());
            }
            synsetNode.setFullyLoaded(true);
        }

        showRelationGUI(synsetNode, direction, relationType);
        addMissingRelationInForest();
        checkAllStates();

        SwingUtilities.invokeLater(() -> {
            recreateLayoutWithFix(synsetNode, synsetNode);
            center();
            vv.repaint();
            vv.setVisible(true);
        });
    }

    public void showRelation(SynsetNode synsetNode, NodeDirection direction) {
        showRelation(synsetNode, direction, null);
    }
    /**
     * @param synsetNode node which relations will be shown
     * @param direction  relation class which will be shown
     */
    private void showRelationGUI(SynsetNode synsetNode, NodeDirection direction, RelationType relationType) {
        List<SynsetEdge> relations = synsetNode.getRelations(direction);
        if (relationType != null) {
            relations = relations.stream()
                    .filter(e -> e.getRelationType() == null && e.getRelationType().getId().equals(relationType.getId()))
                    .collect(Collectors.toList());
        }
        int nodesToInsert = Math.min(MAX_SYNSETS_SHOWN, relations.size()); // number synset to show on the graph
        insertVisibleSynsetNodes(relations, synsetNode, direction, nodesToInsert);
        if (nodesToInsert < relations.size()) {
            insertInvisibleSynsetNodes(relations, synsetNode, direction, nodesToInsert);
        }
    }

    private void insertVisibleSynsetNodes(List<SynsetEdge> relations, SynsetNode nodeSynset,
                                          NodeDirection direction, int maxShowedNodes) {
        IntStream.range(0, maxShowedNodes)
                .forEach(i -> {
                    SynsetNode node = getNodeSynsetFromEdge(relations.get(i), nodeSynset);
                    tryInsertNodeToForest(node, nodeSynset, direction);
                });
    }

    private void insertInvisibleSynsetNodes(List<SynsetEdge> relations, SynsetNode nodeSynset,
                                            NodeDirection direction, int insertedNodes) {

        NodeSet set = nodeSynset.getSynsetSet(direction);

        IntStream.range(insertedNodes, relations.size())
                .forEach(i -> {
                    SynsetNode node = getNodeSynsetFromEdge(relations.get(i), nodeSynset);
                    if (!forest.containsVertex(node)) {
                        if (!set.contains(node)) {
                            node.setNode(nodeSynset, direction);
                            node.setSet(set);
                            set.add(node);
                            node.setDirtyCache(true);
                        }
                    }
                });

        if (!set.getSynsets().isEmpty()) {
            // if set contains only one element, we put out node from set and put in on the graph. Set is not added to graph
            if (set.getSynsets().size() == 1) {
                SynsetNode node = set.getSynsets().iterator().next();
                tryInsertNodeToForest(node, nodeSynset, direction);
            } else {
                set.setNode(nodeSynset, direction);
                forest.addVertex(set);
                addEdgeSynsetSet(nodeSynset, set, direction);
            }
        }
    }


    private void tryInsertNodeToForest(SynsetNode node, SynsetNode spawnerNode, NodeDirection direction) {
        if (!forest.containsVertex(node)) {
            if (node.getSet() != null) {
                node.getSet().remove(node);
                node.setSet(null);
            }
            node.setNode(spawnerNode, direction);
            forest.addVertex(node);
        }
    }

    private SynsetNode getNodeSynsetFromEdge(SynsetEdge edge, SynsetNode originalNode) {
        if (edge.getSource().equals(originalNode.getSynsetId())) {
            return buildSynsetNode(edge.getTarget());
        } else {
            return buildSynsetNode(edge.getSource());
        }
    }

    /**
     * Clear visualisation (removes all edges and nodes).
     */
    public void clear() {
        layout.initialize();
        new ArrayList<>(forest.getEdges()).forEach(forest::removeEdge);
        new ArrayList<>(forest.getVertices()).forEach(forest::removeVertex);
    }

    public SynsetNode buildSynsetNode(UUID synsetId) {
        if (cache.containsKey(synsetId)) {
            SynsetNode s = cache.get(synsetId);
            if (s.isDirtyCache()) {
                s.construct();
            }
            return s;
        }

        SynsetNode synset = new SynsetNode(synsetId, store);
        cache.put(synsetId, synset);

        return synset;
    }

    private NodeDirection findCommonRelationDir(SynsetNode parent,
                                                SynsetNode child) {
        for (NodeDirection dir : NodeDirection.values()) {
            for (SynsetEdge e : parent.getRelations(dir)) {
                if (e.getTarget().equals(child.getSynsetId())
                        || e.getSource().equals(child.getSynsetId())) {
                    return dir;
                }
            }
        }
        return null;
    }

    public void checkMissing() {
        List<Node> nodes = new ArrayList<>(forest.getVertices());

        nodes.stream()
                .filter((node) -> (node instanceof SynsetNode))
                .forEach((node) -> addMissingRelations((SynsetNode) node));

        nodes.stream()
                .filter((node) -> (node instanceof SynsetNode))
                .forEach((node) -> NodeDirection.stream()
                        .forEach(d -> checkState((SynsetNode) node, d)));
    }

    /**
     * Invoke visualisation change events.
     */
    public void graphChanged() {
        graphChangeListeners.forEach(GraphChangeListener::graphChanged);
    }

    /**
     * selected node setter
     *
     * @param selected_node actually clicked
     */
    public void setSelectedNode(Node selected_node) {
        selectedNode = selected_node;
    }

    /**
     * @return visualization viewer
     */
    public VisualizationViewer<Node, Edge> getVisualizationViewer() {
        return vv;
    }

    /**
     * @return visualisation
     */
    public Graph<Node, Edge> getGraph() {
        return forest;
    }

    /**
     * @return layout
     */

    public Layout<Node, Edge> getLayout() {
        return layout;
    }

    /**
     * @return rootNode
     */
    public Node getRootNode() {
        return rootNode;
    }

    /**
     * @return selectedNode
     */
    public Node getSelectedNode() {
        return selectedNode;
    }

    /**
     * @param cursor new value of <code>VisualizationViewer</code> cursor
     * @author amusial
     */
    public void setCursor(Cursor cursor) {
        vv.setCursor(cursor);
    }

    /**
     * v1 and v2 could be same node, then it will stay in same point of view
     *
     * @param v1 node in first point of transformation vector
     * @param v2 node in second point of transformation vector
     * @author amusial
     */
    public void recreateLayoutWithFix(Node v1, Node v2) {
        removeGhostRelations();
        /* remember location of group node */
        Point2D p1 = (Point2D) layout.transform(v1).clone();
        /* recreate layout */
        layout.mapNodesToPoints(rootNode);
        Point2D p2 = (Point2D) layout.transform(v2).clone();
        /* transform layout */
        vv.getRenderContext().getMultiLayerTransformer()
                .getTransformer(Layer.LAYOUT)
                .translate(p1.getX() - p2.getX(), p1.getY() - p2.getY());
        /* fire visualisation changed event, do it somewhere else... */

        graphChanged();
    }

    public void vertexSelectionChange(Node synset) {
        if (selectedNode != synset) {
            selectedNode = synset;
        }
        if (synset instanceof SynsetNode) {
            if(((SynsetNode) synset).isSynsetMode()) {
                graphViewModel.showSynsetProperties(((SynsetNode) synset).getSynsetId());
                graphViewModel.showCorpusExamples(synset.getLabel());

                if (synsetRelationScope.isMakeSynsetRelationMode()) {
                    if (!synsetRelationScope.getFirstSelectedSynset().equals(((SynsetNode) synset).getSynsetId())) {
                        synsetRelationScope.setSecondSelectedSynset(((SynsetNode) synset).getSynsetId());

                        Platform.runLater(
                                this::openSynsetRelationDialog
                        );
                        updateCursorEvent.fire(new UpdateCursorEvent(VisualisationPopupGraphMousePlugin.DEFAULT_CURSOR));
                    }
                }
            }
        }
/*
        if (s.isMergeSynsetsModeOn()) {
            s.mergeSynsets(synset);
            return;
        }*/

        synsetSelectionChangeListeners.forEach((l) -> l.synsetSelectionChangeListener(synset));
    }

    // TODO: refactor
    public void removeRelation(UUID sourceSynset, UUID targetSynset, UUID relationId){
        Edge edge = getEdge(sourceSynset, targetSynset, relationId);
        if(edge != null){
            SynsetEdge synsetEdge = (SynsetEdge)edge;
            SynsetNode sourceNode = (SynsetNode) getNode(synsetEdge.getSource());
            SynsetNode targetNode = (SynsetNode) getNode(synsetEdge.getTarget());
            DataEntry sourceEntry = store.getById(sourceNode.getSynsetId());
            DataEntry targetEntry = store.getById(targetNode.getSynsetId());
            SynsetRelation synsetRelation = synsetEdge.getSynsetRelation();
            SynsetRelation reverseSynsetRelation = new SynsetRelation(synsetRelation.getTarget(), synsetRelation.getSource(), synsetRelation.getRelationType());
            SynsetEdge reverseSynsetEdge = new SynsetEdge(reverseSynsetRelation);
            for(NodeDirection direction : NodeDirection.values()){
                if(direction != NodeDirection.IGNORE){
                    if(sourceNode.getRelations(direction).contains(edge)){
                        sourceNode.getRelations(direction).remove(edge);
//                        sourceNode.getRelations(direction).remove(reverseSynsetEdge);
                        sourceEntry.getRelations(direction).remove(synsetRelation);
                        sourceEntry.getRelations(direction).remove(reverseSynsetRelation);
                    }
                    if(sourceEntry.getRelations(direction).size() == 1 && sourceEntry.getRelations(direction).get(0).getSource().equals(targetEntry.getId())){
                        sourceEntry.getRelations(direction).clear();
                        sourceNode.getRelations(direction).clear();
                    }
                }
                if(direction != NodeDirection.IGNORE) {
                    if(targetNode.getRelations(direction).contains(edge)){
                        targetNode.getRelations(direction).remove(edge);
//                        targetNode.getRelations(direction).remove(reverseSynsetEdge);
                        targetEntry.getRelations(direction).remove(synsetRelation);
                        targetEntry.getRelations(direction).remove(reverseSynsetRelation);
                    }
                    if(targetEntry.getRelations(direction).size() == 1 && targetEntry.getRelations(direction).get(0).getTarget().equals(sourceEntry.getId())){
                        targetEntry.getRelations(direction).clear();
                        targetNode.getRelations(direction).clear();
                    }
                }
            }
            getGraph().removeEdge(edge);
            tryRemoveSynsetNode(sourceNode);
            tryRemoveSynsetNode(targetNode);
        }
    }


    private void tryRemoveSynsetNode(SynsetNode node){
        if(node != rootNode && !hasRelationInForest(node)){
            forest.removeVertex(node);
        }
    }

    private boolean hasRelationInForest(SynsetNode node){
        for(Edge edge : forest.getEdges()){
            if(edge instanceof SynsetEdge){
                SynsetEdge synsetEdge  = (SynsetEdge) edge;
                if(synsetEdge.getChildSynset().equals(node) || synsetEdge.getParentSynset().equals(node)){
                    return true;
                }
            }
        }
        return false;
    }

    private Node getNode(UUID id){
        for(Node node : forest.getVertices()){
            if(node instanceof SynsetNode){
                SynsetNode synsetNode = (SynsetNode) node;
                if(synsetNode.getSynsetId().equals(id)){
                    return node;
                }
            }
        }
        return null;
    }


    private Edge getEdge(UUID sourceSynset, UUID targetSynset, UUID relationId){
        for(Edge edge : getGraph().getEdges()){
            if(edge instanceof SynsetEdge){
                SynsetEdge synsetEdge = (SynsetEdge) edge;
                if(sourceSynset.equals(synsetEdge.getSource())
                && targetSynset.equals(synsetEdge.getTarget())
                && relationId.equals(synsetEdge.getRelationId())){
                    return edge;
                }
            }
        }
       return null;
    }

    public void onRemoveSynsetRelation(@Observes(notifyObserver = Reception.ALWAYS) RemoveRelationEvent event) {
        openRemoveRelationDialog(event.getFirst(), event.getSecond());
    }

    private void openRemoveRelationDialog(UUID src, UUID trg){
        ViewTuple<SynsetRelationRemoveDialogView, SynsetRelationRemoveDialogViewModel> load = FluentViewLoader
                .fxmlView(SynsetRelationRemoveDialogView.class)
                .context(context)
                .load();

        Parent view = load.getView();
        Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/wordnetloom.css");
        load.getCodeBehind().setDisplayingStage(showDialog);
        load.getViewModel().loadRelations(src,trg);
    }

    private void openSynsetRelationDialog() {

        ViewTuple<SynsetRelationDialogView, SynsetRelationDialogViewModel> load = FluentViewLoader
                .fxmlView(SynsetRelationDialogView.class)
                .context(context)
                .load();

        Parent view = load.getView();
        Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/wordnetloom.css");
        load.getCodeBehind().setDisplayingStage(showDialog);

    }
}
