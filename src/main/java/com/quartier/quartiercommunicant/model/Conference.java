package com.quartier.quartiercommunicant.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Conference {
 
    @Id
    private String id;
    private String sujet;
    private String lieu;
    private String dateDebut;
    private int dureeConference;


    public Conference(){
        /**/ 
    }


    public Conference(String id, String sujet, String lieu, String dateDebut, int dureeConference) {
        this.id = id;
        this.sujet = sujet;
        this.lieu = lieu;
        this.dateDebut = dateDebut;
        this.dureeConference = dureeConference;
    }
    
}
