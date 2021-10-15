package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Data
@Entity
public class ReponseStage {
    
    @Id
    private int id;
    private String objet;
    
    @OneToOne
    private CV cv;
    @OneToOne
    private Lettre lettre;


    public ReponseStage(){
        /* */
    }


    public ReponseStage(String objet, CV cv, Lettre lettre) {
        this.objet = objet;
        this.cv = cv;
        this.lettre = lettre;
    }

    
}
