package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Message {
    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private int id;

    private String contenu;
    private String dateEnvoi;
    private String dureeValidite;
    private String type;
    private String description;
    private String dateDebut;
    private String dateFin;

    private String msg;
    private String idMsgPrecedent;

    public Message(){
        
    }

    // Message offre + demande collab
    public Message(String type, String dateEnvoi, String dureeValidite, String description, String dateDebut, String dateFin){
        setType(type);
        setDateEnvoi(dateEnvoi);
        setDureeValidite(dureeValidite);
        setDescription(description);
        setDateDebut(dateDebut);
        setDateFin(dateFin);
    }

    // Message réponse générique
    public Message(String type, String dateEnvoi, String dureeValidite, String msg, String idMsgPrecedent){
        setType(type);
        setDateEnvoi(dateEnvoi);
        setDureeValidite(dureeValidite);
        setMsg(msg);
        setIdMsgPrecedent(idMsgPrecedent);
    }
}

