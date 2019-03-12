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
    private static final String GRAMMATICAL_GENDER_NOTHING_SELECTED_MARKER = "Grammatical Gender";
    private static final String AGE_NOTHING_SELECTED_MARKER = "Age";
    private static final String SOURCE_NOTHING_SELECTED_MARKER = "Source";
    private static final String LEXICAL_CHARACTERISTIC_NOTHING_SELECTED_MARKER = "Lexical Characteristic";
    private static final String SEMANTIC_FIELD_NOTHING_SELECTED_MARKER = "Semantic Field";
    private static final String SEMANTIC_FIELD_MOD_NOTHING_SELECTED_MARKER = "Semantic Field Modifier";
    private static final String YIDDISH_STATUSES_NOTHING_SELECTED_MARKER = "Status";

    private final StringProperty fieldSearch = new SimpleStringProperty();
    private final StringProperty definition = new SimpleStringProperty();
    private final StringProperty comment = new SimpleStringProperty();
    private final StringProperty example = new SimpleStringProperty();
    private final StringProperty synsetId = new SimpleStringProperty();
    private final BooleanProperty progressOverlay = new SimpleBooleanProperty();
    private final StringProperty unitsCount = new SimpleStringProperty();

    private final StringProperty etymology = new SimpleStringProperty();

    private final BooleanProperty selectedSenseMode = new SimpleBooleanProperty();
    private final BooleanProperty selectedSynsetMode = new SimpleBooleanProperty();

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

    private ObservableList<String> grammaticalGenders;
    private final ObjectProperty<Dictionary> grammaticalGender = new SimpleObjectProperty<>();
    private final StringProperty selectedGrammaticalGender = new SimpleStringProperty(GRAMMATICAL_GENDER_NOTHING_SELECTED_MARKER);

    private ObservableList<String> semanticFields;
    private final ObjectProperty<Dictionary> semanticField = new SimpleObjectProperty<>();
    private final StringProperty selectedSemanticField = new SimpleStringProperty(SEMANTIC_FIELD_NOTHING_SELECTED_MARKER);

    private ObservableList<String> semanticFieldMods;
    private final ObjectProperty<Dictionary> semanticFieldMod = new SimpleObjectProperty<>();
    private final StringProperty selectedSemanticFieldMod = new SimpleStringProperty(SEMANTIC_FIELD_MOD_NOTHING_SELECTED_MARKER);

    private ObservableList<String> yiddishStatuses;
    private final ObjectProperty<Dictionary> yiddishStatus = new SimpleObjectProperty<>();
    private final StringProperty selectedYiddishStatus = new SimpleStringProperty(YIDDISH_STATUSES_NOTHING_SELECTED_MARKER);

    private ObservableList<String> ages;
    private final ObjectProperty<Dictionary> age = new SimpleObjectProperty<>();
    private final StringProperty selectedAge = new SimpleStringProperty(AGE_NOTHING_SELECTED_MARKER);

    private ObservableList<String> sources;
    private final ObjectProperty<Dictionary> source = new SimpleObjectProperty<>();
    private final StringProperty selectedSource = new SimpleStringProperty(SOURCE_NOTHING_SELECTED_MARKER);

    private ObservableList<String> lexicalCharacteristics;
    private final ObjectProperty<Dictionary> lexicalCharacteristic = new SimpleObjectProperty<>();
    private final StringProperty selectedLexicalCharacteristic = new SimpleStringProperty(LEXICAL_CHARACTERISTIC_NOTHING_SELECTED_MARKER);

    // Don't inline this field. It's needed to prevent the list mapping from being garbage collected.
    private ItemList<Dictionary> lexiconItemList;
    private ItemList<Dictionary> partOfSpeechItemList;
    private ItemList<Dictionary> domainItemList;
    private ItemList<Dictionary> statusItemList;
    private ItemList<Dictionary> registerItemList;
    private ItemList<RelationType> synsetRelationTypeItemList;
    private ItemList<RelationType> senseRelationTypeItemList;

    private ItemList<Dictionary> grammaticalGenderItemList;
    private ItemList<Dictionary> semanticFieldItemList;
    private ItemList<Dictionary> semanticFieldModItemList;
    private ItemList<Dictionary> yiddishStatusesItemList;
    private ItemList<Dictionary> agesItemList;
    private ItemList<Dictionary> sourcesItemList;
    private ItemList<Dictionary> lexicalCharacteristicsItemList;


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

    public void clearList() {
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

        initAgeItemList();
        initGrammaticalGenderList();
        initLexicalCharacteristicsItemList();
        initSemanticFieldItemList();
        initSemanticFieldModItemList();
        initSourcesItemList();
        initYiddishStatusesItemList();


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

        selectedAge.addListener((obs, oldV, newV) -> {
            Dictionaries.dictionarySelected(obs, oldV, newV, Dictionaries.AGES_DICTIONARY, AGE_NOTHING_SELECTED_MARKER, age);
        });

        selectedGrammaticalGender.addListener((observable, oldValue, newValue) -> {
            Dictionaries.dictionarySelected(observable, oldValue, newValue, Dictionaries.GRAMMATICAL_GENDERS_DICTIONARY,
                    GRAMMATICAL_GENDER_NOTHING_SELECTED_MARKER, grammaticalGender);
        });

        selectedLexicalCharacteristic.addListener((observable, oldValue, newValue) -> {
            Dictionaries.dictionarySelected(observable, oldValue, newValue, Dictionaries.LEXICAL_CHARACTERISTICS_DICTIONARY,
                    LEXICAL_CHARACTERISTIC_NOTHING_SELECTED_MARKER, lexicalCharacteristic);
        });

        selectedYiddishStatus.addListener((observable, oldValue, newValue) -> {
            Dictionaries.dictionarySelected(observable, oldValue, newValue, Dictionaries.YIDDISH_STATUSES_DICTIONARY,
                    YIDDISH_STATUSES_NOTHING_SELECTED_MARKER, yiddishStatus);
        });

        selectedSemanticField.addListener((observable, oldValue, newValue) -> {
            Dictionaries.dictionarySelected(observable, oldValue, newValue, Dictionaries.YIDDISH_DOMAINS_DICTIONARY,
                    SEMANTIC_FIELD_NOTHING_SELECTED_MARKER, semanticField);
        });

        selectedSemanticFieldMod.addListener((observable, oldValue, newValue) -> {
            Dictionaries.dictionarySelected(observable, oldValue, newValue, Dictionaries.YIDDISH_DOMAIN_MODIFIERS_DICTIONARY,
                    SEMANTIC_FIELD_MOD_NOTHING_SELECTED_MARKER, semanticFieldMod);
        });

        selectedSource.addListener((observable, oldValue, newValue) -> {
            Dictionaries.dictionarySelected(observable, oldValue, newValue, Dictionaries.SOURCES_DICTIONARY,
                    SOURCE_NOTHING_SELECTED_MARKER, source);
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
                loadGraphEvent.fireAsync(new LoadGraphEvent(newValue.getSearchListItem().getLinks().getSynsetGraph(), false, false));
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
                    item.getSearchListItem().getLinks().setSynsetGraph(synset.getLinks().getSynsetGraph());
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
            filter.setExample(example.getValue());
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

        if (selectedAge.get() != null && !AGE_NOTHING_SELECTED_MARKER.equals(selectedAge.get())) {
            filter.setAgeId(age.get().getId());
        } else {
            filter.setAgeId(null);
        }

        if (selectedSemanticField.get() != null && !SEMANTIC_FIELD_NOTHING_SELECTED_MARKER
                .equals(selectedSemanticField.get())) {
            filter.setYiddishDomainId(semanticField.get().getId());
        } else {
            filter.setYiddishDomainId(null);
        }

        if (selectedSemanticFieldMod.get() != null && !SEMANTIC_FIELD_MOD_NOTHING_SELECTED_MARKER
                .equals(selectedSemanticFieldMod.get())) {
            filter.setYiddishDomainModificationId(semanticFieldMod.get().getId());
        } else {
            filter.setYiddishDomainModificationId(null);
        }

        if (selectedYiddishStatus.get() != null && !YIDDISH_STATUSES_NOTHING_SELECTED_MARKER
                .equals(selectedYiddishStatus.get())) {
            filter.setYiddishStatusId(yiddishStatus.get().getId());
        } else {
            filter.setYiddishStatusId(null);
        }

        if (selectedLexicalCharacteristic.get() != null && !LEXICAL_CHARACTERISTIC_NOTHING_SELECTED_MARKER
                .equals(selectedLexicalCharacteristic.get())) {
            filter.setLexicalCharacteristicId(lexicalCharacteristic.get().getId());
        } else {
            filter.setLexicalCharacteristicId(null);
        }

        if (selectedGrammaticalGender.get() != null && !GRAMMATICAL_GENDER_NOTHING_SELECTED_MARKER
                .equals(selectedGrammaticalGender.get())) {
            filter.setGrammaticalGenderId(grammaticalGender.get().getId());
        } else {
            filter.setGrammaticalGenderId(null);
        }

        if (etymology.get() != null &&
                !etymology.get().isEmpty()) {
            filter.setEtymology(etymology.getValue());
        } else {
            filter.setEtymology(null);
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

        selectedAge.set(null);
        filter.setAgeId(null);

        selectedSemanticField.set(null);
        filter.setYiddishDomainId(null);

        selectedSemanticFieldMod.set(null);
        filter.setYiddishDomainModificationId(null);

        selectedYiddishStatus.set(null);
        filter.setYiddishStatusId(null);

        selectedLexicalCharacteristic.set(null);
        filter.setLexicalCharacteristicId(null);

        selectedGrammaticalGender.set(null);
        filter.setGrammaticalGenderId(null);

        etymology.set("");
        filter.setEtymology(null);

        senseOnlyWithoutSynset.set(false);
        filter.setSensesWithoutSynset(false);
    }

    public void onUserLexiconLiseUpdated(@Observes(notifyObserver = Reception.ALWAYS) UserLexiconUpdatedEvent event) {
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


    private void initAgeItemList() {
        agesItemList = Dictionaries.initDictionaryItemList(Dictionaries.AGES_DICTIONARY);
        ObservableList<String> mappedList = agesItemList.getTargetList();
        ages = Dictionaries.createListWithNothingSelectedMarker(mappedList, AGE_NOTHING_SELECTED_MARKER);
        ages.addListener((ListChangeListener<String>) p -> selectedAge.set(AGE_NOTHING_SELECTED_MARKER));
    }

    private void initLexicalCharacteristicsItemList() {
        lexicalCharacteristicsItemList = Dictionaries.initDictionaryItemList(Dictionaries.LEXICAL_CHARACTERISTICS_DICTIONARY);
        ObservableList<String> mappedList = lexicalCharacteristicsItemList.getTargetList();
        lexicalCharacteristics = Dictionaries.createListWithNothingSelectedMarker(mappedList, LEXICAL_CHARACTERISTIC_NOTHING_SELECTED_MARKER);
        lexicalCharacteristics.addListener((ListChangeListener<String>) p -> selectedLexicalCharacteristic.set(LEXICAL_CHARACTERISTIC_NOTHING_SELECTED_MARKER));
    }


    private void initSourcesItemList() {
        sourcesItemList = Dictionaries.initDictionaryItemList(Dictionaries.SOURCES_DICTIONARY);
        ObservableList<String> mappedList = sourcesItemList.getTargetList();
        sources = Dictionaries.createListWithNothingSelectedMarker(mappedList, SOURCE_NOTHING_SELECTED_MARKER);
        sources.addListener((ListChangeListener<String>) p -> selectedSource.set(SOURCE_NOTHING_SELECTED_MARKER));
    }

    private void initYiddishStatusesItemList() {
        yiddishStatusesItemList = Dictionaries.initDictionaryItemList(Dictionaries.YIDDISH_STATUSES_DICTIONARY);
        ObservableList<String> mappedList = yiddishStatusesItemList.getTargetList();
        yiddishStatuses = Dictionaries.createListWithNothingSelectedMarker(mappedList, YIDDISH_STATUSES_NOTHING_SELECTED_MARKER);
        yiddishStatuses.addListener((ListChangeListener<String>) p -> selectedYiddishStatus.set(YIDDISH_STATUSES_NOTHING_SELECTED_MARKER));
    }

    private void initSemanticFieldModItemList() {
        semanticFieldModItemList = Dictionaries.initDictionaryItemList(Dictionaries.YIDDISH_DOMAIN_MODIFIERS_DICTIONARY);
        ObservableList<String> mappedList = semanticFieldModItemList.getTargetList();
        semanticFieldMods = Dictionaries.createListWithNothingSelectedMarker(mappedList, SEMANTIC_FIELD_MOD_NOTHING_SELECTED_MARKER);
        semanticFieldMods.addListener((ListChangeListener<String>) p -> selectedSemanticFieldMod.set(SEMANTIC_FIELD_MOD_NOTHING_SELECTED_MARKER));
    }

    private void initSemanticFieldItemList() {
        semanticFieldItemList = Dictionaries.initDictionaryItemList(Dictionaries.YIDDISH_DOMAINS_DICTIONARY);
        ObservableList<String> mappedList = semanticFieldItemList.getTargetList();
        semanticFields = Dictionaries.createListWithNothingSelectedMarker(mappedList, SEMANTIC_FIELD_NOTHING_SELECTED_MARKER);
        semanticFields.addListener((ListChangeListener<String>) p -> selectedSemanticField.set(SEMANTIC_FIELD_NOTHING_SELECTED_MARKER));
    }

    private void initGrammaticalGenderList() {
        grammaticalGenderItemList = Dictionaries.initDictionaryItemList(Dictionaries.GRAMMATICAL_GENDERS_DICTIONARY);
        ObservableList<String> mappedList = grammaticalGenderItemList.getTargetList();
        grammaticalGenders = Dictionaries.createListWithNothingSelectedMarker(mappedList, GRAMMATICAL_GENDER_NOTHING_SELECTED_MARKER);
        grammaticalGenders.addListener((ListChangeListener<String>) p -> selectedGrammaticalGender.set(GRAMMATICAL_GENDER_NOTHING_SELECTED_MARKER));
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

    public String getEtymology() {
        return etymology.get();
    }

    public StringProperty etymologyProperty() {
        return etymology;
    }

    public void setEtymology(String etymology) {
        this.etymology.set(etymology);
    }

    public Boolean getSelectedSenseMode() {
        return selectedSenseMode.get();
    }

    public void setSelectedSenseMode(Boolean selectedSenseMode) {
        this.selectedSenseMode.set(selectedSenseMode);
    }

    public Boolean getSelectedSynsetMode() {
        return selectedSynsetMode.get();
    }

    public void setSelectedSynsetMode(Boolean selectedSynsetMode) {
        this.selectedSynsetMode.set(selectedSynsetMode);
    }

    public boolean isSenseOnlyWithoutSynset() {
        return senseOnlyWithoutSynset.get();
    }

    public void setSenseOnlyWithoutSynset(boolean senseOnlyWithoutSynset) {
        this.senseOnlyWithoutSynset.set(senseOnlyWithoutSynset);
    }

    public ObservableList<String> getPartsOfSpeech() {
        return partsOfSpeech;
    }

    public void setPartsOfSpeech(ObservableList<String> partsOfSpeech) {
        this.partsOfSpeech = partsOfSpeech;
    }

    public Dictionary getPartOfSpeech() {
        return partOfSpeech.get();
    }

    public ObjectProperty<Dictionary> partOfSpeechProperty() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(Dictionary partOfSpeech) {
        this.partOfSpeech.set(partOfSpeech);
    }

    public String getSelectedPartOfSpeech() {
        return selectedPartOfSpeech.get();
    }

    public void setSelectedPartOfSpeech(String selectedPartOfSpeech) {
        this.selectedPartOfSpeech.set(selectedPartOfSpeech);
    }

    public ObservableList<String> getDomains() {
        return domains;
    }

    public void setDomains(ObservableList<String> domains) {
        this.domains = domains;
    }

    public Dictionary getDomain() {
        return domain.get();
    }

    public ObjectProperty<Dictionary> domainProperty() {
        return domain;
    }

    public void setDomain(Dictionary domain) {
        this.domain.set(domain);
    }

    public String getSelectedDomain() {
        return selectedDomain.get();
    }

    public void setSelectedDomain(String selectedDomain) {
        this.selectedDomain.set(selectedDomain);
    }

    public ObservableList<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(ObservableList<String> statuses) {
        this.statuses = statuses;
    }

    public Dictionary getStatus() {
        return status.get();
    }

    public ObjectProperty<Dictionary> statusProperty() {
        return status;
    }

    public void setStatus(Dictionary status) {
        this.status.set(status);
    }

    public String getSelectedStatus() {
        return selectedStatus.get();
    }

    public void setSelectedStatus(String selectedStatus) {
        this.selectedStatus.set(selectedStatus);
    }

    public ObservableList<String> getRegisters() {
        return registers;
    }

    public void setRegisters(ObservableList<String> registers) {
        this.registers = registers;
    }

    public Dictionary getRegister() {
        return register.get();
    }

    public ObjectProperty<Dictionary> registerProperty() {
        return register;
    }

    public void setRegister(Dictionary register) {
        this.register.set(register);
    }

    public String getSelectedRegister() {
        return selectedRegister.get();
    }

    public void setSelectedRegister(String selectedRegister) {
        this.selectedRegister.set(selectedRegister);
    }

    public ObservableList<String> getLexicons() {
        return lexicons;
    }

    public void setLexicons(ObservableList<String> lexicons) {
        this.lexicons = lexicons;
    }

    public Dictionary getLexicon() {
        return lexicon.get();
    }

    public ObjectProperty<Dictionary> lexiconProperty() {
        return lexicon;
    }

    public void setLexicon(Dictionary lexicon) {
        this.lexicon.set(lexicon);
    }

    public String getSelectedLexicon() {
        return selectedLexicon.get();
    }

    public void setSelectedLexicon(String selectedLexicon) {
        this.selectedLexicon.set(selectedLexicon);
    }

    public ObservableList<String> getSenseRelationsTypes() {
        return senseRelationsTypes;
    }

    public void setSenseRelationsTypes(ObservableList<String> senseRelationsTypes) {
        this.senseRelationsTypes = senseRelationsTypes;
    }

    public RelationType getSenseRelationType() {
        return senseRelationType.get();
    }

    public ObjectProperty<RelationType> senseRelationTypeProperty() {
        return senseRelationType;
    }

    public void setSenseRelationType(RelationType senseRelationType) {
        this.senseRelationType.set(senseRelationType);
    }

    public String getSelectedSenseRelation() {
        return selectedSenseRelation.get();
    }

    public StringProperty selectedSenseRelationProperty() {
        return selectedSenseRelation;
    }

    public void setSelectedSenseRelation(String selectedSenseRelation) {
        this.selectedSenseRelation.set(selectedSenseRelation);
    }

    public ObservableList<String> getSynsetRelationsTypes() {
        return synsetRelationsTypes;
    }

    public void setSynsetRelationsTypes(ObservableList<String> synsetRelationsTypes) {
        this.synsetRelationsTypes = synsetRelationsTypes;
    }

    public RelationType getSynsetRelationType() {
        return synsetRelationType.get();
    }

    public ObjectProperty<RelationType> synsetRelationTypeProperty() {
        return synsetRelationType;
    }

    public void setSynsetRelationType(RelationType synsetRelationType) {
        this.synsetRelationType.set(synsetRelationType);
    }

    public String getSelectedSynsetRelation() {
        return selectedSynsetRelation.get();
    }

    public StringProperty selectedSynsetRelationProperty() {
        return selectedSynsetRelation;
    }

    public void setSelectedSynsetRelation(String selectedSynsetRelation) {
        this.selectedSynsetRelation.set(selectedSynsetRelation);
    }

    public ObservableList<String> getGrammaticalGenders() {
        return grammaticalGenders;
    }

    public void setGrammaticalGenders(ObservableList<String> grammaticalGenders) {
        this.grammaticalGenders = grammaticalGenders;
    }

    public Dictionary getGrammaticalGender() {
        return grammaticalGender.get();
    }

    public ObjectProperty<Dictionary> grammaticalGenderProperty() {
        return grammaticalGender;
    }

    public void setGrammaticalGender(Dictionary grammaticalGender) {
        this.grammaticalGender.set(grammaticalGender);
    }

    public String getSelectedGrammaticalGender() {
        return selectedGrammaticalGender.get();
    }

    public StringProperty selectedGrammaticalGenderProperty() {
        return selectedGrammaticalGender;
    }

    public void setSelectedGrammaticalGender(String selectedGrammaticalGender) {
        this.selectedGrammaticalGender.set(selectedGrammaticalGender);
    }

    public ObservableList<String> getSemanticFields() {
        return semanticFields;
    }

    public void setSemanticFields(ObservableList<String> semanticFields) {
        this.semanticFields = semanticFields;
    }

    public Dictionary getSemanticField() {
        return semanticField.get();
    }

    public ObjectProperty<Dictionary> semanticFieldProperty() {
        return semanticField;
    }

    public void setSemanticField(Dictionary semanticField) {
        this.semanticField.set(semanticField);
    }

    public String getSelectedSemanticField() {
        return selectedSemanticField.get();
    }

    public StringProperty selectedSemanticFieldProperty() {
        return selectedSemanticField;
    }

    public void setSelectedSemanticField(String selectedSemanticField) {
        this.selectedSemanticField.set(selectedSemanticField);
    }

    public ObservableList<String> getSemanticFieldMods() {
        return semanticFieldMods;
    }

    public void setSemanticFieldMods(ObservableList<String> semanticFieldMods) {
        this.semanticFieldMods = semanticFieldMods;
    }

    public Dictionary getSemanticFieldMod() {
        return semanticFieldMod.get();
    }

    public ObjectProperty<Dictionary> semanticFieldModProperty() {
        return semanticFieldMod;
    }

    public void setSemanticFieldMod(Dictionary semanticFieldMod) {
        this.semanticFieldMod.set(semanticFieldMod);
    }

    public String getSelectedSemanticFieldMod() {
        return selectedSemanticFieldMod.get();
    }

    public StringProperty selectedSemanticFieldModProperty() {
        return selectedSemanticFieldMod;
    }

    public void setSelectedSemanticFieldMod(String selectedSemanticFieldMod) {
        this.selectedSemanticFieldMod.set(selectedSemanticFieldMod);
    }

    public ObservableList<String> getYiddishStatuses() {
        return yiddishStatuses;
    }

    public void setYiddishStatuses(ObservableList<String> yiddishStatuses) {
        this.yiddishStatuses = yiddishStatuses;
    }

    public Dictionary getYiddishStatus() {
        return yiddishStatus.get();
    }

    public ObjectProperty<Dictionary> yiddishStatusProperty() {
        return yiddishStatus;
    }

    public void setYiddishStatus(Dictionary yiddishStatus) {
        this.yiddishStatus.set(yiddishStatus);
    }

    public String getSelectedYiddishStatus() {
        return selectedYiddishStatus.get();
    }

    public StringProperty selectedYiddishStatusProperty() {
        return selectedYiddishStatus;
    }

    public void setSelectedYiddishStatus(String selectedYiddishStatus) {
        this.selectedYiddishStatus.set(selectedYiddishStatus);
    }

    public ObservableList<String> getAges() {
        return ages;
    }

    public void setAges(ObservableList<String> ages) {
        this.ages = ages;
    }

    public Dictionary getAge() {
        return age.get();
    }

    public ObjectProperty<Dictionary> ageProperty() {
        return age;
    }

    public void setAge(Dictionary age) {
        this.age.set(age);
    }

    public String getSelectedAge() {
        return selectedAge.get();
    }

    public StringProperty selectedAgeProperty() {
        return selectedAge;
    }

    public void setSelectedAge(String selectedAge) {
        this.selectedAge.set(selectedAge);
    }

    public ObservableList<String> getSources() {
        return sources;
    }

    public void setSources(ObservableList<String> sources) {
        this.sources = sources;
    }

    public Dictionary getSource() {
        return source.get();
    }

    public ObjectProperty<Dictionary> sourceProperty() {
        return source;
    }

    public void setSource(Dictionary source) {
        this.source.set(source);
    }

    public String getSelectedSource() {
        return selectedSource.get();
    }

    public StringProperty selectedSourceProperty() {
        return selectedSource;
    }

    public void setSelectedSource(String selectedSource) {
        this.selectedSource.set(selectedSource);
    }

    public ObservableList<String> getLexicalCharacteristics() {
        return lexicalCharacteristics;
    }

    public void setLexicalCharacteristics(ObservableList<String> lexicalCharacteristics) {
        this.lexicalCharacteristics = lexicalCharacteristics;
    }

    public Dictionary getLexicalCharacteristic() {
        return lexicalCharacteristic.get();
    }

    public ObjectProperty<Dictionary> lexicalCharacteristicProperty() {
        return lexicalCharacteristic;
    }

    public void setLexicalCharacteristic(Dictionary lexicalCharacteristic) {
        this.lexicalCharacteristic.set(lexicalCharacteristic);
    }

    public String getSelectedLexicalCharacteristic() {
        return selectedLexicalCharacteristic.get();
    }

    public StringProperty selectedLexicalCharacteristicProperty() {
        return selectedLexicalCharacteristic;
    }

    public void setSelectedLexicalCharacteristic(String selectedLexicalCharacteristic) {
        this.selectedLexicalCharacteristic.set(selectedLexicalCharacteristic);
    }

    public ItemList<Dictionary> getLexiconItemList() {
        return lexiconItemList;
    }

    public void setLexiconItemList(ItemList<Dictionary> lexiconItemList) {
        this.lexiconItemList = lexiconItemList;
    }

    public ItemList<Dictionary> getPartOfSpeechItemList() {
        return partOfSpeechItemList;
    }

    public void setPartOfSpeechItemList(ItemList<Dictionary> partOfSpeechItemList) {
        this.partOfSpeechItemList = partOfSpeechItemList;
    }

    public ItemList<Dictionary> getDomainItemList() {
        return domainItemList;
    }

    public void setDomainItemList(ItemList<Dictionary> domainItemList) {
        this.domainItemList = domainItemList;
    }

    public ItemList<Dictionary> getStatusItemList() {
        return statusItemList;
    }

    public void setStatusItemList(ItemList<Dictionary> statusItemList) {
        this.statusItemList = statusItemList;
    }

    public ItemList<Dictionary> getRegisterItemList() {
        return registerItemList;
    }

    public void setRegisterItemList(ItemList<Dictionary> registerItemList) {
        this.registerItemList = registerItemList;
    }

    public ItemList<RelationType> getSynsetRelationTypeItemList() {
        return synsetRelationTypeItemList;
    }

    public void setSynsetRelationTypeItemList(ItemList<RelationType> synsetRelationTypeItemList) {
        this.synsetRelationTypeItemList = synsetRelationTypeItemList;
    }

    public ItemList<RelationType> getSenseRelationTypeItemList() {
        return senseRelationTypeItemList;
    }

    public void setSenseRelationTypeItemList(ItemList<RelationType> senseRelationTypeItemList) {
        this.senseRelationTypeItemList = senseRelationTypeItemList;
    }

    public ItemList<Dictionary> getGrammaticalGenderItemList() {
        return grammaticalGenderItemList;
    }

    public void setGrammaticalGenderItemList(ItemList<Dictionary> grammaticalGenderItemList) {
        this.grammaticalGenderItemList = grammaticalGenderItemList;
    }

    public ItemList<Dictionary> getSemanticFieldItemList() {
        return semanticFieldItemList;
    }

    public void setSemanticFieldItemList(ItemList<Dictionary> semanticFieldItemList) {
        this.semanticFieldItemList = semanticFieldItemList;
    }

    public ItemList<Dictionary> getSemanticFieldModItemList() {
        return semanticFieldModItemList;
    }

    public void setSemanticFieldModItemList(ItemList<Dictionary> semanticFieldModItemList) {
        this.semanticFieldModItemList = semanticFieldModItemList;
    }

    public ItemList<Dictionary> getYiddishStatusesItemList() {
        return yiddishStatusesItemList;
    }

    public void setYiddishStatusesItemList(ItemList<Dictionary> yiddishStatusesItemList) {
        this.yiddishStatusesItemList = yiddishStatusesItemList;
    }

    public ItemList<Dictionary> getAgesItemList() {
        return agesItemList;
    }

    public void setAgesItemList(ItemList<Dictionary> agesItemList) {
        this.agesItemList = agesItemList;
    }

    public ItemList<Dictionary> getSourcesItemList() {
        return sourcesItemList;
    }

    public void setSourcesItemList(ItemList<Dictionary> sourcesItemList) {
        this.sourcesItemList = sourcesItemList;
    }

    public ItemList<Dictionary> getLexicalCharacteristicsItemList() {
        return lexicalCharacteristicsItemList;
    }

    public void setLexicalCharacteristicsItemList(ItemList<Dictionary> lexicalCharacteristicsItemList) {
        this.lexicalCharacteristicsItemList = lexicalCharacteristicsItemList;
    }
}
