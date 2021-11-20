package com.quartier.quartiercommunicant.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class EnvoiBonCommande {
    
    @Id
    private String id;
    private String dateCommande;
    @OneToMany
    private List<Produit> listeProduit; 
    private float prixCommande;

    public EnvoiBonCommande(){
        /* */
    }

    public EnvoiBonCommande(String id, String dateCommande, List<Produit> listeProduit, float prixCommande) {
        this.id = id;
        this.dateCommande = dateCommande;
        this.listeProduit = listeProduit;
        this.prixCommande = prixCommande;
    }

    
}
