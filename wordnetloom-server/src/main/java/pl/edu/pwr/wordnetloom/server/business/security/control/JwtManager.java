package pl.edu.pwr.wordnetloom.server.business.security.control;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import pl.edu.pwr.wordnetloom.server.business.user.entity.User;

@ApplicationScoped
public class JwtManager {
    private static final String CLAIM_ROLES = "groups";

    public String createUserString(User activeUser) {
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
                .add(CLAIM_ROLES, rolesBuilder.build().toString());

        return claimsBuilder.build().toString();
    }
}