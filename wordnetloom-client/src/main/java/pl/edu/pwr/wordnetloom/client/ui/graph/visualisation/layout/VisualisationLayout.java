package pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.layout;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.map.LazyMap;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.Edge;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.Node;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.NodeAlphabeticComparator;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;


public class VisualisationLayout implements Layout<Node, Edge> {

    /**
     * total size of layout
     */
    protected Dimension size;

    /**
     * visualisation to draw
     */
    protected Graph<Node, Edge> graph;

    /**
     * nodes and its locations
     */
    private Map<Node, Point2D> locations;

    private transient Set<Node> alreadyDone;

    /**
     * default distance from node to node at x axis
     */
    private final static int DEFAULT_X_AXIS_DISTANCE = 120;

    /**
     * default distance from node to node at y axis
     */
    private final static int DEFAULT_Y_AXIS_DISTANCE = 45;


    @Override
    public Graph<Node, Edge> getGraph() {
        return graph;
    }

    @Override
    public Dimension getSize() {
        return size;
    }

    @PostConstruct
    @Override
    public void initialize() {
        this.size = new Dimension(100, 100);
        this.locations = LazyMap.decorate(new HashMap<>(), (Node arg0) -> new Point2D.Double());
        this.alreadyDone = new HashSet<>();
    }

    /**
     * allow to drag 'n' drop all nodes
     */
    @Override
    public boolean isLocked(Node v) {
        return false;
    }

    @Override
    public void lock(Node v, boolean state) {
    }

    @Override
    public void reset() {
        alreadyDone.clear();
        Node center = findRoot();
        if (center != null) {
            mapNodesToPoints(center, null, null);
        }
    }

    /**
     * method tries to find root node of the visualisation
     *
     * @return root node of visualisation or null if cannot find it
     */
    private Node findRoot() {
        for (Node n : graph.getVertices()) {
            if (n.getNode() == null) {
                return n;
            }
        }
        return null;
    }

    @Override
    public void setGraph(Graph<Node, Edge> graph) {
        this.graph = graph;
    }

    @Override
    public void setInitializer(Transformer<Node, Point2D> initializer) {
        graph.getVertices().stream()
                .peek((n) -> setLocation(n, initializer.transform(n)))
                .forEach((n) ->  alreadyDone.add(n));
    }

    @Override
    public void setLocation(Node v, Point2D location) {
        locations.get(v).setLocation(location);
    }

    @Override
    public void setSize(Dimension d) {
        this.size = d;
    }

    /**
     * @return location of the node
     */
    @Override
    public Point2D transform(Node node) {
        return locations.get(node);
    }

    /**
     * set visualisation nodes locations
     *
     * @param center main, central node
     */
    public void mapNodesToPoints(Node center) {
        mapNodesToPoints(center, null, null);
    }

    /*
     * to allow user permanently change node placing: 1. when node(s) is taken
     * from set of nodes - refresh layout 2. when relation was added - refresh
     * layout after expanding a node - place only expanded and its children
     * after reducing a node?
     */
    private void insertNodeToList(List<Node> list, Node element){
        if(!list.contains(element)){
            list.add(element);
        }
    }

