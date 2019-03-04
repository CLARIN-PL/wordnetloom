package pl.edu.pwr.wordnetloom.client.ui.search;

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
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import pl.edu.pwr.wordnetloom.client.events.LoadGraphEvent;
import pl.edu.pwr.wordnetloom.client.events.UpdateSearchItemSynsetEvent;
import pl.edu.pwr.wordnetloom.client.events.UserLexiconUpdatedEvent;
import pl.edu.pwr.wordnetloom.client.model.*;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RelationTypeService;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SensePropertiesDialogScope;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SearchViewModel implements ViewModel {

    public static final String OPEN_ADD_SENSE_DIALOG = "open_add_sense_dialog";

    @Inject
    RemoteService remoteService;

    private static final String PART_OF_SPEECH_NOTHING_SELECTED_MARKER = "Part of speech";
    private static final String DOMAIN_NOTHING_SELECTED_MARKER = "Domain";
    private static final String STATUS_NOTHING_SELECTED_MARKER = "Status";
    private static final String REGISTER_NOTHING_SELECTED_MARKER = "Register";
    private static final String LEXICON_NOTHING_SELECTED_MARKER = "Lexicon";
    private static final String SENSE_RELATION_TYPE_NOTHING_SELECTED_MARKER = "Sense Relation Type";
    private static final String SYNSET_RELATION_TYPE_NOTHING_SELECTED_MARKER = "Synset Relation Type";

    private final StringProperty fieldSearch = new SimpleStringProperty();
    private final StringProperty definition = new SimpleStringProperty();
    private final StringProperty comment = new SimpleStringProperty();
    private final StringProperty example = new SimpleStringProperty();
    private final StringProperty synsetId = new SimpleStringProperty();
    private final BooleanProperty progressOverlay = new SimpleBooleanProperty();
    private final StringProperty unitsCount = new SimpleStringProperty();

    private final Property<Boolean> selectedSenseMode = new SimpleBooleanProperty();
    private final Property<Boolean> selectedSynsetMode = new SimpleBooleanProperty();

    private final BooleanProperty senseOnlyWithoutSynset = new SimpleBooleanProperty();

    private ObservableList<String> partsOfSpeech;
    private final ObjectProperty<Dictionary> partOfSpeech = new SimpleObjectProperty<>();
    private final StringProperty selectedPartOfSpeech = new SimpleStringProperty(PART_OF_SPEECH_NOTHING_SELECTED_MARKER);

    private ObservableList<String> domains;
    private final ObjectProperty<Dictionary> domain = new SimpleObjectProperty<>();
    private final StringProperty selectedDomain = new SimpleStringProperty(DOMAIN_NOTHING_SELECTED_MARKER);

    private ObservableList<String> statuses;
    private final ObjectProperty<Dictionary> status = new SimpleObjectProperty<>();
    private final StringProperty selectedStatus = new SimpleStringProperty(STATUS_NOTHING_SELECTED_MARKER);

    private ObservableList<String> registers;
    private final ObjectProperty<Dictionary> register = new SimpleObjectProperty<>();
    private final StringProperty selectedRegister = new SimpleStringProperty(REGISTER_NOTHING_SELECTED_MARKER);

    private ObservableList<String> lexicons;
    private final ObjectProperty<Dictionary> lexicon = new SimpleObjectProperty<>();
    private final StringProperty selectedLexicon = new SimpleStringProperty(LEXICON_NOTHING_SELECTED_MARKER);

    private ObservableList<String> senseRelationsTypes;
    private final ObjectProperty<RelationType> senseRelationType = new SimpleObjectProperty<>();
    private final StringProperty selectedSenseRelation = new SimpleStringProperty(SENSE_RELATION_TYPE_NOTHING_SELECTED_MARKER);

    private ObservableList<String> synsetRelationsTypes;
    private final ObjectProperty<RelationType> synsetRelationType = new SimpleObjectProperty<>();
    private final StringProperty selectedSynsetRelation = new SimpleStringProperty(SYNSET_RELATION_TYPE_NOTHING_SELECTED_MARKER);


    // Don't inline this field. It's needed to prevent the list mapping from being garbage collected.
    private ItemList<Dictionary> lexiconItemList;
    private ItemList<Dictionary> partOfSpeechItemList;
    private ItemList<Dictionary> domainItemList;
    private ItemList<Dictionary> statusItemList;
    private ItemList<Dictionary> registerItemList;
    private ItemList<RelationType> synsetRelationTypeItemList;
    private ItemList<RelationType> senseRelationTypeItemList;

    private SearchFilter filter = new SearchFilter();

    private ObservableList<SearchListItemViewModel> searchList = FXCollections.observableArrayList();
    private ObjectProperty<SearchListItemViewModel> selectedSearchListItem = new SimpleObjectProperty<>();

    private Command scrollCommand;
    private Command searchCommand;
    private Command resetCommand;
    private Command addSenseWithSynsetCommand;
    private Command addSenseCommand;
    private Command deleteSenseCommand;
    private Command addSenseToNewSynsetCommand;

    private final int SEARCH_LIMIT = 100;
    private int searchLimit = SEARCH_LIMIT;

    private Thread loadingThread = new Thread();
    private Task listLoader;

    public Command getScrollCommand(int start, int limit) {
        filter.setStart(start);
        filter.setLimit(limit);
        return scrollCommand;
    }

    public Command getSearchCommand(int limit) {
        filter.setLimit(limit);
        return searchCommand;
    }

    public void clearList(){
        searchList.clear();
    }

    public Command getSearchCommand() {
        return searchCommand;
    }

    public Command getResetCommand() {
        return resetCommand;
    }

    public Command getAddSenseWithSynsetCommand() {
        return addSenseWithSynsetCommand;
    }

    public Command getAddSenseCommand() {
        return addSenseCommand;
    }

    public Command deleteSenseCommand() {
        return deleteSenseCommand;
    }

    public Command addSenseToNewSynsetCommand() {
        return addSenseToNewSynsetCommand;
    }

    @Inject
    public Event<LoadGraphEvent> loadGraphEvent;

    @InjectScope
    SensePropertiesDialogScope sensePropertiesDialogScope;

    public void initialize() {

        initPartsOfSpeechList();
        initDomainList();
        initStatusList();
        initRegisterList();
        initLexiconList();
        initSynsetRelationTypeList();
        initSenseRelationTypeList();

        scrollCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                SearchFilter filterCopy = filter.clone();
                load(filterCopy);
            }
        });
        searchCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                search();
            }
        });
        resetCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                reset();
            }
        });
        addSenseCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                addSense();
            }
        });
        addSenseWithSynsetCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                addSenseWithSynset();
            }
        });
        deleteSenseCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                deleteSense();
            }
        });

        addSenseToNewSynsetCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
               addSenseToNewSynset();
            }
        });

        progressOverlay.setValue(false);
        selectedSenseMode.setValue(true);

        selectedLexicon.addListener((obs, oldV, newV) -> {
            Dictionaries.dictionarySelected(obs, oldV, newV, Dictionaries.LEXICON_DICTIONARY, LEXICON_NOTHING_SELECTED_MARKER, lexicon);
        });

        selectedPartOfSpeech.addListener((obs, oldV, newV) -> {
            Dictionaries.dictionarySelected(obs, oldV, newV, Dictionaries.PART_OF_SPEECH_DICTIONARY, PART_OF_SPEECH_NOTHING_SELECTED_MARKER, partOfSpeech);
        });

        selectedDomain.addListener((obs, oldV, newV) -> {
            Dictionaries.dictionarySelected(obs, oldV, newV, Dictionaries.DOMAIN_DICTIONARY, DOMAIN_NOTHING_SELECTED_MARKER, domain);
        });

        selectedStatus.addListener((obs, oldV, newV) -> {
            Dictionaries.dictionarySelected(obs, oldV, newV, Dictionaries.STATUS_DICTIONARY, STATUS_NOTHING_SELECTED_MARKER, status);
        });

        selectedRegister.addListener((obs, oldV, newV) -> {
            Dictionaries.dictionarySelected(obs, oldV, newV, Dictionaries.REGISTER_DICTIONARY, REGISTER_NOTHING_SELECTED_MARKER, register);
        });


        selectedSynsetRelation.addListener((obs, oldV, newV) -> {
            if (newV != null && !newV.equals(SYNSET_RELATION_TYPE_NOTHING_SELECTED_MARKER)) {
                Optional<RelationType> matching = RelationTypeService.getSynsetRelationTypes()
                        .stream()
                        .filter(d -> newV.equals(d.getName()))
                        .findFirst();
                matching.ifPresent(synsetRelationType::set);
            } else if (SYNSET_RELATION_TYPE_NOTHING_SELECTED_MARKER.equals(newV)) {
                synsetRelationType.set(null);
                filter.setRelationTypeId(null);
            }
        });

        selectedSenseRelation.addListener((obs, oldV, newV) -> {
            if (newV != null && !newV.equals(SENSE_RELATION_TYPE_NOTHING_SELECTED_MARKER)) {
                Optional<RelationType> matching = RelationTypeService.getSenseRelationTypes()
                        .stream()
                        .filter(d -> newV.equals(d.getName()))
                        .findFirst();
                matching.ifPresent(senseRelationType::set);
            } else if (SENSE_RELATION_TYPE_NOTHING_SELECTED_MARKER.equals(newV)) {
                senseRelationType.set(null);
                filter.setRelationTypeId(null);
            }
        });

        selectedSearchListItem.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadGraphEvent.fireAsync(new LoadGraphEvent(newValue.getSearchListItem().getLinks().getGraph()));
            }
        });

    }

    public void onUpdateSearchItemSynset(@Observes UpdateSearchItemSynsetEvent event) {
        Synset s = remoteService.findSynset(event.getSynset());
        refreshItemOnList(event.getSense(), s);
    }

    private void refreshItemOnList(UUID sense, Synset synset) {
        searchList.stream()
                .filter(i -> i.getSearchListItem().getLinks().getSelf().toString().contains(sense.toString()))
                .findFirst()
                .ifPresent(item -> {
                    item.getSearchListItem().getLinks().setGraph(synset.getLinks().getGraph());
                    item.getSearchListItem().setSynset(synset.getId());
                });
    }

    public void setSearchLimit(int limit) {
        searchLimit = limit;
    }

    private void addSenseToNewSynset() throws IOException {
        SearchListItemViewModel model = selectedSearchListItemProperty().get();
        Sense sense = remoteService.findSense(model.getSearchListItem().getLinks().getSelf());
        Synset synset = remoteService.addSenseToNewSynset(sense.getId());
        refreshItemOnList(sense.getId(), synset);
    }

    private void deleteSense() {
        SearchListItemViewModel model = selectedSearchListItemProperty().get();
        if (selectedSearchListItemProperty().get() != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure? You will remove item " + model.getSearchListItem().getLabel()
                    + " and all his relations.", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                remoteService.delete(model.getSearchListItem().getLinks().getSelf());
                searchList.remove(model);
            }
        }
    }

    private void addSenseWithSynset() {
        Sense s = new Sense();
        s.setCreateSynset(true);
        sensePropertiesDialogScope.setSenseToEdit(s);
        sensePropertiesDialogScope.setSensePropertiesTitle("Create Sense with Synset");
        publish(OPEN_ADD_SENSE_DIALOG);
    }

    private void addSense() {
        sensePropertiesDialogScope.setSenseToEdit(new Sense());
        sensePropertiesDialogScope.setSensePropertiesTitle("Create Sense");
        publish(OPEN_ADD_SENSE_DIALOG);
    }

    private void commitChanges() {
        filter.setLemma(fieldSearch.get());
        filter.setSenseMode(selectedSenseMode.getValue());
        filter.setSynsetMode(selectedSynsetMode.getValue());

        if (selectedLexicon.get() != null && !LEXICON_NOTHING_SELECTED_MARKER.equals(selectedLexicon.get())) {
            filter.setLexicon(lexicon.get().getId());
        } else {
            filter.setLexicon(null);
        }
        if (selectedPartOfSpeech.get() != null && !PART_OF_SPEECH_NOTHING_SELECTED_MARKER.equals(selectedPartOfSpeech.get())) {
            filter.setPartOfSpeechId(partOfSpeech.get().getId());
        } else {
            filter.setPartOfSpeechId(null);
        }

        if (selectedDomain.get() != null && !DOMAIN_NOTHING_SELECTED_MARKER.equals(selectedDomain.get())) {
            filter.setDomainId(domain.get().getId());
        } else {
            filter.setDomainId(null);
        }

        if (selectedStatus.get() != null && !STATUS_NOTHING_SELECTED_MARKER.equals(selectedStatus.get())) {
            filter.setStatusId(status.get().getId());
        } else {
            filter.setStatusId(null);
        }

        if (selectedRegister.get() != null && !REGISTER_NOTHING_SELECTED_MARKER.equals(selectedRegister.get())) {
            filter.setRegisterId(register.get().getId());
        } else {
            filter.setRegisterId(null);
        }

        if (synsetId.get() != null &&
                !synsetId.get().isEmpty()) {
            filter.setSynsetId(UUID.fromString(synsetId.get()));
        }
        if (definition.get() != null &&
                !definition.get().isEmpty()) {
            filter.setDefinition(definition.getValue());
        } else {
            filter.setDefinition(null);
        }
        if (comment.get() != null &&
                !comment.get().isEmpty()) {
            filter.setComment(comment.getValue());
        } else {
            filter.setComment(null);
        }
        if (example.get() != null &&
                !example.get().isEmpty()) {
            filter.setComment(example.getValue());
        } else {
            filter.setExample(null);
        }

        if (senseOnlyWithoutSynset.get()) {
            filter.setSensesWithoutSynset(true);
        } else {
            filter.setSensesWithoutSynset(false);
        }

        if (selectedSenseRelation.get() != null && !SENSE_RELATION_TYPE_NOTHING_SELECTED_MARKER.equals(selectedSenseRelation.get())) {
            filter.setRelationTypeId(senseRelationType.get().getId());
        } else if (selectedSynsetRelation.get() != null && !SYNSET_RELATION_TYPE_NOTHING_SELECTED_MARKER.equals(selectedSynsetRelation.get())) {
            filter.setRelationTypeId(synsetRelationType.get().getId());
        } else {
            filter.setRelationTypeId(null);
        }
    }

    public void search() {
        searchList.clear();
        progressOverlay.setValue(true);

        listLoader = new Task<SearchList>() {
            {
                setOnSucceeded(workerStateEvent -> {
                    SearchListItemViewModel.Type itemType = selectedSenseMode.getValue() ? SearchListItemViewModel.Type.SENSE : SearchListItemViewModel.Type.SYNSET;
                    searchList.addAll(getValue().getRows()
                            .stream()
                            .map(i -> new SearchListItemViewModel(remoteService, i, itemType, loadGraphEvent))
                            .collect(Collectors.toList()));

                    int insertedItems = searchList.size();
                    int allItems = getValue().getSize();

                    IntStream.range(0, allItems - insertedItems)
                            .forEach(i -> searchList.add(
                                    new SearchListItemViewModel(remoteService, new SearchListItem(""), itemType, loadGraphEvent)));

                    unitsCount.setValue(String.valueOf(getValue().getSize()));
                    progressOverlay.setValue(false); // stop displaying the loading indicator
                });

                setOnFailed(workerStateEvent -> {
                    getException().printStackTrace();
                    progressOverlay.setValue(false);
                });
                setOnCancelled(event -> progressOverlay.setValue(false));
            }

            @Override
            protected SearchList call() {
                filter.setLimit(searchLimit);
                filter.setStart(0);
                commitChanges();
                return remoteService.search(filter);
            }
        };

        loadingThread = new Thread(listLoader, "list-loader");
        loadingThread.setDaemon(true);
        loadingThread.start();
    }

    public synchronized void load(SearchFilter searchFilter) {
        progressOverlay.setValue(true);

         listLoader = new Task<SearchList>() {

            {
                setOnSucceeded(workerStateEvent -> {
                    SearchListItemViewModel.Type itemType = selectedSenseMode.getValue() ? SearchListItemViewModel.Type.SENSE : SearchListItemViewModel.Type.SYNSET;
                    List<SearchListItem> items = getValue().getRows();
                    int startIndex = getValue().getStart();
                    int limit = getValue().getLimit();
                    for (int i = startIndex; i < startIndex + limit; i++) {
                        if (i < searchList.size()) {
                            searchList.remove(i, i + 1);
                            searchList.add(i, new SearchListItemViewModel(remoteService,
                                    items.get(i - startIndex), itemType, loadGraphEvent));
                        }
                    }
                    progressOverlay.setValue(false);
                });
                setOnFailed(workerStateEvent -> {
                    getException().printStackTrace();
                    progressOverlay.setValue(false);
                });
                setOnCancelled(event -> progressOverlay.setValue(false));
            }

            @Override
            protected SearchList call() {
                searchFilter.setLimit(searchLimit);
                return remoteService.search(searchFilter);
            }
        };
        loadingThread = new Thread(listLoader, "list-loader");
        loadingThread.setDaemon(true);
        loadingThread.start();
    }

    public void reset() {
        listLoader.cancel();
        loadingThread.interrupt();

        searchList.clear();
        fieldSearch.set("");
        filter.setLemma("");

        selectedLexicon.set(null);
        filter.setLexicon(null);

        selectedDomain.set(null);
        filter.setDomainId(null);

        selectedPartOfSpeech.set(null);
        filter.setPartOfSpeechId(null);

        selectedRegister.set(null);
        filter.setRegisterId(null);

        selectedStatus.set(null);
        filter.setStatusId(null);

        selectedSynsetRelation.set(null);
        selectedSenseRelation.set(null);
        filter.setRelationTypeId(null);

        comment.set("");
        filter.setComment("");

        example.set("");
        filter.setExample("");

        definition.set("");
        filter.setDefinition("");

        synsetId.set("");
        filter.setSynsetId(null);

        senseOnlyWithoutSynset.set(false);
        filter.setSensesWithoutSynset(false);
    }

    public void onUserLexiconLiseUpdated(@Observes(notifyObserver = Reception.ALWAYS) UserLexiconUpdatedEvent event){
        initLexiconList();
    }

    private void initLexiconList() {
        lexiconItemList = Dictionaries.initLexiconDictionaryUserChosenItemList();
        ObservableList<String> mappedList = lexiconItemList.getTargetList();
        lexicons = Dictionaries.createListWithNothingSelectedMarker(mappedList, LEXICON_NOTHING_SELECTED_MARKER);
        lexicons.addListener((ListChangeListener<String>) p -> selectedLexicon.set(LEXICON_NOTHING_SELECTED_MARKER));
    }

    private void initPartsOfSpeechList() {
        partOfSpeechItemList = Dictionaries.initDictionaryItemList(Dictionaries.PART_OF_SPEECH_DICTIONARY);
        ObservableList<String> mappedList = partOfSpeechItemList.getTargetList();
        partsOfSpeech = Dictionaries.createListWithNothingSelectedMarker(mappedList, PART_OF_SPEECH_NOTHING_SELECTED_MARKER);
        partsOfSpeech.addListener((ListChangeListener<String>) p -> selectedPartOfSpeech.set(PART_OF_SPEECH_NOTHING_SELECTED_MARKER));
    }

    private void initDomainList() {
        domainItemList = Dictionaries.initDictionaryItemList(Dictionaries.DOMAIN_DICTIONARY);
        ObservableList<String> mappedList = domainItemList.getTargetList();
        domains = Dictionaries.createListWithNothingSelectedMarker(mappedList, DOMAIN_NOTHING_SELECTED_MARKER);
        domains.addListener((ListChangeListener<String>) p -> selectedDomain.set(DOMAIN_NOTHING_SELECTED_MARKER));
    }

    private void initStatusList() {
        statusItemList = Dictionaries.initDictionaryItemList(Dictionaries.STATUS_DICTIONARY);
        ObservableList<String> mappedList = statusItemList.getTargetList();
        statuses = Dictionaries.createListWithNothingSelectedMarker(mappedList, STATUS_NOTHING_SELECTED_MARKER);
        statuses.addListener((ListChangeListener<String>) p -> selectedStatus.set(STATUS_NOTHING_SELECTED_MARKER));
    }

    private void initRegisterList() {
        registerItemList = Dictionaries.initDictionaryItemList(Dictionaries.REGISTER_DICTIONARY);
        ObservableList<String> mappedList = registerItemList.getTargetList();
        registers = Dictionaries.createListWithNothingSelectedMarker(mappedList, REGISTER_NOTHING_SELECTED_MARKER);
        registers.addListener((ListChangeListener<String>) p -> selectedRegister.set(REGISTER_NOTHING_SELECTED_MARKER));
    }

    private void initSynsetRelationTypeList() {
        synsetRelationTypeItemList = new ItemList<>(RelationTypeService.getSynsetRelationTypes(), RelationType::getName);
        ObservableList<String> mappedList = synsetRelationTypeItemList.getTargetList();

        synsetRelationsTypes = Dictionaries.createListWithNothingSelectedMarker(mappedList, SYNSET_RELATION_TYPE_NOTHING_SELECTED_MARKER);
        synsetRelationsTypes.addListener((ListChangeListener<String>) p -> selectedSynsetRelation.set(SYNSET_RELATION_TYPE_NOTHING_SELECTED_MARKER));
    }

    private void initSenseRelationTypeList() {
        senseRelationTypeItemList = new ItemList<>(RelationTypeService.getSenseRelationTypes(), RelationType::getName);
        ObservableList<String> mappedList = senseRelationTypeItemList.getTargetList();

        senseRelationsTypes = Dictionaries.createListWithNothingSelectedMarker(mappedList, SENSE_RELATION_TYPE_NOTHING_SELECTED_MARKER);
        senseRelationsTypes.addListener((ListChangeListener<String>) p -> selectedSenseRelation.set(SENSE_RELATION_TYPE_NOTHING_SELECTED_MARKER));
    }

    public Property<String> fieldSearchProperty() {
        return fieldSearch;
    }

    public Property<String> definitionProperty() {
        return definition;
    }

    public Property<String> commentProperty() {
        return comment;
    }

    public Property<String> exampleProperty() {
        return example;
    }

    public ObservableList<String> lexiconList() {
        return lexicons;
    }

    public Property<String> selectedLexiconProperty() {
        return selectedLexicon;
    }

    public ObservableList<String> partOfSpeechList() {
        return partsOfSpeech;
    }

    public Property<String> selectedPartOfSpeechProperty() {
        return selectedPartOfSpeech;
    }

    public ObservableList<String> domainList() {
        return domains;
    }

    public Property<String> selectedDomainProperty() {
        return selectedDomain;
    }

    public ObservableList<String> statusList() {
        return statuses;
    }

    public Property<String> selectedStatusProperty() {
        return selectedStatus;
    }

    public ObservableList<String> registerList() {
        return registers;
    }

    public Property<String> selectedRegisterProperty() {
        return selectedRegister;
    }

    public ObservableList<SearchListItemViewModel> searchListProperty() {
        return searchList;
    }

    public ObjectProperty<SearchListItemViewModel> selectedSearchListItemProperty() {
        return selectedSearchListItem;
    }

    public ObservableList<String> senseRelationTypeList() {
        return senseRelationsTypes;
    }

    public Property<String> selectedSenseRelationTypeProperty() {
        return selectedSenseRelation;
    }

    public ObservableList<String> synsetRelationTypeList() {
        return synsetRelationsTypes;
    }

    public Property<String> selectedSynsetRelationTypeProperty() {
        return selectedSynsetRelation;
    }

    public Property<Boolean> progressOverlayProperty() {
        return progressOverlay;
    }

    public Property<Boolean> selectedSenseModeProperty() {
        return selectedSenseMode;
    }

    public Property<Boolean> selectedSynsetModeProperty() {
        return selectedSynsetMode;
    }

    public StringProperty synsetIdProperty() {
        return synsetId;
    }

    public StringProperty unitsCount() {
        return unitsCount;
    }

    public Property<Boolean> senseOnlyWithoutSynsetProperty() {
        return senseOnlyWithoutSynset;
    }
}
