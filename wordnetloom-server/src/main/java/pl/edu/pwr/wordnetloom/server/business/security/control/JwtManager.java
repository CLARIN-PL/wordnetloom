package pl.edu.pwr.wordnetloom.server.business.security.control;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import pl.edu.pwr.wordnetloom.server.business.user.entity.User;

@ApplicationScoped
public class JwtManager {

    public String createUserString(User activeUser) {
        JsonObjectBuilder claimsBuilder = Json.createObjectBuilder()
                .add("sub", activeUser.getEmail())
                .add("first_name", activeUser.getFirstname())
                .add("last_name", activeUser.getLastname())
                .add("email", activeUser.getEmail())
                .add("lexicons", activeUser.getSettings().getChosenLexicons())
                .add("show_marker", activeUser.getSettings().getShowToolTips())
                .add("show_tooltips", activeUser.getSettings().getShowToolTips());

        return claimsBuilder.build().toString();
    }
}
