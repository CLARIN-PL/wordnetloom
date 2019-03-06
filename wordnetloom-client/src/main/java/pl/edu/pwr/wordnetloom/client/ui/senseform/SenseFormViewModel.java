package pl.edu.pwr.wordnetloom.client.ui.senseform;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.itemlist.ItemList;
import de.saxsys.mvvmfx.utils.validation.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.events.UserLexiconUpdatedEvent;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.model.Example;
import pl.edu.pwr.wordnetloom.client.model.Sense;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.alerts.AlertDialogHandler;
import pl.edu.pwr.wordnetloom.client.ui.example.ExampleListItemViewModel;
import pl.edu.pwr.wordnetloom.client.ui.scopes.ExampleDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SensePropertiesDialogScope;
import pl.edu.pwr.wordnetloom.client.ui.sensepropertiesdialog.SensePropertiesDialogViewModel;

import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.edu.pwr.wordnetloom.client.service.Dictionaries.*;

public class SenseFormViewModel implements ViewModel {

    static final String NOTHING_SELECTED_MARKER = "----------";
    public static final String OPEN_EXAMPLE_DIALOG = "open_example_dialog";

    @InjectScope
    SensePropertiesDialogScope sensePropertiesDialogScope;

    @InjectScope
    ExampleDialogScope exampleDialogScope;

    @Inject
    RemoteService service;

    @Inject
    SensePropertiesDialogViewModel sensePropertiesDialogViewModel;

    @Inject
    AlertDialogHandler dialogHandler;

    private final StringProperty lemma = new SimpleStringProperty();
    private final StringProperty link = new SimpleStringProperty();
    private final StringProperty variant = new SimpleStringProperty();
    private final StringProperty owner = new SimpleStringProperty();
    private final StringProperty definition = new SimpleStringProperty();
    private final StringProperty comment = new SimpleStringProperty();
    private final StringProperty technicalComment = new SimpleStringProperty();

    private ObservableList<String> partsOfSpeech;
    private final ObjectProperty<Dictionary> partOfSpeech = new SimpleObjectProperty<>();
    private final StringProperty selectedPartOfSpeech = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> domains;
    private final ObjectProperty<Dictionary> domain = new SimpleObjectProperty<>();
    private final StringProperty selectedDomain = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> statuses;
    private final ObjectProperty<Dictionary> status = new SimpleObjectProperty<>();
    private final StringProperty selectedStatus = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> registers;
    private final ObjectProperty<Dictionary> register = new SimpleObjectProperty<>();
    private final StringProperty selectedRegister = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ObservableList<String> lexicons;
    private final ObjectProperty<Dictionary> lexicon = new SimpleObjectProperty<>();
    private final StringProperty selectedLexicon = new SimpleStringProperty(NOTHING_SELECTED_MARKER);

    private ItemList<Dictionary> lexiconItemList;
    private ItemList<Dictionary> partOfSpeechItemList;
    private ItemList<Dictionary> domainItemList;
    private ItemList<Dictionary> statusItemList;
    private ItemList<Dictionary> registerItemList;

    private ObservableList<ExampleListItemViewModel> exampleList = FXCollections.observableArrayList();
    private ObjectProperty<ExampleListItemViewModel> selectedExampleListItem = new SimpleObjectProperty<>();

    private final CompositeValidator formValidator = new CompositeValidator();

    private Command addExampleCommand;
    private Command editExampleCommand;
    private Command removeExampleCommand;

    private Validator lemmaValidator;
    private Validator lexiconValidator;
    private Validator partOfSpeechValidator;
    private Validator domainValidator;
    private Validator linkValidator;

    private Sense sense;


