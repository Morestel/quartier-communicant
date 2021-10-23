package com.quartier.quartiercommunicant.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class DemandeStage {
    
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private int id;

    @ManyToMany(cascade = CascadeType.REMOVE)
    private List<DmStage> listDmStage = new ArrayList<>();

    public DemandeStage(){
        /* */
    }

    public DemandeStage(List<DmStage> listDmStage){
        setListDmStage(listDmStage);
    }
}
