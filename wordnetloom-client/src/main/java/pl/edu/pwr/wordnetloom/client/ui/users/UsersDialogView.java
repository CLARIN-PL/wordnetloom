package pl.edu.pwr.wordnetloom.client.ui.users;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.*;
import de.saxsys.mvvmfx.utils.viewlist.CachedViewModelCellFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import pl.edu.pwr.wordnetloom.client.ui.DialogHelper;


import javax.inject.Inject;

public class UsersDialogView implements FxmlView<UsersDialogViewModel> {

    @FXML
    public ListView users;

    @FXML
    public Button createButton, deleteButton;  //changePasswordButton;

    @InjectViewModel
    private UsersDialogViewModel viewModel;

    @Inject
    public Stage primeDialog;

    @InjectContext
    private Context context;

    public void initialize() {
        initIcons();
        viewModel.subscribe(UsersDialogViewModel.CLOSE_DIALOG_NOTIFICATION, (key, payload) -> {
            primeDialog.close();
        });

        viewModel.subscribe(UsersDialogViewModel.OPEN_CREATE_USER_DIALOG, (key, payload) -> {
                    ViewTuple<NewUserView, NewUserViewModel> load = FluentViewLoader
                            .fxmlView(NewUserView.class)
                            .context(context)
                            .load();

                    Parent view = load.getView();
                    Stage showDialog = DialogHelper.showDialog(view, primeDialog, "/wordnetloom.css");
                    load.getCodeBehind().setDisplayingStage(showDialog);
                    showDialog.toFront();
                });

        users.setItems(viewModel.getUserList());
        users.setCellFactory(CachedViewModelCellFactory.createForFxmlView(UserListItemView.class));
        viewModel.selectedUserListItemProperty().bind(users.getSelectionModel().selectedItemProperty());
    }

    public void setDisplayingStage(Stage showDialog) {
        this.primeDialog = showDialog;
        this.primeDialog.titleProperty().bind(viewModel.titleProperty());
        showDialog.setOnCloseRequest(event -> {
            showDialog.close();
        });
    }

    private void initIcons(){
        AwesomeDude.setIcon(createButton, AwesomeIcon.USER, "11");
        AwesomeDude.setIcon(deleteButton, AwesomeIcon.TRASH, "11");
      //  AwesomeDude.setIcon(changePasswordButton, AwesomeIcon.LOCK, "11");
    }


    public void create(ActionEvent actionEvent) {
        viewModel.getCreteCommand().execute();
    }

    public void delete(ActionEvent actionEvent) {
        viewModel.getDeleteCommand().execute();
    }

/*    public void changePassword(ActionEvent actionEvent) {
        viewModel.getChangePasswordCommand().execute();
    }*/
}
