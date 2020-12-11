package pl.edu.pwr.wordnetloom.server.business.user.control;

import pl.edu.pwr.wordnetloom.server.business.user.entity.User;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Stateless
public class UserFinder {

    @PersistenceContext
    EntityManager em;

    @Inject
    Principal principal;

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
        return findByEmail(principal.getName());
    }
}