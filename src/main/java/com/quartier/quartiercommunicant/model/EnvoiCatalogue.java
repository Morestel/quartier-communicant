package com.quartier.quartiercommunicant.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class EnvoiCatalogue {
    
    @Id
    private String titreCatalogue;
    @ManyToMany(cascade = CascadeType.REMOVE)
    List<Produit> listeProduit = new ArrayList<>();

    public EnvoiCatalogue(){
        /**/ 
    }

    public EnvoiCatalogue(String titreCatalogue, List<Produit> listeProduit) {
        this.titreCatalogue = titreCatalogue;
        this.listeProduit = listeProduit;
    }

    
}
