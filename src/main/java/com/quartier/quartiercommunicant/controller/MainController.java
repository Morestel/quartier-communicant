package com.quartier.quartiercommunicant.controller;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.text.Format;
import java.text.ParseException;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.quartier.quartiercommunicant.model.DemandeStage;
import com.quartier.quartiercommunicant.model.Fichier;
import com.quartier.quartiercommunicant.model.FormationStage;
import com.quartier.quartiercommunicant.model.Message;
import com.quartier.quartiercommunicant.model.ReponseStage;
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

import org.springframework.data.spel.ExpressionDependencies.ExpressionDependency;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.DOMException;


@Controller
public class MainController {
    

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

    @RequestMapping("envoi")
    public String envoiMessage(){
        return "EnvoiMessage";
    }

    @RequestMapping({"index", "" })
    public String index(Model model){
    
        File repertoire = new File("repertoire");

        Fichier fic = new Fichier(repertoire.getName() + "/fichier.xml");
        System.out.println(fic.getFic().length());
        if (fic.getFic().length() > 10000){
            System.err.println("Trop de caractères");
        }

       
    
        try{
            lireEnTete(fic);
            lireMessage(fic);

            aFichierRepository.save(fic);
        }catch(ParserConfigurationException u){
           u.printStackTrace();
        }catch(SAXException v){
            v.printStackTrace();
        }catch(IOException i){
            i.printStackTrace();
        }catch(DOMException n){
            n.printStackTrace();
        }catch(ParseException w){
            w.printStackTrace();
        }
        
        model.addAttribute("listeMessage", fic.getListMess());
        model.addAttribute("fichier", fic);
        return "index";
    }

    public void lireEnTete(Fichier fic) throws ParserConfigurationException,
    SAXException, IOException, DOMException, ParseException{

        try{
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(fic.getFic());
            
            document.getDocumentElement().normalize();
            String s = document.toString();
            System.out.println(s);
            System.err.println(document);
            System.out.println("IDENTIFIANT = " + document.getDocumentElement().getAttribute("id"));
            System.err.println("------------");
            fic.setId(Integer.valueOf(document.getDocumentElement().getAttribute("id")));
            fic.setExpediteur(document.getElementsByTagName("expediteur").item(0).getTextContent().replace(" ", "").replace("\n", "").replace("\t", ""));
            
            fic.setDestinataire(document.getElementsByTagName("destinataire").item(0).getTextContent().replace(" ", "").replace("\n", "").replace("\t", ""));
            
            int check = Integer.parseInt(document.getElementsByTagName("nbMessages").item(0).getTextContent().replace(" ", "").replace("\n", "").replace("\t", ""));
            fic.setChecksum(check);

        }catch(IOException e){
            e.printStackTrace();
        }

    }
    

    // Indique si l'id a besoin de changer pour suivre le bon format
    public boolean formatIdMessage(String id){
        
        if (id.length() > 5){
            System.err.println(id.substring(0, 4).equals("MAG-"));
            if (id.substring(0, 4).equals("MAG-") || id.substring(0, 4).equals("ENT-") || id.substring(0, 4).equals("ECO-")){
                return false;
            }
        }
        
        return true;
    }