    public SenseFormViewModel() {

        lemmaValidator = new FunctionBasedValidator<>(
                lemmaProperty(),
                lemma -> lemma != null && !lemma.trim().isEmpty(),
                ValidationMessage.error("Lemma may not be empty"));

        lexiconValidator = new FunctionBasedValidator<>(
                selectedLexiconProperty(),
                lexicon -> !lexicon.equals(NOTHING_SELECTED_MARKER),
                ValidationMessage.error("Lexicon must be selected"));

        partOfSpeechValidator = new FunctionBasedValidator<>(
                selectedPartOfSpeechProperty(),
                pos -> !pos.equals(NOTHING_SELECTED_MARKER),
                ValidationMessage.error("Part of speech must be selected"));

        domainValidator = new FunctionBasedValidator<>(
                selectedDomainProperty(),
                domain -> !domain.equals(NOTHING_SELECTED_MARKER),
                ValidationMessage.error("Domain must be selected"));

        linkValidator = new FunctionBasedValidator<>(
                linkProperty(),
                this::isValidURL,
                ValidationMessage.error("Incorrect link format"));

        formValidator.addValidators(lemmaValidator,
                lexiconValidator, domainValidator, partOfSpeechValidator, linkValidator
        );
    }

    public void initialize() {
        initPartsOfSpeechList();
        initDomainList();
        initStatusList();
        initRegisterList();
        initLexiconList();

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


        sensePropertiesDialogScope.subscribe(SensePropertiesDialogScope.RESET_FORMS, (key, payload) -> resetForm());
        sensePropertiesDialogScope.subscribe(SensePropertiesDialogScope.COMMIT, (key, payload) -> commitChanges());

        ObjectProperty<Sense> senseToEditProperty = sensePropertiesDialogScope.senseToEditProperty();
        if (senseToEditProperty.get() != null) {
            initWithSense(senseToEditProperty.get());
        }

        selectedLexicon.addListener((obs, oldV, newV) -> {
            Dictionaries.dictionarySelected(obs, oldV, newV, Dictionaries.LEXICON_DICTIONARY, NOTHING_SELECTED_MARKER, lexicon);
        });

        selectedPartOfSpeech.addListener((obs, oldV, newV) -> {
            Dictionaries.dictionarySelected(obs, oldV, newV, PART_OF_SPEECH_DICTIONARY, NOTHING_SELECTED_MARKER, partOfSpeech);
        });

        selectedDomain.addListener((obs, oldV, newV) -> {
            Dictionaries.dictionarySelected(obs, oldV, newV, Dictionaries.DOMAIN_DICTIONARY, NOTHING_SELECTED_MARKER, domain);
        });

        selectedStatus.addListener((obs, oldV, newV) -> {
            Dictionaries.dictionarySelected(obs, oldV, newV, Dictionaries.STATUS_DICTIONARY, NOTHING_SELECTED_MARKER, status);
        });

        selectedRegister.addListener((obs, oldV, newV) -> {
            Dictionaries.dictionarySelected(obs, oldV, newV, Dictionaries.REGISTER_DICTIONARY, NOTHING_SELECTED_MARKER, register);
        });

        exampleDialogScope.subscribe(ExampleDialogScope.AFTER_COMMIT, (s, objects) -> {

            if (!exampleDialogScope.isSynsetExample()) {
                Example e = exampleDialogScope.getExampleToEdit();
                if (e.getExample() != null && !e.getExample().isEmpty()) {
                    Set<Example> ex = exampleList.stream()
                            .map(ExampleListItemViewModel::getExample)
                            .collect(Collectors.toSet());

                    if (e.getLinks().getSelf() != null) {
                        updateExample(e, ex);
                    } else {
                        addExample(e, ex);
                    }

                    exampleList.clear();
                    ex.forEach(z -> exampleList.add(new ExampleListItemViewModel(z)));
                    exampleDialogScope.publish(ExampleDialogScope.RESET_FORMS);
                    exampleDialogScope.publish(ExampleDialogScope.CLOSE);
                }
            }
        });

        sensePropertiesDialogScope.senseFormValidProperty()
                .bind(formValidator.getValidationStatus().validProperty());
    }

