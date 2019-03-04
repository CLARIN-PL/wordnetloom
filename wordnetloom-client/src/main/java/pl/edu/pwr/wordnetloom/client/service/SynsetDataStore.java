package pl.edu.pwr.wordnetloom.client.service;

import pl.edu.pwr.wordnetloom.client.model.*;
import pl.edu.pwr.wordnetloom.client.ui.graph.visualisation.structure.NodeDirection;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class SynsetDataStore {

    @Inject
    RemoteService service;

    @Inject
    RelationTypeService relationTypeService;

    private Map<UUID, DataEntry> data;

    public SynsetDataStore() {
        data = new ConcurrentHashMap<>();
    }

    /**
     * Loading synset and his relations. For all relations of main synset will be loaded synsets with simple relations.
     * Relations will loaded for all directions (LEFT, RIGHT, BOTTOM, TOP)
     *
     * @param id synset id
     */
    public DataEntry load(UUID id) {
        NodeExpanded node = service.findSynsetGraph(id);
        return  loadNode(node);
    }

    /**
     * Loading synset and his relations. For all relations of main synset will be loaded synsets with simple relations.
     * Relations will loaded for all directions (LEFT, RIGHT, BOTTOM, TOP)
     *
     * @param link
     */
    public DataEntry load(URI link) {
        NodeExpanded node = service.findSynsetGraph(link);
        return  loadNode(node);
    }

    public DataEntry load(NodeExpanded node){
        return loadNode(node);
    }

    private DataEntry loadNode(NodeExpanded node ){
        DataEntry root = mapExpanded(node);
        root.setFullyLoaded(true);
        addData(root);

        node.getOptionalTop()
                .ifPresent(top -> {
                    mapDirectedNodes(root, top.getExpanded(), NodeDirection.TOP);
                    top.getOptionalHidden().ifPresent(hidden -> buildHidden(node, hidden, NodeDirection.TOP));
                });

        node.getOptionalBottom()
                .ifPresent(bottom -> {
                    mapDirectedNodes(root, bottom.getExpanded(), NodeDirection.BOTTOM);
                    bottom.getOptionalHidden().ifPresent(hidden -> buildHidden(node, hidden, NodeDirection.BOTTOM));
                });

        node.getOptionalLeft()
                .ifPresent(left -> {
                    mapDirectedNodes(root, left.getExpanded(), NodeDirection.LEFT);
                    left.getOptionalHidden().ifPresent(hidden -> buildHidden(node, hidden, NodeDirection.LEFT));
                });

        node.getOptionalRight()
                .ifPresent(right -> {
                    mapDirectedNodes(root, right.getExpanded(), NodeDirection.RIGHT);
                    right.getOptionalHidden().ifPresent(hidden -> buildHidden(node, hidden, NodeDirection.RIGHT));
                });
        return root;
    }

    public DataEntry getById(UUID id) {
        if (data.containsKey(id)) {
            return data.get(id);
        }
        return null;
    }

    public void setFullyLoaded(UUID id, boolean fullyLoaded){
        DataEntry entry = getById(id);
        if(entry != null){
            entry.setFullyLoaded(fullyLoaded);
        }
    }

    private void addData(DataEntry entry) {
        if (data.containsKey(entry.getSynsetId())) {
//            data.replace(entry.getSynsetId(), entry);
            // TODO: odkomentować to i sprawić by działało poprawnie
            // TODO: jakoś to pozmieniać, aby wierzchołki mogły być podmieniane na grafie
            if (!data.get(entry.getSynsetId()).isFullyLoaded() && entry.isFullyLoaded()) {
                data.replace(entry.getSynsetId(), entry);
            }
        } else {
            data.put(entry.getSynsetId(), entry);
        }
    }

    public void insertData(DataEntry entry){
//        if(data.containsKey(entry.getSynsetId())){
//            data.replace(entry.getSynsetId(), entry);
//        } else {
//            data.put(entry.getSynsetId(), entry);
//        }
        data.put(entry.getSynsetId(), entry);
    }

    private void mapDirectedNodes(DataEntry root, List<NodeExpanded> exp, NodeDirection dir) {
        exp.forEach(en -> {
            buildExpanded(root, en, dir);
            en.getOptionalTop().ifPresent(top -> buildHidden(en, top.getHidden(), NodeDirection.TOP));
            en.getOptionalBottom().ifPresent(bottom -> buildHidden(en, bottom.getHidden(), NodeDirection.BOTTOM));
            en.getOptionalLeft().ifPresent(left -> buildHidden(en, left.getHidden(), NodeDirection.LEFT));
            en.getOptionalRight().ifPresent(right -> buildHidden(en, right.getHidden(), NodeDirection.RIGHT));
        });
    }

    private void buildHidden(NodeExpanded ex, List<NodeHidden> hidden, NodeDirection dir) {
        hidden.forEach(hn -> {
            DataEntry eh = mapHidden(hn);
            addData(eh);
            hn.getOptionalRel().ifPresent(rel -> {
                SynsetRelation p = mapRelation(ex.getId(), hn.getId(), rel.get(0));
                SynsetRelation c = mapRelation(hn.getId(), ex.getId(), rel.get(1));
                data.get(p.getSource()).addRelation(p, dir);
                data.get(c.getSource()).addRelation(c, dir.getOpposite());
            });
        });
    }

    private void buildExpanded(DataEntry root, NodeExpanded en, NodeDirection dir) {
        DataEntry entry = mapExpanded(en);
        addData(entry);

        en.getOptionalRel().ifPresent(rel -> {
            SynsetRelation p = mapRelation(root.getSynsetId(), en.getId(), rel.get(0));
            SynsetRelation c = mapRelation(en.getId(), root.getSynsetId(), rel.get(1));
            data.get(p.getSource()).addRelation(p, dir);
            data.get(c.getSource()).addRelation(c, dir.getOpposite());
        });

    }

    private SynsetRelation mapRelation(UUID parentId, UUID childId, String rel) {
        AtomicReference<Color> defaultColor = new AtomicReference<>(Color.black);
        AtomicReference<SynsetRelation> relation = new AtomicReference<>(new SynsetRelation()
                .withSource(parentId)
                .withTarget(childId)
                .withColor(defaultColor.get()));

        if(rel == null) return relation.get();

       relationTypeService.getSynsetRelationTypeByShortName(rel)
               .ifPresent( rt -> {
                   defaultColor.set(Color.decode(rt.getColor()));
                   relation.set(new SynsetRelation()
                           .withSource(parentId)
                           .withTarget(childId)
                           .withColor(defaultColor.get())
                           .withRelationType(rt.getId()));
               });

        return relation.get();
    }

    private DataEntry mapExpanded(NodeExpanded node) {
        return new DataEntry()
                .withSynsetId(node.getId())
                .withLabel(node.getLabel())
                .withPartOfSpeech(node.getPos())
                .withLexicon(node.getLex());
    }

    private DataEntry mapHidden(NodeHidden node) {
        return new DataEntry()
                .withSynsetId(node.getId())
                .withLabel(node.getLabel())
                .withPartOfSpeech(node.getPos())
                .withLexicon(node.getLex());
    }
}
