package pl.edu.pwr.wordnetloom.server.business.user.boundary;

import pl.edu.pwr.wordnetloom.server.business.OperationResult;
import pl.edu.pwr.wordnetloom.server.business.user.control.UserFinder;
import pl.edu.pwr.wordnetloom.server.business.user.entity.Role;
import pl.edu.pwr.wordnetloom.server.business.user.entity.User;
import pl.edu.pwr.wordnetloom.server.business.user.entity.UserSettings;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.naming.AuthenticationException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Transactional
@RequestScoped
public class UserService {

    @Inject
    UserFinder service;

    @PersistenceContext
    EntityManager em;

    public String encryptPassword(final String password) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        md.update(password.getBytes());
        return Base64.getMimeEncoder().encodeToString(md.digest());
    }

    public void changePassword(String email, String password) {
        service.findByEmail(email)
                .ifPresent(u -> {
                    u.setPassword(encryptPassword(password));
                    em.merge(u);
                });
    }

    public List<User> findAllUsers() {
        return em.createNamedQuery(User.FIND_ALL, User.class)
                .getResultList();
    }

    public Optional<User> findById(long id) {
        try {
            return Optional.of(
                    em.createNamedQuery(User.FIND_BY_ID, User.class)
                            .setParameter("id", id)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public OperationResult<User> createUser(JsonObject json) {
        OperationResult<User> result = new OperationResult<>();

        String email="";

        if (!json.isNull("email") && !json.getString("email").isEmpty()) {
                email = json.getString("email");
        } else {
            result.addError("email", "May not be empty");
        }

        Optional<User> exists = service.findByEmail(email);
        if (!exists.isPresent()) {
            User u = new User();

            UserSettings settings = new UserSettings();
            settings.setChosenLexicons("1;2;");
            settings.setShowToolTips(true);
            settings.setLexiconMarker(true);
            u.setEmail(email);

            if (!json.isNull("first_name") && !json.getString("first_name").isEmpty()) {
                u.setFirstname(json.getString("first_name"));
            } else {
                result.addError("first_name", "May not be empty");
            }

            if (!json.isNull("last_name") && !json.getString("last_name").isEmpty()) {
                u.setLastname(json.getString("last_name"));
            } else {
                result.addError("last_name", "May not be empty");
            }

            if (!json.isNull("password") && !json.getString("password").isEmpty()) {
                u.setPassword(encryptPassword(json.getString("password")));
            } else {
                result.addError("password", "May not be empty");
            }

            if (!json.isNull("role") && !json.getString("role").isEmpty()) {
                u.setRole(Role.valueOf(json.getString("role")));
            } else {
                result.addError("role", "May not be empty");
            }


            if (!result.hasErrors()) {
                em.persist(u);
                settings.setUser(u);
                em.persist(settings);
                result.setEntity(u);
            }
        }
        return result;
    }

    public void deleteUser(Long id) {
        findById(id)
                .ifPresent(e -> em.remove(e));
    }
}
