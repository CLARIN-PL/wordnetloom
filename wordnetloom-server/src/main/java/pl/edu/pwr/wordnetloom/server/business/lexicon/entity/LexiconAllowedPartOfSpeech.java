package pl.edu.pwr.wordnetloom.server.business.lexicon.entity;

import org.hibernate.envers.Audited;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.Domain;
import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.PartOfSpeech;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tbl_lexicon_allowed_part_of_speech")
@Audited
public class LexiconAllowedPartOfSpeech {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "lexicon_id", referencedColumnName = "id")
    private Lexicon lexicon;

    @ManyToOne
    @JoinColumn(name = "part_of_speech_id", referencedColumnName = "id")
    private PartOfSpeech partOfSpeech;

    @OneToMany
    @JoinTable(
            name = "part_of_speech_allowed_domain",
            joinColumns = @JoinColumn(name = "lexicon_allowed_part_of_speech_id"),
            inverseJoinColumns = @JoinColumn(name = "domain_id")
    )
    private Set<Domain> domain;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Lexicon getLexicon() {
        return lexicon;
    }

    public void setLexicon(Lexicon lexicon) {
        this.lexicon = lexicon;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public Set<Domain> getDomain() {
        return domain;
    }

    public void setDomain(Set<Domain> domain) {
        this.domain = domain;
    }
}
