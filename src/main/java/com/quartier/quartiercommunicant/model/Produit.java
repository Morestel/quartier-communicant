package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Produit {
    
    @Id
    private int id;
    private String nom;
    private int prix;
    private int quantite;
    
    public Produit(){
        /* */
    }

    public Produit(int id, String nom, int prix, int quantite) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
    }
}