    private void updateExample(Example e, Set<Example> ex) {
        try {
            service.updateExample(e);
        } catch (Exception err) {
            dialogHandler.handleErrors(err);
        }
        ex.stream()
                .filter(egz -> egz.getLinks().getSelf().equals(e.getLinks().getSelf()))
                .findFirst().ifPresent(ee -> {
            ee.setExample(e.getExample());
            ee.setType(e.getType());
        });
    }

    private void addExample(Example e, Set<Example> ex) {
        if (sense.getId() != null) {
            try {
                Example egz = service.addExample(sense.getLinks().getExamples(), e);
                ex.add(egz);
            } catch (Exception e1) {
                dialogHandler.handleErrors(e1);
            }
        } else {
            ex.add(e);
        }
    }

    private void removeExample() {
        if (selectedExampleListItem.get() != null) {
            Example example = selectedExampleListItem.get().getExample();
            exampleList.remove(selectedExampleListItem.get());
            if (example.getLinks().getSelf() != null) {
                service.delete(example.getLinks().getSelf());
            }
        }
    }


    private void editExample() {
        if (selectedExampleListItem.get() != null) {
            exampleDialogScope.setExampleToEdit(selectedExampleListItem.get().getExample());
            exampleDialogScope.setSynsetExample(false);
            publish(OPEN_EXAMPLE_DIALOG);
            exampleDialogScope.publish(ExampleDialogScope.ON_LOAD);
        }
    }

    private void addExample() {
        exampleDialogScope.setExampleToEdit(new Example());
        exampleDialogScope.setSynsetExample(false);
        publish(OPEN_EXAMPLE_DIALOG);
        exampleDialogScope.publish(ExampleDialogScope.ON_LOAD, "sense");
    }


    public ValidationStatus lemmaValidation() {
        return lemmaValidator.getValidationStatus();
    }

    public ValidationStatus lexiconValidation() {
        return lexiconValidator.getValidationStatus();
    }

    public ValidationStatus partOfSpeechValidation() {
        return partOfSpeechValidator.getValidationStatus();
    }

    public ValidationStatus domainValidation() {
        return domainValidator.getValidationStatus();
    }

    public ValidationStatus linkValidation() {
        return linkValidator.getValidationStatus();
    }

    public void initWithSense(Sense sense) {
        this.sense = sense;
        lemma.set(sense.getLemma());
        variant.setValue(sense.getVariant() != null ? sense.getVariant().toString() : "");
        definition.set(sense.getDefinition());
        comment.set(sense.getComment());
        owner.set(sense.getOwner());
        technicalComment.set(sense.getTechnicalComment());
        link.set(sense.getLink());

        if (sense.getPartOfSpeech() != null) {
            selectedPartOfSpeech.set(Dictionaries.getDictionaryItemById(PART_OF_SPEECH_DICTIONARY, sense.getPartOfSpeech()));
        }

        if (sense.getStatus() != null) {
            selectedStatus.set(
                    Dictionaries.getDictionaryItemById(STATUS_DICTIONARY, sense.getStatus()));
        }

        if (sense.getDomain() != null) {
            selectedDomain.set(Dictionaries.getDictionaryItemById(DOMAIN_DICTIONARY, sense.getDomain()));
        }

        if (sense.getRegister() != null) {
            selectedRegister.set(Dictionaries.getDictionaryItemById(REGISTER_DICTIONARY, sense.getRegister()));
        }

        if (sense.getLexicon() != null) {
            selectedLexicon.set(Dictionaries.getDictionaryItemById(LEXICON_DICTIONARY, sense.getLexicon()));
        }

        if (sense.getExamples() != null) {
            sense.getExamples().forEach(e -> exampleList.add(new ExampleListItemViewModel(e)));
        }
    }

