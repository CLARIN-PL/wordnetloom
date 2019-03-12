package pl.edu.pwr.wordnetloom.server.business.security.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import pl.edu.pwr.wordnetloom.server.business.user.entity.User;

@ApplicationScoped
public class JwtManager {
    static {
        FileInputStream fis = null;
        char[] password = "secret".toCharArray();
        String alias = "alias";
        PrivateKey pk = null;
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            String configDir = System.getProperty("jboss.server.config.dir");
            String keystorePath = configDir + File.separator + "jwt.keystore";
            fis = new FileInputStream(keystorePath);
            ks.load(fis, password);
            Key key = ks.getKey(alias, password);
            if (key instanceof PrivateKey) {
                pk = (PrivateKey) key;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
        privateKey = pk;
    }

    private static final PrivateKey privateKey;
    private static final int TOKEN_VALIDITY = 14400;
    private static final String CLAIM_ROLES = "groups";
    private static final String ISSUER = "wordnetloom-jwt-issuer";
    private static final String AUDIENCE = "jwt-audience";

    public String createJwt(final User user) throws Exception {

        JWSSigner signer = new RSASSASigner(privateKey);

        JsonArrayBuilder rolesBuilder = Json.createArrayBuilder();
        rolesBuilder.add(user.getRole().name());

        JsonObjectBuilder claimsBuilder = Json.createObjectBuilder()
                .add("sub", user.getEmail())
                .add("first_name", user.getFirstname())
                .add("last_name", user.getLastname())
                .add("email", user.getEmail())
                .add("lexicons", user.getSettings().getChosenLexicons())
                .add("show_marker", user.getSettings().getShowToolTips())
                .add("show_tooltips", user.getSettings().getShowToolTips())
                .add("iss", ISSUER)
                .add("aud", AUDIENCE)
                .add(CLAIM_ROLES, rolesBuilder.build().toString())
                .add("exp", ((System.currentTimeMillis() / 1000) + TOKEN_VALIDITY));

        JWSObject jwsObject = new JWSObject(new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(new JOSEObjectType("jwt")).build(),
                new Payload(claimsBuilder.build().toString()));

        jwsObject.sign(signer);

        return jwsObject.serialize();
    }
}