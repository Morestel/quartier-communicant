package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Produit {
    
    @Id
    private String id;
    private String nom;
    private float prix;
    private int quantite;
    
    public Produit(){
        /* */
    }

    public Produit(String id, String nom, float prix, int quantite) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
    }
}