    private void commitChanges() {

        sense.setLemma(lemma.get());
        sense.setComment(comment.get());
        sense.setDefinition(definition.get());
        sense.setLink(link.get());
        if (lexicon.get() != null) {
            sense.setLexicon(lexicon.get().getId());
        }
        if (partOfSpeech.get() != null) {
            sense.setPartOfSpeech(partOfSpeech.get().getId());
        }
        if (domain.get() != null) {
            sense.setDomain(domain.get().getId());
        }

        if (register.get() != null) {
            sense.setRegister(register.get().getId());
        } else {
            sense.setRegister(null);
        }

        if (status.get() != null) {
            sense.setStatus(status.get().getId());
        } else {
            sense.setStatus(null);
        }

        sense.setTechnicalComment(technicalComment.get());
        if (sense.getId() == null) {
            List<Example> examples = exampleList.stream()
                    .map(ExampleListItemViewModel::getExample)
                    .collect(Collectors.toList());
            sense.setExamples(examples);
        }
    }

    private void resetForm() {
        this.sense = null;
        exampleList.clear();
        lemma.set(null);
        variant.setValue(null);
        definition.set(null);
        comment.set(null);
        exampleList.clear();
        owner.set(null);
        technicalComment.set(null);
        link.set(null);
        selectedPartOfSpeech.set(null);
        selectedDomain.set(null);
        selectedPartOfSpeech.set(null);
        selectedLexicon.set(null);
        selectedRegister.set(null);
        selectedStatus.set(null);
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

    private void initPartsOfSpeechList() {
        partOfSpeechItemList = Dictionaries.initDictionaryItemList(PART_OF_SPEECH_DICTIONARY);
        ObservableList<String> mappedList = partOfSpeechItemList.getTargetList();
        partsOfSpeech = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        partsOfSpeech.addListener((ListChangeListener<String>) p -> selectedPartOfSpeech.set(NOTHING_SELECTED_MARKER));
    }

    private void initDomainList() {
        domainItemList = Dictionaries.initDictionaryItemList(Dictionaries.DOMAIN_DICTIONARY);
        ObservableList<String> mappedList = domainItemList.getTargetList();
        domains = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        domains.addListener((ListChangeListener<String>) p -> selectedDomain.set(NOTHING_SELECTED_MARKER));
    }

    private void initStatusList() {
        statusItemList = Dictionaries.initDictionaryItemList(Dictionaries.STATUS_DICTIONARY);
        ObservableList<String> mappedList = statusItemList.getTargetList();
        statuses = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        statuses.addListener((ListChangeListener<String>) p -> selectedStatus.set(NOTHING_SELECTED_MARKER));
    }

    private void initRegisterList() {
        registerItemList = Dictionaries.initDictionaryItemList(Dictionaries.REGISTER_DICTIONARY);
        ObservableList<String> mappedList = registerItemList.getTargetList();
        registers = Dictionaries.createListWithNothingSelectedMarker(mappedList, NOTHING_SELECTED_MARKER);
        registers.addListener((ListChangeListener<String>) p -> selectedRegister.set(NOTHING_SELECTED_MARKER));
    }

    public Property<String> lemmaProperty() {
        return lemma;
    }

    public Property<String> linkProperty() {
        return link;
    }

    public Property<String> ownerProperty() {
        return owner;
    }

    public Property<String> variantProperty() {
        return variant;
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

    public Property<String> selectedRegisterProperty() {
        return selectedRegister;
    }

    public ObservableList<String> registerList() {
        return registers;
    }

    public ObservableList<ExampleListItemViewModel> exampleListProperty() {
        return exampleList;
    }

    public Command addExampleCommand() {
        return addExampleCommand;
    }

    public Command editExampleCommand() {
        return editExampleCommand;
    }

    public Command removeExampleCommand() {
        return removeExampleCommand;
    }

    public ObjectProperty<ExampleListItemViewModel> selectedExampleListItemProperty() {
        return selectedExampleListItem;
    }

    public boolean isValidURL(String url) {
        if (url == null || url.isEmpty()) return true;

        URL u = null;
        try {
            u = new URL(url);
            u.toURI();
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}

