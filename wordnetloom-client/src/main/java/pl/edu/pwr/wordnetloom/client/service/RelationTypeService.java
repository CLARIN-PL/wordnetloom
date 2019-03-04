package pl.edu.pwr.wordnetloom.client.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.model.RelationArgument;
import pl.edu.pwr.wordnetloom.client.model.RelationType;
import pl.edu.pwr.wordnetloom.client.model.RelationTypes;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.*;

@Singleton
public class RelationTypeService {

    private static RemoteService service;

    private static final ObservableList<RelationType> topSynsetRelationTypes = FXCollections.observableArrayList();
    private static final ObservableList<RelationType> topSenseRelationTypes = FXCollections.observableArrayList();
    private static final ObservableList<RelationType> synsetRelationTypes = FXCollections.observableArrayList();
    private static final ObservableList<RelationType> senseRelationTypes = FXCollections.observableArrayList();
    private static final ObservableList<RelationType> allTopRelationTypes = FXCollections.observableArrayList();
    private static final ObservableList<RelationType> allRelationTypes = FXCollections.observableArrayList();

    public static ObservableList<RelationType> getSynsetRelationTypes() {
        return synsetRelationTypes;
    }

    public static Optional<RelationType> getSynsetRelationTypeById(UUID id) {
        return synsetRelationTypes.stream()
                .filter(rt -> rt.getId().equals(id))
                .findFirst();
    }

    public static Optional<RelationType> getSynsetRelationTypeByShortName(String sname) {
        return synsetRelationTypes.stream()
                .filter(rt -> rt.getShortName().equals(sname))
                .findFirst();
    }

    public static Optional<RelationType> getSenseRelationType(UUID id) {
        return senseRelationTypes.stream()
                .filter(rt -> rt.getId().equals(id))
                .findFirst();
    }

    public static ObservableList<RelationType> getSenseRelationTypes() {
        return senseRelationTypes;
    }

    public static ObservableList<RelationType> getTopSynsetRelationTypes() {
        return topSynsetRelationTypes;
    }

    public static ObservableList<RelationType> getTopRelations() {
        return allTopRelationTypes;
    }

    public static ObservableList<RelationType> getAllRelations() {
        return allRelationTypes;
    }

    public static ObservableList<RelationType> getTopSenseRelationTypes() {
        return topSenseRelationTypes;
    }

    public void setService(RemoteService service) {
        this.service = service;
    }


    public static void initializeRelationTypes(){
        topSynsetRelationTypes.clear();
        topSenseRelationTypes.clear();
        synsetRelationTypes.clear();
        senseRelationTypes.clear();
        allTopRelationTypes.clear();
        allRelationTypes.clear();

        RelationTypes allSynsetRelationTypes = service.findRelationTypes(RelationArgument.SYNSET_RELATION);
        RelationTypes allSenseRelationTypes = service.findRelationTypes(RelationArgument.SENSE_RELATION);

        List<RelationType> onlySynsetRelations = new ArrayList<>();
        List<RelationType> onlySenseRelations = new ArrayList<>();

        allSynsetRelationTypes.getRows().forEach(rt -> {
            rt.setArgument(RelationArgument.SYNSET_RELATION);
            topSynsetRelationTypes.add(rt);
            if (rt.getSubrelations() != null && !rt.getSubrelations().isEmpty()) {
                onlySynsetRelations.addAll(rt.getSubrelations());
            } else {
                onlySynsetRelations.add(rt);
            }
        });

        allSenseRelationTypes.getRows().forEach(rt -> {
            rt.setArgument(RelationArgument.SENSE_RELATION);
            topSenseRelationTypes.add(rt);
            if (rt.getSubrelations() != null && !rt.getSubrelations().isEmpty()) {
                onlySenseRelations.addAll(rt.getSubrelations());
            } else {
                onlySenseRelations.add(rt);
            }
        });

        onlySenseRelations.sort(Comparator.comparing(RelationType::getName));
        onlySynsetRelations.sort(Comparator.comparing(RelationType::getName));

        synsetRelationTypes.addAll(onlySynsetRelations);
        senseRelationTypes.addAll(onlySenseRelations);

        topSenseRelationTypes.sort(Comparator.comparing(RelationType::getName));
        topSynsetRelationTypes.sort(Comparator.comparing(RelationType::getName));

        allTopRelationTypes.addAll(topSynsetRelationTypes);
        allTopRelationTypes.addAll(topSenseRelationTypes);

        allRelationTypes.addAll(synsetRelationTypes);
        allRelationTypes.addAll(senseRelationTypes);

        allTopRelationTypes.sort(Comparator.comparing(RelationType::getName));
        allRelationTypes.sort(Comparator.comparing(RelationType::getName));
    }
}
