package com.quartier.quartiercommunicant.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.quartier.quartiercommunicant.repository.CVRepository;
import com.quartier.quartiercommunicant.repository.DemandeCatalogueRepository;
import com.quartier.quartiercommunicant.repository.DemandeStageRepository;
import com.quartier.quartiercommunicant.repository.DmStageRepository;
import com.quartier.quartiercommunicant.repository.EnvoiBonCommandeRepository;
import com.quartier.quartiercommunicant.repository.EtatCivilRepository;
import com.quartier.quartiercommunicant.repository.ExperienceRepository;
import com.quartier.quartiercommunicant.repository.FichierRepository;
import com.quartier.quartiercommunicant.repository.FormationStageRepository;
import com.quartier.quartiercommunicant.repository.LettreRepository;
import com.quartier.quartiercommunicant.repository.MessageRepository;
import com.quartier.quartiercommunicant.repository.ProduitRepository;
import com.quartier.quartiercommunicant.repository.ReponseStageRepository;
import com.quartier.quartiercommunicant.model.*;

@Controller
public class MessageController {

    @Inject
    FichierRepository aFichierRepository;

    @Inject
    MessageRepository aMessageRepository;

    @Inject
    DemandeStageRepository aDemandeStageRepository;

    @Inject
    DmStageRepository aDmStageRepository;

    @Inject
    ReponseStageRepository aReponseStageRepository;

    @Inject
    CVRepository aCvRepository;

    @Inject
    EtatCivilRepository aEtatCivilRepository;

    @Inject
    ExperienceRepository aExperienceRepository;

    @Inject
    FormationStageRepository aFormationStageRepository;

    @Inject
    LettreRepository aLettreRepository;

    @Inject
    DemandeCatalogueRepository aDemandeCatalogueRepository;

    @Inject
    EnvoiBonCommandeRepository aBonCommandeRepository;

    @Inject
    ProduitRepository aProduitRepository;


    List<Message> listeMessage = new ArrayList<>();
    List<String> listeDestinataire = new ArrayList<>(); // Méthode très sale, à changer si le temps le permet

    @RequestMapping(value = "/reponseGenerique", method = RequestMethod.POST)
    public String reponseGenerique(@RequestParam String textarea, @RequestParam String destinataire, @RequestParam String validite){

        System.out.println(destinataire);
        System.out.println(textarea);
        String pattern = "HH:mm:ss dd-MM-YYYY";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        System.out.println(validite);
        Message m = new Message("reponseGenerique", dateFormat.format(new Date()), validite, textarea, "");
        listeMessage.add(m);
        listeDestinataire.add(destinataire);
        return "EnvoiMessage";
    }

    @RequestMapping(value = "/demandeCollaboration", method = RequestMethod.POST)
    public String demandeCollaboration(@RequestParam String destinataire, @RequestParam String description, @RequestParam String dateDebut, @RequestParam String dateFin, @RequestParam String validite){
        String pattern = "HH:mm:ss dd-MM-YYYY";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

        Message m = new Message("demandeCollab", dateFormat.format(new Date()), description, dateDebut, dateFin);
        listeMessage.add(m);
        listeDestinataire.add(destinataire);
        return "EnvoiMessage";
    }

    @RequestMapping(value = "/demandeCatalogue", method = RequestMethod.POST)
    public String demandeCatalogue(@RequestParam String id, @RequestParam String quantite, @RequestParam String validite){
        String pattern = "HH:mm:ss dd-MM-YYYY";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        DemandeCatalogue dCatalogue = new DemandeCatalogue(Integer.valueOf(id), Integer.valueOf(quantite));
        Message m = new Message("demandeCatalogue", dateFormat.format(new Date()), validite, dCatalogue);
        listeMessage.add(m);
        listeDestinataire.add("Entreprise");
        return "EnvoiMessage";
    }

    @RequestMapping(value = "/demandeStage", method = RequestMethod.POST)
    public String demandeStage( 
                               @RequestParam String objet, 
                               @RequestParam String description, 
                               @RequestParam String lieu, 
                               @RequestParam String remuneration, 
                               @RequestParam String dateDebut, 
                               @RequestParam String dateFin, 
                               @RequestParam String duree,
                               @RequestParam String destinataire, 
                               @RequestParam String validite){
        String pattern = "HH:mm:ss dd-MM-YYYY";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

        List<DmStage> listDmStage = new ArrayList<>();

        // On cherche un ID qui est libre dans les demandes de stages
        int i = 0;
        boolean trouve = false;
        while (i < 5000 && trouve == false){
            if (aDmStageRepository.findById(i).isEmpty()){
                trouve = true;
                System.out.println(i);
            }
            i++;
        }
        DmStage dmS = new DmStage(i, description, objet, lieu, Integer.valueOf(remuneration), dateDebut, Integer.valueOf(duree));
        aDmStageRepository.save(dmS);
        listDmStage.add(dmS);
        DemandeStage ds = new DemandeStage(listDmStage);
        
        Message m = new Message("demandeStage", dateFormat.format(new Date()), validite, ds);

        listeMessage.add(m);
        listeDestinataire.add("Entreprise");
        return "EnvoiMessage";
    }

