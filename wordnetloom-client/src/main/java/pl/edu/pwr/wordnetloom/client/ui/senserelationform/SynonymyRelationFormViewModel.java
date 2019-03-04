package pl.edu.pwr.wordnetloom.client.ui.senserelationform;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.validation.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.model.Sense;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.scopes.SynonymyRelationDialogScope;

import javax.inject.Inject;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SynonymyRelationFormViewModel implements ViewModel {

    @Inject
    RemoteService service;

    @Inject
    ResourceBundle defaultResourceBundle;

    @InjectScope
    SynonymyRelationDialogScope dialogScope;

    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty sourceSynset = new SimpleStringProperty();
    private final StringProperty targetSense = new SimpleStringProperty();

    private ObservableList<TestListItemViewModel> testList = FXCollections.observableArrayList();
    private ObjectProperty<TestListItemViewModel> selectedTestListItem = new SimpleObjectProperty<>();

    private final CompositeValidator formValidator = new CompositeValidator();

    private Validator sourceSynsetValidator;
    private Validator targetSenseValidator;
    private Validator sourceSenseInSynsetValidator;

    public SynonymyRelationFormViewModel() {
        sourceSynsetValidator = new FunctionBasedValidator<>(
                sourceSynsetProperty(),
                ss -> ss != null && !ss.trim().isEmpty(),
                ValidationMessage.error("Source synset must be selected")
        );

        targetSenseValidator = new FunctionBasedValidator<>(
                targetSenseProperty(),
                ss -> ss != null && !ss.trim().isEmpty(),
                ValidationMessage.error("Target sense must be selected")
        );

    }

    public void initialize() {
        dialogScope.subscribe(SynonymyRelationDialogScope.RESET_FORMS, (key, payload) -> resetForm());
        dialogScope.subscribe(SynonymyRelationDialogScope.REFRESH_SENSES,
                (key, payload) -> setSenses());
        dialogScope.synonymyRelationFormValidProperty().bind(formValidator.getValidationStatus().validProperty());

        description.set(defaultResourceBundle.getString("synonymy.form.definition"));

        sourceSenseInSynsetValidator = new FunctionBasedValidator<>(
                dialogScope.senseProperty(),
                s -> s != null && null == s.getSynset(),
                ValidationMessage.error("Selected sense is already part of another synset")
        );
        formValidator.addValidators(
                sourceSynsetValidator, targetSenseValidator, sourceSenseInSynsetValidator
        );
    }

    private void resetForm() {
        sourceSynset.set(null);
        targetSense.set(null);
        testList.clear();
    }

    public void setSenses() {
        if(dialogScope.getSense() != null && dialogScope.getSynset() !=null) {
            sourceSynset.set(constructSynsetLabel(dialogScope.getSynset().getSenses()));
            targetSense.set(constructSenseLabel(dialogScope.getSense()));
        }
    }

    private String constructSynsetLabel(List<Sense> senses) {
        return senses.stream()
                .map(Sense::getLabel)
                .collect(Collectors.joining(","));
    }

    private String constructSenseLabel(Sense sense) {
        StringBuilder sb = new StringBuilder();
        sb.append(sense.getLemma());
        sb.append(" ").append(sense.getVariant());
        if (sense.getDomain() != null) {
            sb.append(" (")
                    .append(Dictionaries.getDictionaryItemById(Dictionaries.DOMAIN_DICTIONARY, sense.getDomain()))
                    .append(") ");
        }
        if (sense.getLexicon() != null) {
            sb.append(Dictionaries.getDictionaryItemById(Dictionaries.LEXICON_DICTIONARY, sense.getLexicon()));
        }
        return sb.toString();
    }


    public StringProperty descriptionProperty() {
        return description;
    }

    public ObservableList<TestListItemViewModel> getTestList() {
        return testList;
    }

    public TestListItemViewModel getSelectedTestListItem() {
        return selectedTestListItem.get();
    }

    public ObjectProperty<TestListItemViewModel> selectedTestListItemProperty() {
        return selectedTestListItem;
    }

    public String getSourceSynset() {
        return sourceSynset.get();
    }

    public StringProperty sourceSynsetProperty() {
        return sourceSynset;
    }

    public String getTargetSense() {
        return targetSense.get();
    }

    public StringProperty targetSenseProperty() {
        return targetSense;
    }

    public ValidationStatus senseInSynsetValidation() {
        return sourceSenseInSynsetValidator.getValidationStatus();
    }
}