    /**
     * @return center of layout area
     */
    public Point2D getCenter() {
        return new Point2D.Double(size.getWidth() / 2, size.getHeight() / 2);
    }
    /**
     * set visualisation nodes locations
     * <p>
     * TODO: bug fix #1 cycles in visualisation causes stack overflow, wont fix
     *
     * @param center main, central node
     * @param loc    location in which central node should stay, null for root node
     * @param placed nodes mapped in previous method calls
     * @return mapped in current call
     */
    public Set<Node> mapNodesToPoints(Node center, Point2D loc, Set<Node> placed) {

        boolean root = false;

        if (loc == null) {
            loc = getCenter();
            root = true;
        }
        if (placed == null) {
            placed = new HashSet<>();
        }

        // nodes mapped in actual method call
        Set<Node> actual = new HashSet<>();

        // map all nodes
        if (!placed.contains(center)) {

            // place it in the center
            setLocation(center, loc);
            // mark as mapped
            actual.add(center);

            // now, time to place its neighbors
            // Divide neighbors according to relation type, and future place
            // in visualisation
            List<Node> bottom = new ArrayList<>();
            List<Node> top = new ArrayList<>();
            List<Node> right = new ArrayList<>();
            List<Node> left = new ArrayList<>();

            Collection<Edge> edges = graph.getIncidentEdges(center);

            if(edges!=null){
                for (Edge edge : edges) {
                    Node opposite = graph.getOpposite(center, edge);

                    if (center.equals(opposite.getNode())
                            && (opposite.getNodeDirection() != null)) {
                        switch (opposite.getNodeDirection()) {
                            case BOTTOM:
                                insertNodeToList(bottom, opposite);
                                break;
                            case TOP:
                                insertNodeToList(top, opposite);
                                break;
                            case RIGHT:
                                insertNodeToList(right, opposite);
                                break;
                            case LEFT:
                                insertNodeToList(left, opposite);
                                break;
                        }
                    }
                }
            }

            // sort children alphabetically
            bottom.sort(new NodeAlphabeticComparator());
            top.sort(new NodeAlphabeticComparator());
            right.sort(new NodeAlphabeticComparator());
            left.sort(new NodeAlphabeticComparator());

            // place a node here ;-)
            Point p = new Point();
            double ile;
            int i = 0;

            // place lower
            placeLower(loc, actual, bottom, p, i);
            placeUpper(loc, actual, top, p);
            double updownmax = placeRight(loc, actual, bottom, top, right, p);
            placeLeft(loc, actual, left, p, updownmax);

            // =========================== lower level
            for (Node vn : bottom) {
                Set<Node> children = mapNodesToPoints(vn, locations.get(vn), placed);

                correctSubGraphMapping(center, vn, actual, children);
                actual.addAll(children);
            }
            for (Node vn : top) {
                Set<Node> children = mapNodesToPoints(vn, locations.get(vn), placed);
                correctSubGraphMapping(center, vn, actual, children);

                actual.addAll(children);
            }
            for (Node vn : right) {
                Set<Node> children = mapNodesToPoints(vn, locations.get(vn), placed);

                correctSubGraphMapping(center, vn, actual, children);
                actual.addAll(children);
            }
            for (Node vn : left) {
                Set<Node> children = mapNodesToPoints(vn, locations.get(vn), placed);

                correctSubGraphMapping(center, vn, actual, children);
                actual.addAll(children);
            }

        }
        if (root) {
            alreadyDone.clear();
            alreadyDone.addAll(graph.getVertices());

            // correct vertices locations and set size
            correctGraph(alreadyDone, center);
        }

        return actual;
    }

    private void placeLeft(Point2D loc, Set<Node> actual, List<Node> left, Point p, double updownmax) {
        double ile;
        int i;
        if (updownmax < left.size()) {
            p.x = (int) loc.getX() - DEFAULT_X_AXIS_DISTANCE
                    - (int) ((updownmax - 1) * DEFAULT_X_AXIS_DISTANCE) / 2;
        } else {
            p.x = (int) loc.getX() - DEFAULT_X_AXIS_DISTANCE - ((left.size() - 1) * DEFAULT_X_AXIS_DISTANCE) / 2;
        }
        p.y = (int) loc.getY() - ((left.size() - 1) * DEFAULT_Y_AXIS_DISTANCE) / 2 + DEFAULT_Y_AXIS_DISTANCE / 3;
        ile = left.size();
        i = 0;

        for (Node vn : left) {
            // place it
            setLocation(vn, new Point2D.Double(p.x - Math.sqrt(ile) * DEFAULT_Y_AXIS_DISTANCE
                    * Math.cos(Math.PI * ((ile / 2D - i - 0.5D) / ile)),
                    p.y));
            // mark as mapped
            actual.add(vn);

            p.y += DEFAULT_Y_AXIS_DISTANCE;
            ++i;
        }
    }

    private double placeRight(Point2D loc, Set<Node> actual, List<Node> bottom, List<Node> top, List<Node> right, Point p) {
        double ile;
        int i;
        double updownmax = Math.max((double) Math.max(bottom.size(), top.size()), 1);
        if (updownmax < right.size()) {
            p.x = (int) loc.getX() + DEFAULT_X_AXIS_DISTANCE
                    + (int) ((updownmax - 1) * DEFAULT_X_AXIS_DISTANCE) / 2;
        } else {
            p.x = (int) loc.getX() + DEFAULT_X_AXIS_DISTANCE + ((right.size() - 1) * DEFAULT_X_AXIS_DISTANCE) / 2;
        }
        p.y = (int) loc.getY() - ((right.size() - 1) * DEFAULT_Y_AXIS_DISTANCE) / 2 + DEFAULT_Y_AXIS_DISTANCE / 3;
        ile = right.size();
        i = 0;
        for (Node vn : right) {
            // place it
            setLocation(
                    vn,
                    new Point2D.Double((p.x + Math.sqrt(ile)
                            * DEFAULT_Y_AXIS_DISTANCE
                            * Math.cos(Math.PI
                            * ((ile / 2D - i - 0.5D) / ile))), p.y));
            // mark as mapped
            actual.add(vn);

            p.y += DEFAULT_Y_AXIS_DISTANCE;
            ++i;
        }
        return updownmax;
    }

