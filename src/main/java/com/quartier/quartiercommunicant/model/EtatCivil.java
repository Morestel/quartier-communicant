package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class EtatCivil {
    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private int id;
    private String nom;
    private String prenom;
    private String dateNaissance;
    private String lieuResidence;
    private String photo;
    private String email;
    private int tel;
   
    public EtatCivil(){
        /* */
    }

    public EtatCivil(String nom, String prenom, String dateNaissance, String lieuResidence, String photo, String email, int tel){
        setNom(nom);
        setPrenom(prenom);
        setDateNaissance(dateNaissance);
        setLieuResidence(lieuResidence);
        setPhoto(photo);
        setEmail(email);
        setTel(tel);
    }
}
