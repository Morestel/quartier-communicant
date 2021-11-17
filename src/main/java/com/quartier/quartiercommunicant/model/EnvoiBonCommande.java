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
    private int id;
    private String dateCommande;
    @OneToMany
    private List<Produit> listeProduit; 
    private int prixCommande;

    public EnvoiBonCommande(){
        /* */
    }

    public EnvoiBonCommande(int id, String dateCommande, List<Produit> listeProduit, int prixCommande) {
        this.id = id;
        this.dateCommande = dateCommande;
        this.listeProduit = listeProduit;
        this.prixCommande = prixCommande;
    }

    
}
