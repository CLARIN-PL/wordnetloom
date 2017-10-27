package pl.edu.pwr.wordnetloom.client.plugins.viwordnet.visualization.control;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.picking.PickedState;
import pl.edu.pwr.wordnetloom.client.plugins.viwordnet.ViWordNetPerspective;
import pl.edu.pwr.wordnetloom.client.plugins.viwordnet.ViWordNetService;
import pl.edu.pwr.wordnetloom.client.plugins.viwordnet.structure.*;
import pl.edu.pwr.wordnetloom.client.plugins.viwordnet.views.ViwnGraphViewUI;
import pl.edu.pwr.wordnetloom.client.plugins.viwordnet.views.ViwnLockerViewUI;
import pl.edu.pwr.wordnetloom.client.systems.ui.MButton;
import pl.edu.pwr.wordnetloom.client.utils.Labels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

public class ViwnGraphViewPopupGraphMousePlugin extends AbstractPopupGraphMousePlugin {

    protected JPopupMenu popup = new JPopupMenu();
    protected ViwnGraphViewUI vgvui;
    protected JList synset_list_ = null;

    public ViwnGraphViewPopupGraphMousePlugin(ViwnGraphViewUI vgvui) {
        this.vgvui = vgvui;
    }

    private void addSynsets(ViwnNode v) {
        popup.setVisible(false);
        Object[] syns = synset_list_.getSelectedValues();
        if (syns.length > 0) {
            vgvui.deselectAll();
        }
        ViwnNode other = v;
        for (Object obj : syns) {
            vgvui.addSynsetFromSet((ViwnNodeSynset) obj);
            other = (ViwnNode) obj;
        }
        if (syns.length > 0) {
            ViwnNode p2 = v;
            Graph<ViwnNode, ViwnEdge> g = vgvui.getGraph();
            ViwnNode parent = v.getSpawner();
            boolean dissapear = true;
            for (ViwnEdge edge : g.getIncidentEdges(parent)) {
                ViwnNode opposite = g.getOpposite(parent, edge);
                if (parent.equals(opposite.getSpawner())
                        && (opposite.getSpawnDir() != null)) {
                    if (opposite == v) {
                        dissapear = false;
                    }
                }
            }
            if (dissapear) {
                if (other != null) {
                    p2 = other;
                } else {
                    p2 = v.getSpawner();
                }
            }
            vgvui.recreateLayoutWithFix(v, p2);
        }
    }

