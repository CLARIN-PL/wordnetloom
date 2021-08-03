package pl.edu.pwr.wordnetloom.server.business.user.control;

import io.quarkus.security.identity.SecurityIdentity;
import pl.edu.pwr.wordnetloom.server.business.user.entity.User;
import java.util.Optional;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Transactional
@RequestScoped
public class UserFinder {

    @PersistenceContext
    EntityManager em;

    @Inject
    SecurityIdentity identity;

    public Optional<User> findByEmail(String email) {
        try {
            return Optional.of(
                    em.createNamedQuery(User.FIND_BY_EMAIL, User.class)
                            .setParameter("email", email)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<User> getCurrentUser(){
        return findByEmail(identity.getPrincipal().getName());
    }
}