    // Rajoute un préfixe pour indiquer de qui est le message (Afin d'éviter un remplacement des messages dans la base de données au cas où un message de deux entités différentes aient le même id)
    public String ajoutPrefixe(String id, String expediteur){
        String finalId;
        if (expediteur.toLowerCase().matches("ecole")){
            finalId = "ECO-";
        }
        else if (expediteur.toLowerCase().matches("entreprise")){
            finalId = "ENT-";
        }
        else if (expediteur.toLowerCase().matches("magasin")){
            finalId = "MAG-";
        }
        else{
            finalId = "UNK-"; // Pour inconnu
        }
        return finalId + id;
    }
    public void lireMessage(Fichier fic){
        
        try {
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(fic.getFic());
            document.getDocumentElement().normalize();
            System.out.println("Root Element :" + document.getDocumentElement().getNodeName());
            
            NodeList listeMessage = document.getElementsByTagName("message");

            System.out.println(document.getElementsByTagName("offreCollab").item(0).getTextContent());
            System.out.println(document.getElementsByTagName("description").item(0).getTextContent());
            
            System.out.println("Taille liste message : " + listeMessage.getLength());

            /* 
                Type de messages:
                    - Offre collab
                    - Demande collab
                    - Réponse générique
            */

            // Stock les types de messages dans une liste de noeud
            NodeList oCollab = document.getElementsByTagName("offreCollab");
            NodeList dCollab = document.getElementsByTagName("demandeCollab");
            NodeList rGenerique = document.getElementsByTagName("reponseGenerique");
            NodeList dStage = document.getElementsByTagName("demandeStage");

            System.out.println("Nombre d'offre collab : " + oCollab.getLength());
            System.out.println("Nombre de demande collab : " + dCollab.getLength());
            System.out.println("Nombre de réponses génériques : " + rGenerique.getLength());
            System.out.println("Nombre de demande de stage : " + dStage.getLength());
            
            String description;
            String dateDebut;
            String dateFin;

            String id_message;
            String id;
            String remuneration;
            String lieu;
            String objet;
            String duree;
            int quantite;
            
            String msg;
            String idMsgPrecedent;

            String dateEnvoi;
            String dureeValidite;

            String dateCommande;

            Message m;
            List<Message> lMessage = new ArrayList<>();

            Produit produit = new Produit();
            List<Produit> listeProduit = new ArrayList<>();

            DemandeStage demandeStage = new DemandeStage();
            DmStage dmStage = new DmStage();
            ReponseStage rpStage = new ReponseStage();
            CV cv = new CV();
            EtatCivil etatCivil = new EtatCivil();
            FormationStage formationStage = new FormationStage();
            Experience experience = new Experience();
            Lettre lettre = new Lettre();
            DemandeCatalogue demandeCatalogue = new DemandeCatalogue();
            EnvoiBonCommande envoiBonCommande = new EnvoiBonCommande();

            int nbDmStage = 0;
            List<DmStage> listDmStage = new ArrayList<>(); 
            NodeList nList = document.getElementsByTagName("message");

            // On stock tous les id dans une liste pour vérifier qu'on ai pas deux fois les mêmes
            List<String> listId = new ArrayList<>();
            for (int l = 0; l < nList.getLength(); l++){
                Node nNode = nList.item(l);
                if (nNode.getNodeType() == Node.ELEMENT_NODE){
                    Element elem = (Element) nNode;
                    listId.add(elem.getAttribute("id"));
                }
            }
            
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                System.out.println(nNode.getNodeName());
                System.out.println("\nCurrent Element :" + nNode.getNodeName());
               
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) nNode;
                    id_message = elem.getAttribute("id");
                    if (formatIdMessage(id_message)){ // On regarde si c'est déjà au bon format, sinon on le met nous même
                        id_message = ajoutPrefixe(id_message, fic.getExpediteur());
                    }
                    System.err.println(id_message);
                    System.out.println("Date d'envoi : " + elem.getElementsByTagName("dateEnvoi").item(0).getTextContent());
                    System.out.println("Durée validité : " + elem.getElementsByTagName("dureeValidite").item(0).getTextContent());
                    dateEnvoi = elem.getElementsByTagName("dateEnvoi").item(0).getTextContent();
                    dureeValidite = elem.getElementsByTagName("dureeValidite").item(0).getTextContent();

                    // Offre de collaborations
                    if (elem.getElementsByTagName("offreCollab").getLength() > 0){

                        description = elem.getElementsByTagName("description").item(0).getTextContent();
                        dateDebut = elem.getElementsByTagName("dateDebut").item(0).getTextContent();
                        dateFin = elem.getElementsByTagName("dateFin").item(0).getTextContent();

                        m = new Message("offreCollab", dateEnvoi, dureeValidite, description, dateDebut, dateFin);
                        m.setId(id_message);
                        lMessage = fic.getListMess();
                        lMessage.add(m);
                        fic.setListMess(lMessage);
                        aMessageRepository.save(m);
                        System.err.println("Sauvegarde effectuée");
                    }
                    
                    // Demande de collaboration
                    
                    if (elem.getElementsByTagName("demandeCollab").getLength() > 0){

                        description = elem.getElementsByTagName("description").item(0).getTextContent();
                        dateDebut = elem.getElementsByTagName("dateDebut").item(0).getTextContent();
                        dateFin = elem.getElementsByTagName("dateFin").item(0).getTextContent();   
                        
                        m = new Message("demandeCollab", dateEnvoi, dureeValidite, description, dateDebut, dateFin);
                        m.setId(id_message);
                        lMessage = fic.getListMess();
                        lMessage.add(m);
                        fic.setListMess(lMessage);
                        aMessageRepository.save(m);
                    }

                    // Réponse générique
                    if (elem.getElementsByTagName("reponseGenerique").getLength() > 0){

                        msg = elem.getElementsByTagName("msg").item(0).getTextContent();
                        idMsgPrecedent = elem.getElementsByTagName("idMsgPrécédent").item(0).getTextContent();

                        m = new Message("reponseGenerique", dateEnvoi, dureeValidite, msg, idMsgPrecedent);
                        m.setId(id_message);
                        lMessage = fic.getListMess();
                        lMessage.add(m);
                        fic.setListMess(lMessage);
                        aMessageRepository.save(m);
                    }
                    
                    
                    // Demande de stage                    
                    if (elem.getElementsByTagName("demandeStage").getLength() > 0){
                        demandeStage = new DemandeStage();
                        dmStage = new DmStage();
                        listDmStage = new ArrayList<>();
                        nbDmStage = elem.getElementsByTagName("dmStage").getLength();
                        System.err.println("Nombre de demandes de stage: " + nbDmStage);
                        for (int dmStageTemp = 0; dmStageTemp < nbDmStage; dmStageTemp++){

                            id = elem.getElementsByTagName("id").item(dmStageTemp).getTextContent();
                            objet = elem.getElementsByTagName("objet").item(dmStageTemp).getTextContent();
                            description = elem.getElementsByTagName("description").item(dmStageTemp).getTextContent();
                            lieu = elem.getElementsByTagName("lieu").item(dmStageTemp).getTextContent();
                            dateDebut = elem.getElementsByTagName("dateDebut").item(dmStageTemp).getTextContent();
                            remuneration = elem.getElementsByTagName("remuneration").item(dmStageTemp).getTextContent();
                            duree = elem.getElementsByTagName("duree").item(dmStageTemp).getTextContent();
                            System.err.println("DUREE " + duree);
                            System.err.println("INFORMATION DEMANDE DE STAGE : " + id + " " + objet + " " + description + " " + lieu + " " + dateDebut + " " + remuneration + " " + duree);

                            dmStage = new DmStage(Integer.valueOf(id), description, objet, lieu, Integer.valueOf(remuneration), dateDebut, Integer.valueOf(duree));
                            listDmStage.add(dmStage);
                            System.err.println("Taille de la liste " + listDmStage.size());
                            aDmStageRepository.save(dmStage);
                        }
                        demandeStage = new DemandeStage(listDmStage);
                        aDemandeStageRepository.save(demandeStage); // On sauve la demande de stage
                        m = new Message("demandeStage", dateEnvoi, dureeValidite, demandeStage);
                        m.setId(id_message);
                        lMessage = fic.getListMess();
                        lMessage.add(m);
                        fic.setListMess(lMessage);
                        aMessageRepository.save(m);
                    }

                    
                    /*
                    // Réponse de stage
                    if (elem.getElementsByTagName("reponseStage").getLength() > 0){
                        
                        id = elem.getElementsByTagName("id").item(0).getTextContent();
                        objet = elem.getElementsByTagName("objet").item(0).getTextContent();
                        
                        // Remplissage de l'état civil
                        etatCivil = new EtatCivil(elem.getElementsByTagName("nom").item(0).getTextContent()
                                                , elem.getElementsByTagName("prenom").item(0).getTextContent()
                                                , elem.getElementsByTagName("dateNaissance").item(0).getTextContent()
                                                , elem.getElementsByTagName("lieuResidence").item(0).getTextContent()
                                                , elem.getElementsByTagName("photo").item(0).getTextContent()
                                                , elem.getElementsByTagName("email").item(0).getTextContent()
                                                , Integer.valueOf(elem.getElementsByTagName("tel").item(0).getTextContent()));
                        aEtatCivilRepository.save(etatCivil);

                        // Remplissage de la formation
                        formationStage = new FormationStage(elem.getElementsByTagName("titre").item(0).getTextContent()
                                                          , elem.getElementsByTagName("dateDebut").item(0).getTextContent()
                                                          , Integer.valueOf(elem.getElementsByTagName("duree").item(0).getTextContent())
                                                          , elem.getElementsByTagName("lieu").item(0).getTextContent()
                                                          , elem.getElementsByTagName("mention").item(0).getTextContent()
                                                          , elem.getElementsByTagName("description").item(0).getTextContent());
                        
                        aFormationStageRepository.save(formationStage);
                                    
                        // Remplissage de l'expérience
                        experience = new Experience(elem.getElementsByTagName("titre").item(0).getTextContent()
                                                  , elem.getElementsByTagName("dateDebut").item(0).getTextContent()
                                                  , Integer.valueOf(elem.getElementsByTagName("duree").item(0).getTextContent())
                                                  , elem.getElementsByTagName("lieu").item(0).getTextContent()
                                                  , elem.getElementsByTagName("fonction").item(0).getTextContent()
                                                  , elem.getElementsByTagName("description").item(0).getTextContent());

                        aExperienceRepository.save(experience);

                        cv = new CV(etatCivil, formationStage, experience);
                        aCvRepository.save(cv);

                        lettre = new Lettre(etatCivil,
                                            elem.getElementsByTagName("description").item(0).getTextContent());
                        aLettreRepository.save(lettre);

                        rpStage = new ReponseStage(objet, cv, lettre);
                        aReponseStageRepository.save(rpStage);
                        
                        m = new Message("reponseStage", dateEnvoi, dureeValidite, rpStage);
                        m.setId(id_message);
                        lMessage = fic.getListMess();
                        lMessage.add(m);
                        fic.setListMess(lMessage);
                        aMessageRepository.save(m);
                    }
                    */
                    /*
                    // Demande de catalogue
                    if (elem.getElementsByTagName("demandeCatalogue").getLength() > 0){
                        id = elem.getElementsByTagName("identifiant").item(0).getTextContent();
                        quantite = Integer.valueOf(elem.getElementsByTagName("quantite").item(0).getTextContent());

                        demandeCatalogue = new DemandeCatalogue(Integer.valueOf(id), quantite);
                        aDemandeCatalogueRepository.save(demandeCatalogue);

                        m = new Message("demandeCatalogue", dateEnvoi, dureeValidite, demandeCatalogue);
                        m.setId(id_message);
                        lMessage = fic.getListMess();
                        lMessage.add(m);
                        fic.setListMess(lMessage);
                        aMessageRepository.save(m);
                    }
                    */
                    
                    // Envoi de bon de commande
                    if (elem.getElementsByTagName("envoiBonCommande").getLength() > 0){

                        id = elem.getElementsByTagName("numCommande").item(0).getTextContent();
                        dateCommande = elem.getElementsByTagName("dateCommande").item(0).getTextContent(); 
                
                        produit = new Produit(Integer.valueOf(elem.getElementsByTagName("produit").item(0).getAttributes().item(0).getTextContent())
                                            , elem.getElementsByTagName("nom").item(0).getTextContent()
                                            , Integer.valueOf(elem.getElementsByTagName("prix").item(0).getTextContent())
                                            , Integer.valueOf(elem.getElementsByTagName("quantite").item(0).getTextContent())); 
                        
                                            
                        aProduitRepository.save(produit);                
                        listeProduit.add(produit);
                        envoiBonCommande = new EnvoiBonCommande(Integer.valueOf(id), dateCommande, listeProduit, Integer.valueOf(elem.getElementsByTagName("prixCommande").item(0).getTextContent()));
                        aBonCommandeRepository.save(envoiBonCommande);

                        m = new Message("envoiBonCommande", dateEnvoi, dureeValidite, envoiBonCommande);
                        m.setId(id_message);
                        lMessage = fic.getListMess();
                        lMessage.add(m);
                        fic.setListMess(lMessage);
                        aMessageRepository.save(m);

                    }
                }
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }catch(ParserConfigurationException u){
            u.printStackTrace();
        }catch(SAXException v){
            v.printStackTrace();
        }catch(NullPointerException ex){
            ex.printStackTrace();
    }
}
}
