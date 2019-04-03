package pl.edu.pwr.wordnetloom.server.business.localistaion.control;

import pl.edu.pwr.wordnetloom.server.business.localistaion.entity.LocalisedString;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class LocalisedStringCommandService {

    @PersistenceContext
    EntityManager em;

    @Inject
    LocalisedStringsQueryService locator;

    public LocalisedString save(LocalisedString str) {
        if (str.getKey().getId() == null) {
            Long id = locator.findMax().get() + 1;
            locator.getSupportedLanguages()
                    .forEach(lang -> {
                        str.getKey().setId(id);
                        str.getKey().setLanguage(lang);
                        em.persist(str);
                    });
        } else {
            em.merge(str);
        }
        em.flush();
        return str;
    }

    public void remove(Long id){
        List<LocalisedString> list = locator.findAllById(id);
        list.forEach(s -> em.remove(s));
    }
}
