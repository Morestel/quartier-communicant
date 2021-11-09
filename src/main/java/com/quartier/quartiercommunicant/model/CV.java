package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Data
@Entity
public class CV {
    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private int id;

    @OneToOne
    private EtatCivil etatCivil;
    @OneToOne
    private FormationStage formationStage;
    @OneToOne
    private Experience experience;

    public CV(){

        /* */
    }

    public CV(EtatCivil etatCivil, FormationStage formationStage, Experience experience) {
        this.etatCivil = etatCivil;
        this.formationStage = formationStage;
        this.experience = experience;
    }

    
}
