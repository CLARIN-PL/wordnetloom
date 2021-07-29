package pl.edu.pwr.wordnetloom.server.business.localistaion.entity;

import javax.persistence.*;
import org.hibernate.annotations.NamedQuery;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "tbl_application_localised_string")
@NamedQuery(name = LocalisedString.FIND_ALL ,
        query = "SELECT s FROM  LocalisedString s")
@NamedQuery(name = LocalisedString.FIND_BY_ID ,
        query = "SELECT s FROM LocalisedString s WHERE s.key.id =:id and s.key.language= :lang")
@NamedQuery(name = LocalisedString.FIND_ALL_BY_ID ,
        query = "SELECT s FROM LocalisedString s WHERE s.key.id =:id")
@NamedQuery(name = LocalisedString.FIND_MAX_ID ,
        query = "SELECT MAX(s.key.id) FROM LocalisedString s")
public class LocalisedString implements Serializable {

    public static final String FIND_ALL = "LocalisedString.FIND_ALL";
    public static final String FIND_BY_ID = "LocalisedString.FIND_BY_ID";
    public static final String FIND_MAX_ID = "LocalisedString.FIND_MAX_ID";
    public static final String FIND_ALL_BY_ID = "LocalisedString.FIND_ALL_ID";

    @Valid
    @NotNull
    @EmbeddedId
    private LocalisedKey key;

    @Column(name = "value")
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalisedString() {
    }

    public LocalisedString(Long id, String lang, String value) {
        addKey(id, lang);
        this.value = value;
    }

    public void addKey(Long id, String language) {
        if (key != null) {
            key.setId(id);
            key.setLanguage(language);
        } else {
            key = new LocalisedKey(id, language);
        }
    }

    public LocalisedKey getKey() {
        return key;
    }

    public void setKey(LocalisedKey key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocalisedString)) {
            return false;
        }

        LocalisedString string = (LocalisedString) o;

        if (key != null ? !key.equals(string.key) : string.key != null) {
            return false;
        }
        return value != null ? value.equals(string.value) : string.value == null;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
