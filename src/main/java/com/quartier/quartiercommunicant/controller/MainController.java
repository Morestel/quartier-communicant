package com.quartier.quartiercommunicant.controller;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.quartier.quartiercommunicant.model.DemandeStage;
import com.quartier.quartiercommunicant.model.Fichier;
import com.quartier.quartiercommunicant.model.Message;
import com.quartier.quartiercommunicant.repository.DemandeStageRepository;
import com.quartier.quartiercommunicant.repository.FichierRepository;
import com.quartier.quartiercommunicant.repository.MessageRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
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
        /*
        for (Message m : fic.getListMess()){
            System.out.println();
            System.out.println(m.getType());
            System.out.println(m.getDescription());
            System.out.println(m.getDateDebut());
            System.out.println(m.getDateFin());
            System.out.println();
        }
        */
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

    public void lireMessage(Fichier fic){
        try {
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(fic.getFic());
            document.getDocumentElement().normalize();
            System.out.println("Root Element :" + document.getDocumentElement().getNodeName());
            
            NodeList listeMessage = document.getElementsByTagName("message");
            Node mess = document.getElementsByTagName("message").item(0);
            Node dateEnv = document.getElementsByTagName("dateEnvoi").item(0);
            Node dureeValid = document.getElementsByTagName("dureeValidite").item(0);
            //setDureeValidite(dureeValid.getTextContent());
            //setDateEnvoi(dateEnv.getTextContent());
            
            NodeList list = mess.getChildNodes();
            
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

            String msg;
            String idMsgPrecedent;

            String dateEnvoi;
            String dureeValidite;

            Message m;
            List<Message> lMessage = new ArrayList<>();
            // On commence par gérer les offre de collaborations
            for (int i = 0; i < oCollab.getLength(); i++){
                
                dateEnvoi = oCollab.item(0).getParentNode().getFirstChild().getNextSibling().getTextContent();
                dureeValidite = oCollab.item(0).getParentNode().getFirstChild().getNextSibling().getNextSibling().getNextSibling().getTextContent();
                description = oCollab.item(0).getChildNodes().item(1).getTextContent();
                dateDebut = oCollab.item(0).getChildNodes().item(3).getChildNodes().item(1).getTextContent();
                dateFin = oCollab.item(0).getChildNodes().item(3).getChildNodes().item(3).getTextContent();

                m = new Message("offreCollab", dateEnvoi, dureeValidite, description, dateDebut, dateFin);
                lMessage = fic.getListMess();
                lMessage.add(m);
                fic.setListMess(lMessage);
                aMessageRepository.save(m);
               
            }

            // Demande de collaboration
            for (int i = 0; i < dCollab.getLength(); i++){

                dateEnvoi = dCollab.item(0).getParentNode().getFirstChild().getNextSibling().getTextContent();
                dureeValidite = dCollab.item(0).getParentNode().getFirstChild().getNextSibling().getNextSibling().getNextSibling().getTextContent();
                description = dCollab.item(0).getChildNodes().item(1).getTextContent();
                dateDebut = dCollab.item(0).getChildNodes().item(3).getChildNodes().item(1).getTextContent();
                dateFin = dCollab.item(0).getChildNodes().item(3).getChildNodes().item(3).getTextContent();

                m = new Message("demandeCollab", dateEnvoi, dureeValidite, description, dateDebut, dateFin);
                lMessage = fic.getListMess();
                lMessage.add(m);
                fic.setListMess(lMessage);
                aMessageRepository.save(m);
            }

            // Réponse générique
            for (int i = 0; i < rGenerique.getLength(); i++){

                dateEnvoi = rGenerique.item(0).getParentNode().getFirstChild().getNextSibling().getTextContent();
                dureeValidite = rGenerique.item(0).getParentNode().getFirstChild().getNextSibling().getNextSibling().getNextSibling().getTextContent();
                msg = rGenerique.item(0).getChildNodes().item(1).getTextContent();
                idMsgPrecedent = rGenerique.item(0).getChildNodes().item(3).getTextContent();
                
                m = new Message("reponseGenerique", dateEnvoi, dureeValidite, msg, idMsgPrecedent);
                lMessage = fic.getListMess();
                lMessage.add(m);
                fic.setListMess(lMessage);
                aMessageRepository.save(m);
            }

            // Demande de stage
            DemandeStage dmStage = new DemandeStage();
            for (int i = 0; i < dStage.getLength(); i++){
                dateEnvoi = dStage.item(0).getParentNode().getFirstChild().getNextSibling().getTextContent();
                dureeValidite = dStage.item(0).getParentNode().getFirstChild().getNextSibling().getNextSibling().getNextSibling().getTextContent();
                dmStage.setId(Integer.valueOf(dStage.item(0).getChildNodes().item(1).getChildNodes().item(1).getTextContent()));
                dmStage.setObjet(dStage.item(0).getChildNodes().item(1).getChildNodes().item(3).getTextContent());
                dmStage.setDescription(dStage.item(0).getChildNodes().item(1).getChildNodes().item(5).getTextContent());
                dmStage.setLieu(dStage.item(0).getChildNodes().item(1).getChildNodes().item(7).getTextContent());
                dmStage.setRemuneration(Integer.valueOf(dStage.item(0).getChildNodes().item(1).getChildNodes().item(9).getTextContent()));
                dmStage.setDateDebut(dStage.item(0).getChildNodes().item(1).getChildNodes().item(11).getChildNodes().item(1).getTextContent());
                dmStage.setDuree(Integer.valueOf(dStage.item(0).getChildNodes().item(1).getChildNodes().item(11).getChildNodes().item(3).getTextContent()));

                // On insère la demande de stage dans le message
                aDemandeStageRepository.save(dmStage); // On sauve la demande de stage
                m = new Message("demandeStage", dateEnvoi, dureeValidite, dmStage);
                lMessage = fic.getListMess();
                lMessage.add(m);
                fic.setListMess(lMessage);
                aMessageRepository.save(m);
            }
            /*
            System.err.println("Premier fils : " + mess.getFirstChild().getTextContent());
            NodeList nL = document.getDocumentElement().getChildNodes();
            
            for (int j = 0; j < list.getLength(); j++){
                System.err.println("Item " + j + " " + list.item(j).getTextContent());
                System.out.println();
            }
            
            
            System.out.println("----------------------------");

            for (int i = 0; i < nList.getLength(); i++){
                System.out.println("Message numéro " + i);
                System.out.println(nList.item(i).getTextContent());
                System.out.println();
            }
            */
            /*
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = list.item(temp);
                System.out.println(nNode.getNodeName());
                System.out.println("\nCurrent Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("Message: " + eElement.getElementsByTagName("message").item(0).getTextContent());
                    // System.out.println("Employee id : " + eElement.getAttribute("id"));
                    // System.out.println("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
                    // System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
                    // System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());
                }
            }*/
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
