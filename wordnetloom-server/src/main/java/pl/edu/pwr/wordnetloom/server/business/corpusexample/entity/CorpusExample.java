package pl.edu.pwr.wordnetloom.server.business.corpusexample.entity;

import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Table(name = "tbl_corpus_example")
@Audited
@NamedQuery(name = CorpusExample.FIND_BY_WORD,
        query = "SELECT c FROM CorpusExample c WHERE c.word = :word")
public class CorpusExample {

    private static final long serialVersionUID = -86878893575269138L;
    public static final String FIND_BY_WORD = "CorpusExample.findByWord";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Lob
    @Column
    private String text;

    private String word;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }
}
