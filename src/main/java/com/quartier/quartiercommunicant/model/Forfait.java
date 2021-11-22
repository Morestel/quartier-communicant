package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Forfait {
    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private int id;

    private float prix;

    public Forfait(){
        /**/
    }

    public Forfait(float prix){
        this.prix = prix;
    }
}
