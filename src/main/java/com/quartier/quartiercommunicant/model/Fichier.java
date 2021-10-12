package com.quartier.quartiercommunicant.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class Fichier {
    
    @Id
    private int id;

    private String expediteur;
    private String destinataire;
    private int checksum;
    private File fic;

    @OneToMany(orphanRemoval = true)
    private List<Message> listMess = new ArrayList<>();

    
    public Fichier(){
        /* Default constructor */
    }

    public Fichier(String pathname){
        setChecksum(0);
        fic = new File(pathname);
    }   
}