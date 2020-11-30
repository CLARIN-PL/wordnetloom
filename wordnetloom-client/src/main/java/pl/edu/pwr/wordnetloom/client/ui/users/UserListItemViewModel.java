package pl.edu.pwr.wordnetloom.client.ui.users;

import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import pl.edu.pwr.wordnetloom.client.model.UserSimple;


public class UserListItemViewModel implements ViewModel {

    private UserSimple user;
    private ObservableList<Node> children = FXCollections.observableArrayList();

    public UserListItemViewModel(UserSimple u) {
        this.user = u;
        Text role = new Text(u.getRole());
        role.setFill(Color.GREEN);
        children.add(role);
        children.add(new Text("  " + u.getFirstName() +" " + u.getLastName()));
        children.add(new Text(" (" + u.getEmail() + ")  "));
    }

    public UserSimple getUser() {
        return user;
    }


    public ObservableList<Node> getChildren() {
        return children;
    }
}

