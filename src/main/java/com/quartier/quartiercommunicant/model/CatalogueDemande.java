package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class CatalogueDemande {
    
    @Id
    private String id;
    private String titreCatalogueDemande;
    private int quantite;


    public CatalogueDemande(){
        /* */
    }

    public CatalogueDemande(String id, String titreCatalogueDemande, int quantite){
        setId(id);
        setTitreCatalogueDemande(titreCatalogueDemande);
        setQuantite(quantite);
    }
}
