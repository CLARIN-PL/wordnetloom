package pl.edu.pwr.wordnetloom.server.business.revisions;

import io.quarkus.arc.Unremovable;
import org.hibernate.envers.RevisionListener;
import pl.edu.pwr.wordnetloom.server.business.user.control.UserControl;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
@Unremovable
public class RevisionsListener implements RevisionListener {

    @Inject
    UserControl userControl;

    @Override
    public void newRevision(Object revisionEntity) {
        RevisionsInfo revisionsInfo = (RevisionsInfo) revisionEntity;
        revisionsInfo.setUserEmail(userControl.getCurrentUser().get().getEmail());
    }
}
