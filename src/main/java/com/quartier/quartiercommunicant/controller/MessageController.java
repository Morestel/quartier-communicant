package com.quartier.quartiercommunicant.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import com.quartier.quartiercommunicant.model.DemandeCatalogue;
import com.quartier.quartiercommunicant.model.DemandeCommerciale;
import com.quartier.quartiercommunicant.model.CatalogueDemande;
import com.quartier.quartiercommunicant.model.DemandeStage;
import com.quartier.quartiercommunicant.model.DmStage;
import com.quartier.quartiercommunicant.model.Fichier;
import com.quartier.quartiercommunicant.model.Message;
import com.quartier.quartiercommunicant.repository.CVRepository;
import com.quartier.quartiercommunicant.repository.DemandeCatalogueRepository;
import com.quartier.quartiercommunicant.repository.DemandeCommercialeRepository;
import com.quartier.quartiercommunicant.repository.DemandeConferenceRepository;
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
import com.quartier.quartiercommunicant.repository.CatalogueDemandeRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


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

    @Inject
    CatalogueDemandeRepository aCatalogueDemandeRepository;

    @Inject
    DemandeCommercialeRepository aDemandeCommercialeRepository;

    List<Message> listeMessage = new ArrayList<>();
    List<DmStage> listeDmStage = new ArrayList<>();
    int totalDmStageEcole = 0;
    int totalDmStageEntreprise = 0;
    List<CatalogueDemande> listeCatalogueDemande = new ArrayList<>();

    List<Message> listeMessageEcole = new ArrayList<>();
    List<Message> listeMessageMagasin = new ArrayList<>();
    List<Message> listeMessageEntreprise = new ArrayList<>();
    int idTemporaire = 0;
    String pattern = "HH:mm:ss dd-MM-YYYY";
    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

    @RequestMapping(value = "/reponseGenerique", method = RequestMethod.POST)
    public String reponseGenerique(@RequestParam String textarea, @RequestParam String destinataire,
            @RequestParam String validite) {

        
        String pattern = "HH:mm:ss dd-MM-YYYY";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                
        Message m = new Message("reponseGenerique", dateFormat.format(new Date()), validite, textarea, "");
        m.setId(""+idTemporaire);
        idTemporaire++;
        switch (destinataire) {
        case "Magasin":
            listeMessageMagasin.add(m);
            break;
        case "Entreprise":
            listeMessageEntreprise.add(m);
            break;
        case "Ecole":
            listeMessageEcole.add(m);
            break;
        default:
            break;

        }
        return "redirect:/envoiMessage";
    }

    @RequestMapping(value = "/demandeCollaboration", method = RequestMethod.POST)
    public String demandeCollaboration(@RequestParam String destinataire, @RequestParam String description,
            @RequestParam String dateDebut, @RequestParam String dateFin, @RequestParam String validite) {
        String pattern = "HH:mm:ss dd-MM-YYYY";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        
        Date d = new Date();
        Date dF = new Date();
        try {
            String pat = "YYYY-MM-dd";
            SimpleDateFormat df = new SimpleDateFormat(pat);
            d = df.parse(dateDebut);
            dF = df.parse(dateFin);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Message m = new Message("demandeCollab", dateFormat.format(new Date()), validite, description, dateFormat.format(d),
                dateFormat.format(dF));
                m.setId(""+idTemporaire);
                idTemporaire++;
        switch (destinataire) {
        case "Magasin":
            listeMessageMagasin.add(m);
            break;
        case "Entreprise":
            listeMessageEntreprise.add(m);
            break;
        case "Ecole":
            listeMessageEcole.add(m);
            break;
        default:
            break;

        }

        return "redirect:/envoiMessage";
    }

    @RequestMapping(value = "/demandeCatalogue", method = RequestMethod.POST)
    public String demandeCatalogue(@RequestParam String id, @RequestParam String titreCatalogueDemande, @RequestParam int quantite,
            @RequestParam String validite) {
        String pattern = "HH:mm:ss dd-MM-YYYY";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        /*DemandeCatalogue dCatalogue = new DemandeCatalogue(Integer.valueOf(id), Integer.valueOf(quantite));
        Message m = new Message("demandeCatalogue", dateFormat.format(new Date()), validite, dCatalogue);
        listeMessageMagasin.add(m);*/

        // On cherche un ID qui est libre dans les demandes de catalogue
        int i = 0;
        boolean trouve = false;
        while (i < 5000 && !trouve) {
            i++;
            if (aCatalogueDemandeRepository.findById(i).isEmpty()) {
                trouve = true;
            }

        }
        List<CatalogueDemande> vListTemp = new ArrayList<>();
        CatalogueDemande cdm = new CatalogueDemande(i, titreCatalogueDemande, quantite);
        aCatalogueDemandeRepository.save(cdm);
        vListTemp.add(cdm);
        DemandeCatalogue dmC = new DemandeCatalogue(vListTemp);

        Message m = new Message("demandeCatalogue", dateFormat.format(new Date()), validite, dmC);
        m.setId(""+idTemporaire);
        idTemporaire++;
        listeMessageMagasin.add(m);

        return "redirect:/envoiMessage";
    }


    @RequestMapping(value = "/demandeCommerciale", method = RequestMethod.POST)
    public String demandeCommerciale(@RequestParam int prixProposition, @RequestParam String description, @RequestParam String validite, @RequestParam String dateDebut, @RequestParam String dateFin, @RequestParam String duree){
        String pattern = "HH:mm:ss dd-MM-YYYY";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date d = new Date();
        Date dF = new Date();
        try {
            String pat = "YYYY-MM-dd";
            SimpleDateFormat df = new SimpleDateFormat(pat);
            d = df.parse(dateDebut);
            dF = df.parse(dateFin);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DemandeCommerciale dc = new DemandeCommerciale(prixProposition, description, dateFormat.format(d), dateFormat.format(dF));
        Message m = new Message("demandeCommerciale", dateFormat.format(new Date()), validite, dc);
        m.setId(""+idTemporaire);
        idTemporaire++;
        aDemandeCommercialeRepository.save(dc);
        listeMessageMagasin.add(m);

        return "redirect:/envoiMessage";
    }


    @RequestMapping(value = "/demandeStage", method = RequestMethod.POST)
    public String demandeStage(@RequestParam String objet, @RequestParam String description, @RequestParam String lieu,
            @RequestParam String remuneration, @RequestParam String dateDebut, @RequestParam String dateFin,
            @RequestParam String duree, @RequestParam String destinataire, @RequestParam String validite) {
        String pattern = "HH:mm:ss dd-MM-YYYY";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

        // On cherche un ID qui est libre dans les demandes de stages
        int i = 0;
        boolean trouve = false;
        while (i < 5000 && !trouve) {
            i++;
            if (aDmStageRepository.findById(i).isEmpty()) {
                trouve = true;
            }

        }
        List<DmStage> vListTemp = new ArrayList<>();
        Date d = new Date();
        try {
            String pat = "YYYY-MM-dd";
            SimpleDateFormat df = new SimpleDateFormat(pat);
            d = df.parse(dateDebut);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DmStage dmS = new DmStage(i, description, objet, lieu, Integer.valueOf(remuneration), dateFormat.format(d),
                Integer.valueOf(duree));
        aDmStageRepository.save(dmS);
        vListTemp.add(dmS);
        DemandeStage ds = new DemandeStage(vListTemp);
        aDemandeStageRepository.save(ds);
        Message m = new Message("demandeStage", dateFormat.format(new Date()), validite, ds);
        m.setId(""+idTemporaire);
        idTemporaire++;
        switch (destinataire) {
        case "Magasin":
            listeMessageMagasin.add(m);
            break;
        case "Entreprise":
            totalDmStageEntreprise++;
            listeMessageEntreprise.add(m);
            break;
        case "Ecole":
            totalDmStageEcole++;
            listeMessageEcole.add(m);
            break;
        default:
            break;

        }

        return "redirect:/envoiMessage";
    }

    @RequestMapping(value = "/waitingRoom")
    public String waitingRoom(Model model){
        ArrayList<Message> listeMessageP = new ArrayList<>();
        listeMessageP.addAll(listeMessageEcole);
        listeMessageP.addAll(listeMessageEntreprise);
        listeMessageP.addAll(listeMessageMagasin);
        model.addAttribute("listeEcole", listeMessageEcole);
        model.addAttribute("listeEntreprise", listeMessageEntreprise);
        model.addAttribute("listeMagasin", listeMessageMagasin);
        model.addAttribute("listeMessage", listeMessageP);
        return "WaitingRoom";
    }

    @RequestMapping(value = "/delete/{id}")
    public String deleteMessage(Model model, @PathVariable int id){
        Message temp = new Message();
        int whichOne = -1;
        // On retrouve le message dans les différentes listes puis on regarde son type
        // pour bien le supprimer de partout
        for (Message m : listeMessageEcole){
            if (m.getId().equals(""+id)){
                temp = m;
                whichOne = 0;
                switch(m.getType()){ // On gère les cas problématiques. A savoir demande de stage et demande de catalogue
                    case "demandeStage":
                        DemandeStage dm = m.getDemandeStage();
                        listeDmStage.removeAll(dm.getListDmStage());
                        break;
                    case "demandeCatalogue":
                        DemandeCatalogue dc = m.getDemandeCatalogue();
                        listeCatalogueDemande.removeAll(dc.getListCatalogueDemande());
                        break;
                    default:
                        break;
                }
            }
        }
        for (Message m : listeMessageMagasin){
            if (m.getId().equals(""+id)){
                temp = m;
                whichOne = 1;
                switch(m.getType()){ // On gère les cas problématiques. A savoir demande de stage et demande de catalogue
                    case "demandeStage":
                        DemandeStage dm = m.getDemandeStage();
                        listeDmStage.removeAll(dm.getListDmStage());
                        break;
                    case "demandeCatalogue":
                        DemandeCatalogue dc = m.getDemandeCatalogue();
                        listeCatalogueDemande.removeAll(dc.getListCatalogueDemande());
                        break;
                    default:
                        break;
                }
            }
        }
        for (Message m : listeMessageEntreprise){
            if (m.getId().equals(""+id)){
                temp = m;
                whichOne = 2;
                switch(m.getType()){ // On gère les cas problématiques. A savoir demande de stage et demande de catalogue
                    case "demandeStage":
                        DemandeStage dm = m.getDemandeStage();
                        listeDmStage.removeAll(dm.getListDmStage());
                        break;
                    case "demandeCatalogue":
                        DemandeCatalogue dc = m.getDemandeCatalogue();
                        listeCatalogueDemande.removeAll(dc.getListCatalogueDemande());
                        break;
                    default:
                        break;
                }
            }
        }
        
        // On regarde laquelle des listes est concernée
        if (whichOne == 0){
            listeMessageEcole.remove(temp);
        }
        else if (whichOne == 1){
            listeMessageMagasin.remove(temp);
        }
        else if (whichOne == 2){
            listeMessageEntreprise.remove(temp);
        }
        
        return "redirect:/waitingRoom";
    }

    @RequestMapping(value = "/toutEnvoyer", method = RequestMethod.POST)
    public String toutEnvoyer() {

        // On écrit tous les messages pour chaque destinataire, ce qui peut conduire à
        // plusieurs fichiers
        if (!listeMessageEcole.isEmpty()) {
            EcrireMessages("Ecole");
        }

        if (!listeMessageEntreprise.isEmpty()) {
            EcrireMessages("Entreprise");
        }

        if (!listeMessageMagasin.isEmpty()) {
            EcrireMessages("Magasin");
        }

        // On supprime les listes vu qu'on vient de les traiter
        listeMessageEcole = new ArrayList<>();
        listeMessageEntreprise = new ArrayList<>();
        listeMessageMagasin = new ArrayList<>();
        listeMessage = new ArrayList<>();
        listeDmStage = new ArrayList<>();
        totalDmStageEcole = 0;
        totalDmStageEntreprise = 0;
        
       listeCatalogueDemande = new ArrayList<>();
        return "redirect:/envoiMessage";
    }

    @RequestMapping("envoiMessage")
    public String envoiMessage(Model model) {
        model.addAttribute("listeEcole", listeMessageEcole);
        model.addAttribute("listeEntreprise", listeMessageEntreprise);
        model.addAttribute("listeMagasin", listeMessageMagasin);
        return "EnvoiMessage";
    }

    public int compteNombreMessage(List<Message> listeMess){
        int total = 0;
        boolean boolStage = false;
        boolean boolCatalogue = false;
        for (Message m: listeMess){
            switch(m.getType()){
                case "demandeCollab":
                    total++;
                    break;
                case "demandeStage":
                    if (!boolStage){
                        total++;
                        boolStage = true;
                    }
                    break;
                case "demandeCatalogue":
                    if (!boolCatalogue){
                        total++;
                        boolCatalogue = true;
                    }
                    break;
                default:
                    total++;
                    break;
            }
        }
        return total;
    }

    public void EcrireMessages(String destinataire) {

        int i = 0;
        boolean trouve = false;
        String vMessage = "";

        String id = "";
        String dureeValidite = "";

        Fichier fic = new Fichier();
        fic.setDestinataire(destinataire);
        fic.setExpediteur("Laboratoire");
        String pattern = "HH:mm:ss dd-MM-YYYY";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

        int id_fichier = -1;
        while (i < 5000 && !trouve) {
            if (aFichierRepository.findById("LAB-" + i).isEmpty()) {
                trouve = true;
                id_fichier = i;
                fic.setId("LAB-" + i);
            }
            i++;
        }
        // On remet i à 0 et trouve a false
        i = 0;
        trouve = false;
        try (FileWriter fw = new FileWriter(
                "repertoire/envoi/" + destinataire.toLowerCase() + "/LAB-" + id_fichier + ".xml", false);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {

            // Pour éviter de faire pour chaque cas on va coller la bonne liste dans une
            // liste faite pour
            switch (destinataire) {
            case "Ecole":
                listeMessage.addAll(listeMessageEcole);
                break;
            case "Entreprise":
                listeMessage.addAll(listeMessageEntreprise);
                break;
            case "Magasin":
                listeMessage.addAll(listeMessageMagasin);
                break;
            default:
                System.err.println("Erreur");
                break;
            }

            fic.setChecksum(listeMessage.size());
            out.write(ajoutDTD());

            out.write(EcrireEnTete(id_fichier, destinataire, compteNombreMessage(listeMessage)));

            for (Message m : listeMessage) {

                // On cherche un id de message libre
                switch (m.getType()) {
                    case "reponseGenerique":
                        while (i < 50000 && !trouve) {
                            if (aMessageRepository.findById("LAB-" + i).isEmpty()) {
                                m.setId("LAB-" + i);
                                trouve = true;
                            }
                            i++;
                        }
                        out.write(ajoutHeaderMessage(m.getId(), m.getDateEnvoi(), m.getDureeValidite()));
                        aMessageRepository.save(m);
                        // On écrit le message
                        vMessage = "\n\t<typeMessage>" + "\n\t\t<reponseGenerique>" + "\n\t\t\t<msg>" + m.getMsg() + "</msg>"
                                + "\n\t\t\t<idMsgPrécédent>" + m.getIdMsgPrecedent() + "</idMsgPrécédent>"
                                + "\n\t\t</reponseGenerique>" + "\n\t</typeMessage>" + "\n\t</message>";
                        out.write(vMessage);
                        trouve = false;
                        break;

                    case "demandeCollab":
                        while (i < 50000 && !trouve) {
                            if (aMessageRepository.findById("LAB-" + i).isEmpty()) {
                                m.setId("LAB-" + i);
                                trouve = true;
                            }
                            i++;
                        }
                        out.write(ajoutHeaderMessage(m.getId(), m.getDateEnvoi(), m.getDureeValidite()));
                        aMessageRepository.save(m);
                        // On écrit le message
                        vMessage = "\n\t<typeMessage>" + "\n\t\t<demandeCollab>" + "\n\t\t\t<description>" + m.getDescription()
                                + "</description>" + "\n\t\t\t<date>" + "\n\t\t\t\t<dateDebut>" + m.getDateDebut()
                                + "</dateDebut>" + "\n\t\t\t\t<dateFin>" + m.getDateFin() + "</dateFin>" + "\n\t\t\t</date>"
                                + "\n\t\t</demandeCollab>" + "\n\t</typeMessage>" + "\n\t</message>";
                        out.write(vMessage);
                        trouve = false;
                        break;

                        case "demandeCommerciale":
                            while (i < 50000 && !trouve) {
                                if (aMessageRepository.findById("LAB-" + i).isEmpty()) {
                                    m.setId("LAB-" + i);
                                    trouve = true;
                                }
                                i++;
                            }
                            out.write(ajoutHeaderMessage(m.getId(), m.getDateEnvoi(), m.getDureeValidite()));
                            aMessageRepository.save(m);
                            // Ecriture du message
                            

                            vMessage = "\n\t<typeMessage>" + "\n\t\t<demandeCommerciale>" + 
                                       "\n\t\t\t<prixProposition>" + "\n\t\t\t\t<forfait>" + m.getDemandeCommerciale().getPrixProposition() +"</forfait>"+ "\n\t\t\t</prixProposition>" +
                                       "\n\t\t\t<description>" + m.getDemandeCommerciale().getDescription() + "</description>" + 
                                       "\n\t\t\t<contrat>" +
                                       "\n\t\t\t\t<dateDebut>" + m.getDemandeCommerciale().getDateDebut() + "</dateDebut>" + 
                                       "\n\t\t\t\t<dateFin>" + m.getDemandeCommerciale().getDateFin() + "</dateFin>" + 
                                       "\n\t\t\t</contrat>" + 
                                       "\n\t\t</demandeCommerciale>" + 
                                       "\n\t</typeMessage>" + 
                                       "\n\t</message>";
                            out.write(vMessage);
                            trouve = false;
                            break;
                            

                    case "demandeCatalogue":
                        m.setId("Temp");
                        listeCatalogueDemande.add(m.getDemandeCatalogue().getListCatalogueDemande().get(0));
                        dureeValidite= m.getDureeValidite();
                        aDemandeCatalogueRepository.save(m.getDemandeCatalogue());
                        aMessageRepository.save(m);
                        break;

                    case "demandeStage":
                        // On regroupe toutes les demandes dans la liste
                        listeDmStage.add(m.getDemandeStage().getListDmStage().get(0));  
                        dureeValidite = m.getDureeValidite();
                        m.setId("Temp");
                        aDemandeStageRepository.save(m.getDemandeStage());
                        aMessageRepository.save(m);
                        // On attend d'être arrivé au nombre total de messages contenant des demandes de stages avant de passer à l'enregistrement
                        int totalDmStage = 0;
                        switch(destinataire){
                            case "Entreprise":
                                totalDmStage = totalDmStageEntreprise; 
                                break;
                            case "Ecole":
                                totalDmStage = totalDmStageEcole;
                                break;
                            default:
                                totalDmStage = 0;
                        }
                        if (totalDmStage == listeDmStage.size()){
                            DemandeStage ds = new DemandeStage(listeDmStage);
                            aDemandeStageRepository.save(ds);
                            while (i < 50000 && !trouve) {
                                if (aMessageRepository.findById("LAB-" + i).isEmpty()) {
                                    id = "LAB-" + i;
                                    trouve = true;
                                }
                                i++;
                            }
                            
                            m.setDemandeStage(ds);
                            m.setId(id);
                            out.write(ajoutHeaderMessage(m.getId(), m.getDateEnvoi(), m.getDureeValidite()));
                            aDemandeStageRepository.save(ds);
                            aMessageRepository.save(m);
                            vMessage = "<\n\t<typeMessage>" + "\n\t<demandeStage>";
                            for (DmStage vDmTemp : ds.getListDmStage()) {
                                vMessage += "\n\t\t<dmStage>" + "\n\t\t\t<id>" + vDmTemp.getId() + "</id>" + "\n\t\t\t<objet>"
                                        + vDmTemp.getObjet() + "</objet>" + "\n\t\t\t<description>" + vDmTemp.getDescription()
                                        + "</description>" + "\n\t\t\t<lieu>" + vDmTemp.getLieu() + "</lieu>"
                                        + "\n\t\t\t<remuneration>" + vDmTemp.getRemuneration() + "</remuneration>"
                                        + "\n\t\t\t<date>" + "\n\t\t\t\t<dateDebut>" + vDmTemp.getDateDebut() + "</dateDebut>"
                                        + "\n\t\t\t\t<duree>" + vDmTemp.getDuree() + "</duree>" + "\n\t\t\t</date>"
                                        + "\n\t\t</dmStage>";
                            }
                            vMessage += "\n\t</demandeStage>" + "\n\t</typeMessage>" + "\n</message>";
                            out.write(vMessage);
                            trouve = false;
                            listeDmStage = new ArrayList<>();
                        }
                        break;
                    default : 
                        break;
                    }
                
                i++;
            }

            // on écrit les messages de demande de catalogue
            if (listeCatalogueDemande.size() != 0) {
                DemandeCatalogue dc = new DemandeCatalogue(listeCatalogueDemande);
                aDemandeCatalogueRepository.save(dc);
                while (i < 50000 && !trouve) {
                    if (aMessageRepository.findById("LAB-" + i).isEmpty()) {
                        id = "LAB-" + i;
                        trouve = true;
                    }
                    i++;
                }
                trouve = false;
                Message ms = new Message("demandeCatalogue", dateFormat.format(new Date()), dureeValidite, dc);
                ms.setId(id);
                aMessageRepository.save(ms);
                out.write(ajoutHeaderMessage(ms.getId(), ms.getDateEnvoi(), ms.getDureeValidite()));
                
                vMessage = "\n\t<typeMessage>" + "\n\t<demandeCatalogue>";
                for (CatalogueDemande vDmTemp : dc.getListCatalogueDemande()) {
                    vMessage += "\n\t\t<CatalogueDemande identifiant=\"" + vDmTemp.getId() + "\">" + "\n\t\t\t<titreCatalogueDemande>"
                            + vDmTemp.getTitreCatalogueDemande() + "</titreCatalogueDemande>" + "\n\t\t\t<quantite>" + vDmTemp.getQuantite()
                            + "</quantite>"
                            + "\n\t\t</CatalogueDemande>";
                }
                vMessage += "\n\t</demandeCatalogue>" + "\n\t</typeMessage>" +  "\n</message>";
                out.write(vMessage);
            }

            
            out.write(ajoutFin());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // On a fini on sauvegarde le fichier
        fic.setFic(new File("repertoire/envoi/" + destinataire.toLowerCase() + "/LAB-" + id_fichier + ".xml"));
        fic.setListMess(listeMessage);
        aFichierRepository.save(fic);
        // On clear la liste de message aussi sinon si on envoie plusieurs fichiers on
        // ne fait que ajouter d'autres msg
        listeMessage = new ArrayList<>();
        listeDmStage = new ArrayList<>();
    }

    @RequestMapping(value = "/reponseRapide", method = RequestMethod.POST)
    public String reponseRapide(
                                @RequestParam String msg,
                                @RequestParam(name = "idMsgPrecedent") String idMsgPrecedent,
                                @RequestParam String destinataire
                                ) {
        String vMessage = "\n";
        Fichier fic = new Fichier();
        fic.setDestinataire(destinataire);
        fic.setExpediteur("Laboratoire");
        String pattern = "HH:mm:ss dd-MM-YYYY";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        int i = 0;
        boolean trouve = false;
        
        Message m = new Message("reponseGenerique", dateFormat.format(new Date()), "2160", msg, idMsgPrecedent);
        while (i < 50000 && !trouve) {
            if (aMessageRepository.findById("LAB-" + i).isEmpty()) {
                m.setId("LAB-" + i);
                trouve = true;
            }
            i++;
        }
        aMessageRepository.save(m);
        try (FileWriter fw = new FileWriter(
            "repertoire/envoi/" + destinataire.toLowerCase() + "/LAB-" + m.getId() + ".xml", false);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
                out.write(ajoutDTD());
                out.write(EcrireEnTete(i, destinataire, 1));
                out.write(ajoutHeaderMessage(m.getId(), m.getDateEnvoi(), m.getDureeValidite()));
                vMessage += "\n\t<typeMessage>" + 
                            "\n\t<reponseGenerique>" + 
                            "\n\t\t<msg>" + m.getMsg() + "</msg>" + 
                            "\n\t\t<idMsgPrecedent>" + m.getIdMsgPrecedent() + "</idMsgPrecedent>" + 
                            "\n\t</reponseGenerique>" + 
                            "\n\t </typeMessage>" + 
                            "\n</message>";
                out.write(vMessage);
                out.write(ajoutFin());
            }catch(IOException e){
                e.printStackTrace();
            }
        return "redirect:/index ";
    }

    // Retourne la DTD dans un string
    public String ajoutDTD() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n<!DOCTYPE fichierGlobal ["
                + "\n<!ELEMENT fichierGlobal (destinataire+,expediteur,nbMessages,message+)>"
                + "\n<!ATTLIST fichierGlobal id ID #REQUIRED>" + "\n<!ELEMENT destinataire (#PCDATA)>"
                + "\n<!ELEMENT expediteur (#PCDATA)>" + "\n<!ELEMENT nbMessages (#PCDATA)>"
                + "\n<!ELEMENT message (dateEnvoi,dureeValidite,typeMessage)>" + "\n<!ATTLIST message id ID #REQUIRED>"
                + "\n<!-- Nomenclature dateEnvoi : hh:mm:ss JJ-MM-AAAA -->" + "\n<!ELEMENT dateEnvoi (#PCDATA)>"
                + "\n<!-- Nomenclature dureeValidite : nombre entier : nombres d'heures -->"
                + "\n<!ELEMENT dureeValidite (#PCDATA)>" + "\n<!ELEMENT typeMessage (" + "\n    envoiCatalogue | "
                + "\n    demandeCatalogue | " + "\n    envoiBonCommande | " + "\n    accuseReception | "
                + "\n    offreCollab | " + "\n    demandeCollab | " + "\n    reponseGenerique | "
                + "\n    propositionCommerciale | " + "\n    demandeCommerciale | " + "\n    rechercheSousTraitant |"
                + "\n    propositionSousTraitant |" + "\n    demandeConference | " + "\n    reponseConference | "
                + "\n    demandeListeFormation | " + "\n    reponseListeFormation | " + "\n    demandeStage | "
                + "\n    reponseStage)>" +

                "\n<!-- DTD de l'envoi de catalogue -->" + "\n<!ELEMENT envoiCatalogue (titreCatalogue,listeProduit)> "
                + "\n<!ELEMENT titreCatalogue (#PCDATA)> " + "\n<!ELEMENT listeProduit (produit+)> "
                + "\n<!ELEMENT produit (nom,prix,quantite)> " + "\n<!ATTLIST produit identifiant ID #REQUIRED> "
                + "\n<!ELEMENT nom (#PCDATA)> " + "\n<!ELEMENT prix (#PCDATA)>" + "\n<!ELEMENT quantite (#PCDATA)>" +

                "\n<!-- DTD de la demande de catalogue -->" + "\n<!ELEMENT demandeCatalogue (catalogueDemande+)> "
                + "\n<!ELEMENT catalogueDemande (titreCatalogueDemande,quantite)>"
                + "\n<!ATTLIST catalogueDemande identifiant ID #REQUIRED>"
                + "\n<!ELEMENT titreCatalogueDemande (#PCDATA)>" +

                "\n<!-- DTD de l'envoi de Bon de commande -->"
                + "\n<!ELEMENT envoiBonCommande (numCommande,dateCommande,listeProduit,prixCommande)>"
                + "\n<!ELEMENT numCommande (#PCDATA)>" + "\n<!ELEMENT dateCommande (#PCDATA)>"
                + "\n<!ELEMENT prixCommande (#PCDATA)>" +

                "\n<!-- DTD de l'accusé de réception de commande -->"
                + "\n<!ELEMENT accuseReception (numCommande, dateCommande, dateReceptionAccuseDeReception, listeProduit, prixCommande)>"
                + "\n<!ATTLIST numCommande identifiantCommande ID #REQUIRED>"
                + "\n<!ELEMENT dateReceptionAccuseDeReception (#PCDATA)>" +

                "\n<!-- Offre Collab -->" + "\n<!ELEMENT offreCollab (description,date)>"
                + "\n<!ELEMENT description (#PCDATA)>" +

                "\n<!-- Demande Collab -->" + "\n<!ELEMENT demandeCollab (description,date)>" +

                "\n<!-- Réponse Générique -->" + "\n<!ELEMENT reponseGenerique (msg, idMsgPrécédent)>"
                + "\n<!ELEMENT msg (#PCDATA)>" + "\n<!ELEMENT idMsgPrécédent (#PCDATA)>" +

                "\n<!-- Proposition commerciale -->"
                + "\n<!ELEMENT propositionCommerciale (prixProposition,description,contrat)>"
                + "\n<!ELEMENT prixProposition (forfait+)> " + "\n<!ELEMENT forfait (#PCDATA)>"
                + "\n<!ELEMENT contrat (date)>" +

                "\n<!-- Demande Commerciale -->"
                + "\n<!ELEMENT demandeCommerciale (prixProposition,description,contrat)>" +

                "\n<!-- Proposition Sous Traitant -->"
                + "\n<!ELEMENT propositionSousTraitant (prixProposition,description,contrat)>" +

                "\n<!-- Recherche Sous Traitant -->"
                + "\n<!ELEMENT rechercheSousTraitant (prixProposition,description,contrat)>" +

                "\n<!-- Demande de conférence -->" + "\n<!ELEMENT demandeConference (conf+)>"
                + "\n<!ELEMENT conf (id,sujet,lieu,dateDebut,dureeConference)>" + "\n<!ELEMENT id (#PCDATA)>"
                + "\n<!ELEMENT sujet (#PCDATA)>" + "\n<!ELEMENT lieu (#PCDATA)>" + "\n<!-- dureeConference en heure -->"
                + "\n<!ELEMENT dureeConference (#PCDATA)>" +

                "\n<!-- Réponse de conférence -->" + "\n<!ELEMENT reponseConference (reponseGenerique)>" +

                "\n<!-- Demande de listes de formations -->" + "\n<!ELEMENT demandeListeFormation (form+)>"
                + "\n<!ELEMENT form (id,branche)>" + "\n<!ELEMENT branche (filiere+)>"
                + "\n<!ELEMENT filiere (#PCDATA)>" +

                "\n<!-- Reponse de listes de formations -->" + "\n<!ELEMENT reponseListeFormation (catalogue+)>"
                + "\n<!ELEMENT catalogue (id,liste)>" + "\n<!ELEMENT liste (formation+)>"
                + "\n<!ELEMENT formation (titre,description,filiere)>" + "\n<!ELEMENT titre (#PCDATA)>" +

                "\n<!-- Demande de stage -->" + "\n<!ELEMENT demandeStage (dmStage+)>"
                + "\n<!ELEMENT dmStage (id,objet,description,lieu,remuneration,date)>"
                + "\n<!ELEMENT date (dateDebut, (dateFin | duree))>"
                + "\n<!-- Nomenclature dateDebut : hh:mm:ss JJ-MM-AAAA -->" + "\n<!ELEMENT dateDebut (#PCDATA)>"
                + "\n<!ELEMENT dateFin (#PCDATA)>" + "\n<!-- Nomenclature duree : nombre entier : nombres de jours -->"
                + "\n<!ELEMENT duree (#PCDATA)>" + "\n<!ELEMENT objet (#PCDATA)>"
                + "\n<!ELEMENT remuneration (#PCDATA)>" +

                "\n<!-- Réponse de stage -->" + "\n<!ELEMENT reponseStage (rpStage+)>"
                + "\n<!ELEMENT rpStage (id,objet,cv,lettre)>"
                + "\n<!ELEMENT cv (etatcivil,formationStage+,experience*)>"
                + "\n<!ELEMENT lettre (etatcivil,description)>"
                + "\n<!ELEMENT etatcivil (nom,prenom,dateNaissance,lieuNaissance,lieuResidence,photo?,email?,tel?)>"
                + "\n<!ELEMENT formationStage (titre,date,lieu?,mention?,description?)>"
                + "\n<!ELEMENT experience (titre,date,lieu,fonction?,description?)>" + "\n<!ELEMENT prenom (#PCDATA)>"
                + "\n<!ELEMENT dateNaissance (#PCDATA)>" + "\n<!ELEMENT lieuNaissance (#PCDATA)>"
                + "\n<!ELEMENT lieuResidence (#PCDATA)>" + "\n<!ELEMENT photo (#PCDATA)>"
                + "\n<!ELEMENT email (#PCDATA)>" + "\n<!ELEMENT tel (#PCDATA)>" + "\n<!ELEMENT mention (#PCDATA)>"
                + "\n<!ELEMENT fonction (#PCDATA)>" +

                "\n]>";
    }

    public String EcrireEnTete(int id, String destinataire, int nbMessage) {

        return "\n" + "\n<fichierGlobal id =\"" + id + "\">" + "\n" + "\n\t<destinataire>" + destinataire
                + "</destinataire>" + "\n\t<expediteur>Laboratoire</expediteur>" + "\n\t<nbMessages>" + nbMessage
                + "</nbMessages>";

    }

    public String ajoutFin() {

        return "\n</fichierGlobal>";
    }

    public String ajoutHeaderMessage(String id, String dateEnvoi, String dureeValidite) {

        return "\n" + "\n\t<message id=\"" + id + "\">" + "\n\t\t<dateEnvoi>" + dateEnvoi + "</dateEnvoi>"
                + "\n\t\t<dureeValidite>" + dureeValidite + "</dureeValidite>"

        ;
    }
}
