package pl.edu.pwr.wordnetloom.server.business.lexicon.entity;

import javax.persistence.*;
import org.hibernate.annotations.NamedQuery;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "tbl_lexicon")
@NamedQuery(name = Lexicon.FIND_ALL, query = "SELECT l FROM Lexicon l")
@NamedQuery(name = Lexicon.FIND_ALL_ID, query = "SELECT l.id FROM Lexicon l")
@NamedQuery(name = Lexicon.FIND_BY_ID, query = "SELECT l FROM Lexicon l WHERE l.id = :id")
public class Lexicon implements Serializable {

    public static final String FIND_ALL = "Lexicon.findAll";
    public static final String FIND_BY_ID = "Lexicon.findById";
    public static final String FIND_ALL_ID = "Lexicon.findByIdAllIds";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;

    @NotNull
    private String identifier;

    @NotNull
    @Column(name = "language_name")
    private String languageName;

    @Column(name = "lexicon_version")
    private String lexiconVersion;

    @Column(name = "language_shortcut")
    private String languageShortcut;

    private String license;

    private String email;

    @Column(name = "reference_url")
    private String referenceUrl;

    @Lob
    private String citation;

    @Column(name = "confidence_score")
    private String confidenceScore;

    public Lexicon() {
    }

    public Lexicon(String name, String identifier, String languageName) {
        this.name = name;
        this.identifier = identifier;
        this.languageName = languageName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLexiconVersion() {
        return lexiconVersion;
    }

    public void setLexiconVersion(String lexiconVersion) {
        this.lexiconVersion = lexiconVersion;
    }

    public String getLanguageShortcut() {
        return languageShortcut;
    }

    public void setLanguageShortcut(String languageShortcut) {
        this.languageShortcut = languageShortcut;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReferenceUrl() {
        return referenceUrl;
    }

    public void setReferenceUrl(String referenceUrl) {
        this.referenceUrl = referenceUrl;
    }

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    public String getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(String confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lexicon lexicon = (Lexicon) o;

        if (id != lexicon.id) return false;
        if (name != null ? !name.equals(lexicon.name) : lexicon.name != null) return false;
        if (identifier != null ? !identifier.equals(lexicon.identifier) : lexicon.identifier != null) return false;
        if (languageName != null ? !languageName.equals(lexicon.languageName) : lexicon.languageName != null)
            return false;
        return lexiconVersion != null ? lexiconVersion.equals(lexicon.lexiconVersion) : lexicon.lexiconVersion == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (identifier != null ? identifier.hashCode() : 0);
        result = 31 * result + (languageName != null ? languageName.hashCode() : 0);
        result = 31 * result + (lexiconVersion != null ? lexiconVersion.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