    @Override
    protected void handlePopup(MouseEvent e) {

        VisualizationViewer<ViwnNode, ViwnEdge> vv
                = (VisualizationViewer<ViwnNode, ViwnEdge>) e.getSource();
        Layout<ViwnNode, ViwnEdge> layout = vv.getGraphLayout();
        Graph<ViwnNode, ViwnEdge> graph = layout.getGraph();
        Point2D p = e.getPoint();
        Point2D ivp = p;
        GraphElementAccessor<ViwnNode, ViwnEdge> pickSupport = vv.getPickSupport();
        boolean list_focus = false;
        synset_list_ = null;

        // exit make relation mode
        ViWordNetService s
                = ((ViWordNetService) vgvui.getWorkbench().getService(
                "pl.edu.pwr.wordnetloom.client.plugins.viwordnet.ViWordNetService"));
        if (s.isMakeRelationModeOn()) {
            s.switchMakeRelationMode();
            return;
        }

        if (s.isMergeSynsetsModeOn()) {
            s.switchMergeSynsetsMode();
            return;
        }

        if (pickSupport != null) {

            popup.removeAll();

            ViwnNode vertex = pickSupport.getVertex(layout, ivp.getX(), ivp.getY());
            ViwnEdge edge = pickSupport.getEdge(layout, ivp.getX(), ivp.getY());
            PickedState<ViwnNode> pickedVertexState = vv.getPickedVertexState();
            PickedState<ViwnEdge> pickedEdgeState = vv.getPickedEdgeState();

            if (vertex != null && vertex instanceof ViwnNodeWord) {
                popup.add(new AbstractAction(Labels.CREATE_RELATION_WITH) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        s.setFirstMakeRelation(vertex);
                    }
                });

            } else if (vertex != null && vertex instanceof ViwnNodeSynset) {
                // vertex clicked
                popup.add(new JLabel(Labels.SYNSET_OPTIONS));

                if (!vertex.isMarked()) {
                    // mark synset
                    popup.add(new AbstractAction(Labels.SYNSET_MARK) {
                        /**
                         *
                         */
                        private static final long serialVersionUID = -5157166133351047723L;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            pickedVertexState.pick(vertex, true);
                            vertex.setMarked(true);
                        }
                    });
                } else {
                    // unmark synset
                    popup.add(new AbstractAction(Labels.SYNSET_UNMARK) {
                        /**
                         *
                         */
                        private static final long serialVersionUID = -5157166132341047723L;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            pickedVertexState.pick(vertex, true);
                            vertex.setMarked(false);
                        }
                    });
                }

                popup.add(new AbstractAction(Labels.PATH_TO_HIPERONIM) {
                    private static final long serialVersionUID = 0L;

                    @Override
                    public void actionPerformed(ActionEvent e) {
//                        Synset synset = ((ViwnNodeSynset) vertex).getSynset();
//                        List<Synset> path
//                                = RemoteUtils.synsetRelationRemote.dbGetTopPathInSynsets(synset,
//                                        RelationTypes.getByName("hiponimia").Id());
//                        s.getActiveGraphView().getUI().
//                                addConnectedSynsetsToGraph((ViwnNodeSynset) vertex, path);
                    }
                });

                // add to locker
                popup.add(new AbstractAction(Labels.ADD_TO_CLIPBOARD) {
                    /**
                     *
                     */
                    private static final long serialVersionUID = -1354196137333218291L;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ViWordNetService s = (ViWordNetService) vgvui.getWorkbench().getService("pl.edu.pwr.wordnetloom.client.plugins.viwordnet.ViWordNetService");
                        s.addToLocker(vertex, ViwnLockerViewUI.getInstance().new ViwnNodeRenderer());
                    }
                });

                popup.add(new AbstractAction(Labels.OPEN_IN_NEW_TAB) {
                    /**
                     *
                     */
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ViWordNetService s = (ViWordNetService) vgvui.getWorkbench().getService("pl.edu.pwr.wordnetloom.client.plugins.viwordnet.ViWordNetService");
                        s.addGraphView();
                        s.getActiveGraphView().loadSynset(((ViwnNodeSynset) vertex).getSynset());
                        ViWordNetPerspective p
                                = (ViWordNetPerspective) vgvui.getWorkbench().getActivePerspective();
                        p.setTabTitle(
                                s.getActiveGraphView().getUI().getRootNode().getLabel());
                    }
                });

                AbstractAction group_action = new AbstractAction(Labels.GRUPPING) {
                    {
                        setEnabled(false);
                    }

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Iterator<ViwnNode> pick_itr = pickedVertexState.getPicked().iterator();
                        ViwnNode set = vertex; // need to remember last synset from set or set
                        while (pick_itr.hasNext()) {
                            ViwnNode n = pick_itr.next();
                            if (n instanceof ViwnNodeSynset) {
                                set = vgvui.addSynsetToSet((ViwnNodeSynset) n);
                            }
                        }
                        /* recreate layout with fixing */
                        vgvui.recreateLayoutWithFix(vertex, set);
                    }
                };

                if (vgvui.canGroupSynsets()) {
                    group_action.setEnabled(true);
                }

                // group
                popup.add(group_action);

                // enter make relation mode
                popup.add(new AbstractAction(Labels.SYNSET_CREATE_RELATION_WITH) {
                    /**
                     *
                     */
                    private static final long serialVersionUID = 1892743918624978L;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ViWordNetService s = (ViWordNetService) vgvui.getWorkbench().getService("pl.edu.pwr.wordnetloom.client.plugins.viwordnet.ViWordNetService");
                        s.setFirstMakeRelation(vertex);
                    }
                });

                popup.add(new AbstractAction(Labels.SYNSET_MERGE_WITH) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ViWordNetService s = (ViWordNetService) vgvui.getWorkbench().getService("pl.edu.pwr.wordnetloom.client.plugins.viwordnet.ViWordNetService");
                        s.setFirstMergeSynsets(vertex);
                    }
                });

                // split synset options from lexical unit options
                popup.addSeparator();
                popup.add(new JLabel(Labels.LEXICAL_UNIT_OPTIONS));

                // TODO: make lexical units relations
                // show lexical units of synset
                JMenuItem lexicalUnits = new JMenu(Labels.UNIT_CREATE_RELATION_WITH);

//                for (final Sense lu : RemoteUtils.synsetRemote.dbFastGetUnits(((ViwnNodeSynset) vertex).getSynset(), LexiconManager.getInstance().getLexicons())) {
//                    lexicalUnits.add(new JMenuItem(new AbstractAction(lu.toString()) {
//
//                        private static final long serialVersionUID = 65468435418L;
//
//                        public void actionPerformed(ActionEvent ae) {
//                            ViWordNetService s = (ViWordNetService) vgvui.getWorkbench().getService("pl.edu.pwr.wordnetloom.client.plugins.viwordnet.ViWordNetService");
//                            s.setFirstMakeRelation(lu);
//                        }
//                    }));
//                }
                popup.add(lexicalUnits);

                // add lexical unit to locker
                lexicalUnits = new JMenu(Labels.UNIT_ADD_TO_CLIPBOARD);
