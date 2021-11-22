package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity
public class Message {
    
    
    @Id
    private String id;

    private String contenu;
    private String dateEnvoi;
    private String dureeValidite;
    private String type;
    private String description;
    private String dateDebut;
    private String dateFin;

    private String msg;
    private String idMsgPrecedent;

    @OneToOne
    private DemandeStage demandeStage;
    @OneToOne
    private ReponseStage reponseStage;
    @OneToOne
    private DemandeCatalogue demandeCatalogue;
    @OneToOne
    private EnvoiBonCommande envoiBonCommande;
    @OneToOne
    private DemandeConference demandeConference;
    @OneToOne
    private DemandeCommerciale demandeCommerciale;
    @OneToOne
    private AccuseReception accuseReception;
    @OneToOne
    private EnvoiCatalogue envoiCatalogue;
    @OneToOne
    private PropositionCommerciale propositionCommerciale;
    
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

    // Message demande de stage
    public Message(String type, String dateEnvoi, String dureeValidite, DemandeStage demandeStage){
        setType(type);
        setDateEnvoi(dateEnvoi);
        setDureeValidite(dureeValidite);
        setDemandeStage(demandeStage);
    }

    // Message réponse de stage
    public Message(String type, String dateEnvoi, String dureeValidite, ReponseStage reponseStage){
        setType(type);
        setDateEnvoi(dateEnvoi);
        setDureeValidite(dureeValidite);
        setReponseStage(reponseStage);
    }

    // Message demande de catalogue
    public Message(String type, String dateEnvoi, String dureeValidite, DemandeCatalogue demandeCatalogue){
        setType(type);
        setDateEnvoi(dateEnvoi);
        setDureeValidite(dureeValidite);
        setDemandeCatalogue(demandeCatalogue);
    }

    // Message envoi bon de commande
    public Message(String type, String dateEnvoi, String dureeValidite, EnvoiBonCommande envoiBonCommande){
        setType(type);
        setDateEnvoi(dateEnvoi);
        setDureeValidite(dureeValidite);
        setEnvoiBonCommande(envoiBonCommande);
    }

    // Message demande de conférence
    public Message(String type, String dateEnvoi, String dureeValidite, DemandeConference demandeConference){
        setType(type);
        setDateEnvoi(dateEnvoi);
        setDureeValidite(dureeValidite);
        setDemandeConference(demandeConference);
    }

    // Message demande commerciale
    public Message(String type, String dateEnvoi, String dureeValidite, DemandeCommerciale demandeCommerciale){
        setType(type);
        setDateEnvoi(dateEnvoi);
        setDureeValidite(dureeValidite);
        setDemandeCommerciale(demandeCommerciale);
    }

    // Message proposition commerciale
    public Message(String type, String dateEnvoi, String dureeValidite, PropositionCommerciale propositionCommerciale){
        setType(type);
        setDateEnvoi(dateEnvoi);
        setDureeValidite(dureeValidite);
        setPropositionCommerciale(propositionCommerciale);
    }

    // Message accusé de réception
    public Message(String type, String dateEnvoi, String dureeValidite, AccuseReception accuseReception){
        setType(type);
        setDateEnvoi(dateEnvoi);
        setDureeValidite(dureeValidite);
        setAccuseReception(accuseReception);
    }

    // Message envoi de catalogue
    public Message(String type, String dateEnvoi, String dureeValidite, EnvoiCatalogue envoiCatalogue){
        setType(type);
        setDateEnvoi(dateEnvoi);
        setDureeValidite(dureeValidite);
        setEnvoiCatalogue(envoiCatalogue);
    }
}

