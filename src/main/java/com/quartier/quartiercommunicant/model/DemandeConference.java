package com.quartier.quartiercommunicant.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class DemandeConference {
    
    @Id
    private int id;
    private String sujet;
    private String lieu;
    private Date dateDebut;
    private int dureeConference;

    public DemandeConference(){
        /* */
    }

    public DemandeConference(int id, String sujet, String lieu, Date dateDebut, int dureeConference) {
        this.id = id;
        this.sujet = sujet;
        this.lieu = lieu;
        this.dateDebut = dateDebut;
        this.dureeConference = dureeConference;
    }

    
}
