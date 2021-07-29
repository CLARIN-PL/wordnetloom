package pl.edu.pwr.wordnetloom.server.business.dictionary.entity;

import javax.persistence.*;
import org.hibernate.annotations.NamedQuery;
import java.io.Serializable;

@Entity
@Table(name = "tbl_domain")
@NamedQuery(name = Domain.FIND_ALL, query = "SELECT d FROM Domain d")
@NamedQuery(name = Domain.FIND_BY_ID, query = "SELECT d FROM Domain d WHERE d.id =:id")
public class Domain implements Serializable {

    public static final String FIND_ALL = "Domain.findAll";
    public static final String FIND_BY_ID = "Domain.findById";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name_id")
    private Long name;

    @Column(name = "description_id")
    private Long description;

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
