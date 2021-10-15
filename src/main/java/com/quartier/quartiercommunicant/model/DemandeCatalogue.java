package com.quartier.quartiercommunicant.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class DemandeCatalogue {
    
    @Id
    int id;
    int quantite;

    public DemandeCatalogue(){
        /* */
    }

    public DemandeCatalogue(int id, int quantite){
        setId(id);
        setQuantite(quantite);
    }
}
