package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class FormationStage {
    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private int id;
    private String titre;
    private String dateDebut;
    private int duree;
    private String lieu;

    
    private String mention;
    private String description;

    public FormationStage(){
        /* */
    }
    
    public FormationStage(String titre, String dateDebut, int duree, String lieu, String mention,
    String description) {
        this.titre = titre;
        this.dateDebut = dateDebut;
        this.duree = duree;
        this.lieu = lieu;
        this.mention = mention;
        this.description = description;
    }   

}
