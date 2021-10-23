package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class DmStage {
    
    @Id
    private int id;
    private String description;
    private String objet;
    private String lieu;
    private int remuneration;
    private String dateDebut;
    private int duree;


    public DmStage(){
        /* */
    }

    public DmStage(int id, String description, String objet, String lieu, int remuneration, String dateDebut, int duree){
        setId(id);
        setDescription(description);
        setObjet(objet);
        setLieu(lieu);
        setRemuneration(remuneration);
        setDateDebut(dateDebut);
        setDuree(duree);
    }
}
