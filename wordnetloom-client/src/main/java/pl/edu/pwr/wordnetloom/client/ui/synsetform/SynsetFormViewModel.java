package pl.edu.pwr.wordnetloom.client.ui.synsetform;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.itemlist.ItemList;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.events.UserLexiconUpdatedEvent;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.model.Example;
import pl.edu.pwr.wordnetloom.client.model.Synset;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.service.ValidationException;
import pl.edu.pwr.wordnetloom.client.ui.alerts.AlertDialogHandler;
import pl.edu.pwr.wordnetloom.client.ui.example.ExampleListItemViewModel;
import pl.edu.pwr.wordnetloom.client.ui.scopes.ExampleDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SensePropertiesDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SynsetPropertiesDialogScope;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.edu.pwr.wordnetloom.client.service.Dictionaries.LEXICON_DICTIONARY;
import static pl.edu.pwr.wordnetloom.client.service.Dictionaries.STATUS_DICTIONARY;

public class SynsetFormViewModel implements ViewModel {

    private static final String NOTHING_SELECTED_MARKER = "----------";
    public static final String OPEN_EXAMPLE_DIALOG = "open_synset_example_dialog";

    @InjectScope
    private ExampleDialogScope exampleDialogScope;

    @InjectScope
    private SynsetPropertiesDialogScope dialogScope;

    @Inject
    private RemoteService remoteService;

    @Inject
    AlertDialogHandler dialogHandler;

    private final StringProperty link = new SimpleStringProperty();
    private final StringProperty definition = new SimpleStringProperty();
    private final StringProperty comment = new SimpleStringProperty();
    private final StringProperty technicalComment = new SimpleStringProperty();
    private final StringProperty owner = new SimpleStringProperty();
    private final BooleanProperty artificial = new SimpleBooleanProperty();

    private ObservableList<String> statuses;
    private final ObjectProperty<Dictionary> status = new SimpleObjectProperty<>();
    private final StringProperty selectedStatus = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> lexicons;
    private final ObjectProperty<Dictionary> lexicon = new SimpleObjectProperty<>();
    private final StringProperty selectedLexicon = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ItemList<Dictionary> lexiconItemList;
    private ItemList<Dictionary> statusItemList;

    private ObservableList<ExampleListItemViewModel> exampleList = FXCollections.observableArrayList();
    private ObjectProperty<ExampleListItemViewModel> selectedExampleListItem = new SimpleObjectProperty<>();


    private Command editExampleCommand;
    private Command addExampleCommand;
    private Command removeExampleCommand;

    private Synset synset;