//                for (final Sense lu : RemoteUtils.synsetRemote.dbFastGetUnits(((ViwnNodeSynset) vertex).getSynset(), LexiconManager.getInstance().getLexicons())) {
//                    lexicalUnits.add(new JMenuItem(new AbstractAction(lu.toString()) {
//                        /**
//                         *
//                         */
//                        private static final long serialVersionUID = 712639812536152L;
//
//                        public void actionPerformed(ActionEvent ae) {
//                            ViWordNetService s = (ViWordNetService) vgvui.getWorkbench().getService("pl.edu.pwr.wordnetloom.client.plugins.viwordnet.ViWordNetService");
//                            s.addToLocker(lu, ViwnLockerViewUI.getInstance().new SenseRenderer());
//                        }
//                    }));
//                }
                popup.add(lexicalUnits);

            } else if (vertex != null && vertex instanceof ViwnNodeSet) {
                ViwnNodeSet set = (ViwnNodeSet) vertex;
                DefaultListModel model = new DefaultListModel();

                ArrayList<ViwnNodeSynset> col = new ArrayList<>(set.getSynsets());
                Collections.sort(col, new ViwnNodeAlphabeticComparator());
                for (ViwnNodeSynset syns : col) {
                    model.addElement(syns);
                }

                list_focus = true;
                synset_list_ = new JList(model);
                synset_list_.setSelectedIndex(0);

                JScrollPane scroll_pane = new JScrollPane();
                scroll_pane.setPreferredSize(new Dimension(250, 200));
                scroll_pane.getViewport().setView(synset_list_);
                JPanel panel = new JPanel(new BorderLayout());

                JButton but_expand = new MButton()
                        .withActionListener(l -> addSynsets(vertex))
                        .withCaption(Labels.EXPAND)
                        .withMnemonic(KeyEvent.VK_R);

                JButton but_cancel = MButton.buildCancelButton()
                        .withActionListener(l -> popup.setVisible(false));

                JButton but_all = new MButton()
                        .withCaption(Labels.VALUE_ALL)
                        .withMnemonic(KeyEvent.VK_W)
                        .withActionListener(l -> {

                            popup.setVisible(false);
                            DefaultListModel list = (DefaultListModel) synset_list_.getModel();
                            if (list.size() > 0) {
                                vgvui.deselectAll();
                            }
                            for (int i = 0; i < list.size(); i++) {
                                ViwnNodeSynset obj = (ViwnNodeSynset) list.get(i);
                                vgvui.addSynsetFromSet(obj);
                            }
                            if (list.size() > 0) {
                            /* recreate view with fixing location */
                                vgvui.recreateLayoutWithFix(vertex, (ViwnNode) list.get(list.size() - 1));
                            }
                        });

                synset_list_.addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            addSynsets(vertex);
                        }
                    }
                });

                panel.add(scroll_pane, BorderLayout.PAGE_START);

                JPanel inner_panel = new JPanel(new FlowLayout());
                inner_panel.add(but_expand);
                inner_panel.add(but_cancel);
                inner_panel.add(but_all);

                panel.add(inner_panel, BorderLayout.CENTER);

                popup.add(panel);

            } else if (edge != null) {
                // edge clicked
                popup.add(new AbstractAction(Labels.FOLLOW_EGDE) {
                    /**
                     *
                     */
                    private static final long serialVersionUID = 5625564155297832427L;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ViwnNode vn = graph.getDest(edge);
                        vgvui.setSelectedNode(vn);
                        vgvui.center();
                        vv.repaint();
                    }
                });

                if (edge instanceof ViwnEdgeSynset) {
                    popup.add(new AbstractAction(Labels.REMOVE_RELATION) {

                        private static final long serialVersionUID = -9382109827346L;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ViWordNetService s = (ViWordNetService) vgvui.getWorkbench().getService("pl.edu.pwr.wordnetloom.client.plugins.viwordnet.ViWordNetService");
                            Pair<ViwnNode> c = vgvui.getGraph().getEndpoints(edge);
                            HashSet<ViwnEdge> rel = new HashSet<>(vgvui.getGraph().findEdgeSet(c.getFirst(), c.getSecond()));
                            rel.addAll(vgvui.getGraph().findEdgeSet(c.getSecond(), c.getFirst()));
                            s.removeRelation(rel);
                        }
                    });
                }

            } else {
                ViwnNode node = s.getActiveGraphView().getUI().getRootNode();
                if (node != null) {
                    String label = node.getLabel();

                    String filename = label.split(" ")[0] + ".png";
                    popup.add(new AbstractAction(Labels.SAVE_GRAPH_TO_FILE_COLON + filename) {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            s.getActiveGraphView().getUI().saveToFile(filename);
                        }
                    });
                }
            }

            if (popup.getComponentCount() > 0) {
                popup.show(vv, e.getX(), e.getY());
                if (list_focus) {
                    synset_list_.requestFocusInWindow();
                }
            }
        }

    }

}
