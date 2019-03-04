package pl.edu.pwr.wordnetloom.client.ui.relationtypes;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import pl.edu.pwr.wordnetloom.client.model.RelationArgument;
import pl.edu.pwr.wordnetloom.client.model.RelationType;
import pl.edu.pwr.wordnetloom.client.service.RelationTypeService;
import pl.edu.pwr.wordnetloom.client.ui.scopes.RelationTypeDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.senserelations.TreeItemObject;
import pl.edu.pwr.wordnetloom.client.ui.senserelations.TreeItemType;

import java.util.ArrayList;
import java.util.List;

public class RelationTypesViewModel implements ViewModel {

    private List<TreeItem<TreeItemObject>> senseRelations = new ArrayList<>();
    private List<TreeItem<TreeItemObject>> synsetRelations = new ArrayList<>();
    private ObjectProperty<TreeItem<TreeItemObject>> selectedSenseTreeListItem = new SimpleObjectProperty<>();
    private ObjectProperty<TreeItem<TreeItemObject>> selectedSynsetTreeListItem = new SimpleObjectProperty<>();
    private ObjectProperty<MultipleSelectionModel<TreeItem>> selectedSynsetTreeModel = new SimpleObjectProperty<>();
    private ObjectProperty<MultipleSelectionModel<TreeItem>> selectedSenseTreeModel = new SimpleObjectProperty<>();

    private ObjectProperty<TreeItem<TreeItemObject>> synsetRoot = new SimpleObjectProperty<>(new TreeItem<>(new TreeItemObject("relations", null, TreeItemType.ROOT)));

    private ObjectProperty<TreeItem<TreeItemObject>> senseRoot = new SimpleObjectProperty<>(new TreeItem<>(new TreeItemObject("relations", null, TreeItemType.ROOT)));


    @InjectScope
    RelationTypeDialogScope relationTypeDialogScope;

    public void initialize() {
        senseRoot.get().setExpanded(true);
        synsetRoot.get().setExpanded(true);

        selectedSynsetTreeListItem.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                RelationType rt = (RelationType) newValue.getValue().getItem();
                relationTypeDialogScope.setRelationTypeToEdit(rt);
                relationTypeDialogScope.publish(RelationTypeDialogScope.SHOW_RELATION);
            }
        });

        selectedSenseTreeListItem.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                RelationType rt = (RelationType) newValue.getValue().getItem();
                relationTypeDialogScope.setRelationTypeToEdit(rt);
            }
        });

        relationTypeDialogScope.subscribe(RelationTypeDialogScope.RELOAD_RELATIONS, (s, objects) -> {
            synsetRelations.clear();
            senseRelations.clear();
            RelationTypeService.initializeRelationTypes();
            loadRelations();
            if(objects != null && objects.length > 0) {
                RelationType rt = (RelationType) objects[0];
                TreeItem<TreeItemObject> name = new TreeItem<>(new TreeItemObject(rt.getName(), rt, TreeItemType.RELATION_TYPE));
                if (rt.getArgument().equals(RelationArgument.SYNSET_RELATION)) {
                    selectedSynsetTreeModel.get().select(name);
                } else if (rt.getArgument().equals(RelationArgument.SENSE_RELATION)) {
                    selectedSenseTreeModel.get().select(name);
                }
            }
        });

        loadRelations();
    }

    private void loadRelations() {
        Platform.runLater(() -> {
            RelationTypeService.getTopSynsetRelationTypes()
                    .forEach(rt -> {
                        TreeItem<TreeItemObject> name = new TreeItem<>(new TreeItemObject(rt.getName(), rt, TreeItemType.RELATION_TYPE));
                        if (rt.getSubrelations() != null) {
                            rt.getSubrelations().forEach(srt -> {
                                TreeItem<TreeItemObject> n = new TreeItem<>(new TreeItemObject(srt.getName(), srt, TreeItemType.RELATION_TYPE));
                                name.getChildren().add(n);
                            });
                        }
                        synsetRelations.add(name);
                    });

            RelationTypeService.getTopSenseRelationTypes()
                    .forEach(rt -> {
                        TreeItem<TreeItemObject> name = new TreeItem<>(new TreeItemObject(rt.getName(), rt, TreeItemType.RELATION_TYPE));
                        if (rt.getSubrelations() != null) {
                            rt.getSubrelations().forEach(srt -> {
                                TreeItem<TreeItemObject> n = new TreeItem<>(new TreeItemObject(srt.getName(), srt, TreeItemType.RELATION_TYPE));
                                name.getChildren().add(n);
                            });
                        }
                        senseRelations.add(name);
                    });
            synsetRoot.get().getChildren().clear();
            senseRoot.get().getChildren().clear();
            synsetRoot.get().getChildren().addAll(synsetRelations);
            senseRoot.get().getChildren().addAll(senseRelations);
        });

    }

    public ObjectProperty<TreeItem<TreeItemObject>> selectedSenseTreeItemProperty() {
        return selectedSenseTreeListItem;
    }

    public ObjectProperty<TreeItem<TreeItemObject>> selectedSynsetTreeItemProperty() {
        return selectedSynsetTreeListItem;
    }

    public TreeItem<TreeItemObject> getSynsetRoot() {
        return synsetRoot.get();
    }

    public ObjectProperty<TreeItem<TreeItemObject>> synsetRootProperty() {
        return synsetRoot;
    }

    public TreeItem<TreeItemObject> getSenseRoot() {
        return senseRoot.get();
    }

    public ObjectProperty<TreeItem<TreeItemObject>> senseRootProperty() {
        return senseRoot;
    }

    public ObjectProperty<MultipleSelectionModel<TreeItem>> selectedSynsetTreeModelProperty() {
        return selectedSynsetTreeModel;
    }

    public ObjectProperty<MultipleSelectionModel<TreeItem>> selectedSenseTreeModelProperty() {
        return selectedSenseTreeModel;
    }
}

