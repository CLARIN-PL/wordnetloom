package pl.edu.pwr.wordnetloom.server.business.localistaion.entity;

import org.hibernate.envers.Audited;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Embeddable
@Audited
public class LocalisedKey implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    @Size(min = 2)
    private String language;

    public LocalisedKey() {
    }

    public LocalisedKey(Long id, String language) {
        this.id = id;
        this.language = language;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocalisedKey)) {
            return false;
        }

        LocalisedKey that = (LocalisedKey) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        return language != null ? language.equals(that.language) : that.language == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (language != null ? language.hashCode() : 0);
        return result;
    }
}
