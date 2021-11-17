package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Data
@Entity
public class Lettre {
    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private int id;

    @OneToOne
    private EtatCivil etatCivil;
    private String description;

    public Lettre(){
        /* */
    }

    public Lettre(EtatCivil etatCivil, String description) {
        this.etatCivil = etatCivil;
        this.description = description;
    }

    
}
