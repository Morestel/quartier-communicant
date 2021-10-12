package com.quartier.quartiercommunicant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.quartier.quartiercommunicant.model.*;

@Controller
public class MainController {
    

    @RequestMapping({"index", "" })
    
    public String index(Model model){
    
        File repertoire = new File("repertoire");

        Fichier fic = new Fichier(repertoire.getName() + "/fichier.xml");
        System.out.println(fic.getFic().length());
        if (fic.getFic().length() > 10000){
            System.err.println("Trop de caractères");
        }

       
    
        try{
            fic.lireEnTete();
            fic.lireMessage();
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
}
