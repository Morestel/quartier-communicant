package com.quartier.quartiercommunicant.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.quartier.quartiercommunicant.model.*;
import com.quartier.quartiercommunicant.repository.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.util.DateUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


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
    RpStageRepository aRpStageRepository;

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
    CatalogueDemandeRepository aCatalogueDemandeRepository;

    @Inject
    EnvoiBonCommandeRepository aBonCommandeRepository;

    @Inject
    ProduitRepository aProduitRepository;

    @Inject
    DemandeConferenceRepository aDemandeConferenceRepository;

    @Inject
    ConferenceRepository aConferenceRepository;

    @Inject
    EnvoiCatalogueRepository aEnvoiCatalogueRepository;

    @Inject
    ForfaitRepository aForfaitRepository;

    @Inject
    PropositionCommercialeRepository aPropositionCommercialeRepository;

    String tmpExpediteur = "";
    String idTmp = "-1";
    
    @RequestMapping(value = {"index", "" }, method = RequestMethod.GET)
    public String index(Model model){
        
        model.addAttribute("form", new UploadForm());
        File repertoire = new File("repertoire/");
        List<File> listeFichier = new ArrayList<>();
        int compteur = 0;
        File vTemp = new File("");
        try{
            if (repertoire.listFiles().length > 0){
                for (File f : repertoire.listFiles()){
                    if (f.isFile()){
                        compteur++;
                        listeFichier.add(f);
                        vTemp = f;
                    }
                }
                if (compteur == 1){
                    return "redirect:/lecture/" + vTemp.getName();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        model.addAttribute("listeFichier", listeFichier);
        
        return "index";
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String upload(Model model, UploadForm uploadForm) {

        if (uploadForm.getFile().isEmpty()) {
            return "update-user";
        }

        // Check if directory exist

        Path path = Paths.get("repertoire");
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (NoSuchFileException ex) {
                System.err.println(ex);
            } catch (IOException e) {
                System.err.println(e);
            }
        }

        int dot = uploadForm.getFile().getOriginalFilename().lastIndexOf(".");
        String extention = "";
        if (dot > 0) {
            extention = uploadForm.getFile().getOriginalFilename().substring(dot).toLowerCase();
        }

        if (!extention.equals(".xml")) {
            return "redirect:/index";
        }

        String filename = uploadForm.getFile().getOriginalFilename();
        Path uploadfile = Paths.get("repertoire/" + filename);

        try (OutputStream os = Files.newOutputStream(uploadfile, StandardOpenOption.CREATE)) {
            byte[] bytes = uploadForm.getFile().getBytes();
            os.write(bytes);
        } catch (IOException ex) {
            System.err.println(ex);
        }
       
        return "redirect:/index";

    }


    @RequestMapping("/fichier/{id}")
    public String lectureFichier(Model model, @PathVariable String id){
        Fichier fic = aFichierRepository.findFichierById(id);
        
        model.addAttribute("listeMessage", fic.getListMess());
        model.addAttribute("fichier", fic);

        return "LectureFichier";
    }

    public void deplacerFichier(String source, String destination){
        try {
            Files.move(Paths.get("repertoire/" + source), Paths.get("repertoire/" + destination + "/" + source),  StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("Impossible de déplacer le fichier");
            e.printStackTrace();
        }
    }


    @RequestMapping("/lecture/{nom}")
    public String lecture(Model model, @PathVariable String nom){
        
        Fichier fic = new Fichier("repertoire/" + nom);
        if (fic.getFic().length() > 10000){
            System.err.println("Trop de caractères");
        }

        try{
            String statut_en_tete = lireEnTete(fic);

            switch(statut_en_tete){
                case "OK": // C'est ok on passe à la suite
                    break;
                case "ERR-IDFICHIER": // Id de fichier déjà traité
                    System.err.println("Fichier déjà traité");
                   
                    deplacerFichier(nom,"erreur/" + tmpExpediteur);
                    model.addAttribute("raison", "Fichier déjà traité - Déplacement dans le dossier erreur");
                    return "ErreurLecture";
                    
                case "ERR-DESTINATAIRE": // Mauvais destinataire = Pas nous
                    System.err.println("Mauvais destinataire");
                    deplacerFichier(nom,"erreur/" + tmpExpediteur);
                    model.addAttribute("raison", "Mauvais destinataire - Déplacement dans le dossier erreur/" + tmpExpediteur);
                    return "ErreurLecture";
                case "ERR-EXPEDITEUR":
                    deplacerFichier(nom,"erreur/" + tmpExpediteur);
                    System.err.println("Expéditeur inconnu ");
                    model.addAttribute("raison", "Mauvais expéditeur - Déplacement dans le dossier erreur/" + tmpExpediteur);
                    return "ErreurLecture";
                    
                default:
                    System.err.println("Uncaught");
                    break;
            }

            String statut_messages = lireMessage(fic);
            switch(statut_messages){
                case "OK": // C'est bon on sauvegarde
                    aFichierRepository.save(fic);
                    break;
                case "ERR-CHECKSUM":
                    model.addAttribute("raison", "Checksum non conforme - Rejet du fichier");
                    return "ErreurLecture";
                    
            }

            // Tout s'est bien passé on déplace le fichier dans les archives
            deplacerFichier(nom, "archive/" + tmpExpediteur);
            
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
        
        return "LectureFichier";
    }

    public String lireEnTete(Fichier fic) throws ParserConfigurationException,
    SAXException, IOException, DOMException, ParseException{

        try{
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(fic.getFic());
            
            document.getDocumentElement().normalize();
            String id = document.getDocumentElement().getAttribute("id");
            System.err.println(id);
            String tmpExpediteur = document.getElementsByTagName("expediteur").item(0).getTextContent().replace(" ", "").replace("\n", "").replace("\t", "");
            switch(tmpExpediteur.toLowerCase()){
                case "magasins":
                    tmpExpediteur = "Magasin";
                    break;
                case "ecoles":
                    tmpExpediteur = "Ecole";
                    break;
                case "entreprises":
                    tmpExpediteur = "Entreprise";
                    break;
                case "magasin":
                    tmpExpediteur = "Magasin";
                    break;
                case "ecole":
                    tmpExpediteur = "Ecole";
                    break;
                case "entreprise":
                    tmpExpediteur = "Entreprise";
                    break;
                default:
                    this.tmpExpediteur = "Autre";
                    System.out.println(this.tmpExpediteur);
                    return "ERR-EXPEDITEUR";
            }
            fic.setExpediteur(tmpExpediteur);
           
            if (!aFichierRepository.findById(id).isEmpty() && tmpExpediteur.equals("Laboratoire")){ // Si il existe déjà et qu'on est l'expéditeur
                idTmp = id;
                return "ERR-EXPEDITEUR";
            }
            
            String destinataire = "";
            boolean trouveDest = false;
            for (int i = 0; i < document.getElementsByTagName("destinataire").getLength(); i++){
                destinataire = document.getElementsByTagName("destinataire").item(i).getTextContent().replace(" ", "").replace("\n", "").replace("\t", "");
                if (destinataire.equalsIgnoreCase("Laboratoire") || destinataire.equalsIgnoreCase("Laboratoires")){
                    trouveDest = true;
                    fic.setDestinataire(destinataire);
                }
            }
            if (!trouveDest){
                return "ERR-DESTINATAIRE";
            }
           
            System.out.println("Vérification si le fichier a déjà été traité ...");
            for (Fichier f : aFichierRepository.findAll()){
                if (f.getId().equals(id)){ // Fichier déjà traité
                    return "ERR-IDFICHIER";
                }
            }
            fic.setId(id);
            
            
            int check = Integer.parseInt(document.getElementsByTagName("nbMessages").item(0).getTextContent().replace(" ", "").replace("\n", "").replace("\t", ""));
            fic.setChecksum(check);

        }catch(IOException e){
            e.printStackTrace();
        }
        return "OK";

    }
    

    // Indique si l'id a besoin de changer pour suivre le bon format
    public boolean formatIdMessage(String id){
        
        if (id.length() > 5){
            if (id.substring(0, 4).equals("MAG-") || id.substring(0, 4).equals("ENT-") || id.substring(0, 4).equals("ECO-")){
                return false;
            }
        }
        
        return true;
    }

    // Rajoute un préfixe pour indiquer de qui est le message (Afin d'éviter un remplacement des messages dans la base de données au cas où un message de deux entités différentes aient le même id)
    public String ajoutPrefixe(String id, String expediteur){
        String finalId;
        if (expediteur.toLowerCase().matches("ecole") || expediteur.toLowerCase().matches("ecoles")){
            finalId = "ECO-";
        }
        else if (expediteur.toLowerCase().matches("entreprise") || expediteur.toLowerCase().matches("entreprises")){
            finalId = "ENT-";
        }
        else if (expediteur.toLowerCase().matches("magasin") || expediteur.toLowerCase().matches("magasins")){
            finalId = "MAG-";
        }
        else{
            finalId = "UNK-"; // Pour inconnu
        }
        return finalId + id;
    }
    public String lireMessage(Fichier fic){
        
        try {
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(fic.getFic());
            document.getDocumentElement().normalize();            
            NodeList listeMessage = document.getElementsByTagName("message");
            System.out.println("Taille liste message : " + listeMessage.getLength());

            /* 
                Type de messages:
                    - Offre collab
                    - Demande collab
                    - Réponse générique
            */

            // Stock les types de messages dans une liste de noeud
            /*NodeList oCollab = document.getElementsByTagName("offreCollab");
            NodeList dCollab = document.getElementsByTagName("demandeCollab");
            NodeList rGenerique = document.getElementsByTagName("reponseGenerique");
            NodeList dStage = document.getElementsByTagName("demandeStage");*/
            /*
            System.out.println("Nombre d'offre collab : " + oCollab.getLength());
            System.out.println("Nombre de demande collab : " + dCollab.getLength());
            System.out.println("Nombre de réponses génériques : " + rGenerique.getLength());
            System.out.println("Nombre de demande de stage : " + dStage.getLength());
            */
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
            String sujet;
            
            String msg;
            String idMsgPrecedent;

            String dateEnvoi;
            String dureeValidite;

            String dateCommande;

            String identifiantCommande;

            Message m;
            List<Message> lMessage = new ArrayList<>();

            Produit produit = new Produit();
            List<Produit> listeProduit = new ArrayList<>();

            DemandeStage demandeStage = new DemandeStage();
            DmStage dmStage = new DmStage();

            ReponseStage reponseStage = new ReponseStage();
            List<RpStage> listRpStage = new ArrayList<>();
            int nbRpStage = 0;
            RpStage rpStage = new RpStage();
            CV cv = new CV();
            EtatCivil etatCivil = new EtatCivil();
            FormationStage formationStage = new FormationStage();
            Experience experience = new Experience();
            Lettre lettre = new Lettre();

            int nbCatalogueDemande = 0;
            List<CatalogueDemande> listCatalogueDemande = new ArrayList<>();
            DemandeCatalogue demandeCatalogue = new DemandeCatalogue();
            CatalogueDemande catalogueDemande= new CatalogueDemande();
            String titreCatalogueDemande;

            EnvoiBonCommande envoiBonCommande = new EnvoiBonCommande();
            DemandeConference demandeConference = new DemandeConference();
            Conference conference = new Conference();
            List<Conference> listeConference = new ArrayList<>();
            int nbConference = 0;

            EnvoiCatalogue envoiCatalogue = new EnvoiCatalogue();
            int nbDmStage = 0;
            List<DmStage> listDmStage = new ArrayList<>(); 

            Forfait forfait = new Forfait();
            PropositionCommerciale propositionCommerciale = new PropositionCommerciale();
            List<Forfait> listeForfait = new ArrayList<>();

            NodeList nList = document.getElementsByTagName("message");

            
            if (fic.getChecksum() != nList.getLength()){
                return "ERR-CHECKSUM";
            }

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
                    dateEnvoi = elem.getElementsByTagName("dateEnvoi").item(0).getTextContent().replaceAll("\\s", "");
                    dureeValidite = elem.getElementsByTagName("dureeValidite").item(0).getTextContent().replaceAll("\\s", "");
                    dateEnvoi = dateEnvoi.substring(0, 8) + " " + dateEnvoi.substring(8);
                    System.err.println(dateEnvoi + " " + dureeValidite);
                    // On récupère la date d'envoi, on ajoute la durée de validité et on compare à la date d'aujourd'hui
                    String pattern = "HH:mm:ss dd-MM-yyyy";
                    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                    Date debut = new Date();
                    Calendar cal = Calendar.getInstance();
                    
                    try {
                        debut = dateFormat.parse(dateEnvoi);
                        cal.setTime(debut);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(dureeValidite));
                    
                    // ASCII elem.getTextContent().matches("\\A\\p{ASCII}*\\z") && 
                    if (cal.getTime().after(new Date())){ // Vérification du message en ASCII ET si la date n'est pas dépassée

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

                        
                        
                        // Réponse de stage
                        if (elem.getElementsByTagName("reponseStage").getLength() > 0){

                            reponseStage = new ReponseStage();
                            rpStage = new RpStage();
                            listRpStage = new ArrayList<>();
                            nbRpStage = elem.getElementsByTagName("rpStage").getLength();
                            System.err.println("Nombre de réponses de stage: " + nbRpStage);

                            for (int rpStageTmp = 0; rpStageTmp < nbRpStage; rpStageTmp++) {

                                id = elem.getElementsByTagName("id").item(rpStageTmp).getTextContent();
                                objet = elem.getElementsByTagName("objet").item(rpStageTmp).getTextContent();
                            
                                // Remplissage de l'état civil
                                etatCivil = new EtatCivil(elem.getElementsByTagName("nom").item(rpStageTmp).getTextContent()
                                                        , elem.getElementsByTagName("prenom").item(rpStageTmp).getTextContent()
                                                        , elem.getElementsByTagName("dateNaissance").item(rpStageTmp).getTextContent()
                                                        , elem.getElementsByTagName("lieuResidence").item(rpStageTmp).getTextContent()
                                                        , elem.getElementsByTagName("photo").item(rpStageTmp).getTextContent()
                                                        , elem.getElementsByTagName("email").item(rpStageTmp).getTextContent()
                                                        , Integer.valueOf(elem.getElementsByTagName("tel").item(rpStageTmp).getTextContent()));
                                aEtatCivilRepository.save(etatCivil);

                                // Remplissage de la formation
                                formationStage = new FormationStage(elem.getElementsByTagName("titre").item(rpStageTmp).getTextContent()
                                                                , elem.getElementsByTagName("dateDebut").item(rpStageTmp).getTextContent()
                                                                , Integer.valueOf(elem.getElementsByTagName("duree").item(rpStageTmp).getTextContent())
                                                                , elem.getElementsByTagName("lieu").item(rpStageTmp).getTextContent()
                                                                , elem.getElementsByTagName("mention").item(rpStageTmp).getTextContent()
                                                                , elem.getElementsByTagName("description").item(rpStageTmp).getTextContent());
                                
                                aFormationStageRepository.save(formationStage);
                                            
                                // Remplissage de l'expérience
                                experience = new Experience(elem.getElementsByTagName("titre").item(rpStageTmp).getTextContent()
                                                        , elem.getElementsByTagName("dateDebut").item(rpStageTmp).getTextContent()
                                                        , Integer.valueOf(elem.getElementsByTagName("duree").item(rpStageTmp).getTextContent())
                                                        , elem.getElementsByTagName("lieu").item(rpStageTmp).getTextContent()
                                                        , elem.getElementsByTagName("fonction").item(rpStageTmp).getTextContent()
                                                        , elem.getElementsByTagName("description").item(rpStageTmp).getTextContent());

                                aExperienceRepository.save(experience);

                                cv = new CV(etatCivil, formationStage, experience);
                                aCvRepository.save(cv);

                                lettre = new Lettre(etatCivil,
                                                    elem.getElementsByTagName("description").item(rpStageTmp).getTextContent());
                                aLettreRepository.save(lettre);

                                rpStage = new RpStage(Integer.valueOf(id), objet, cv, lettre);
                                listRpStage.add(rpStage);
                                aRpStageRepository.save(rpStage);

                            }

                            reponseStage = new ReponseStage(listRpStage);
                            aReponseStageRepository.save(reponseStage);
                            
                            m = new Message("reponseStage", dateEnvoi, dureeValidite, reponseStage);
                            m.setId(id_message);
                            lMessage = fic.getListMess();
                            lMessage.add(m);
                            fic.setListMess(lMessage);
                            aMessageRepository.save(m);
                        }
                        

                        
                        // Demande de catalogue
                        if (elem.getElementsByTagName("demandeCatalogue").getLength() > 0){
                            demandeCatalogue = new DemandeCatalogue();
                            catalogueDemande = new CatalogueDemande();
                            listCatalogueDemande = new ArrayList<>();
                            nbCatalogueDemande = elem.getElementsByTagName("catalogueDemande").getLength();
                            System.err.println("Nombre de demandes de catalogue: " + nbCatalogueDemande);
                            for (int nbCatalogueDemandeTemp = 0; nbCatalogueDemandeTemp < nbCatalogueDemande; nbCatalogueDemandeTemp++){

                                id = elem.getElementsByTagName("id").item(nbCatalogueDemandeTemp).getTextContent();
                                titreCatalogueDemande = elem.getElementsByTagName("titreCatalogueDemande").item(nbCatalogueDemandeTemp).getTextContent();
                                quantite = Integer.parseInt(elem.getElementsByTagName("quantite").item(nbCatalogueDemandeTemp).getTextContent());

                                catalogueDemande = new CatalogueDemande(Integer.valueOf(id), titreCatalogueDemande, quantite);
                                listCatalogueDemande.add(catalogueDemande);
                                System.err.println("Taille de la liste " + listCatalogueDemande.size());
                                aCatalogueDemandeRepository.save(catalogueDemande);
                            }
                            demandeCatalogue = new DemandeCatalogue(listCatalogueDemande);
                            aDemandeCatalogueRepository.save(demandeCatalogue); // On sauve la demande de catalogue
                            m = new Message("demandeCatalogue", dateEnvoi, dureeValidite, demandeCatalogue);
                            m.setId(id_message);
                            lMessage = fic.getListMess();
                            lMessage.add(m);
                            fic.setListMess(lMessage);
                            aMessageRepository.save(m);
                        }
                        
                        
                        // Envoi de bon de commande
                        if (elem.getElementsByTagName("envoiBonCommande").getLength() > 0){

                            id = elem.getElementsByTagName("numCommande").item(0).getTextContent();
                            dateCommande = elem.getElementsByTagName("dateCommande").item(0).getTextContent(); 
                            identifiantCommande = elem.getElementsByTagName("numCommande").item(0).getAttributes().item(0).getTextContent();
                            
                            int nbProduit = elem.getElementsByTagName("produit").getLength();
                            listeProduit = new ArrayList<>();
                            for (int nbTempProd = 0; nbTempProd < nbProduit; nbTempProd++){
                                produit = new Produit(elem.getElementsByTagName("produit").item(nbTempProd).getAttributes().item(0).getTextContent()
                                                    , elem.getElementsByTagName("nom").item(nbTempProd).getTextContent()
                                                    , Float.valueOf(elem.getElementsByTagName("prix").item(nbTempProd).getTextContent().replaceAll("\\s", ""))
                                                    , Integer.valueOf(elem.getElementsByTagName("quantite").item(nbTempProd).getTextContent().replaceAll("\\s", ""))); 
                            
                            listeProduit.add(produit);
                            aProduitRepository.save(produit); 
                            
                            }               
                            
                            envoiBonCommande = new EnvoiBonCommande(id, identifiantCommande, dateCommande, listeProduit, Float.valueOf(elem.getElementsByTagName("prixCommande").item(0).getTextContent().replaceAll("\\s", "")));
                            aBonCommandeRepository.save(envoiBonCommande);

                            m = new Message("envoiBonCommande", dateEnvoi, dureeValidite, envoiBonCommande);
                            m.setId(id_message);
                            lMessage = fic.getListMess();
                            lMessage.add(m);
                            fic.setListMess(lMessage);
                            aMessageRepository.save(m);

                        }

                        if (elem.getElementsByTagName("demandeConference").getLength() > 0){
                        
                            demandeConference = new DemandeConference();
                            conference = new Conference();
                            nbConference= elem.getElementsByTagName("conf").getLength();
                            for (int nbConferenceTemp = 0; nbConferenceTemp < nbConference; nbConferenceTemp++){
                                id = elem.getElementsByTagName("id").item(nbConferenceTemp).getTextContent();
                                sujet = elem.getElementsByTagName("sujet").item(nbConferenceTemp).getTextContent();
                                lieu = elem.getElementsByTagName("lieu").item(nbConferenceTemp).getTextContent();
                                dateDebut = elem.getElementsByTagName("dateDebut").item(nbConferenceTemp).getTextContent();
                                duree = elem.getElementsByTagName("dureeConference").item(nbConferenceTemp).getTextContent();
                                conference = new Conference(id, sujet, lieu, dateDebut, Integer.valueOf(duree));
                                listeConference.add(conference);
                                aConferenceRepository.save(conference);
                            }

                            demandeConference = new DemandeConference(listeConference);
                            aDemandeConferenceRepository.save(demandeConference);
                            m = new Message("demandeConference", dateEnvoi, dureeValidite, demandeConference);
                            m.setId(id_message);
                            lMessage = fic.getListMess();
                            lMessage.add(m);
                            fic.setListMess(lMessage);
                            aMessageRepository.save(m);
                        }

                        if (elem.getElementsByTagName("envoiCatalogue").getLength() > 0){
                            
                            titreCatalogueDemande = elem.getElementsByTagName("titreCatalogue").item(0).getTextContent();
                            int nbProduit = elem.getElementsByTagName("produit").getLength();
                            listeProduit = new ArrayList<>();
                            for (int nbTempProd = 0; nbTempProd < nbProduit; nbTempProd++){
                                produit = new Produit(elem.getElementsByTagName("produit").item(nbTempProd).getAttributes().item(0).getTextContent(),
                                                      elem.getElementsByTagName("nom").item(nbTempProd).getTextContent(),
                                                      Float.parseFloat(elem.getElementsByTagName("prix").item(nbTempProd).getTextContent().replaceAll("\\s", "")),
                                                      Integer.parseInt(elem.getElementsByTagName("quantite").item(nbTempProd).getTextContent().replaceAll("\\s", "")));
                                listeProduit.add(produit);
                                aProduitRepository.save(produit);
                            }
                            envoiCatalogue = new EnvoiCatalogue(titreCatalogueDemande, listeProduit);
                            aEnvoiCatalogueRepository.save(envoiCatalogue);
                            m = new  Message("envoiCatalogue", dateEnvoi,dureeValidite, envoiCatalogue);
                            m.setId(id_message);
                            lMessage = fic.getListMess();
                            lMessage.add(m);
                            fic.setListMess(lMessage);
                            aMessageRepository.save(m);
                            listeProduit = new ArrayList<>();
                        }

                        if (elem.getElementsByTagName("propositionCommerciale").getLength() > 0){
                            
                            float prixProposition = 0;
                            int nbForfait = elem.getElementsByTagName("forfait").getLength();
                            for (int nbForfaitTmp = 0; nbForfaitTmp < nbForfait; nbForfaitTmp++){
                                prixProposition = Float.parseFloat(elem.getElementsByTagName("forfait").item(nbForfaitTmp).getTextContent());
                                forfait = new Forfait(prixProposition);
                                aForfaitRepository.save(forfait);
                                listeForfait.add(forfait);
                            }
                            description = elem.getElementsByTagName("description").item(0).getTextContent();
                            dateDebut = elem.getElementsByTagName("dateDebut").item(0).getTextContent();
                            propositionCommerciale = new PropositionCommerciale(listeForfait, description, dateDebut);
                            if (elem.getElementsByTagName("dateFin").getLength() != 0){
                                propositionCommerciale.setDateFin(elem.getElementsByTagName("dateFin").item(0).getTextContent());
                            }
                            else{
                                propositionCommerciale.setDateFin(null);
                            }
                            if (elem.getElementsByTagName("duree").getLength() != 0){
                                propositionCommerciale.setDuree(Integer.parseInt(elem.getElementsByTagName("duree").item(0).getTextContent()));
                            }
                            else{
                                propositionCommerciale.setDuree(0);
                            }
                            aPropositionCommercialeRepository.save(propositionCommerciale);
                            m = new  Message("propositionCommerciale", dateEnvoi,dureeValidite, propositionCommerciale);
                            m.setId(id_message);
                            lMessage = fic.getListMess();
                            lMessage.add(m);
                            fic.setListMess(lMessage);
                            aMessageRepository.save(m);
                            listeForfait = new ArrayList<>();
                        }
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
    return "OK";
}
}
