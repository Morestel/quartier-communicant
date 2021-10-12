package com.quartier.quartiercommunicant.model;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.Data;

import org.springframework.data.annotation.Id;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Data
public class Fichier {
    
    @Id
    private int id;

    private String expediteur;
    private String destinataire;
    private int checksum;
    private File fic;
    private List<Message> listMess;

    public Fichier(String pathname){
        setChecksum(0);
        fic = new File(pathname);
        listMess = new ArrayList<>();
    }

    public void lireEnTete() throws ParserConfigurationException,
    SAXException, IOException, DOMException, ParseException{

        try{
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(getFic());
            
            document.getDocumentElement().normalize();
            System.out.println("IDENTIFIANT = " + document.getDocumentElement().getAttribute("id"));
            System.err.println("------------");
            setId(Integer.valueOf(document.getDocumentElement().getAttribute("id")));
            setExpediteur(document.getElementsByTagName("expediteur").item(0).getTextContent().replace(" ", "").replace("\n", "").replace("\t", ""));
            
            setDestinataire(document.getElementsByTagName("destinataire").item(0).getTextContent().replace(" ", "").replace("\n", "").replace("\t", ""));
            
            int check = Integer.parseInt(document.getElementsByTagName("nbMessages").item(0).getTextContent().replace(" ", "").replace("\n", "").replace("\t", ""));
            setChecksum(check);

        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void lireMessage(){
        try {
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(getFic());
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

            System.out.println("Nombre d'offre collab : " + oCollab.getLength());
            System.out.println("Nombre de demande collab : " + dCollab.getLength());
            System.out.println("Nom de réponses génériques : " + rGenerique.getLength());
            
            String description;
            String dateDebut;
            String dateFin;

            String msg;
            String idMsgPrecedent;

            String dateEnvoi;
            String dureeValidite;

            Message m;
            // On commence par gérer les offre de collaborations
            for (int i = 0; i < oCollab.getLength(); i++){
                
                dateEnvoi = oCollab.item(0).getParentNode().getFirstChild().getNextSibling().getTextContent();
                dureeValidite = oCollab.item(0).getParentNode().getFirstChild().getNextSibling().getNextSibling().getNextSibling().getTextContent();
                description = oCollab.item(0).getChildNodes().item(1).getTextContent();
                dateDebut = oCollab.item(0).getChildNodes().item(3).getChildNodes().item(1).getTextContent();
                dateFin = oCollab.item(0).getChildNodes().item(3).getChildNodes().item(3).getTextContent();

                m = new Message("offreCollab", dateEnvoi, dureeValidite, description, dateDebut, dateFin);
                listMess.add(m);

            }

            // Demande de collaboration
            for (int i = 0; i < dCollab.getLength(); i++){

                dateEnvoi = dCollab.item(0).getParentNode().getFirstChild().getNextSibling().getTextContent();
                dureeValidite = dCollab.item(0).getParentNode().getFirstChild().getNextSibling().getNextSibling().getNextSibling().getTextContent();
                description = dCollab.item(0).getChildNodes().item(1).getTextContent();
                dateDebut = dCollab.item(0).getChildNodes().item(3).getChildNodes().item(1).getTextContent();
                dateFin = dCollab.item(0).getChildNodes().item(3).getChildNodes().item(3).getTextContent();

                m = new Message("demandeCollab", dateEnvoi, dureeValidite, description, dateDebut, dateFin);
                listMess.add(m);
            }

            // Réponse générique
            for (int i = 0; i < rGenerique.getLength(); i++){

                dateEnvoi = rGenerique.item(0).getParentNode().getFirstChild().getNextSibling().getTextContent();
                dureeValidite = rGenerique.item(0).getParentNode().getFirstChild().getNextSibling().getNextSibling().getNextSibling().getTextContent();
                msg = rGenerique.item(0).getChildNodes().item(1).getTextContent();
                idMsgPrecedent = rGenerique.item(0).getChildNodes().item(3).getTextContent();
                
                m = new Message("reponseGenerique", dateEnvoi, dureeValidite, msg, idMsgPrecedent);
                listMess.add(m);

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

    public void chargerMessages() throws ParserConfigurationException,
    SAXException, IOException, DOMException, ParseException{
        
        
        /* */
    }
}
