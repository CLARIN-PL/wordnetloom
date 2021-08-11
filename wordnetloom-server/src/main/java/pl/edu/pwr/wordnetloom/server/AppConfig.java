package pl.edu.pwr.wordnetloom.server;

import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@OpenAPIDefinition(
        info = @Info(
                title="Wornetloom-server API",
                version = "3.0.0",
                contact = @Contact(
                        name = "Wrocław University of Science and Technology (PWr)"),
                license = @License(
                        name = "GNU Lesser General Public License",
                        url = "http://www.gnu.org/licenses/")),
        components = @Components(
                securitySchemes = {
                        @SecurityScheme(
                                securitySchemeName = "bearerAuth",
                                type = SecuritySchemeType.HTTP,
                                scheme = "bearer",
                                bearerFormat = "oauth2"
                        )
                }
        )
)
@ApplicationPath("/wordnetloom-server/resources")
public class AppConfig extends Application {
}
