package pl.edu.pwr.wordnetloom.client.ui.lexiconform;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.validation.*;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.model.Lexicon;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.dictionaryform.DictionaryListItemViewModel;
import pl.edu.pwr.wordnetloom.client.ui.scopes.LexiconDialogScope;

import javax.inject.Inject;
import java.util.stream.Collectors;


public class LexiconViewModel implements ViewModel {

    @Inject
    RemoteService service;

    @InjectScope
    LexiconDialogScope lexiconDialogScope;

    private StringProperty name = new SimpleStringProperty();
    private StringProperty shortName = new SimpleStringProperty();
    private StringProperty version = new SimpleStringProperty();
    private StringProperty language = new SimpleStringProperty();
    private StringProperty email = new SimpleStringProperty();
    private StringProperty identifier = new SimpleStringProperty();
    private StringProperty license = new SimpleStringProperty();
    private StringProperty referenceUrl = new SimpleStringProperty();
    private StringProperty citation = new SimpleStringProperty();
    private StringProperty confidenceScore = new SimpleStringProperty();
    private ObservableList<DictionaryListItemViewModel> lexiconList = FXCollections.observableArrayList();
    private ObjectProperty<DictionaryListItemViewModel> selectedLexiconListItem = new SimpleObjectProperty<>();
    private Command addCommand;
    private Command removeCommand;
    private Validator nameValidator;
    private Validator languageValidator;
    private Validator identifierValidator;

    private final ReadOnlyBooleanWrapper removeValid = new ReadOnlyBooleanWrapper();
    private final CompositeValidator formValidator = new CompositeValidator();

    public LexiconViewModel() {
        nameValidator = new FunctionBasedValidator<>(
                nameProperty(),
                name -> name != null && !name.trim().isEmpty(),
                ValidationMessage.error("Lexicon name may not be empty"));

        languageValidator = new FunctionBasedValidator<>(
                languageProperty(),
                lang -> lang != null && !lang.trim().isEmpty(),
                ValidationMessage.error("Language may not be empty"));

        identifierValidator = new FunctionBasedValidator<>(
                identifierProperty(),
                ident -> ident != null && !ident.trim().isEmpty(),
                ValidationMessage.error("Identifier may not be empty"));

        formValidator.addValidators(
                nameValidator,
                languageValidator,
                identifierValidator);
    }


    public void initialize() {

        lexiconList.addAll(Dictionaries.getDictionary(Dictionaries.LEXICON_DICTIONARY)
                .stream()
                .map(DictionaryListItemViewModel::new)
                .collect(Collectors.toList()));

        addCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                add();
            }
        });
        removeCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                remove();
            }
        });

        selectedLexiconListItem.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Lexicon lexicon = service.findLexicon(newValue.getDictionaryItem().getLinks().getSelf());
                mapLexicon(lexicon);
                lexiconDialogScope.lexiconProperty().set(lexicon);
            }
        });

        lexiconDialogScope.subscribe(LexiconDialogScope.COMMIT, (s, objects) -> {
            commitChanges();
        });

        removeValid.bind(selectedLexiconListItemProperty().isNotNull());
        lexiconDialogScope.lexiconFormValidProperty().bind(formValidator.getValidationStatus().validProperty());
    }

    private void commitChanges() {
        Lexicon l = lexiconDialogScope.getLexicon();
        l.setName(name.get());
        l.setLanguageShortcut(shortName.get());
        l.setVersion(version.get());
        l.setEmail(email.get());
        l.setLanguage(language.get());
        l.setLicense(license.get());
        l.setIdentifier(identifier.get());
        l.setCitation(citation.get());
        l.setConfidenceScore(confidenceScore.get());
        l.setReferenceUrl(referenceUrl.get());
        lexiconDialogScope.setLexicon(l);
    }

    private void mapLexicon(Lexicon l) {
        name.set(l.getName());
        shortName.set(l.getLanguageShortcut());
        version.set(l.getVersion());
        language.set(l.getLanguage());
        email.set(l.getEmail());
        identifier.set(l.getIdentifier());
        license.set(l.getLicense());
        referenceUrl.set(l.getReferenceUrl());
        confidenceScore.set(l.getConfidenceScore());
        citation.set(l.getCitation());
    }

    private void remove() {
    }

    private void add() {
        mapLexicon(new Lexicon());
    }

    public ObservableList<DictionaryListItemViewModel> getLexiconList() {
        return lexiconList;
    }

    public void setLexiconList(ObservableList<DictionaryListItemViewModel> lexiconList) {
        this.lexiconList = lexiconList;
    }

    public DictionaryListItemViewModel getSelectedLexiconListItem() {
        return selectedLexiconListItem.get();
    }

    public void setSelectedLexiconListItem(DictionaryListItemViewModel selectedLexiconListItem) {
        this.selectedLexiconListItem.set(selectedLexiconListItem);
    }

    public ObjectProperty<DictionaryListItemViewModel> selectedLexiconListItemProperty() {
        return selectedLexiconListItem;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getShortName() {
        return shortName.get();
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    public StringProperty shortNameProperty() {
        return shortName;
    }

    public String getVersion() {
        return version.get();
    }

    public void setVersion(String version) {
        this.version.set(version);
    }

    public StringProperty versionProperty() {
        return version;
    }

    public String getLanguage() {
        return language.get();
    }

    public void setLanguage(String language) {
        this.language.set(language);
    }

    public StringProperty languageProperty() {
        return language;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getIdentifier() {
        return identifier.get();
    }

    public void setIdentifier(String identifier) {
        this.identifier.set(identifier);
    }

    public StringProperty identifierProperty() {
        return identifier;
    }

    public String getLicense() {
        return license.get();
    }

    public void setLicense(String license) {
        this.license.set(license);
    }

    public StringProperty licenseProperty() {
        return license;
    }

    public Command getAddCommand() {
        return addCommand;
    }

    public Command getRemoveCommand() {
        return removeCommand;
    }

    public String getReferenceUrl() {
        return referenceUrl.get();
    }

    public void setReferenceUrl(String referenceUrl) {
        this.referenceUrl.set(referenceUrl);
    }

    public StringProperty referenceUrlProperty() {
        return referenceUrl;
    }

    public String getCitation() {
        return citation.get();
    }

    public void setCitation(String citation) {
        this.citation.set(citation);
    }

    public StringProperty citationProperty() {
        return citation;
    }

    public String getConfidenceScore() {
        return confidenceScore.get();
    }

    public void setConfidenceScore(String confidenceScore) {
        this.confidenceScore.set(confidenceScore);
    }

    public StringProperty confidenceScoreProperty() {
        return confidenceScore;
    }

    public ObservableBooleanValue removeButtonDisabledProperty() {
        return removeValid.not();
    }

    public ValidationStatus getNameValidator() {
        return nameValidator.getValidationStatus();
    }

    public ValidationStatus getIdentifierValidator() {
        return identifierValidator.getValidationStatus();
    }

    public ValidationStatus getLanguageValidator() {
        return languageValidator.getValidationStatus();
    }
}

