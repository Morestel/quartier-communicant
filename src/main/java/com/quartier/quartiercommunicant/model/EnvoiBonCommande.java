package com.quartier.quartiercommunicant.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class EnvoiBonCommande {
    
    @Id
    private String id;
    private String identifiantCommande;
    private String dateCommande;
    @ManyToMany(cascade = CascadeType.REMOVE)
    private List<Produit> listeProduit = new ArrayList<>(); 
    private float prixCommande;

    public EnvoiBonCommande(){
        /* */
    }

    public EnvoiBonCommande(String id, String identifiantCommande, String dateCommande, List<Produit> listeProduit, float prixCommande) {
        this.id = id;
        this.identifiantCommande = identifiantCommande;
        this.dateCommande = dateCommande;
        this.listeProduit = listeProduit;
        this.prixCommande = prixCommande;
    }

    
}
