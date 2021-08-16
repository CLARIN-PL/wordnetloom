package pl.edu.pwr.wordnetloom.server.business.security.boundary;

import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import pl.edu.pwr.wordnetloom.server.business.OperationResult;
import pl.edu.pwr.wordnetloom.server.business.security.entity.Jwt;
import pl.edu.pwr.wordnetloom.server.business.user.control.UserControl;
import pl.edu.pwr.wordnetloom.server.business.user.entity.User;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.inject.Inject;
import javax.json.JsonObject;
import java.util.Optional;

@Transactional
@RequestScoped
public class SecurityService {

    @Inject
    UserControl service;

    public Jwt authenticate(final String email, String password) {
        AuthzClient authzClient = AuthzClient.create();
        AuthorizationRequest request = new AuthorizationRequest();
        AuthorizationResponse response = authzClient.authorization(email, password).authorize(request);

        return new Jwt(response.getToken());
    }

    public OperationResult<User> updateUser(JsonObject json) {
        OperationResult<User> result = new OperationResult<>();

        Optional<User> u = service.getCurrentUser();
        if (u.isPresent()) {
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
                service.saveUserSettings(u.get());
                result.setEntity(u.get());
            }
        }
        return result;
    }
}
