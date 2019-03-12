package pl.edu.pwr.wordnetloom.client.ui.synsetproperties;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import pl.edu.pwr.wordnetloom.client.events.ShowSenseRelationsEvent;
import pl.edu.pwr.wordnetloom.client.events.ShowSynsetPropertiesEvent;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.model.Example;
import pl.edu.pwr.wordnetloom.client.model.Sense;
import pl.edu.pwr.wordnetloom.client.model.Synset;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.alerts.AlertDialogHandler;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SensePropertiesDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SynonymyRelationDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SynsetPropertiesDialogScope;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class SynsetPropertiesViewModel implements ViewModel {

    public static final String OPEN_EDIT_SENSE_DIALOG = "open_edit_sense";
    public static final String OPEN_EDIT_SYNSET_DIALOG = "open_edit_synset";
    public static final String OPEN_ATTACH_SENSE_DIALOG = "open_attach_sense";

    private StringProperty synsetId = new SimpleStringProperty(this, "value");
    private StringProperty ili = new SimpleStringProperty(this, "value");
    private StringProperty princeton = new SimpleStringProperty(this, "value");

    private StringProperty lexicon = new SimpleStringProperty(this, "value");
    private StringProperty definition = new SimpleStringProperty(this, "value");
    private StringProperty comment = new SimpleStringProperty(this, "value");
    private StringProperty example = new SimpleStringProperty(this, "value");
    private StringProperty link = new SimpleStringProperty(this, "value");
    private BooleanProperty artificial = new SimpleBooleanProperty(this, "value");

    private StringProperty owner = new SimpleStringProperty(this, "value");
    private StringProperty status = new SimpleStringProperty(this, "value");
    private StringProperty technicalComment = new SimpleStringProperty(this, "value");

    private ObservableList<SenseListItemViewModel> senseList = FXCollections.observableArrayList();
    private ObjectProperty<SenseListItemViewModel> selectedSenseListItem = new SimpleObjectProperty<>();

    @Inject
    RemoteService service;

    @Inject
    private Event<ShowSenseRelationsEvent> showSenseRelations;

    @InjectScope
    SensePropertiesDialogScope sensePropertiesEditDialogScope;

    @InjectScope
    SynsetPropertiesDialogScope synsetPropertiesDialogScope;

    @InjectScope
    SynonymyRelationDialogScope synonymyRelationDialogScope;

    @Inject
    AlertDialogHandler alertDialogHandler;

    private Synset synset;
    private Sense selectedSense;

    private Command editSynsetCommand;
    private Command moveSenseUpCommand;
    private Command moveSenseDownCommand;
    private Command attachSenseCommand;
    private Command deAttachSenseCommand;
    private Command extractToNewSynsetWithRelationCommand;

    public void initialize() {

        editSynsetCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                synsetPropertiesDialogScope.setSynsetToEdit(synset);
                publish(OPEN_EDIT_SYNSET_DIALOG);
            }
        });

        moveSenseUpCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                moveSenseUp();
            }
        });

        moveSenseDownCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                moveSenseDown();
            }
        });

        attachSenseCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                attachSense();
            }
        });

        deAttachSenseCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                detachSense();
            }
        });

        extractToNewSynsetWithRelationCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                extractToNewSynsetWithRelation();
            }
        });

        selectedSenseListItem.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedSense = service.findSense(newValue.getSense().getLinks().getSelf());
                showSenseRelations.fire(new ShowSenseRelationsEvent(selectedSense));
            }
        });
    }

    private void extractToNewSynsetWithRelation() {

    }

    private void detachSense() {
        if (selectedSense != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure? You will detach sense from synset " +
                    selectedSenseListItem.get().getSense().getLabel()
                    + ".", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                selectedSense.getActions()
                        .stream().filter(act -> act.getName().equals("detach-from-synset"))
                        .findFirst()
                        .ifPresent(ac -> {
                            boolean success = service.executeAction(ac, Response.Status.OK);
                            if (success) {
                                senseList.remove(selectedSenseListItem.get());
                            }
                        });
            }
        }
    }

    private void attachSense() {
        synonymyRelationDialogScope.setSynset(this.synset);
        publish(OPEN_ATTACH_SENSE_DIALOG);
    }

    private void moveSenseDown() {
        if (selectedSense != null) {
            selectedSense.getActions()
                    .stream().filter(act -> act.getName().equals("move-down"))
                    .findFirst()
                    .ifPresent(ac -> {
                        boolean success = service.executeAction(ac, Response.Status.OK);
                        if (success) {
                            int current = senseList.indexOf(selectedSenseListItem.get());
                            if (current >= 0 && current < senseList.size() - 1) {
                                SenseListItemViewModel slivm = senseList.get(current + 1);
                                senseList.set(current, slivm);
                                senseList.set(current + 1, selectedSenseListItem.get());

                            }
                        }
                    });
        }
    }

    private void moveSenseUp() {
        if (selectedSense != null) {
            selectedSense.getActions()
                    .stream().filter(act -> act.getName().equals("move-up"))
                    .findFirst()
                    .ifPresent(ac -> {
                        boolean success = service.executeAction(ac, Response.Status.OK);
                        if (success) {
                            int current = senseList.indexOf(selectedSenseListItem.get());
                            if (current > 0) {
                                SenseListItemViewModel slivm = senseList.get(current - 1);
                                senseList.set(current - 1, selectedSenseListItem.get());
                                senseList.set(current, slivm);
                            }
                        }
                    });
        }
    }

    public void onShowSynsetProperties(@Observes ShowSynsetPropertiesEvent event) {
        this.synset = service.findSynset(event.getId());

        if (synset.getLinks().getExamples() != null) {
            synset.setExamples(fetchExamples(synset.getLinks().getExamples()));
        }
        synsetPropertiesDialogScope.setSynsetToEdit(synset);
        Platform.runLater(() -> initWithSynset(synset));
    }

    private List<Example> fetchExamples(URI link) {
        return service.findExamples(link);
    }

    private void initWithSynset(Synset synset) {
        senseList.clear();

        if (synset.getSenses() != null) {
            senseList.addAll(synset.getSenses()
                    .stream()
                    .map(sense -> new SenseListItemViewModel(sense, service))
                    .collect(Collectors.toList()));
        }

        if (synset.getId() != null) {
            synsetId.set(synset.getId().toString());
        }

        ili.set(synset.getIliId());
        princeton.set(synset.getPrincetonId());

        lexicon.set(Dictionaries.getDictionary(Dictionaries.LEXICON_DICTIONARY)
                .stream()
                .filter(d -> d.getId().equals(synset.getLexicon()))
                .map(Dictionary::getName).findFirst().orElse(""));
        artificial.set(synset.getAbstract());
        comment.set(synset.getComment());
        definition.set(synset.getDefinition());
        link.set(synset.getLink());

        example.set(examplesAsText(synset));

        owner.set(synset.getOwner());

        status.set(Dictionaries.getDictionary(Dictionaries.STATUS_DICTIONARY)
                .stream()
                .filter(d -> d.getId().equals(synset.getStatus()))
                .map(Dictionary::getName).findFirst().orElse(""));

        technicalComment.set(synset.getTechnicalComment());

    }

    public void showSenseProperties(URI senseId) {
        Sense sense = service.findSense(senseId);
        if (sense != null) {
            if (sense.getLinks().getExamples() != null) {
                sense.setExamples(fetchExamples(sense.getLinks().getExamples()));
            }
            sensePropertiesEditDialogScope.setSenseToEdit(sense);
            publish(OPEN_EDIT_SENSE_DIALOG);
        }
    }

    private String examplesAsText(Synset synset) {
        if (synset.getExamples() != null) {
            return synset.getExamples().stream()
                    .map(Example::getExample)
                    .collect(Collectors.joining("\n"));
        }
        return "";
    }

    public Property<String> lexiconProperty() {
        return lexicon;
    }

    public Property<String> definitionProperty() {
        return definition;
    }

    public Property<String> synsetIdProperty() {
        return synsetId;
    }

    public Property<String> ownerProperty() {
        return owner;
    }

    public Property<String> iliProperty() {
        return ili;
    }

    public Property<String> princetonProperty() {
        return princeton;
    }

    public Property<String> commentProperty() {
        return comment;
    }

    public Property<String> exampleProperty() {
        return example;
    }

    public BooleanProperty artificialProperty() {
        return artificial;
    }

    public Property<String> statusProperty() {
        return status;
    }

    public Property<String> technicalCommentProperty() {
        return technicalComment;
    }

    public ObservableList<SenseListItemViewModel> senseListProperty() {
        return senseList;
    }

    public ObjectProperty<SenseListItemViewModel> selectedSenseListItemProperty() {
        return selectedSenseListItem;
    }

    public Command editSynsetCommand() {
        return editSynsetCommand;
    }

    public Property<String> linkProperty() {
        return link;
    }

    public Command moveSenseUpCommand() {
        return moveSenseUpCommand;
    }

    public Command moveSenseDownCommand() {
        return moveSenseDownCommand;
    }

    public Command attachSenseCommand() {
        return attachSenseCommand;
    }

    public Command detachSenseCommand() {
        return deAttachSenseCommand;
    }

    public Command extractToNewSynsetWithRelationCommand() {
        return extractToNewSynsetWithRelationCommand;
    }

    public Property<Boolean> auxiliaryLabelVisible() {
        return artificial;
    }
}
