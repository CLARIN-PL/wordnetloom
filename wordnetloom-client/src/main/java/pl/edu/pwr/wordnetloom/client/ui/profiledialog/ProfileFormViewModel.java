package pl.edu.pwr.wordnetloom.client.ui.profiledialog;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.validation.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.controlsfx.control.IndexedCheckModel;
import pl.edu.pwr.wordnetloom.client.model.Dictionary;
import pl.edu.pwr.wordnetloom.client.model.User;
import pl.edu.pwr.wordnetloom.client.service.Dictionaries;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.scopes.ProfileDialogScope;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.stream.Collectors;


public class ProfileFormViewModel implements ViewModel {

    @Inject
    RemoteService service;

    @InjectScope
    ProfileDialogScope dialogScope;

    private StringProperty firstName = new SimpleStringProperty();
    private StringProperty lastName = new SimpleStringProperty();
    private StringProperty email = new SimpleStringProperty();

    private BooleanProperty tooltips = new SimpleBooleanProperty();
    private BooleanProperty markers = new SimpleBooleanProperty();

    private ObservableList<Dictionary> lexicons = FXCollections.observableArrayList();
    private IndexedCheckModel<Dictionary> checkModel;

    private Validator firstnameValidator;
    private Validator lastnameValidator;

    private final Validator emailValidator = new EmailValidator(emailProperty());

    private final CompositeValidator formValidator = new CompositeValidator();

    public ProfileFormViewModel() {
        firstnameValidator = new FunctionBasedValidator<>(
                firstNameProperty(),
                firstName -> firstName != null && !firstName.trim().isEmpty(),
                ValidationMessage.error("Firstname may not be empty"));

        lastnameValidator = new FunctionBasedValidator<>(lastNameProperty(), lastName -> {
            if (lastName == null || lastName.isEmpty()) {
                return ValidationMessage.error("Lastname may not be empty");
            } else if (lastName.trim().isEmpty()) {
                return ValidationMessage.error("Lastname may not only contain whitespaces");
            }

            return null;
        });

        formValidator.addValidators(
                firstnameValidator,
                lastnameValidator,
                emailValidator);
    }

    public void initialize() {

        mapSettings(service.activeUser());

        dialogScope.subscribe(ProfileDialogScope.COMMIT, (s, objects) -> {
            commitChanges();
        });

        dialogScope.userFormValidProperty().bind(formValidator.getValidationStatus().validProperty());
    }

    private void mapSettings(User u) {
        firstName.set(u.getFirstName());
        lastName.set(u.getLastName());
        email.set(u.getEmail());
        tooltips.set(u.getShowTooltips());
        markers.set(u.getShowMarkers());

        lexicons.addAll(Dictionaries.getDictionary(Dictionaries.LEXICON_DICTIONARY));
        Arrays.asList(u.getLexicons().split(";"))
                .forEach(id -> {
                    Platform.runLater(() -> {
                        checkModel.check(Dictionaries.getDictionaryById(Dictionaries.LEXICON_DICTIONARY, Long.parseLong(id)));
                    });
                });
    }

    private void commitChanges() {
        service.activeUser().setEmail(email.get());
        service.activeUser().setFirstName(firstName.get());
        service.activeUser().setLastName(lastName.get());
        service.activeUser().setShowTooltips(tooltips.get());
        service.activeUser().setShowMarkers(markers.get());
        String lexicons = checkModel.getCheckedItems().stream()
                .map(d -> d.getId().toString())
                .collect(Collectors.joining(";"));
        service.activeUser().setLexicons(lexicons);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public boolean isTooltips() {
        return tooltips.get();
    }

    public BooleanProperty tooltipsProperty() {
        return tooltips;
    }

    public void setTooltips(boolean tooltips) {
        this.tooltips.set(tooltips);
    }

    public boolean isMarkers() {
        return markers.get();
    }

    public BooleanProperty markersProperty() {
        return markers;
    }

    public void setMarkers(boolean markers) {
        this.markers.set(markers);
    }

    public ObservableList<Dictionary> getLexicons() {
        return lexicons;
    }

    public void setLexicons(ObservableList<Dictionary> lexicons) {
        this.lexicons = lexicons;
    }

    public IndexedCheckModel<Dictionary> getCheckModel() {
        return checkModel;
    }

    public void setCheckModel(IndexedCheckModel<Dictionary> checkModel) {
        this.checkModel = checkModel;
    }

    public ValidationStatus getFirstnameValidator() {
        return firstnameValidator.getValidationStatus();
    }

    public ValidationStatus getLastnameValidator() {
        return lastnameValidator.getValidationStatus();
    }

    public ValidationStatus getEmailValidator() {
        return emailValidator.getValidationStatus();
    }

    public ValidationStatus getFormValidator() {
        return formValidator.getValidationStatus();
    }
}