    private void placeUpper(Point2D loc, Set<Node> actual, List<Node> top, Point p) {
        double ile;
        int i;
//        p.x = (int) loc.getX() - ((top.size() - 1) * DEFAULT_X_AXIS_DISTANCE) / 2 + DEFAULT_X_AXIS_DISTANCE / 11;

        p.x = (int)loc.getX() - (top.size() - 1) * DEFAULT_X_AXIS_DISTANCE /2;
        p.y = (int) loc.getY() - (DEFAULT_Y_AXIS_DISTANCE * top.size()) / 2;
        ile = top.size();
        i = 0;
        for (Node vn : top) {
            setLocation(
                    vn,
                    new Point2D.Double(p.x,
                            p.y - Math.sqrt(ile) * DEFAULT_Y_AXIS_DISTANCE * Math.cos(Math.PI * ((ile / 2D - i - 0.5D) / ile))));
            // mark as mapped
            actual.add(vn);

            p.x += DEFAULT_X_AXIS_DISTANCE;
            ++i;
        }
    }

    private void placeLower(Point2D loc, Set<Node> actual, List<Node> bottom, Point p, int i) {
        double ile;
//        p.x = (int) loc.getX() - ((bottom.size() - 1) * DEFAULT_X_AXIS_DISTANCE) / 2 + DEFAULT_X_AXIS_DISTANCE / 11;
        p.x = (int) loc.getX() - (bottom.size() - 1) * DEFAULT_X_AXIS_DISTANCE /2;
        p.y = (int) loc.getY() + (DEFAULT_Y_AXIS_DISTANCE * bottom.size()) / 2;
        ile = bottom.size();
        for (Node vn : bottom) {
            setLocation(vn, new Point2D.Double(p.x,
                    p.y + Math.sqrt(ile) * DEFAULT_Y_AXIS_DISTANCE * Math.cos(Math.PI * ((ile / 2D - i - 0.5D) / ile))));
            // mark as mapped
            actual.add(vn);

            p.x += DEFAULT_X_AXIS_DISTANCE;
            ++i;
        }
    }

    /**
     * @param col collection of nodes which location should be corrected
     * @param dx  x axis correction
     * @param dy  y axis correction
     */
    protected void correctNode2PointMapping(Collection<Node> col, int dx,
                                            int dy) {
        col.forEach((vn) -> {
            Point2D current = locations.get(vn);
            setLocation(vn, new Point2D.Double(current.getX() + dx, current.getY() + dy));
        });
    }

    /**
     * @param parent parent of central node of collection to correct
     * @param center central node of set to correct
     * @param upper  parent node neighbors
     * @param lower  central node neighbors, nodes to correct
     */
    protected void correctSubGraphMapping(Node parent, Node center,
                                          Collection<Node> upper, Collection<Node> lower) {
        // calculate direction vector from parent to center
        Point2D par = locations.get(parent);
        Point2D cen = locations.get(center);
        Point2D dir = new Point2D.Double((cen.getX() - par.getX()),
                (cen.getY() - par.getY()));

        double dist = dir.distance(0, 0);
        dir.setLocation(dir.getX() / dist, dir.getY() / dist);

        boolean end = false;
        int[] dim;

        Set<Node> ad = new HashSet<>(upper);
        ad.removeAll(lower);

        // calculate boundaries of subgraph
        dim = findMappedGraphBoundaries(center, lower);
        // check for conflicts
        end = !isAnyInsideBounds(ad, dim);

        while (!end) {
            // move subgraph
            correctNode2PointMapping(lower, (int) (100 * dir.getX()),
                    (int) (100 * dir.getY()));
            // calculate boundaries of subgraph
            dim = findMappedGraphBoundaries(center, lower);
            // check for conflicts
            end = !isAnyInsideBounds(ad, dim);
        }

    }

    /**
     * @param center center of subgraph to get
     * @return subgraph from node center
     */
    private Set<Node> getSubGraphOf(Node center) {
        Set<Node> ret = new HashSet<>();
        ret.add(center);

        for (Edge edge : graph.getIncidentEdges(center)) {
            Node opposite = graph.getOpposite(center, edge);
            if (center.equals(opposite.getNode())
                    && (opposite.getNodeDirection() != null)) {
                ret.addAll(getSubGraphOf(opposite));
            }
        }

        return ret;
    }

