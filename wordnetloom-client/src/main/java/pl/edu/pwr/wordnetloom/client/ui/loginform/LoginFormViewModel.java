package pl.edu.pwr.wordnetloom.client.ui.loginform;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.itemlist.ItemList;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.model.Language;
import pl.edu.pwr.wordnetloom.client.model.User;
import pl.edu.pwr.wordnetloom.client.ui.scopes.LoginDialogScope;

import java.util.Optional;

public class LoginFormViewModel implements ViewModel {

    private ObservableList<String> languages;
    private final ObjectProperty<Language> language = new SimpleObjectProperty<>(Language.English);
    private final StringProperty selectedLanguage = new SimpleStringProperty(Language.English.getName());

    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();

    private final ReadOnlyBooleanWrapper valid = new ReadOnlyBooleanWrapper(true);

    @InjectScope
    LoginDialogScope dialogScope;

    private User user;

    // Don't inline this field. It's needed to prevent the list mapping from being garbage collected.
    private ItemList<Language> languageItemList;

    public void signInAction() {
        dialogScope.publish(LoginDialogScope.OK_BEFORE_COMMIT);
    }

    public void initialize() {
        dialogScope.subscribe(LoginDialogScope.RESET_FORMS, (key, payload) -> resetForm());
        dialogScope.subscribe(LoginDialogScope.COMMIT, (key, payload) -> commitChanges());

        ObjectProperty<User> userProperty = dialogScope.userProperty();

        if (userProperty.get() != null) {
            initWithUser(userProperty.get());
        }

       userProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                initWithUser(newValue);
            }
       });

       initLanguageList();

       selectedLanguage.addListener((obs, oldV, newV) -> {
                Optional<Language> matchingLanguage = Language.getLanguages().stream()
                        .filter(language -> newV.equals(language.getName()))
                        .findFirst();
                matchingLanguage.ifPresent(language::set);
        });

       dialogScope.loginFormValidProperty().bind(valid);
    }

    private void initLanguageList() {
        languageItemList = new ItemList<>(Language.getLanguages(), Language::getName);
        ObservableList<String> mappedList = languageItemList.getTargetList();

        languages = createList(mappedList);
        languages.addListener((ListChangeListener<String>) c -> selectedLanguage.set(Language.English.getName()));
    }

    private void commitChanges() {
       user.setUsername(username.get());
       user.setPassword(password.get());
       user.setLanguage(language.get());
    }

    public void initWithUser(User user) {
        this.user = user;
        username.set(user.getUsername());
        password.set(user.getPassword());
    }

    private void resetForm() {
        username.set("");
        password.set("");
        selectedLanguage.set(Language.English.getName());
    }

    static ObservableList<String> createList(ObservableList<String> source) {
        final ObservableList<String> result = FXCollections.observableArrayList();
        result.addAll(source);

        // for sure there are better solutions for this but it's sufficient for our demo
        source.addListener((ListChangeListener<String>) c -> {
            result.clear();
            result.addAll(source);
        });
        return result;
    }

    public ObservableList<String> languagesList() {
        return languages;
    }

    public StringProperty selectedLanguageProperty() {
        return selectedLanguage;
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty passwordProperty() {
        return password;
    }
}
