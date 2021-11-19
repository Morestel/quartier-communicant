package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Data
@Entity
public class RpStage {
    
    @Id
    private int id;
    private String objet;
    
    @OneToOne
    private CV cv;
    @OneToOne
    private Lettre lettre;


    public RpStage(){
        /* */
    }


    public RpStage(int id, String objet, CV cv, Lettre lettre) {
        setId(id);
        setObjet(objet);
        setCv(cv);
        setLettre(lettre);
    }
}