    /**
     * @param col    collection of vertices to check
     * @param bounds bounds in which vertices should not be
     * @return true if at least one node from col is inside bounds
     */
    protected boolean isAnyInsideBounds(Collection<Node> col, int[] bounds) {
        for (Node vn : col) {
            if (bounds[0] < locations.get(vn).getX()
                    && bounds[1] > locations.get(vn).getX()
                    && bounds[2] < locations.get(vn).getY()
                    && bounds[3] > locations.get(vn).getY()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param col collection of visualisation nodes
     * @return visualisation dimension
     */
    public Dimension calcSetSize(Collection<Node> col) {
        col = alreadyDone;
        if (col.size() > 0) {
            int minx, maxx, miny, maxy;
            Node n = col.iterator().next();
            minx = maxx = (int) locations.get(n).getX();
            miny = maxy = (int) locations.get(n).getY();

            for (Node vn : col) {
                Point2D p = locations.get(vn);
                if (p.getX() > maxx) {
                    maxx = (int) p.getX();
                } else if (p.getX() < minx) {
                    minx = (int) p.getX();
                }
                if (p.getY() > maxy) {
                    maxy = (int) p.getY();
                } else if (p.getY() < miny) {
                    miny = (int) p.getY();
                }
            }

            return (new Dimension((maxx - minx), (maxy - miny)));
        } else {
            return new Dimension();
        }
    }

    /**
     * @param central central node
     * @param col     collection of already mapped vertices
     * @return table of 4 integers minx, maxx, miny, maxy
     */
    protected int[] findMappedGraphBoundaries(Node central,
                                              Collection<Node> col) {

        // boundary coordinates
        int minx, maxx, miny, maxy;

        // random node to initialize boundary coordinates
        minx = maxx = (int) locations.get(central).getX();
        miny = maxy = (int) locations.get(central).getY();

        // for all nodes already placed, check their locations
        for (Node vn : col) {
            Point2D p = locations.get(vn);
            if (p.getX() > maxx) {
                maxx = (int) p.getX();
            } else if (p.getX() < minx) {
                minx = (int) p.getX();
            }
            if (p.getY() > maxy) {
                maxy = (int) p.getY();
            } else if (p.getY() < miny) {
                miny = (int) p.getY();
            }
        }

        return new int[]{minx - DEFAULT_X_AXIS_DISTANCE, maxx + DEFAULT_X_AXIS_DISTANCE, miny - DEFAULT_Y_AXIS_DISTANCE,
                maxy + DEFAULT_Y_AXIS_DISTANCE};
    }

    /*
     * place all vertices in positive coordinates set size value to contain full
     * visualisation in it
     *
     * @param toCorrect collection of nodes which position should be corrected
     *
     * @param anode node from visualisation
     */
    protected void correctGraph(Collection<Node> toCorrect, Node anode) {

        // boundary coordinates
        int minx, maxx, miny, maxy;

        // initialize boundary coordinates
        minx = maxx = (int) locations.get(anode).getX();
        miny = maxy = (int) locations.get(anode).getY();

        // for all nodes already place, check their locations
        for (Node vn : toCorrect) {
            Point2D p = locations.get(vn);
            if (p.getX() > maxx) {
                maxx = (int) p.getX();
            } else if (p.getX() < minx) {
                minx = (int) p.getX();
            }
            if (p.getY() > maxy) {
                maxy = (int) p.getY();
            } else if (p.getY() < miny) {
                miny = (int) p.getY();
            }
        }

        // replace nodes
        correctNode2PointMapping(toCorrect, -minx + DEFAULT_X_AXIS_DISTANCE, -miny + DEFAULT_Y_AXIS_DISTANCE);

        // set visualisation size
        size = new Dimension((maxx - minx) + 2 * DEFAULT_X_AXIS_DISTANCE, (maxy - miny) + 2 * DEFAULT_Y_AXIS_DISTANCE);

    }

    /**
     * place all vertices in positive coordinates set size value to contain full
     * visualisation in it
     *
     * @param toCorrect collection of nodes which position should be corrected
     */
    protected void correctGraph(Collection<Node> toCorrect) {

        // boundary coordinates
        int minx, maxx, miny, maxy;

        // initialize boundary coordinates
        minx = maxx = size.width / 2;
        miny = maxy = size.height / 2;

        // for all nodes already place, check their locations
        for (Node vn : toCorrect) {
            Point2D p = locations.get(vn);
            if (p.getX() > maxx) {
                maxx = (int) p.getX();
            } else if (p.getX() < minx) {
                minx = (int) p.getX();
            }
            if (p.getY() > maxy) {
                maxy = (int) p.getY();
            } else if (p.getY() < miny) {
                miny = (int) p.getY();
            }
        }

        // replace nodes
        correctNode2PointMapping(toCorrect, -minx + DEFAULT_X_AXIS_DISTANCE, -miny + DEFAULT_Y_AXIS_DISTANCE);

        // set visualisation size
        size = new Dimension((maxx - minx) + 2 * DEFAULT_X_AXIS_DISTANCE, (maxy - miny) + 2 * DEFAULT_Y_AXIS_DISTANCE);

    }

}
