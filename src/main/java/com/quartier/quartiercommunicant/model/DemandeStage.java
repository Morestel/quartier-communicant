package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class DemandeStage {
    
    @Id
    private int id;
    private String description;
    private String objet;
    private String lieu;
    private int remuneration;
    private String dateDebut;
    private int duree;


    public DemandeStage(){
        /* */
    }

    public DemandeStage(String description, String objet, String lieu, int remuneration, String dateDebut, int duree){
        setDescription(description);
        setObjet(objet);
        setLieu(lieu);
        setRemuneration(remuneration);
        setDateDebut(dateDebut);
        setDuree(duree);
    }
}
