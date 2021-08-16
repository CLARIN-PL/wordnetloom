package pl.edu.pwr.wordnetloom.server.business.user.control;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import pl.edu.pwr.wordnetloom.server.business.user.entity.User;
import pl.edu.pwr.wordnetloom.server.business.user.entity.UserSettings;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;


@RequestScoped
public class UserControl {

    private static final Logger log = Logger.getLogger(UserControl.class.getName());

    @Inject
    JsonWebToken accessToken;

    @ConfigProperty(name = "keycloak.admin.login")
    String keycloakAdminLogin;

    @ConfigProperty(name = "keycloak.admin.password")
    String keycloakAdminPassword;

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String keycloakServerUrl;

    @ConfigProperty(name = "keycloak.admin.url")
    String keycloakAdminUrl;

    public Optional<User> getCurrentUser(){
        User user = new User();
        UserSettings userSettings = new UserSettings();

        user.setFirstname(accessToken.getClaimNames().contains("given_name") ? accessToken.getClaim("given_name") : " ");
        user.setLastname(accessToken.getClaimNames().contains("family_name") ? accessToken.getClaim("family_name") : " ");
        user.setEmail(accessToken.getClaim("email"));

        userSettings.setLexiconMarker(Boolean.parseBoolean(
                accessToken.getClaimNames().contains("lexicon_marker") ?
                        accessToken.getClaim("lexicon_marker") : "true"));

        userSettings.setShowToolTips(Boolean.parseBoolean(
                accessToken.getClaimNames().contains("show_tool_tips") ?
                        accessToken.getClaim("show_tool_tips") : "true"));

        userSettings.setChosenLexicons(accessToken.getClaimNames().contains("chosen_lexicons") ?
                accessToken.getClaim("chosen_lexicons") : "1");

        user.setSettings(userSettings);

        return Optional.of(user);
    }

    public void saveUserSettings(User user) {
        String userId = accessToken.getClaim("user_id");
        HashMap<String, String> userSettingsHashMap = createUserAttributesHashMap(user);
        HashMap<String, String> userBasicInfoHashMap = createUserBasicInfoHashMap(user);

        setUserAttribute(userId, userSettingsHashMap, userBasicInfoHashMap);
    }

    private HashMap<String, String> createUserAttributesHashMap(User user) {
        return new HashMap<>() {{
           put("lexicon_marker", user.getSettings().getLexiconMarker().toString());
           put("show_tool_tips", user.getSettings().getShowToolTips().toString());
           put("chosen_lexicons", user.getSettings().getChosenLexicons());
        }};
    }

    private HashMap<String, String> createUserBasicInfoHashMap(User user) {
        return new HashMap<>() {{
           put("firstName", user.getFirstname());
           put("lastName", user.getLastname());
        }};
    }

    private String obtainKeycloakAdminToken() {
            String url = keycloakServerUrl + "/protocol/openid-connect/token";
        String urlParameters = "username=" + keycloakAdminLogin +
                "&password=" + keycloakAdminPassword + "&grant_type=password&client_id=admin-cli";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection con;

        try {
            URL myurl = new URL(url);
            con = (HttpURLConnection) myurl.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postData);
            }

            StringBuilder content;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String line;
                content = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }
            JSONObject json = (JSONObject) new JSONParser().parse(content.toString());
            log.info("token: " + json.get("access_token").toString());
            return json.get("access_token").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private void setUserAttribute(String userId,
                                  HashMap<String, String> userAttributesMap,
                                  HashMap<String, String> userBasicInfoMap) {
        String url = keycloakAdminUrl + "/users/" + userId;
        String accessToken = obtainKeycloakAdminToken();
        HttpURLConnection con;

        String jsonString = createUserInfoJson(userAttributesMap, userBasicInfoMap).toString();
        byte[] putData = jsonString.getBytes(StandardCharsets.UTF_8);

        try {
            URL myurl = new URL(url);
            con = (HttpURLConnection) myurl.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("PUT");
            con.setRequestProperty("Authorization", "Bearer " + accessToken);
            con.setRequestProperty("Content-Type", "application/json");

            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(putData);
            }

            log.info(con.getResponseCode() + " ");
            log.info(jsonString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private org.json.JSONObject createUserInfoJson(HashMap<String, String> userAttributesMap,
                                                   HashMap<String, String> basicInfo) {
        org.json.JSONObject jsonAttributesObject = new org.json.JSONObject();

        for (String key : userAttributesMap.keySet())
            jsonAttributesObject.put(key, userAttributesMap.get(key));

        org.json.JSONObject jsonUserObject = new org.json.JSONObject()
                .put("attributes", jsonAttributesObject);

        for (String key : basicInfo.keySet())
            jsonUserObject.put(key, basicInfo.get(key));

        return jsonUserObject;
    }
}
