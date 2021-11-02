package com.quartier.quartiercommunicant.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.quartier.quartiercommunicant.model.Fichier;
import com.quartier.quartiercommunicant.repository.FichierRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class NavigationController {
    
    @Inject
    FichierRepository aFichierRepository;

    @RequestMapping("envoi")
    public String envoi(Model model){
        model.addAttribute("titre", "Labo - Envoi");
        model.addAttribute("source", "envoi");
        return "Portail";
    }

    @RequestMapping("archive")
    public String archive(Model model){
        model.addAttribute("titre", "Labo - Archive");
        model.addAttribute("source", "archive");
        return "Portail";
    }

    @RequestMapping("erreur")
    public String erreur(Model model){
        model.addAttribute("titre", "Labo - Erreur");
        model.addAttribute("source", "erreur");
        return "Portail";
    }

    @RequestMapping("{source}/{expediteur}")
    public String magasin(Model model, @PathVariable String source, @PathVariable String expediteur){

        File repertoire;
        if (expediteur.equals("Magasin")){
            repertoire = new File("repertoire/" + source + "/Magasin");
        }else if (expediteur.equals("entreprise")){
            repertoire = new File("repertoire/" + source + "/Entreprise");
        }else{
            repertoire = new File("repertoire/" + source + "/Ecole");
        }
        List<File> lf = new ArrayList<>();
        try{
            System.err.println(repertoire.listFiles().length);
            System.err.println(repertoire.getAbsolutePath());
            System.err.println(expediteur);
            if (repertoire.listFiles().length > 0){
                for (File f : repertoire.listFiles()){
                    lf.add(f);
                }
            }
        }catch(Exception e){
            
        }
        

        List<Fichier> listeFichier = aFichierRepository.findFichierByExped(expediteur);
        model.addAttribute("titre", source + " - " + expediteur);
        if (source.equals("archive")){
            model.addAttribute("listeFichier", listeFichier);
            return "Archive";
        }
        else if(source.equals("envoi")){
            model.addAttribute("listeFichier", listeFichier);
            return "Envoi";
        }
        else{
            model.addAttribute("listeFichier", lf);
            return "Erreur";
        }
       
        
    }
}
