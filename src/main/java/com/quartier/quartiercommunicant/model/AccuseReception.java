package com.quartier.quartiercommunicant.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Data;

@Data
@Entity
public class AccuseReception {
    
    @Id
    private String numCommande;
    private String identifiantCommande;
    private String dateCommande;
    private String dateReceptionAccuseDeReception;
    @OneToMany
    List<Produit> listeProduit;
    private float prixCommande;

    public AccuseReception(){
        /* */
    }

    public AccuseReception(String numCommande, String identifiantCommande, String dateCommande,
            String dateReceptionAccuseDeReception, List<Produit> listeProduit, float prixCommande) {
        this.numCommande = numCommande;
        this.identifiantCommande = identifiantCommande;
        this.dateCommande = dateCommande;
        this.dateReceptionAccuseDeReception = dateReceptionAccuseDeReception;
        this.listeProduit = listeProduit;
        this.prixCommande = prixCommande;
    }

    
}
