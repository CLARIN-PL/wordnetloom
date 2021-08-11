package pl.edu.pwr.wordnetloom.server;

import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationInfo;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@ApplicationScoped
public class DbMigrator {
    private final Logger log = Logger.getLogger(DbMigrator.class.getName());

    @Inject
    Flyway flyway;

    @ConfigProperty(name = "quarkus.flyway.migrate-at-start")
    Boolean isEnable;

    @PostConstruct
    public void onStartup() {
        if (isEnable) {
            log.log(Level.INFO, "Flyway is running.");
            for (MigrationInfo i : flyway.info().all()) {
                log.log(Level.INFO, "Migrate task: {0} : {1} from file: {2}", new Object[]{i.getVersion(), i.getDescription(), i.getScript()});
            }

            try{
                flyway.migrate();
            } catch (FlywayException e){
                e.printStackTrace();
                flyway.repair();
            }
        } else {
            log.log(Level.INFO, "Flyway deactivated.");
        }
    }
}
