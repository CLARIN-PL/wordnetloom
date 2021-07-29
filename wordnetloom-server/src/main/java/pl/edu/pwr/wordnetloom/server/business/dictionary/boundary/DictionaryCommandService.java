package pl.edu.pwr.wordnetloom.server.business.dictionary.boundary;

import pl.edu.pwr.wordnetloom.server.business.dictionary.control.DictionaryQueryService;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Dictionary;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Register;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Status;
import pl.edu.pwr.wordnetloom.server.business.localistaion.control.LocalisedStringCommandService;
import pl.edu.pwr.wordnetloom.server.business.localistaion.control.LocalisedStringsQueryService;
import pl.edu.pwr.wordnetloom.server.business.localistaion.entity.LocalisedString;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Locale;
import java.util.Optional;

@Transactional
@RequestScoped
public class DictionaryCommandService {

    @PersistenceContext
    EntityManager em;

    @Inject
    LocalisedStringsQueryService localisedStringsQueryService;

    @Inject
    LocalisedStringCommandService localisedStringCommandService;

    @Inject
    DictionaryQueryService query;

    public Optional<Status> updateStatus(long id, Locale locale, JsonObject json) {

        String lang = localisedStringsQueryService.getDefaultLanguage();
        if (locale != null) {
            lang = locale.getLanguage().substring(0, 2);
        }

        String finalLang = lang;
        Status s =  query.findDictionaryById(id)
                .filter(d -> d instanceof Status)
                .map(d -> (Status) d).get();

            updateCommonFields(json, finalLang, s);
            if (!json.isNull("color")) {
                s.setColor(json.getString("color"));
            }
            em.merge(s);

        return Optional.of(s);
    }

    public Optional<Status> addStatus(Locale locale, JsonObject json) {
        Status s = new Status();

        String lang = localisedStringsQueryService.getDefaultLanguage();
        if (locale != null) {
            lang = locale.getLanguage().substring(0, 2);
        }
        String finalLang = lang;
        saveCommonFields(json, finalLang, s);
        if (!json.isNull("color")) {
            s.setColor(json.getString("color"));
        }
        em.persist(s);
        return Optional.of(s);
    }

    public Optional<Register> updateRegister(long id, Locale locale, JsonObject json) {

        String lang = localisedStringsQueryService.getDefaultLanguage();
        if (locale != null) {
            lang = locale.getLanguage().substring(0, 2);
        }

        String finalLang = lang;
        Register r =  query.findDictionaryById(id)
                .filter(d -> d instanceof Register)
                .map(d -> (Register) d).get();

        updateCommonFields(json, finalLang, r);

        em.merge(r);

        return Optional.of(r);
    }

    public Optional<Register> addRegister(Locale locale, JsonObject json) {
        Register register = new Register();

        String lang = localisedStringsQueryService.getDefaultLanguage();
        if (locale != null) {
            lang = locale.getLanguage().substring(0, 2);
        }
        String finalLang = lang;
        saveCommonFields(json, finalLang, register);
        em.persist(register);
        return Optional.of(register);
    }

    private void saveCommonFields(JsonObject json, String lang, Dictionary dic) {
        String name = "";
        if (!json.isNull("name")) {
            name = json.getString("name");
        }

        LocalisedString locName =  new LocalisedString();
        locName.getKey().setLanguage(lang);
        locName.setValue(name);
        localisedStringCommandService.save(locName);
        dic.setName(locName.getKey().getId());

        String dsc = "";
        if (!json.isNull("description")) {
            dsc = json.getString("description");
        }
        LocalisedString locDesc =  new LocalisedString();
        locDesc.getKey().setLanguage(lang);
        locDesc.setValue(dsc);
        localisedStringCommandService.save(locDesc);
        dic.setDescription(locDesc.getKey().getId());

        if (!json.isNull("is_default")) {
            dic.setDefault(json.getBoolean("is_default"));
        }

    }

    private void updateCommonFields(JsonObject json, String lang, Dictionary dic) {
        String name = "";
        if (!json.isNull("name")) {
            name = json.getString("name");
        }
        Optional<LocalisedString> locNam = localisedStringsQueryService.findById(dic.getName(), lang);
        if (locNam.isPresent()) {
            locNam.get().setValue(name);
            localisedStringCommandService.save(locNam.get());
        }

        String dsc = "";
        if (!json.isNull("description")) {
            dsc = json.getString("description");
        }
        Optional<LocalisedString> dscNam = localisedStringsQueryService.findById(dic.getDescription(), lang);
        if (dscNam.isPresent()) {
            dscNam.get().setValue(dsc);
            localisedStringCommandService.save(dscNam.get());
        }

        if (!json.isNull("is_default")) {
            dic.setDefault(json.getBoolean("is_default"));
        }
    }

}
