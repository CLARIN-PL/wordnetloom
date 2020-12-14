package pl.edu.pwr.wordnetloom.client.ui.users;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.edu.pwr.wordnetloom.client.events.ReloadUsersEvent;
import pl.edu.pwr.wordnetloom.client.events.ShowCorpusExampleEvent;
import pl.edu.pwr.wordnetloom.client.model.UserSimple;
import pl.edu.pwr.wordnetloom.client.service.RemoteService;
import pl.edu.pwr.wordnetloom.client.ui.alerts.AlertDialogHandler;
import pl.edu.pwr.wordnetloom.client.ui.scopes.UsersDialogScope;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class UsersDialogViewModel implements ViewModel {

    public static final String OPEN_CREATE_USER_DIALOG = "user_create";
    public static final String OPEN_DELETE_USER_DIALOG = "user_delete";
    public static final String RELOAD_USER_LIST = "user_delete";
    public static final String OPEN_CHANGE_PASSWORD_DIALOG = "usr_change_password";
    public static final String CLOSE_DIALOG_NOTIFICATION = "close_dialog";

    @Inject
    RemoteService service;

    @Inject
    AlertDialogHandler dialogHandler;

    @Inject
    UsersDialogScope dialogScope;

    private ObservableList<UserListItemViewModel> userList = FXCollections.observableArrayList();
    private ObjectProperty<UserListItemViewModel> selectedUserListItem = new SimpleObjectProperty<>();

    private final StringProperty title = new SimpleStringProperty("Users");

    private Command createCommand;
    private Command deleteCommand;
    private Command changePasswordCommand;

    public Command getCreteCommand() {
        return createCommand;
    }

    public Command getDeleteCommand() {
        return deleteCommand;
    }

    public Command getChangePasswordCommand() {
        return changePasswordCommand;
    }


    public UsersDialogViewModel() {
    }

    public void initialize() {

        loadUsers();

        dialogScope.userProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadUsers();
            }
        });

        selectedUserListItem.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
            }
        });

        createCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                create();
            }
        });
        deleteCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                delete();
            }
        });
        changePasswordCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                changePassword();
            }
        });
    }

    public void onOnReloadUsers(@Observes ReloadUsersEvent event){
        loadUsers();
    }

    private void loadUsers(){
        try {
            userList.clear();
            List<UserSimple> u = service.getUsers();
            userList.addAll(u.stream()
                    .map(UserListItemViewModel::new)
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            dialogHandler.handleErrors(e);
        }
    }
    private void create() {
        publish(OPEN_CREATE_USER_DIALOG);
    }

    private void delete() {
        if(selectedUserListItem.get() != null) {
            service.delete(selectedUserListItem.get().getUser().getLinks().getSelf());
            loadUsers();
        }
    }

    private void changePassword() {
    }

    public StringProperty titleProperty() {
        return title;
    }

    public ObservableList<UserListItemViewModel> getUserList() {
        return userList;
    }

    public UserListItemViewModel getSelectedUserListItem() {
        return selectedUserListItem.get();
    }

    public ObjectProperty<UserListItemViewModel> selectedUserListItemProperty() {
        return selectedUserListItem;
    }
}

