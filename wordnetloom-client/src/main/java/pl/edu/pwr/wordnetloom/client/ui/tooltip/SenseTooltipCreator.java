package pl.edu.pwr.wordnetloom.client.ui.tooltip;

import pl.edu.pwr.wordnetloom.client.model.*;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;

import javax.inject.Singleton;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class SenseTooltipCreator {

    final static ResourceBundle bundle = ResourceBundle.getBundle("default");
    final static String unit_label = bundle.getString("tooltip.unit");
    final static String lexicon_label = bundle.getString("tooltip.lexicon");
    final static String domain_label = bundle.getString("tooltip.domain");
    final static String part_of_speech_label = bundle.getString("tooltip.part_of_speech");
    final static String register_label = bundle.getString("tooltip.register");
    final static String definition_label = bundle.getString("tooltip.definition");
    final static String examples_label = bundle.getString("tooltip.examples");
    final static String synoyms_label = bundle.getString("tooltip.synonyms");

    public static String create(SearchListItem item, RemoteService remoteService){
        Sense sense = remoteService.findSense(item.getLinks().getSelf());
        return createText(sense, remoteService);
    }

    public static String create(Sense sense, RemoteService remoteService){
        Sense fullSense = remoteService.findSense(sense.getLinks().getSelf());
        return createText(fullSense, remoteService);
    }

    public static String create(UUID senseId, RemoteService remoteService){
        Sense sense = remoteService.findSense(senseId);
        return createText(sense, remoteService);
    }

    private static String createText(Sense sense, RemoteService remoteService){
        TooltipBuilder tooltipBuilder = new TooltipBuilder();

        createGeneralText(sense, tooltipBuilder);
        createSenseText(sense, remoteService, tooltipBuilder);
        createExamplesText(sense, remoteService, tooltipBuilder);

        return tooltipBuilder.toString();
    }

    private static void createExamplesText(Sense sense, RemoteService remoteService, TooltipBuilder tooltipBuilder) {
        final List<Example> examplesList = remoteService.getExamples(sense.getLinks().getExamples());
        if(examplesList != null && !examplesList.isEmpty()) {
            tooltipBuilder.setLevel(0);
            tooltipBuilder.addTitle(examples_label).
                    setLevel(1);
            for(Example example : examplesList){
                tooltipBuilder.addValue(example.getExample());
            }
        }
    }

    private static void createSenseText(Sense sense, RemoteService remoteService, TooltipBuilder tooltipBuilder) {
        if(sense.getLinks().getSynset() != null) {
            Synset synset = remoteService.findSynset(sense.getLinks().getSynset());
            if (synset.getSenses().size() > 1) {
                tooltipBuilder.setLevel(0);
                tooltipBuilder.addTitle(synoyms_label);
                tooltipBuilder.setLevel(1);
                for (int i = 0; i < synset.getSenses().size(); i++) {
                    Sense senseSynset = remoteService.findSense(synset.getSenses().get(i).getLinks().getSelf());
                    tooltipBuilder.addValue(senseSynset.getLemma() + " " + senseSynset.getVariant());
                }
            }
        }
    }

    private static void createGeneralText(Sense sense, TooltipBuilder tooltipBuilder) {
        final String lexicon_value = Dictionaries.getDictionaryItemById(Dictionaries.LEXICON_DICTIONARY, sense.getLexicon());
        final String domain_value = Dictionaries.getDictionaryItemById(Dictionaries.DOMAIN_DICTIONARY, sense.getDomain());
        final String part_of_speech_value = Dictionaries.getDictionaryItemById(Dictionaries.PART_OF_SPEECH_DICTIONARY, sense.getPartOfSpeech());
        final String register_value = Dictionaries.getDictionaryItemById(Dictionaries.REGISTER_DICTIONARY, sense.getRegister());

        tooltipBuilder.add(null, sense.getLemma() + " " + sense.getVariant())
                .add(lexicon_label, lexicon_value)
                .add(part_of_speech_label, part_of_speech_value)
                .add(domain_label, domain_value)
                .add(register_label, register_value, register_value != null)
                .add(definition_label, sense.getDefinition(), sense.getDefinition() != null && !sense.getDefinition().isEmpty());
    }

}
