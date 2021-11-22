package com.quartier.quartiercommunicant.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;


import lombok.Data;

@Data
@Entity
public class PropositionCommerciale {
 
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private int id;

    @ManyToMany
    private List<Forfait> listeForfait = new ArrayList<>();
    private String description;
    private String dateDebut;
    private String dateFin;
    private int duree;

    public PropositionCommerciale(){
        /* */
    }

    public PropositionCommerciale(List<Forfait> listeForfait, String description, String dateDebut, String dateFin,
            int duree) {
        this.listeForfait = listeForfait;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.duree = duree;
    }

    public PropositionCommerciale(List<Forfait> listeForfait, String description, String dateDebut) {
        this.listeForfait = listeForfait;
        this.description = description;
        this.dateDebut = dateDebut;
    }

    
}
