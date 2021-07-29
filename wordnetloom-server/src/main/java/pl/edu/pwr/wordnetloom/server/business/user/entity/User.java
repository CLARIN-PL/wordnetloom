package pl.edu.pwr.wordnetloom.server.business.user.entity;

import org.hibernate.annotations.NamedQuery;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "tbl_users")
@NamedQuery(name = User.FIND_BY_EMAIL, query = "SELECT u FROM User u " +
        "JOIN FETCH u.settings " +
        "WHERE u.email = :email")
@NamedQuery(name = User.FIND_ALL, query = "SELECT u FROM User u")
@NamedQuery(name = User.FIND_BY_ID, query = "SELECT u FROM User u WHERE u.id = :id")
public class User implements Serializable {

    public static final String FIND_BY_EMAIL = "User.findByEmail";
    public static final String FIND_ALL = "User.findAll";
    public static final String FIND_BY_ID = "User.findById";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotNull
    @Size(min = 3)
    private String firstname;

    @NotNull
    private String lastname;

    @NotNull
    private String email;

    @NotNull
    @Size(min = 8, max = 64)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserSettings settings;

    public User() {
    }

    public User(String firstname, String lastname, String email, String password, Role role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFullname() {
        return firstname + " " + lastname;
    }

    public UserSettings getSettings() {
        return settings;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }
}
