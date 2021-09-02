package pl.edu.pwr.wordnetloom.server.business.revisions.control;

import io.quarkus.arc.Unremovable;
import org.hibernate.envers.RevisionListener;
import pl.edu.pwr.wordnetloom.server.business.revisions.entity.RevisionsInfo;
import pl.edu.pwr.wordnetloom.server.business.user.control.UserControl;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Dependent
@Unremovable
public class RevisionsListener implements RevisionListener {

    @Inject
    UserControl userControl;

    @Override
    public void newRevision(Object revisionEntity) {
        RevisionsInfo revisionsInfo = (RevisionsInfo) revisionEntity;

        revisionsInfo.setUserEmail(userControl.getCurrentUser().get().getEmail());

        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        revisionsInfo.setTimestamp(Timestamp.valueOf(localDateTime).getTime());
    }
}
