package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class DemandeCommerciale {
    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private int id;
    private int prixProposition;
    private String description;
    private String dateDebut;
    private String dateFin;
    private int duree;

    public DemandeCommerciale(){
        /* */
    }

    public DemandeCommerciale(int prixProposition, String description, String dateDebut, String dateFin) {
        this.prixProposition = prixProposition;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public DemandeCommerciale(int prixProposition, String description, String dateDebut, int duree) {
        this.prixProposition = prixProposition;
        this.description = description;
        this.dateDebut = dateDebut;
        this.duree = duree;
    }

    
}
