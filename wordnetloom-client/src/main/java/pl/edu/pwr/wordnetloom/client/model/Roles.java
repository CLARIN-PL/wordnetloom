package pl.edu.pwr.wordnetloom.client.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Locale;

public enum Roles {
    ADMIN("Administrator"),
    USER("User");

    private final static ObservableList<Roles> roles = FXCollections.observableArrayList();

    private final String fullname;

    static {
        roles.addAll(Roles.values());
    }

    Roles(String fullname) {
        this.fullname = fullname;
    }

    public String getFullName() {
        return fullname;
    }

    public String getName(){ return  name();}

    public static Roles get(String fullname) {
        return roles
                .stream()
                .filter(l -> l.fullname.equals(fullname))
                .findFirst().orElse(USER);
    }

    public static ObservableList<Roles> getRoles() { return roles; }
}
