package pl.edu.pwr.wordnetloom.server.business.security.boundary;

import pl.edu.pwr.wordnetloom.server.business.OperationResult;
import pl.edu.pwr.wordnetloom.server.business.user.control.UserFinder;
import pl.edu.pwr.wordnetloom.server.business.user.entity.User;
import pl.edu.pwr.wordnetloom.server.business.user.entity.UserSettings;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.naming.AuthenticationException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@Transactional
@RequestScoped
public class SecurityService {

    @Inject
    UserFinder service;

    @PersistenceContext
    EntityManager em;

    public User authenticate(final String email, final String password) throws AuthenticationException {
        User user = service.findByEmail(email).orElseThrow(AuthenticationException::new);
        if (user.getPassword().equals(encryptPassword(password))) {
            return user;
        }
        throw new AuthenticationException("Failed logging in org.jboss.user with name '" + email + "': unknown username or wrong password");
    }

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
                    u.setPassword(password);
                    em.merge(u);
                });
    }

    public OperationResult<User> updateUser(String email, JsonObject json) {
        OperationResult<User> result = new OperationResult<>();

        Optional<User> u = service.findByEmail(email);
        if (u.isPresent()) {
            if (!json.isNull("email") && !json.getString("email").isEmpty()) {
                u.get().setEmail(json.getString("email"));
            } else {
                result.addError("email", "May not be empty");
            }

            if (!json.isNull("first_name") && !json.getString("first_name").isEmpty()) {
                u.get().setFirstname(json.getString("first_name"));
            } else {
                result.addError("first_name", "May not be empty");
            }

            if (!json.isNull("last_name") && !json.getString("last_name").isEmpty()) {
                u.get().setLastname(json.getString("last_name"));
            } else {
                result.addError("last_name", "May not be empty");
            }

            if (!json.isNull("lexicons")) {
                u.get().getSettings().setChosenLexicons(json.getString("lexicons"));
            } else {
                result.addError("lexicons", "May not be empty");
            }

            if (!json.isNull("show_tooltips")) {
                u.get().getSettings().setShowToolTips(json.getBoolean("show_tooltips"));
            }

            if (!json.isNull("show_markers")) {
                u.get().getSettings().setLexiconMarker(json.getBoolean("show_markers"));
            }

            if (!result.hasErrors()) {
                em.merge(u.get());
                result.setEntity(u.get());
            }
        }
        return result;
    }
}
