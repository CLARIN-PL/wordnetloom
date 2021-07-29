package pl.edu.pwr.wordnetloom.server.business.security.control;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import pl.edu.pwr.wordnetloom.server.business.user.entity.User;

@ApplicationScoped
public class JwtManager {
    private static final int TOKEN_VALIDITY = 14400;
    private static final String CLAIM_ROLES = "groups";
    private static final String ISSUER = "wordnetloom-jwt-issuer";
    private static final String AUDIENCE = "jwt-audience";
    private String keyCloakToken = "myToken";
    private User activeUser;

    public String createJwt(final User user) {
        // TODO: keycloak auth

        activeUser = user;
        return keyCloakToken;
    }

    public String createUserString() {
        JsonArrayBuilder rolesBuilder = Json.createArrayBuilder();
        rolesBuilder.add(activeUser.getRole().name());

        JsonObjectBuilder claimsBuilder = Json.createObjectBuilder()
                .add("sub", activeUser.getEmail())
                .add("first_name", activeUser.getFirstname())
                .add("last_name", activeUser.getLastname())
                .add("email", activeUser.getEmail())
                .add("lexicons", activeUser.getSettings().getChosenLexicons())
                .add("show_marker", activeUser.getSettings().getShowToolTips())
                .add("show_tooltips", activeUser.getSettings().getShowToolTips())
                .add("iss", ISSUER)
                .add("aud", AUDIENCE)
                .add(CLAIM_ROLES, rolesBuilder.build().toString())
                .add("exp", ((System.currentTimeMillis() / 1000) + TOKEN_VALIDITY));

        return claimsBuilder.build().toString();
    }

    public boolean checkToken(String token) {
        return token.equals(keyCloakToken);
    }
}