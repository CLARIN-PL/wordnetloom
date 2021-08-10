package pl.edu.pwr.wordnetloom.server.business.revisions;

import io.quarkus.arc.Unremovable;
import org.hibernate.envers.RevisionListener;
import pl.edu.pwr.wordnetloom.server.business.user.control.UserFinder;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Unremovable
public class RevisionsListener implements RevisionListener {

    @Inject
    UserFinder userFinder;

    @Override
    public void newRevision(Object revisionEntity) {
        RevisionsInfo revisionsInfo = (RevisionsInfo) revisionEntity;
        revisionsInfo.setUserEmail(userFinder.getCurrentUser().get().getEmail());
    }
}
