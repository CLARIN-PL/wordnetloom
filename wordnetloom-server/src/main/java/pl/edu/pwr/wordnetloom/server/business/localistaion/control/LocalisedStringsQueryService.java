package pl.edu.pwr.wordnetloom.server.business.localistaion.control;

import pl.edu.pwr.wordnetloom.server.business.localistaion.entity.LocalisedString;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Startup
@Singleton
public class LocalisedStringsQueryService {

    @PersistenceContext
    EntityManager em;

    private Map<String, Map<Long, String>> localisedStrings;

    public final String defaultLanguage = "en";

    @PostConstruct
    void init() {
        reload();
    }

    public void reload() {
        localisedStrings = new ConcurrentHashMap<>(findAll());
    }

    public Set<String> getSupportedLanguages(){
        return localisedStrings.keySet();
    }
    public String find(final Long id, final Locale locale) {
        String lang = defaultLanguage;
        if (locale != null) {
            String l = locale.getLanguage().substring(0, 2);
            lang = localisedStrings.containsKey(l) ? l : defaultLanguage;
        }

        if (localisedStrings.get(lang).containsKey(id)) {
            return localisedStrings.get(lang).get(id);
        }
        return id != null ? id.toString() : null;
    }

    public Long findId(final String value, final Locale locale) {
        String lang = defaultLanguage;
        if (locale != null) {
            String l = locale.getLanguage().substring(0, 2);
            lang = localisedStrings.containsKey(l) ? l : defaultLanguage;
        }
        for (Map.Entry<Long, String> entry : localisedStrings.get(lang).entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Optional<LocalisedString> findById(Long id, String lang) {
        try {
            return Optional.of(
                    em.createNamedQuery(LocalisedString.FIND_BY_ID, LocalisedString.class)
                            .setParameter("id", id)
                            .setParameter("lang", lang)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<LocalisedString> findAllById(Long id) {
        return em.createNamedQuery(LocalisedString.FIND_ALL_BY_ID, LocalisedString.class)
                .setParameter("id", id)
                .getResultList();
    }

    public Optional<Long> findMax() {
        try {
            return Optional.of(
                    em.createNamedQuery(LocalisedString.FIND_MAX_ID, Long.class)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    private Map<String, Map<Long, String>> findAll() {
        final List<LocalisedString> list = em.createNamedQuery(LocalisedString.FIND_ALL, LocalisedString.class)
                .getResultList();
        return list.stream()
                .collect(Collectors.groupingBy(o -> o.getKey().getLanguage(),
                        Collectors.toMap(c -> c.getKey().getId(), LocalisedString::getValue)));
    }
}
