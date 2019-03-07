package pl.edu.pwr.wordnetloom.server.business.dictionary.entity;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Audited
@Entity
@Table(name = "tbl_dictionaries")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "dtype",
        discriminatorType = DiscriminatorType.STRING)
@NamedQuery(name = Dictionary.FIND_ALL_BY_TYPE, query = "SELECT d FROM Dictionary d WHERE TYPE(d) = :type")
@NamedQuery(name = Dictionary.FIND_BY_ID, query = "SELECT d FROM Dictionary d WHERE d.id= :id")
public abstract class Dictionary implements Serializable {

    public static final String FIND_ALL_BY_TYPE = "Dictionary.findAllByType";
    public static final String FIND_BY_ID = "Dictionary.findById";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @NotNull
    @Column(name = "name_id")
    protected Long name;

    @Column(name = "description_id")
    protected Long description;

    @Column(name = "is_default")
    protected Boolean isDefault = false;

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getName() {
        return name;
    }

    public void setName(Long name) {
        this.name = name;
    }

    public Long getDescription() {
        return description;
    }

    public void setDescription(Long description) {
        this.description = description;
    }
}
