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
        if (expediteur.equalsIgnoreCase("magasin")){
            repertoire = new File("repertoire/" + source + "/magasin");
        }else if (expediteur.equalsIgnoreCase("entreprise")){
            repertoire = new File("repertoire/" + source + "/entreprise");
        }else{
            repertoire = new File("repertoire/" + source + "/ecole");
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
        model.addAttribute("expediteur", expediteur);
        if (source.equals("archive")){
            model.addAttribute("listeFichier", listeFichier);
            return "Archive";
        }
        else if(source.equals("envoi")){
            if (expediteur.equals("Ecole")){
                List<Fichier> listeFichierEcole = new ArrayList<>();
                for (Fichier f: aFichierRepository.findAll()){
                    if (f.getExpediteur().equals("Laboratoire") && f.getDestinataire().equals("Ecole")){
                        listeFichierEcole.add(f);
                    }
                }
                model.addAttribute("listeFichier", listeFichierEcole);
            }
            if (expediteur.equals("Entreprise")){
                List<Fichier> listeFichierEntreprise = new ArrayList<>();
                for (Fichier f: aFichierRepository.findAll()){
                    if (f.getExpediteur().equals("Laboratoire") && f.getDestinataire().equals("Entreprise")){
                        listeFichierEntreprise.add(f);
                    }
                }
                model.addAttribute("listeFichier", listeFichierEntreprise);
            }

            if (expediteur.equals("Magasin")){
                List<Fichier> listeFichierMagasin = new ArrayList<>();
                for (Fichier f: aFichierRepository.findAll()){
                    if (f.getExpediteur().equals("Laboratoire") && f.getDestinataire().equals("Magasin")){
                        listeFichierMagasin.add(f);
                    }
                }
                model.addAttribute("listeFichier", listeFichierMagasin);
            }
            
            return "Envoi";
        }
        else{
            model.addAttribute("listeFichier", lf);
            return "Erreur";
        }
       
        
    }
}
