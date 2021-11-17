package com.quartier.quartiercommunicant.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Data;

@Data
@Entity
public class DemandeCatalogue {
    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private int id;

    @ManyToMany(cascade = CascadeType.REMOVE)
    private List<CatalogueDemande> listCatalogueDemande = new ArrayList<>();

    public DemandeCatalogue(){
        /* */
    }

    public DemandeCatalogue(List<CatalogueDemande> listCatalogueDemande){
        setListCatalogueDemande(listCatalogueDemande);
    }
}
