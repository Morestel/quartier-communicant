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

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Data
public class Fichier {
    
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

            Message m;
            // On commence par gérer les offre de collaborations
            for (int i = 0; i < oCollab.getLength(); i++){
                description = oCollab.item(0).getChildNodes().item(1).getTextContent();
                dateDebut = oCollab.item(0).getChildNodes().item(3).getChildNodes().item(1).getTextContent();
                dateFin = oCollab.item(0).getChildNodes().item(3).getChildNodes().item(3).getTextContent();

                m = new Message("offreCollab", dateEnv.getTextContent(), dureeValid.getTextContent(), description, dateDebut, dateFin);
                listMess.add(m);
            }

            // Demande de collaboration
            for (int i = 0; i < dCollab.getLength(); i++){
                description = dCollab.item(0).getChildNodes().item(1).getTextContent();
                dateDebut = dCollab.item(0).getChildNodes().item(3).getChildNodes().item(1).getTextContent();
                dateFin = dCollab.item(0).getChildNodes().item(3).getChildNodes().item(3).getTextContent();

                m = new Message("demandeCollab", dateEnv.getTextContent(), dureeValid.getTextContent(), description, dateDebut, dateFin);
                listMess.add(m);
            }

            // Réponse générique
            for (int i = 0; i < rGenerique.getLength(); i++){
                msg = rGenerique.item(0).getChildNodes().item(1).getTextContent();
                idMsgPrecedent = rGenerique.item(0).getChildNodes().item(3).getTextContent();
                
                m = new Message("reponseGenerique", dateEnv.getTextContent(), dureeValid.getTextContent(), msg, idMsgPrecedent);
                listMess.add(m);

                /* 
                <message id="7">
        <dateEnvoi>10:15:30 01-02-1999</dateEnvoi>
        <dureeValidite>60</dureeValidite>
        <reponseGenerique>
            <msg>Salut c'est non</msg>
            <idMsgPrécédent>4</idMsgPrécédent>
        </reponseGenerique>
    </message>
                */
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