    public void initialize() {
        initStatusList();
        initLexiconList();

        dialogScope.subscribe(SynsetPropertiesDialogScope.RESET_FORMS, (key, payload) -> resetForm());
        dialogScope.subscribe(SynsetPropertiesDialogScope.COMMIT, (key, payload) -> commitChanges());

        ObjectProperty<Synset> synsetToEditProperty = dialogScope.synsetToEditProperty();
        if (synsetToEditProperty.get() != null) {
            initWithSynset(synsetToEditProperty.get());
        }

        selectedLexicon.addListener((obs, oldV, newV) -> {
            Dictionaries.dictionarySelected(obs, oldV, newV, Dictionaries.LEXICON_DICTIONARY, NOTHING_SELECTED_MARKER, lexicon);
        });

        selectedStatus.addListener((obs, oldV, newV) -> {
            Dictionaries.dictionarySelected(obs, oldV, newV, Dictionaries.STATUS_DICTIONARY, NOTHING_SELECTED_MARKER, status);
        });

        addExampleCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                addExample();
            }
        });
        editExampleCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                editExample();
            }
        });
        removeExampleCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                removeExample();
            }
        });

        exampleDialogScope.subscribe(ExampleDialogScope.AFTER_COMMIT, (s, objects) -> {

            if (exampleDialogScope.isSynsetExample()) {
                Example e = exampleDialogScope.getExampleToEdit();
                if (e.getExample() != null && !e.getExample().isEmpty()) {
                    Set<Example> examplesList = exampleList.stream()
                            .map(ExampleListItemViewModel::getExample)
                            .collect(Collectors.toSet());

                    if (e.getLinks().getSelf() != null) {
                        updateExample(e, examplesList);
                    } else {
                        addExample(e, examplesList);
                    }
                    exampleList.clear();
                    examplesList.forEach(z -> exampleList.add(new ExampleListItemViewModel(z)));
                    exampleDialogScope.publish(ExampleDialogScope.RESET_FORMS);
                    exampleDialogScope.publish(ExampleDialogScope.CLOSE);
                }
            }

        });
    }

    private void addExample(Example e, Set<Example> examplesList) {
        if (synset.getId() != null) {
            try {
                Example savedExample = remoteService.addExample(synset.getLinks().getExamples(), e);
                examplesList.add(savedExample);
            } catch (Exception e1) {
                dialogHandler.handleErrors(e1);
            }
        }
    }

    private void updateExample(Example e, Set<Example> examplesList) {
        try {
            remoteService.updateExample(e);
        } catch (Exception e1) {
            dialogHandler.handleErrors(e1);
        }

        examplesList.stream()
                .filter(egz -> egz.getLinks().getSelf().equals(e.getLinks().getSelf()))
                .findFirst().ifPresent(ee -> {
            ee.setExample(e.getExample());
            ee.setType(e.getType());
        });
    }

    private void removeExample() {
        if (selectedExampleListItem.get() != null) {
            Example example = selectedExampleListItem.get().getExample();
            exampleList.remove(selectedExampleListItem.get());
            remoteService.delete(example.getLinks().getSelf());
        }
    }

    private void editExample() {
        if (selectedExampleListItem.get() != null) {
            publish(OPEN_EXAMPLE_DIALOG);
            exampleDialogScope.setExampleToEdit(selectedExampleListItem.get().getExample());
            exampleDialogScope.setSynsetExample(true);
            exampleDialogScope.publish(ExampleDialogScope.ON_LOAD);
        }
    }

    private void addExample() {
        publish(OPEN_EXAMPLE_DIALOG);
        exampleDialogScope.setExampleToEdit(new Example());
        exampleDialogScope.setSynsetExample(true);
        exampleDialogScope.publish(ExampleDialogScope.ON_LOAD, "synset");
    }

    public void initWithSynset(Synset synset) {
        this.synset = synset;

        artificial.set(synset.getAbstract());
        comment.set(synset.getComment());
        definition.set(synset.getDefinition());
        owner.set(synset.getOwner());
        link.set(synset.getLink());
        technicalComment.set(synset.getTechnicalComment());

        if (synset.getLexicon() != null) {
            Dictionary lexiconDictionary = Dictionaries.getDictionaryById(LEXICON_DICTIONARY, synset.getLexicon());
            lexicon.set(lexiconDictionary);
            selectedLexicon.set(Dictionaries.getDictionaryItemById(LEXICON_DICTIONARY, synset.getLexicon()));
        }

        if (synset.getStatus() != null) {
            Dictionary statusDictionary = Dictionaries.getDictionaryById(STATUS_DICTIONARY, synset.getStatus());
            status.set(statusDictionary);
            selectedStatus.set(Dictionaries.getDictionaryItemById(STATUS_DICTIONARY, synset.getStatus()));
        }

        exampleList.clear();
        if (synset.getExamples() != null) {
            synset.getExamples().forEach(e -> exampleList.add(new ExampleListItemViewModel(e)));
        }
    }

    private void commitChanges() {
        synset = dialogScope.getSynsetToEdit();
        synset.setDefinition(definition.get());
        synset.setComment(comment.get());
        synset.setTechnicalComment(technicalComment.get());
        synset.setOwner(owner.get());
        synset.setStatus(status.get() != null ? status.get().getId() : null);
        synset.setLexicon(lexicon.get() != null ? lexicon.get().getId() : null);

        commitExamples();

        synset.setAbstract(artificial.get());
        synset.setLink(link.get());
        dialogScope.setSynsetToEdit(synset);
    }


    public void commitExamples() {
        List<Example> examples = new ArrayList<>();
        exampleList.forEach(example -> examples.add(example.getExample()));
        synset.setExamples(examples);
    }

    private void resetForm() {
    }

    public void onUserLexiconLiseUpdated(@Observes(notifyObserver = Reception.ALWAYS) UserLexiconUpdatedEvent event) {
        initLexiconList();
    }

    private void initLexiconList() {
        lexiconItemList = Dictionaries.initLexiconDictionaryUserChosenItemList();
        ObservableList<String> mappedList = lexiconItemList.getTargetList();
        lexicons = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        lexicons.addListener((ListChangeListener<String>) p -> selectedLexicon.set(NOTHING_SELECTED_MARKER));
    }

    private void initStatusList() {
        statusItemList = Dictionaries.initDictionaryItemList(Dictionaries.STATUS_DICTIONARY);
        ObservableList<String> mappedList = statusItemList.getTargetList();
        statuses = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        statuses.addListener((ListChangeListener<String>) p -> selectedStatus.set(NOTHING_SELECTED_MARKER));
    }

    public Property<String> linkProperty() {
        return link;
    }

    public Property<String> definitionProperty() {
        return definition;
    }

    public Property<String> commentProperty() {
        return comment;
    }

    public Property<String> technicalCommentProperty() {
        return technicalComment;
    }

    public ObservableList<String> lexiconList() {
        return lexicons;
    }

    public Property<String> selectedLexiconProperty() {
        return selectedLexicon;
    }

    public ObservableList<String> statusList() {
        return statuses;
    }

    public Property<String> selectedStatusProperty() {
        return selectedStatus;
    }

    public ObservableList<ExampleListItemViewModel> exampleListProperty() {
        return exampleList;
    }

    public ObjectProperty<ExampleListItemViewModel> selectedExampleListItemProperty() {
        return selectedExampleListItem;
    }

    public Property<Boolean> artificialProperty() {
        return artificial;
    }

    public Property<String> ownerProperty() {
        return owner;
    }

    public Command editExampleCommand() {
        return editExampleCommand;
    }

    public Command addExampleCommand() {
        return addExampleCommand;
    }

    public Command removeExampleCommand() {
        return removeExampleCommand;
    }
}

