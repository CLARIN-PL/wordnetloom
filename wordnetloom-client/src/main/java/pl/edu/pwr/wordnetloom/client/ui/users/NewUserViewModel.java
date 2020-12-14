package pl.edu.pwr.wordnetloom.client.ui.users;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.itemlist.ItemList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.events.ReloadUsersEvent;
import pl.edu.pwr.wordnetloom.client.model.Roles;
import pl.edu.pwr.wordnetloom.client.model.UserSimple;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.alerts.AlertDialogHandler;
import pl.edu.pwr.wordnetloom.client.ui.scopes.UsersDialogScope;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class NewUserViewModel implements ViewModel {

    public static final String CLOSE_DIALOG_NOTIFICATION = "close_dialog";

    private ObservableList<String> roles;
    private final ObjectProperty<Roles> role = new SimpleObjectProperty<>(Roles.USER);
    private final StringProperty selectedRole = new SimpleStringProperty(Roles.USER.getName());

    private final StringProperty firstname = new SimpleStringProperty();
    private final StringProperty lastname = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();

    private ItemList<Roles> roleItemList;

    private Command saveCommand;

    @Inject
    RemoteService service;

    @Inject
    AlertDialogHandler dialogHandler;

    @Inject
    UsersDialogScope dialogScope;

    @Inject
    Event<ReloadUsersEvent> publisherReloadUsers;

    public void initialize() {
        initLanguageList();

        saveCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                save();
            }
        });
    }

    private void save() {
        UserSimple us = new UserSimple();
        us.setEmail(email.get());
        us.setFirstName(firstname.get());
        us.setLastName(lastname.get());
        us.setRole(selectedRole.get());
        us.setPassword(password.get());
        try {
            us = service.saveUser(us);
            publisherReloadUsers.fire( new ReloadUsersEvent());
            publish(CLOSE_DIALOG_NOTIFICATION);
        } catch (IOException e) {
            dialogHandler.handleErrors(e);
        }
    }

    public Command getSaveCommand() {
        return saveCommand;
    }


    private void initLanguageList() {
        roleItemList = new ItemList<>(Roles.getRoles(), Roles::getName);
        ObservableList<String> mappedList = roleItemList.getTargetList();

        roles = createList(mappedList);
        roles.addListener((ListChangeListener<String>) c -> selectedRole.set(Roles.USER.getName()));
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

    public ObservableList<String> getRoles() {
        return roles;
    }

    public void setRoles(ObservableList<String> roles) {
        this.roles = roles;
    }

    public Roles getRole() {
        return role.get();
    }

    public ObjectProperty<Roles> roleProperty() {
        return role;
    }

    public void setRole(Roles role) {
        this.role.set(role);
    }

    public String getSelectedRole() {
        return selectedRole.get();
    }

    public StringProperty selectedRoleProperty() {
        return selectedRole;
    }

    public void setSelectedRole(String selectedRole) {
        this.selectedRole.set(selectedRole);
    }

    public String getFirstname() {
        return firstname.get();
    }

    public StringProperty firstnameProperty() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname.set(firstname);
    }

    public String getLastname() {
        return lastname.get();
    }

    public StringProperty lastnameProperty() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname.set(lastname);
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

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public ItemList<Roles> getRoleItemList() {
        return roleItemList;
    }

    public void setRoleItemList(ItemList<Roles> roleItemList) {
        this.roleItemList = roleItemList;
    }
}

