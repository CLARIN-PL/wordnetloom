package pl.edu.pwr.wordnetloom.client.ui.tooltip;

import pl.edu.pwr.wordnetloom.client.model.*;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;

import java.util.List;
import java.util.ResourceBundle;

public class SynsetTooltipCreator {

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("default");
    private final static String synset_id_label = resourceBundle.getString("tooltip.synset_id");
    private final static String synset_label = resourceBundle.getString("tooltip.synset");
    private final static String lexicon_label = resourceBundle.getString("tooltip.lexicon");

    private final static String definition_label = resourceBundle.getString("tooltip.definition");
    private final static String status_label = resourceBundle.getString("tooltip.status");
    private final static String owner_label = resourceBundle.getString("tooltip.owner");
    private final static String examples_label = resourceBundle.getString("tooltip.examples");
    private final static String incoming_relations_label = resourceBundle.getString("tooltip.incoming");
    private final static String outgoing_relations_label = resourceBundle.getString("tooltip.outgoing");
    private final static String relations_label = resourceBundle.getString("tooltip.relations");
    private final static String abstract_label = resourceBundle.getString("tooltip.abstract");
    private final static String yes_label = resourceBundle.getString("common.yes");

    public static String create(SearchListItem item, RemoteService remoteService) {
        Synset synset = remoteService.findSynset(item.getLinks().getSelf());
        SenseRelations synsetRelations = remoteService.findSenseRelations(synset.getLinks().getRelations());
        List<Example> examples = remoteService.findExamples(synset.getLinks().getExamples());

        TooltipBuilder tooltipBuilder = new TooltipBuilder();

        appendGeneral(synset, tooltipBuilder);
        if(!examples.isEmpty()){
            appendExamples(examples, tooltipBuilder);
        }
        if(synsetRelations != null){
            appendRelations(synsetRelations, tooltipBuilder);
        }

        return tooltipBuilder.toString();

    }

    private static void appendRelations(SenseRelations synsetRelations, TooltipBuilder tooltipBuilder) {
        tooltipBuilder.addNewLine()
                .addTitle(relations_label);

        if(synsetRelations.getIncoming() !=null && !synsetRelations.getIncoming().isEmpty()){
            appendRelation(incoming_relations_label, synsetRelations.getIncoming(), tooltipBuilder);
        }
        if(synsetRelations.getOutgoing() !=null &&!synsetRelations.getOutgoing().isEmpty()){
            appendRelation(outgoing_relations_label, synsetRelations.getOutgoing(), tooltipBuilder);
        }
    }

    private static void appendRelation(String relations_label,List<RelationTree> relations, TooltipBuilder tooltipBuilder ){
        tooltipBuilder.setLevel(0)
                .addTitle(relations_label);
        relations.forEach(relationTree -> {
            tooltipBuilder.setLevel(1)
                    .addTitle(relationTree.getRelationTypeName());
            relationTree.getRows().forEach(relation ->
                    tooltipBuilder.setLevel(2)
                            .addValue(relation.getLabel()));
        });
    }

    private static void appendExamples(List<Example> examples, TooltipBuilder tooltipBuilder) {
        tooltipBuilder.addNewLine()
                .addTitle(examples_label);
        examples.stream().map(Example::getExample).forEach(tooltipBuilder::addValue);
    }

    private static void appendGeneral(Synset synset, TooltipBuilder tooltipBuilder) {
        String sensesLabel = getSensesLabel(synset.getSenses());
        String lexicon = Dictionaries.getDictionaryItemById(Dictionaries.LEXICON_DICTIONARY, synset.getLexicon());
        String status = Dictionaries.getDictionaryItemById(Dictionaries.STATUS_DICTIONARY, synset.getStatus());

        tooltipBuilder.add(synset_id_label, synset.getId().toString())
                .add(synset_label, sensesLabel)
                .add(lexicon_label, lexicon)
                .add(status_label, status)
                .add(owner_label, synset.getOwner())
                .add(definition_label, synset.getDefinition(), synset.getDefinition() != null && ! synset.getDefinition().isEmpty())
                .add(abstract_label, yes_label, synset.getAbstract());
    }

    private static String getSensesLabel(List<Sense> senses) {
        StringBuilder labelBuilder = new StringBuilder();
        for(Sense sense : senses){
            labelBuilder.append(sense.getLabel());
            labelBuilder.append("|");
        }
        labelBuilder.delete(labelBuilder.length()-1, labelBuilder.length());
        return labelBuilder.toString();
    }
}
