package pl.edu.pwr.wordnetloom.server.business.revisions.control;

import pl.edu.pwr.wordnetloom.server.business.revisions.entity.RevisionsInfo;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@ApplicationScoped
public class RevisionsQueryService {

    @PersistenceContext
    EntityManager em;

    public List<String> findAllUsersEmails() {
        return em.createNamedQuery(RevisionsInfo.FIND_ALL_USERS, String.class)
                .getResultList();
    }

    public List<RevisionsInfo> findAllUsersEmailsByTimestamps(long begin, long end) {
        return em.createNamedQuery(RevisionsInfo.FIND_USERS_BY_TIMESTAMPS, RevisionsInfo.class)
                .setParameter("timestamp_start", begin)
                .setParameter("timestamp_end", end)
                .getResultList();
    }
}
