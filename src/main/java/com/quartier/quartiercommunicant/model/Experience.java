package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Experience {
    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private int id;

    private String titre;
    private String dateDebut;
    private int duree;
    private String lieu;
    private String fonction;
    private String description;


    public Experience(){
        /* */
    }
    
    public Experience(String titre, String dateDebut, int duree, String lieu, String fonction, String description) {
        this.titre = titre;
        this.dateDebut = dateDebut;
        this.duree = duree;
        this.lieu = lieu;
        this.fonction = fonction;
        this.description = description;
    }

    
}
