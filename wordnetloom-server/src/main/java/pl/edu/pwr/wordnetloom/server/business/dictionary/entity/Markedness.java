package pl.edu.pwr.wordnetloom.server.business.dictionary.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Markedness extends Dictionary {

    @Column(name = "value")
    private Long value;

    public Long getValue(){
        return value;
    }

    public void setValue(Long value){
        this.value = value;
    }
}
