package pl.edu.pwr.wordnetloom.server.business.lexicon.boundary;

import pl.edu.pwr.wordnetloom.server.business.OperationResult;
import pl.edu.pwr.wordnetloom.server.business.lexicon.control.LexiconQueryService;
import pl.edu.pwr.wordnetloom.server.business.lexicon.entity.Lexicon;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@RequestScoped
public class LexiconCommandService {

    @PersistenceContext
    EntityManager em;

    @Inject
    LexiconQueryService query;

    public OperationResult<Lexicon> save(JsonObject json) {

        Lexicon nl = new Lexicon();

        OperationResult<Lexicon> result = mapJsonToLexiconOperationResult(json, nl);
        if(!result.hasErrors()) {
            em.persist(nl);
            result.setEntity(nl);
        }
        return result;
    }

    public OperationResult<Lexicon> update(JsonObject json) {
        OperationResult<Lexicon> result = new OperationResult<>();
        if (!json.isNull("id")) {
            Long id = (long) json.getInt("id");

            Optional<Lexicon> lexicon = query.findById(id);
            if (lexicon.isPresent()) {
                Lexicon toUpdate = lexicon.get();
                result = mapJsonToLexiconOperationResult(json, toUpdate);
                if(!result.hasErrors()) {
                    em.merge(toUpdate);
                    result.setEntity(toUpdate);
                }
            } else {
                result.addError("msg", "Unable to find lexicon");
            }
        } else {
            result.addError("id", "Incorrect id value");
        }
        return result;
    }

    private OperationResult<Lexicon> mapJsonToLexiconOperationResult(JsonObject json, Lexicon nl) {
        OperationResult<Lexicon> result = new OperationResult<>();

        if (!json.isNull("name") && !json.getString("name").isEmpty()) {
            nl.setName(json.getString("name"));
        } else {
            result.addError("name", "May not be empty");
        }

        if (!json.isNull("identifier") && !json.getString("identifier").isEmpty()) {
            nl.setIdentifier(json.getString("identifier"));
        } else {
            result.addError("identifier", "May not be empty");
        }

        if (!json.isNull("language") && !json.getString("language").isEmpty()) {
            nl.setLanguageName(json.getString("language"));
        } else {
            result.addError("language", "Language may not be empty");
        }

        if (!json.isNull("version")) {
            nl.setLexiconVersion(json.getString("version"));
        }

        if (!json.isNull("language_shortcut")) {
            nl.setLanguageShortcut(json.getString("language_shortcut"));
        }

        if (!json.isNull("license")) {
            nl.setLicense(json.getString("license"));
        }

        if (!json.isNull("email")) {
            nl.setEmail(json.getString("email"));
        }

        if (!json.isNull("citation")) {
            nl.setCitation(json.getString("citation"));
        }

        if (!json.isNull("reference_url")) {
            nl.setReferenceUrl(json.getString("reference_url"));
        }
        if (!json.isNull("confidence_score")) {
            nl.setConfidenceScore(json.getString("confidence_score"));
        }
        return result;
    }
}