    @RequestMapping(value = "/toutEnvoyer", method = RequestMethod.POST)
    public String toutEnvoyer(){
        int i = 0;
        for (Message m : listeMessage){
            switch(m.getType()){
                case "reponseGenerique":
                    aMessageRepository.save(m);
                    listeMessage.remove(m);
                    break;
            }
            System.err.println("Message : " + i);
            i++;
        }
        return "redirect:/envoiMessage";
    }

    @RequestMapping("envoiMessage")
    public String envoiMessage(){
        /*
        try(FileWriter fw = new FileWriter("Fichier.txt", true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw)){

            out.write(ajoutDTD());
        }catch(IOException e){
            e.printStackTrace();
        }
        
        */
        return "EnvoiMessage";
    }

    // Retourne la DTD dans un string
    public String ajoutDTD(){
        String dtd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
        "\n<!DOCTYPE fichierGlobal ["+
        "\n<!ELEMENT fichierGlobal (destinataire+,expediteur,nbMessages,message+)>"+
        "\n<!ATTLIST fichierGlobal id ID #REQUIRED>"+
        "\n<!ELEMENT destinataire (#PCDATA)>"+
        "\n<!ELEMENT expediteur (#PCDATA)>"+
        "\n<!ELEMENT nbMessages (#PCDATA)>"+
        "\n<!ELEMENT message (dateEnvoi,dureeValidite,typeMessage)>"+
        "\n<!ATTLIST message id ID #REQUIRED>"+
        "\n<!-- Nomenclature dateEnvoi : hh:mm:ss JJ-MM-AAAA -->"+
        "\n<!ELEMENT dateEnvoi (#PCDATA)>"+
        "\n<!-- Nomenclature dureeValidite : nombre entier : nombres d'heures -->"+
        "\n<!ELEMENT dureeValidite (#PCDATA)>"+
        "\n<!ELEMENT typeMessage ("+
        "\n    envoiCatalogue | "+
        "\n    demandeCatalogue | "+
        "\n    envoiBonCommande | "+
        "\n    accuseReception | "+
        "\n    offreCollab | "+
        "\n    demandeCollab | "+
        "\n    reponseGenerique | "+
        "\n    propositionCommerciale | "+ 
        "\n    demandeCommerciale | "+
        "\n    rechercheSousTraitant |"+ 
        "\n    propositionSousTraitant |"+ 
        "\n    demandeConference | "+
        "\n    reponseConference | "+
        "\n    demandeListeFormation | "+ 
        "\n    reponseListeFormation | "+
        "\n    demandeStage | "+
        "\n    reponseStage)>"+
        
        "\n<!-- DTD de l'envoi de catalogue -->"+
        "\n<!ELEMENT envoiCatalogue (titreCatalogue,listeProduit)> "+
        "\n<!ELEMENT titreCatalogue (#PCDATA)> "+
        "\n<!ELEMENT listeProduit (produit+)> "+
        "\n<!ELEMENT produit (nom,prix,quantite)> "+
        "\n<!ATTLIST produit identifiant ID #REQUIRED> "+
        "\n<!ELEMENT nom (#PCDATA)> "+
        "\n<!ELEMENT prix (#PCDATA)>"+
        "\n<!ELEMENT quantite (#PCDATA)>"+
        
        "\n<!-- DTD de la demande de catalogue -->"+
        "\n<!ELEMENT demandeCatalogue (catalogueDemande+)> "+
        "\n<!ELEMENT catalogueDemande (titreCatalogueDemande,quantite)>"+
        "\n<!ATTLIST catalogueDemande identifiant ID #REQUIRED>"+
        "\n<!ELEMENT titreCatalogueDemande (#PCDATA)>"+
        
        "\n<!-- DTD de l'envoi de Bon de commande -->"+
        "\n<!ELEMENT envoiBonCommande (numCommande,dateCommande,listeProduit,prixCommande)>"+
        "\n<!ELEMENT numCommande (#PCDATA)>"+
        "\n<!ELEMENT dateCommande (#PCDATA)>"+
        "\n<!ELEMENT prixCommande (#PCDATA)>"+
        
        "\n<!-- DTD de l'accusé de réception de commande -->"+
        "\n<!ELEMENT accuseReception (numCommande, dateCommande, dateReceptionAccuseDeReception, listeProduit, prixCommande)>"+
        "\n<!ATTLIST numCommande identifiantCommande ID #REQUIRED>"+
        "\n<!ELEMENT dateReceptionAccuseDeReception (#PCDATA)>"+
        
        "\n<!-- Offre Collab -->"+
        "\n<!ELEMENT offreCollab (description,date)>"+
        "\n<!ELEMENT description (#PCDATA)>"+
        
        "\n<!-- Demande Collab -->"+
        "\n<!ELEMENT demandeCollab (description,date)>"+
        
        "\n<!-- Réponse Générique -->"+
        "\n<!ELEMENT reponseGenerique (msg, idMsgPrécédent)>"+
        "\n<!ELEMENT msg (#PCDATA)>"+
        "\n<!ELEMENT idMsgPrécédent (#PCDATA)>"+
        
        "\n<!-- Proposition commerciale -->"+
        "\n<!ELEMENT propositionCommerciale (prixProposition,description,contrat)>"+
        "\n<!ELEMENT prixProposition (forfait+)> "+
        "\n<!ELEMENT forfait (#PCDATA)>"+
        "\n<!ELEMENT contrat (date)>"+
        
        "\n<!-- Demande Commerciale -->"+
        "\n<!ELEMENT demandeCommerciale (prixProposition,description,contrat)>"+
        
        "\n<!-- Proposition Sous Traitant -->"+
        "\n<!ELEMENT propositionSousTraitant (prixProposition,description,contrat)>"+
        
        "\n<!-- Recherche Sous Traitant -->"+
        "\n<!ELEMENT rechercheSousTraitant (prixProposition,description,contrat)>"+
        
        "\n<!-- Demande de conférence -->"+
        "\n<!ELEMENT demandeConference (conf+)>"+
        "\n<!ELEMENT conf (id,sujet,lieu,dateDebut,dureeConference)>"+
        "\n<!ELEMENT id (#PCDATA)>"+
        "\n<!ELEMENT sujet (#PCDATA)>"+
        "\n<!ELEMENT lieu (#PCDATA)>"+
        "\n<!-- dureeConference en heure -->"+
        "\n<!ELEMENT dureeConference (#PCDATA)>"+
        
        "\n<!-- Réponse de conférence -->"+
        "\n<!ELEMENT reponseConference (reponseGenerique)>"+
        
        "\n<!-- Demande de listes de formations -->"+
        "\n<!ELEMENT demandeListeFormation (form+)>"+
        "\n<!ELEMENT form (id,branche)>"+
        "\n<!ELEMENT branche (filiere+)>"+
        "\n<!ELEMENT filiere (#PCDATA)>"+
        
        "\n<!-- Reponse de listes de formations -->"+
        "\n<!ELEMENT reponseListeFormation (catalogue+)>"+
        "\n<!ELEMENT catalogue (id,liste)>"+
        "\n<!ELEMENT liste (formation+)>"+
        "\n<!ELEMENT formation (titre,description,filiere)>"+
        "\n<!ELEMENT titre (#PCDATA)>"+
        
        "\n<!-- Demande de stage -->"+
        "\n<!ELEMENT demandeStage (dmStage+)>"+
        "\n<!ELEMENT dmStage (id,objet,description,lieu,remuneration,date)>"+
        "\n<!ELEMENT date (dateDebut, (dateFin | duree))>"+
        "\n<!-- Nomenclature dateDebut : hh:mm:ss JJ-MM-AAAA -->"+
        "\n<!ELEMENT dateDebut (#PCDATA)>"+
        "\n<!ELEMENT dateFin (#PCDATA)>"+
        "\n<!-- Nomenclature duree : nombre entier : nombres de jours -->"+
        "\n<!ELEMENT duree (#PCDATA)>"+
        "\n<!ELEMENT objet (#PCDATA)>"+
        "\n<!ELEMENT remuneration (#PCDATA)>"+
        
        "\n<!-- Réponse de stage -->"+
        "\n<!ELEMENT reponseStage (rpStage+)>"+
        "\n<!ELEMENT rpStage (id,objet,cv,lettre)>"+
        "\n<!ELEMENT cv (etatcivil,formationStage+,experience*)>"+
        "\n<!ELEMENT lettre (etatcivil,description)>"+
        "\n<!ELEMENT etatcivil (nom,prenom,dateNaissance,lieuNaissance,lieuResidence,photo?,email?,tel?)>"+
        "\n<!ELEMENT formationStage (titre,date,lieu?,mention?,description?)>"+
        "\n<!ELEMENT experience (titre,date,lieu,fonction?,description?)>"+
        "\n<!ELEMENT prenom (#PCDATA)>"+
        "\n<!ELEMENT dateNaissance (#PCDATA)>"+
        "\n<!ELEMENT lieuNaissance (#PCDATA)>"+
        "\n<!ELEMENT lieuResidence (#PCDATA)>"+
        "\n<!ELEMENT photo (#PCDATA)>"+
        "\n<!ELEMENT email (#PCDATA)>"+
        "\n<!ELEMENT tel (#PCDATA)>"+
        "\n<!ELEMENT mention (#PCDATA)>"+
        "\n<!ELEMENT fonction (#PCDATA)>"+
        
        "\n]>";

        
        return dtd;
    }

}
