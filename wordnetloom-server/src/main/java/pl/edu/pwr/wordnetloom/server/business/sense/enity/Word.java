package pl.edu.pwr.wordnetloom.server.business.sense.enity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@NamedQuery(name = Word.FIND_BY_WORD,
        query = "SELECT w FROM Word w WHERE CONVERT(w.word, BINARY) = :word")

@NamedQuery(name = Word.COUNT_BY_WORD,
        query = "SELECT COUNT(w.word) FROM Word w WHERE w.word = :word")
@Entity
@Table(name = "tbl_word")
public class Word implements Serializable {

    public static final String FIND_BY_WORD = "Word.findByWord";
    public static final String COUNT_BY_WORD = "Word.CountByWord";

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotNull
    private String word;

    public Word() {
    }

    public UUID getId(){
        return id;
    }

    public void setId(UUID id){
        this.id = id;
    }

    public Word(String lemma) {
        word = lemma;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return word;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word1 = (Word) o;

        if (id != null ? !id.equals(word1.id) : word1.id != null) return false;
        return word != null ? word.equals(word1.word) : word1.word == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (word != null ? word.hashCode() : 0);
        return result;
    }
}
